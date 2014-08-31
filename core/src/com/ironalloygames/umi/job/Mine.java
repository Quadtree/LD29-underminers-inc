package com.ironalloygames.umi.job;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Position;
import com.ironalloygames.umi.Terrain.TerrainType;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.MiningPlan;
import com.ironalloygames.umi.entity.Ore;
import com.ironalloygames.umi.entity.Unit;

public class Mine extends Job {

	private static final int ORE_PER_BLOCK = 8;

	boolean isMining = false;

	MiningPlan plan;

	Move subMove;

	public Mine(Unit u, MiningPlan plan) {
		super(u);
		this.plan = plan;

		subMove = new Move(u, plan.getPosition());
	}

	@Override
	public int getPriority() {
		return super.getPriority() + (int) (plan.getPosition().dst(u.getPosition()) * 100);
	}

	@Override
	public boolean keep() {
		return plan.keep() && u.getMiningRate() > 0;
	}

	@Override
	public void render() {
		super.render();

		if (isMining) {
			Vector2 center = u.getPosition().cpy().add(plan.getPosition()).scl(.5f);
			Vector2 delta = plan.getPosition().cpy().sub(u.getPosition());

			UMI.batch.draw(UMI.a.getSprite("mining_laser"), center.x - .5f, center.y - .5f, .5f, .5f, 1, 1, delta.len(), .4f, delta.angle());
		}
	}

	@Override
	public void update() {

		if (subMove.getRangeToDest() > 1.5f) {
			subMove.update();
			isMining = false;
		} else {
			if (u.getPosition().dst(plan.getPosition()) <= 1.5f) {
				isMining = true;

				Vector2 delta = plan.getPosition().cpy().sub(u.getPosition());

				u.setRotation(delta.angle());

				Position p = new Position(plan.getPosition());

				int startLevel = (int) (UMI.gs.terrain.getMinedLevel(p.x, p.y) * ORE_PER_BLOCK);

				UMI.gs.terrain.mine(p.x, p.y);

				int endLevel = (int) (UMI.gs.terrain.getMinedLevel(p.x, p.y) * ORE_PER_BLOCK);

				if (UMI.gs.terrain.getTerrainType(p.x, p.y) == TerrainType.ORE && startLevel != endLevel) {
					Vector2 center = u.getPosition().cpy().add(plan.getPosition()).scl(.5f);

					Ore o = new Ore();
					o.setMinedBy(u.getFaction());
					o.setPosition(center);
					UMI.gs.entityAddQueue.add(o);
				}
			} else {
				subMove = new Move(u, plan.getPosition());
			}
		}
	}

}
