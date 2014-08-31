package com.ironalloygames.umi.job;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Unit;

public class Construct extends Job {
	boolean isMining = false;

	Unit plan;

	Move subMove;

	public Construct(Unit u, Unit plan) {
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
		return plan.keep() && !plan.isFullyConstructed();
	}

	@Override
	public void render() {
		super.render();

		if (isMining) {
			Vector2 center = u.getPosition().cpy().add(plan.getPosition()).scl(.5f);
			Vector2 delta = plan.getPosition().cpy().sub(u.getPosition());

			UMI.batch.draw(UMI.a.getSprite("construction_beam"), center.x - .5f, center.y - .5f, .5f, .5f, 1, 1, delta.len(), .4f, delta.angle());
		}
	}

	@Override
	public void update() {
		if (subMove.getRangeToDest() > Unit.CONSTRUCTION_BEAM_RANGE) {
			subMove.update();
			isMining = false;
		} else {
			isMining = true;

			Vector2 delta = plan.getPosition().cpy().sub(u.getPosition());

			u.setRotation(delta.angle());

			plan.setConstructed(plan.getConstructed() + u.getConstructionRate() / 60f / plan.getConstructionTime());
		}
	}
}
