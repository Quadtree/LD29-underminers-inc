package com.ironalloygames.umi;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class Terrain {

	public enum TerrainType {
		DIRT,
		ORE,
		STONE
	}

	public static int HEIGHT = 50;
	public static int WIDTH = 50;

	Body body;
	public boolean extraImpassibleSquares[][] = new boolean[WIDTH][HEIGHT];
	Fixture fixtures[][] = new Fixture[WIDTH][HEIGHT];
	boolean isInLOS;
	boolean mined[][] = new boolean[WIDTH][HEIGHT];

	float minedLevel[][] = new float[WIDTH][HEIGHT];

	TerrainType terrain[][] = new TerrainType[WIDTH][HEIGHT];

	public Terrain() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				terrain[x][y] = TerrainType.DIRT;
			}
		}
	}

	public void bake() {
		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.StaticBody;

		body = UMI.gs.world.createBody(bd);

		for (int x = -1; x < WIDTH + 1; x++) {
			for (int y = -1; y < HEIGHT + 1; y++) {
				if (!isPassable(x, y)) {
					PolygonShape ps = new PolygonShape();
					ps.setAsBox(.5f, .5f, new Vector2(x, y), 0);

					Fixture tf = body.createFixture(ps, 0);
					Filter fl = new Filter();
					fl.categoryBits = 2;
					fl.maskBits = Short.MAX_VALUE;
					tf.setFilterData(fl);

					if (x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT) {
						fixtures[x][y] = tf;
					}
				}
			}
		}
	}

	public void carveCavern(int x, int y, int radius) {
		for (int cx = x - radius; cx <= x + radius; cx++) {
			for (int cy = y - radius; cy <= y + radius; cy++) {
				float distSqr = (float) (Math.pow(cx - x, 2) + Math.pow(cy - y, 2));

				if (distSqr < radius * radius) {
					setMined(cx, cy, true);
				}
			}
		}
	}

	public void createNodule(int x, int y, int radius, TerrainType type) {
		for (int cx = x - radius; cx <= x + radius; cx++) {
			for (int cy = y - radius; cy <= y + radius; cy++) {
				float distSqr = (float) (Math.pow(cx - x, 2) + Math.pow(cy - y, 2));

				if (distSqr < radius * radius) {
					setTerrainType(cx, cy, type);
				}
			}
		}
	}

	public float getMinedLevel(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return 0;

		return this.minedLevel[x][y];
	}

	public TerrainType getTerrainType(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return null;

		return terrain[x][y];
	}

	public boolean isExtraImpassable(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return false;

		return extraImpassibleSquares[x][y];
	}

	public boolean isInLOS(Vector2 start, Vector2 end) {
		return isInLOS(start, end, true);
	}

	public boolean isInLOS(Vector2 start, Vector2 end, final boolean allowBuildings) {
		isInLOS = true;

		UMI.gs.world.rayCast(new RayCastCallback() {

			@Override
			public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
				if (((fixture.getFilterData().categoryBits & 2) > 0 && allowBuildings) || ((fixture.getFilterData().categoryBits == 2 && !allowBuildings))) {
					isInLOS = false;
					return 0;
				} else {
					return 1;
				}
			}
		}, start, end);

		return isInLOS;
	}

	public boolean isInLOS(Vector2 start, Vector2 end, float radius) {
		return isInLOS(start, end, radius, true);
	}

	public boolean isInLOS(Vector2 start, Vector2 end, float radius, boolean allowBuildings) {

		if (!isInLOS(start, end))
			return false;

		for (float f = 0; f < MathUtils.PI2; f += MathUtils.PI / 2) {
			Vector2 delta = new Vector2(MathUtils.cos(f) * radius, MathUtils.sin(f) * radius);
			if (!isInLOS(start.cpy().add(delta), end.cpy().add(delta), allowBuildings)) {
				return false;
			}
		}

		return true;
	}

	public boolean isMined(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return false;

		return mined[x][y];
	}

	public boolean isPassable(int x, int y) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return false;

		if (extraImpassibleSquares[x][y] == true)
			return false;

		return mined[x][y];
	}

	public boolean isPassable(Position p) {
		return isPassable(p.x, p.y);
	}

	public void mine(int x, int y) {

		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return;

		float rate = 1 / 60f / .75f;

		if (terrain[x][y] == TerrainType.ORE)
			rate = 1 / 60f / 25f;
		if (terrain[x][y] == TerrainType.STONE)
			rate = 1 / 60f / 4f;

		this.minedLevel[x][y] += rate;

		if (this.minedLevel[x][y] > 1) {
			setMined(x, y, true);
		}
	}

	public void render() {
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				String sprite = null;

				if (terrain[x][y] == TerrainType.DIRT) {
					if (mined[x][y]) {
						sprite = "dirt_dug";
					} else {
						sprite = "dirt1";
					}
				} else if (terrain[x][y] == TerrainType.ORE) {
					if (mined[x][y]) {
						sprite = "ore_mined";
					} else {
						sprite = "ore";
					}
				}

				UMI.batch.draw(UMI.a.getSprite(sprite), x - .5f, y - .5f, .5f, .5f, 1, 1, 1, 1, 0);
			}
		}
	}

	public void setExtraImpassibleSquare(int x, int y, boolean value) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return;

		this.extraImpassibleSquares[x][y] = value;
	}

	public void setMined(int x, int y, boolean mined) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return;

		if (this.mined[x][y] == false && mined == true) {
			if (fixtures[x][y] != null) {
				body.destroyFixture(fixtures[x][y]);
				fixtures[x][y] = null;
				System.out.println("Fixture at " + x + " " + y + " destroyed");
			}
		}

		this.mined[x][y] = mined;
	}

	public void setTerrainType(int x, int y, TerrainType type) {
		if (x < 0 || y < 0 || x >= WIDTH || y >= HEIGHT)
			return;

		terrain[x][y] = type;
	}
}
