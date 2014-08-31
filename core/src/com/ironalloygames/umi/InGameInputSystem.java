package com.ironalloygames.umi;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ironalloygames.umi.Terrain.TerrainType;
import com.ironalloygames.umi.entity.Driller;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.MiningPlan;
import com.ironalloygames.umi.entity.Unit;
import com.ironalloygames.umi.entity.Unit.Faction;
import com.ironalloygames.umi.job.Attack;
import com.ironalloygames.umi.job.Construct;
import com.ironalloygames.umi.job.Mine;
import com.ironalloygames.umi.job.Move;

public class InGameInputSystem implements InputProcessor {

	private static final float CAM_MOVE_SPEED = .5f;

	public boolean defeatScreenUp = false;

	boolean deleting = false;

	boolean dragInProgress = false;

	public boolean helpScreenUp = true;
	InputMultiplexer im = new InputMultiplexer();

	boolean isLeftButtonDown = false;

	int lastDragScreenX, lastDragScreenY;

	Vector2 mouseDownWorldPosition;

	Vector2 mouseWorldPosition;

	boolean movingDown = false;

	boolean movingLeft = false;

	boolean movingRight = false;

	boolean movingUp = false;

	int screenX, screenY;

	HashSet<Unit> selected = new HashSet<Unit>();

	ShapeRenderer sr = new ShapeRenderer();

	public boolean titleScreenUp = true;

	Toolbar toolbar;

	SpriteBatch uiBatch = new SpriteBatch();
	OrthographicCamera uiCamera = new OrthographicCamera(1024, 768);
	Stage uiStage;
	public boolean victoryScreenUp = false;

	public InGameInputSystem() {
		Gdx.input.setInputProcessor(im);
		im.addProcessor(this);
		mouseWorldPosition = new Vector2();

		uiStage = new Stage();

		toolbar = new Toolbar(uiStage);
	}

	private Rectangle getSelectionRectangle() {
		Vector2 corner = mouseDownWorldPosition.cpy();
		Vector2 size = mouseWorldPosition.cpy().sub(mouseDownWorldPosition);

		if (size.x < 0) {
			corner.x += size.x;
			size.x = Math.abs(size.x);
		}

		if (size.y < 0) {
			corner.y += size.y;
			size.y = Math.abs(size.y);
		}

		Rectangle r = new Rectangle(corner.x, corner.y, size.x, size.y);
		return r;
	}

