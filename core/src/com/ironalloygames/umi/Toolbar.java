package com.ironalloygames.umi;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ironalloygames.umi.entity.Buzzsaw;
import com.ironalloygames.umi.entity.Constructor;
import com.ironalloygames.umi.entity.Driller;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.Fabricator;
import com.ironalloygames.umi.entity.MiningPlan;
import com.ironalloygames.umi.entity.Peltast;
import com.ironalloygames.umi.entity.Porter;
import com.ironalloygames.umi.entity.Refinery;
import com.ironalloygames.umi.entity.Sentinel;
import com.ironalloygames.umi.entity.Unit;
import com.ironalloygames.umi.entity.Unit.Faction;
import com.ironalloygames.umi.job.Construct;
import com.ironalloygames.umi.job.Mine;

public class Toolbar {

	class BuzzsawPlanPlacer extends PlanPlacer {

		public BuzzsawPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Buzzsaw().getCost();
		}

		@Override
		public String getDescription() {
			return "HKB-3 \"Buzzsaw\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"A weaponized driller, the buzzsaw is decently fast and its mining laser can do tremendous damage close in.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Buzzsaw;
		}

		@Override
		void setBrush() {
			brush = new Buzzsaw();
		}
	}

	class ConstructorPlanPlacer extends PlanPlacer {

		public ConstructorPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Constructor().getCost();
		}

		@Override
		public String getDescription() {
			return "FAB-27 \"Constructor\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Mobile builder, capable of building any plan. Slower than the fabricator.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Constructor;
		}

		@Override
		void setBrush() {
			brush = new Constructor();
		}

	}

	class DrillerPlanPlacer extends PlanPlacer {

		public DrillerPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Driller().getCost();
		}

		@Override
		public String getDescription() {
			return "MMB-2 \"Driller\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Classic mining bot. Place drilling plans to have it drill through walls and collect ore.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Driller;
		}

		@Override
		void setBrush() {
			brush = new Driller();
		}

	}

	class FabricatorPlanPlacer extends PlanPlacer {

		public FabricatorPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Fabricator().getCost();
		}

		@Override
		public String getDescription() {
			return "SFB-8 \"Fabricator\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"The fabricator is an immobile factory. Place plans next to it to have it build them rapidly.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Fabricator;
		}

		@Override
		void setBrush() {
			brush = new Fabricator();
		}

	}

	class MiningPlanPlacer extends PlanPlacer {

		public MiningPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		public String getDescription() {
			return "Place this mining plan on walls by left clicking. Drillers will automatically begin to dig into them. Try to find ore to send to the refineries.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof MiningPlan;
		}

		@Override
		void setBrush() {
			brush = new MiningPlan();
			((MiningPlan) brush).creatingFaction = Faction.PLAYER;
		}

	}

	class PeltastPlanPlacer extends PlanPlacer {

		public PeltastPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Peltast().getCost();
		}

		@Override
		public String getDescription() {
			return "LKB-54 \"Peltast\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Fast attack bot, able to hit quickly and retreat. Low damage and armor, however.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Peltast;
		}

		@Override
		void setBrush() {
			brush = new Peltast();
		}

	}

	abstract class PlanPlacer extends ToolbarItem
	{
		public PlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		Color getColor() {
			return isSelected() ? Color.RED : Color.GRAY;
		}

		@Override
		void hasBeenClicked() {
			super.hasBeenClicked();

			setBrush();
			if (brush instanceof Unit) {
				((Unit) brush).setConstructed(0);
			}
		}

		abstract boolean isSelected();

		abstract void setBrush();

	}

	class PorterPlanPlacer extends PlanPlacer {

		public PorterPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Porter().getCost();
		}

		@Override
		public String getDescription() {
			return "UB-1 \"Porter\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Small bot that automatically carries ore from wherever it is mined to the refineries.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Porter;
		}

		@Override
		void setBrush() {
			brush = new Porter();
		}

	}

	class RefineryPlanPlacer extends PlanPlacer {

		public RefineryPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Refinery().getCost();
		}

		@Override
		public String getDescription() {
			return "FRF-2 \"Refinery\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Fragile building with the ability to turn ore brought to it into credits.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Refinery;
		}

		@Override
		void setBrush() {
			brush = new Refinery();
		}
	}

	class SentinelPlanPlacer extends PlanPlacer {

		public SentinelPlanPlacer(Vector2 pos, String graphic) {
			super(pos, graphic);
		}

		@Override
		float getCost() {
			return new Sentinel().getCost();
		}

		@Override
		public String getDescription() {
			return "DKB-217 \"Sentinel\"\n" +
					"Cost: " + (int) getCost() + " Credits\n" +
					"\n" +
					"Sophisticated long range bot with a rapidly regenerating shield. Slow movement speed.";
		}

		@Override
		boolean isSelected() {
			return brush instanceof Sentinel;
		}

		@Override
		void setBrush() {
			brush = new Sentinel();
		}

	}

	class ToolbarItem {
		String graphic;
		Vector2 pos;

		public ToolbarItem(Vector2 pos, String graphic) {
			this.pos = pos.cpy();
			this.graphic = graphic;
		}

		boolean clicked(Vector2 mpos) {
			if (isClicked(mpos)) {
				hasBeenClicked();
				return true;
			}
			return false;
		}

		Color getColor() {
			return Color.WHITE;
		}

		float getCost() {
			return 0;
		}

		public String getDescription() {
			return "NONE";
		}

		void hasBeenClicked() {

		}

		boolean isClicked(Vector2 mpos) {
			Rectangle mr = new Rectangle(pos.x - 56 / 2, pos.y - 56 / 2, 56, 56);

			return mr.contains(mpos);
		}

		void render() {
			UMI.a.np.setColor(getColor());
			UMI.a.np.draw(sb, pos.x - 56 / 2, pos.y - 56 / 2, 56, 56);
			sb.draw(UMI.a.getSprite(graphic), pos.x, pos.y, .5f, .5f, 1, 1, 48, 48, 0);
		}
	}

	ToolbarItem activeToolbarItem;

	Entity brush;

	OrthographicCamera cam = new OrthographicCamera(1024, 768);
	boolean clearBrushOnMouseRelease = false;

	ArrayList<ToolbarItem> items = new ArrayList<ToolbarItem>();

	Vector2 lastBrushPlacement = null;;

	int lastPlacement = 0;

	SpriteBatch sb = new SpriteBatch();

	Vector2 tooltipPos = null;

	String tooltipText;

	boolean wallClip = false;

	public Toolbar(Stage uiStage) {

		items.add(new MiningPlanPlacer(getNextPos(), "toolbar_mine"));
		items.add(new DrillerPlanPlacer(getNextPos(), "miner"));
		items.add(new SentinelPlanPlacer(getNextPos(), "sentinel"));
		items.add(new BuzzsawPlanPlacer(getNextPos(), "buzzsaw"));
		items.add(new PeltastPlanPlacer(getNextPos(), "peltast"));
		items.add(new PorterPlanPlacer(getNextPos(), "porter"));
		items.add(new ConstructorPlanPlacer(getNextPos(), "constructor"));
		items.add(new RefineryPlanPlacer(getNextPos(), "refinery"));
		items.add(new FabricatorPlanPlacer(getNextPos(), "fabricator"));
	}

	public boolean clicked(Vector2 mpos, int button) {

		if (button == Buttons.RIGHT && brush != null) {
			brush = null;
			return true;
		}

		if (button != Buttons.LEFT) {
			return false;
		}

		Vector3 v3 = new Vector3(mpos.x, mpos.y, 0);
		v3 = cam.unproject(v3);
		mpos = new Vector2(v3.x, v3.y);

		for (ToolbarItem ti : items) {
			if (ti.clicked(mpos)) {
				activeToolbarItem = ti;
				lastBrushPlacement = null;
				return true;

			}
		}

		// clicked somewhere and therei s abtrush
		if (brush != null && lastPlacement + 2 < UMI.gs.tick && UMI.gs.playerCredits >= activeToolbarItem.getCost()) {

			wallClip = false;

			Rectangle bbr = brush.getBoundingRectangle();

			UMI.gs.world.QueryAABB(new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() != null && fixture.getUserData() == brush)
						return true;

					// we hit another plan...
					if ((fixture.getFilterData().categoryBits & 8) > 0) {
						wallClip = true;
						System.out.println("HIT " + fixture.getUserData());
						return false;
					}

					// we hit a wall
					if (!brush.canBeBuiltInWalls() && ((fixture.getFilterData().categoryBits & 2) > 0)) {
						wallClip = true;
						return false;
					}

					return true;
				}
			}, bbr.x + .2f,
					bbr.y + .2f,
					bbr.x + bbr.width - .2f,
					bbr.y + bbr.height - .2f);

			if (!wallClip) {
				if (!(brush instanceof MiningPlan) || !UMI.gs.terrain.isPassable((int) brush.getPosition().x, (int) brush.getPosition().y)) {

					lastPlacement = UMI.gs.tick;
					clearBrushOnMouseRelease = true;
					lastBrushPlacement = UMI.gs.igis.mouseWorldPosition.cpy();
					UMI.gs.entityAddQueue.add(brush);

					UMI.gs.playerCredits -= activeToolbarItem.getCost();

					if (brush instanceof MiningPlan) {
						for (Unit u : UMI.gs.igis.selected) {
							u.order(new Mine(u, (MiningPlan) brush));
							System.out.println(u);
						}
					} else {
						for (Unit u : UMI.gs.igis.selected) {
							if (u.getConstructionRate() > 0) {
								u.order(new Construct(u, (Unit) brush));
								System.out.println("ORDERED TO CONSTRUCT");
							}
						}
					}

					brush = null;
					activeToolbarItem.hasBeenClicked();
					if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {

						clearBrushOnMouseRelease = false;
					}

				}
			}
		}

		return false;
	}

	private Vector2 getNextPos() {
		return new Vector2(350 + (items.size() % 2 == 0 ? 64 : 0), 200 - (items.size() / 2 * 64));
	}

	public void mouseUp() {
		if (clearBrushOnMouseRelease) {
			brush = null;
			clearBrushOnMouseRelease = false;
		}

	}

	public void moved(Vector2 mpos) {
		// System.out.println(mpos);
		Vector3 v3 = new Vector3(mpos.x, mpos.y, 0);
		v3 = cam.unproject(v3);
		mpos = new Vector2(v3.x, v3.y);
		tooltipPos = null;
		for (ToolbarItem ti : items) {
			if (ti.isClicked(mpos)) {
				tooltipPos = mpos;
				tooltipText = ti.getDescription();
			}
		}
	}

	public void render() {
		sb.setProjectionMatrix(cam.combined);
		sb.begin();

		for (ToolbarItem ti : items) {
			ti.render();
		}

		if (tooltipPos != null) {
			TextBounds tb = UMI.a.getFont("small").getWrappedBounds(tooltipText, 200);

			UMI.a.np.draw(sb, tooltipPos.x - 100, tooltipPos.y - tb.height - 20, tb.width + 20, tb.height + 20);

			UMI.a.getFont("small").drawWrapped(sb, tooltipText, tooltipPos.x + 10 - 100, tooltipPos.y - 10, 200);
		}

		sb.end();

		if (brush != null) {
			UMI.batch.begin();
			brush.setPosition(UMI.gs.igis.mouseWorldPosition);
			brush.render();
			UMI.batch.end();
		}

	}
}
