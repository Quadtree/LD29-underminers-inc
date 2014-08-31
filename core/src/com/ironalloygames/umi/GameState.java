package com.ironalloygames.umi;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.ironalloygames.umi.Terrain.TerrainType;
import com.ironalloygames.umi.entity.Constructor;
import com.ironalloygames.umi.entity.Driller;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.Porter;
import com.ironalloygames.umi.entity.Refinery;
import com.ironalloygames.umi.entity.Unit;
import com.ironalloygames.umi.entity.Unit.Faction;

public class GameState implements Disposable, ContactListener {
	public static short COL_BIT_AI_SHOT = 32;
	public static short COL_BIT_AI_UNIT = 4;

	public static short COL_BIT_PLAN = 8;
	public static short COL_BIT_PLAYER_SHOT = 16;

	public static short COL_BIT_PLAYER_UNIT = 1;

	public static short COL_BIT_WALL = 2;

	public static final float SCREEN_TILE_HEIGHT = 16;

	public static final float SCREEN_TILE_WIDTH = 21.3333f;

	AIController aic = new AIController();

	public float aiCredits = 0;

	Box2DDebugRenderer dr;

	ArrayList<Entity> entities = new ArrayList<Entity>();

	public ArrayList<Entity> entityAddQueue = new ArrayList<Entity>();

	public InGameInputSystem igis;

	public float playerCredits;
	ArrayList<Entity> temp;
	public Terrain terrain;
	public int tick;
	long ticksRun = 0;
	public World world;

	public OrthographicCamera worldCam;

	public GameState() {

	}

	@Override
	public void beginContact(Contact contact) {

		Entity e1 = null, e2 = null;

		if (contact.getFixtureA().getUserData() instanceof Entity) {
			e1 = (Entity) contact.getFixtureA().getUserData();
		}

		if (contact.getFixtureB().getUserData() instanceof Entity) {
			e2 = (Entity) contact.getFixtureB().getUserData();
		}

		if (e1 != null)
			e1.onCollision(e2);

		if (e2 != null)
			e2.onCollision(e1);
	}

	public void create() {
		world = new World(new Vector2(0, -.8f), true);
		terrain = new Terrain();

		worldCam = new OrthographicCamera(SCREEN_TILE_WIDTH, SCREEN_TILE_HEIGHT);

		igis = new InGameInputSystem();

		ticksRun = System.currentTimeMillis();

		worldCam.position.x = SCREEN_TILE_WIDTH / 2 - .5f;
		worldCam.position.y = Terrain.HEIGHT / 2;
		worldCam.update();

		createFactionStart(2, Terrain.HEIGHT / 2, Faction.PLAYER);
		createFactionStart(Terrain.WIDTH - 2, Terrain.HEIGHT / 2, Faction.AI);

		for (int i = 0; i < 15; i++) {
			terrain.createNodule(MathUtils.random.nextInt(Terrain.WIDTH), MathUtils.random.nextInt(Terrain.HEIGHT), MathUtils.random(1, 4), TerrainType.ORE);
		}

		terrain.bake();

		dr = new Box2DDebugRenderer();

		world.setContactListener(this);
	}

	private void createFactionStart(int x, int y, Faction faction) {
		Unit u = new Refinery();
		u.setPosition(new Vector2(x, y));
		u.setFaction(faction);
		entityAddQueue.add(u);

		u = new Porter();
		u.setPosition(new Vector2(x, y + 2));
		u.setFaction(faction);
		entityAddQueue.add(u);

		u = new Driller();
		u.setPosition(new Vector2(x, y + 3));
		u.setFaction(faction);
		entityAddQueue.add(u);

		u = new Constructor();
		u.setPosition(new Vector2(x, y + 4));
		u.setFaction(faction);
		entityAddQueue.add(u);

		terrain.carveCavern(x, y, 5);
	}

	@Override
	public void dispose() {
		world.dispose();
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public List<Entity> getEntitiesPickedInRectangle(Rectangle rect) {
		ArrayList<Entity> ret = new ArrayList<Entity>();

		for (Entity e : entities) {
			if (e.getBoundingRectangle().overlaps(rect)) {
				ret.add(e);
			}
		}

		return ret;
	}

	public List<Entity> getEntitiesWithinLOSOf(final Vector2 position, final float maxRange, final boolean allowBuildings) {
		temp = new ArrayList<Entity>();
		/*
		 * world.QueryAABB(new QueryCallback() {
		 *
		 * @Override public boolean reportFixture(Fixture fixture) { if
		 * (fixture.getUserData() != null) { Entity e = (Entity)
		 * fixture.getUserData();
		 *
		 * float dist = e.getPosition().dst2(position);
		 *
		 * if (dist < maxRange * maxRange) { if (dist < .01f ||
		 * terrain.isInLOS(position, e.getPosition(), allowBuildings)) {
		 * temp.add(e); } } } return true; } }, position.x - maxRange,
		 * position.y - maxRange, position.x + maxRange, position.y + maxRange);
		 */

		for (Entity e : entities) {
			float dist = e.getPosition().dst2(position);

			if (dist < maxRange * maxRange) {
				if (dist < .01f || terrain.isInLOS(position, e.getPosition(), allowBuildings)) {
					temp.add(e);
				}
			}
		}

		return temp;
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	public void render() {
		UMI.batch.setProjectionMatrix(worldCam.combined);
		UMI.batch.begin();
		terrain.render();
		UMI.batch.end();

		UMI.batch.begin();
		for (Entity e : entities) {
			e.render();
		}
		UMI.batch.end();

		igis.render();

		// dr.render(world, worldCam.combined);

		int updates = 0;

		while (ticksRun < System.currentTimeMillis() && updates++ < 4) {
			update();
			ticksRun += 16;
			tick++;
		}

		if (!UMI.a.getMusic("ambient").isPlaying()) {
			UMI.a.getMusic("ambient").setLooping(true);
			UMI.a.getMusic("ambient").setVolume(.5f);
			UMI.a.getMusic("ambient").play();
		}

		// System.out.println(new Path(terrain, new Position((int)
		// worldCam.position.x, Terrain.HEIGHT), new Position((int)
		// worldCam.position.x - 11, Terrain.HEIGHT - 11)).getPath());
	}

	public void update() {
		if (igis.defeatScreenUp || igis.victoryScreenUp || igis.titleScreenUp || igis.helpScreenUp)
			return;
		igis.update();
		aic.update();

		for (Entity e : entityAddQueue) {
			e.created();
			entities.add(e);
		}
		entityAddQueue.clear();

		int playerUnits = 0;
		int aiUnits = 0;

		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).keep()) {
				entities.get(i).update();

				if (entities.get(i) instanceof Unit && ((Unit) entities.get(i)).getFaction() == Faction.PLAYER && ((Unit) entities.get(i)).isFullyConstructed())
					playerUnits++;
				if (entities.get(i) instanceof Unit && ((Unit) entities.get(i)).getFaction() == Faction.AI && ((Unit) entities.get(i)).isFullyConstructed())
					aiUnits++;
			} else {
				entities.get(i).destroyed();
				entities.remove(i--);
			}
		}

		if (aiUnits == 0) {
			GA.trackEvent("Game Action", "Win", 0);
			igis.victoryScreenUp = true;
		}

		if (playerUnits == 0) {
			GA.trackEvent("Game Action", "Lose", 0);
			igis.defeatScreenUp = true;
		}

		world.step(0.016f, 1, 1);
	}
}