	public boolean isSelectedByPlayer(Entity u) {
		return selected.contains(u);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (victoryScreenUp || defeatScreenUp)
			return true;

		if (titleScreenUp) {
			titleScreenUp = false;
			return true;
		} else if (helpScreenUp) {
			helpScreenUp = false;
			return true;
		}

		if (keycode == Keys.H) {
			helpScreenUp = true;
		}

		if (keycode == Keys.BACKSPACE || keycode == Keys.DEL) {
			deleting = true;
		}
		if (keycode == Keys.LEFT) {
			movingLeft = true;
		}
		if (keycode == Keys.RIGHT) {
			movingRight = true;
		}
		if (keycode == Keys.UP) {
			movingUp = true;
		}
		if (keycode == Keys.DOWN) {
			movingDown = true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {

		if (victoryScreenUp || defeatScreenUp)
			return true;

		if (keycode == Keys.BACKSPACE || keycode == Keys.DEL) {
			deleting = false;
		}
		if (keycode == Keys.LEFT) {
			movingLeft = false;
		}
		if (keycode == Keys.RIGHT) {
			movingRight = false;
		}
		if (keycode == Keys.UP) {
			movingUp = false;
		}
		if (keycode == Keys.DOWN) {
			movingDown = false;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (victoryScreenUp || defeatScreenUp)
			return true;

		this.screenX = screenX;
		this.screenY = screenY;

		toolbar.moved(new Vector2(screenX, screenY));

		return false;
	}

	public void render() {
		uiBatch.setProjectionMatrix(uiCamera.combined);

		if (victoryScreenUp) {
			uiBatch.begin();
			uiBatch.draw(UMI.a.getTexture("victoryscreen"), -512, -384);
			uiBatch.end();
			return;
		}
		if (defeatScreenUp) {
			uiBatch.begin();
			uiBatch.draw(UMI.a.getTexture("defeatscreen"), -512, -384);
			uiBatch.end();
			return;
		}
		if (titleScreenUp) {
			uiBatch.begin();
			uiBatch.draw(UMI.a.getTexture("title"), -512, -384);
			UMI.a.getFont("small").draw(uiBatch, "Made by Quadtree for Ludum Dare 29", -490, -360);
			uiBatch.end();
			return;
		}
		if (helpScreenUp) {
			uiBatch.begin();
			uiBatch.draw(UMI.a.getTexture("helpscreen"), -512, -384);
			uiBatch.end();
			return;
		}

		uiStage.draw();
		toolbar.render();

		if (mouseDownWorldPosition != null && toolbar.brush == null) {
			Rectangle r = getSelectionRectangle();

			sr.setColor(Color.WHITE);
			sr.setProjectionMatrix(UMI.gs.worldCam.combined);
			sr.begin(ShapeType.Line);
			sr.rect(r.x, r.y, r.width, r.height);
			sr.end();
		}

		if (UMI.a.getFont("standard") != null) {
			uiBatch.setProjectionMatrix(uiCamera.combined);

			uiBatch.begin();
			UMI.a.np.setColor(Color.WHITE);
			UMI.a.np.draw(uiBatch, -510, 330, 230, 40);
			UMI.a.getFont("standard").draw(uiBatch, "Credits: " + (int) UMI.gs.playerCredits, -500, 360);

			uiBatch.end();
		}

		int minimapX = 365;
		int minimapY = -340;

		uiBatch.setProjectionMatrix(uiCamera.combined);

		uiBatch.begin();
		UMI.a.np.draw(uiBatch, minimapX - 5, minimapY - 5, Terrain.WIDTH * 2 + 10, Terrain.HEIGHT * 2 + 10);
		uiBatch.end();

		sr.setProjectionMatrix(uiCamera.combined);
		sr.begin(ShapeType.Filled);
		for (int x = 0; x < Terrain.WIDTH; x++) {
			for (int y = 0; y < Terrain.WIDTH; y++) {
				if (UMI.gs.terrain.isMined(x, y)) {
					sr.setColor(Color.BLACK);
				} else {
					if (UMI.gs.terrain.getTerrainType(x, y) == TerrainType.DIRT) {
						sr.setColor(new Color(.5f, .25f, 0, 1));
					} else {
						sr.setColor(Color.GRAY);
					}
				}
				sr.rect(minimapX + x * 2, minimapY + y * 2, 2, 2);
			}
		}

		for (Entity e : UMI.gs.entities) {
			if (e instanceof Unit) {
				Unit u = (Unit) e;

				if (u.getFaction() == Faction.PLAYER)
					sr.setColor(Color.BLUE);
				else
					sr.setColor(Color.RED);

				sr.rect(minimapX + u.getPosition().x * 2, minimapY + u.getPosition().y * 2, 1, 1);
			}
		}

		sr.end();
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if (victoryScreenUp || defeatScreenUp)
			return true;

		if (titleScreenUp) {
			titleScreenUp = false;
			return true;
		} else if (helpScreenUp) {
			helpScreenUp = false;
			return true;
		}

		if (button == Buttons.LEFT) {
			isLeftButtonDown = true;
		}

		if (toolbar.clicked(new Vector2(screenX, screenY), button)) {
			return true;
		}

		if (button == Buttons.MIDDLE) {
			dragInProgress = true;
			lastDragScreenX = screenX;
			lastDragScreenY = screenY;
		}
		if (button == Buttons.LEFT) {
			mouseDownWorldPosition = mouseWorldPosition.cpy();
		}
		if (button == Buttons.RIGHT) {
			Position p = new Position(mouseWorldPosition);

			for (Entity e : UMI.gs.getEntitiesPickedInRectangle(new Rectangle(mouseWorldPosition.x - .25f, mouseWorldPosition.y - .25f, .5f, .5f))) {
				if (e instanceof MiningPlan) {
					boolean atLeastOneOrdered = false;

					for (Unit u : selected) {
						if (u instanceof Driller) {
							if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
								u.clearJobQueue();
							}
							u.order(new Mine(u, (MiningPlan) e));
							atLeastOneOrdered = true;
						}
					}

					if (atLeastOneOrdered)
						return true;
				}
				if (e instanceof Unit && ((Unit) e).getFaction() == Faction.AI && ((Unit) e).isCollidable()) {
					boolean atLeastOneOrdered = false;

					for (Unit u : selected) {
						if (u.isCombatType()) {
							if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
								u.clearJobQueue();
							}
							u.order(new Attack(u, e));
							atLeastOneOrdered = true;
						}
					}

					if (atLeastOneOrdered)
						return true;
				}
				if (e instanceof Unit && ((Unit) e).getFaction() == Faction.PLAYER && !((Unit) e).isFullyConstructed()) {
					boolean atLeastOneOrdered = false;

					for (Unit u : selected) {
						if (u.getConstructionRate() > 0) {
							if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
								u.clearJobQueue();
							}
							u.order(new Construct(u, (Unit) e));
							atLeastOneOrdered = true;
						}
					}

					if (atLeastOneOrdered)
						return true;
				}
			}

			if (UMI.gs.terrain.isPassable(p)) {
				for (Unit u : selected) {
					if (!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
						u.clearJobQueue();
					}
					u.order(new Move(u, mouseWorldPosition));
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		this.screenX = screenX;
		this.screenY = screenY;

		if (isLeftButtonDown) {
			if (toolbar.clicked(new Vector2(screenX, screenY), Buttons.LEFT)) {
				return true;
			}
		}

		if (dragInProgress) {
			Vector3 v31 = UMI.gs.worldCam.unproject(new Vector3(this.lastDragScreenX, this.lastDragScreenY, 0)).cpy();
			Vector3 v32 = UMI.gs.worldCam.unproject(new Vector3(screenX, screenY, 0)).cpy();

			UMI.gs.worldCam.position.x -= v32.x - v31.x;
			UMI.gs.worldCam.position.y -= v32.y - v31.y;

			UMI.gs.worldCam.position.x = MathUtils.clamp(UMI.gs.worldCam.position.x, GameState.SCREEN_TILE_WIDTH / 2 - .5f, Terrain.WIDTH - GameState.SCREEN_TILE_WIDTH / 2 - .5f);
			UMI.gs.worldCam.position.y = MathUtils.clamp(UMI.gs.worldCam.position.y, GameState.SCREEN_TILE_HEIGHT / 2 - .5f, Terrain.WIDTH - GameState.SCREEN_TILE_HEIGHT / 2 - .5f);

			UMI.gs.worldCam.update();

			// System.out.println(this.lastDragScreenX + " " +
			// this.lastDragScreenY);
			// System.out.println(screenX + " " + screenY);

			// System.out.println(v31);
			// System.out.println(v32);

			lastDragScreenX = screenX;
			lastDragScreenY = screenY;
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (victoryScreenUp || defeatScreenUp)
			return true;

		if (button == Buttons.LEFT) {
			isLeftButtonDown = false;
		}
		if (button == Buttons.MIDDLE) {
			dragInProgress = false;
		}
		if (button == Buttons.LEFT && mouseDownWorldPosition != null && toolbar.brush == null) {
			Rectangle r = getSelectionRectangle();

			System.out.println(r);

			selected.clear();
			for (Entity e : UMI.gs.getEntitiesPickedInRectangle(r)) {
				if (e instanceof Unit && e.isControllableByPlayer()) {
					selected.add((Unit) e);
				}
			}
			System.out.println(selected);
		}

		toolbar.mouseUp();

		mouseDownWorldPosition = null;

		return false;
	}

	public void update() {
		Vector3 v3 = UMI.gs.worldCam.unproject(new Vector3(this.screenX, this.screenY, 0));
		mouseWorldPosition.x = v3.x;
		mouseWorldPosition.y = v3.y;
		uiStage.act(0.016f);

		if (movingLeft)
			UMI.gs.worldCam.position.x -= CAM_MOVE_SPEED;
		if (movingRight)
			UMI.gs.worldCam.position.x += CAM_MOVE_SPEED;
		if (movingDown)
			UMI.gs.worldCam.position.y -= CAM_MOVE_SPEED;
		if (movingUp)
			UMI.gs.worldCam.position.y += CAM_MOVE_SPEED;

		UMI.gs.worldCam.position.x = MathUtils.clamp(UMI.gs.worldCam.position.x, GameState.SCREEN_TILE_WIDTH / 2 - .5f, Terrain.WIDTH - GameState.SCREEN_TILE_WIDTH / 2 - .5f);
		UMI.gs.worldCam.position.y = MathUtils.clamp(UMI.gs.worldCam.position.y, GameState.SCREEN_TILE_HEIGHT / 2 - .5f, Terrain.WIDTH - GameState.SCREEN_TILE_HEIGHT / 2 - .5f);

		UMI.gs.worldCam.update();

		if (deleting) {
			for (Entity e : UMI.gs.getEntitiesPickedInRectangle(new Rectangle(mouseWorldPosition.x - .01f, mouseWorldPosition.y - .01f, .02f, .02f))) {
				if (e.isControllableByPlayer()) {
					e.selfDestruct = true;
				}
			}
		}
	}

}
