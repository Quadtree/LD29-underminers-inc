package com.ironalloygames.umi.job;

import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.Unit;

public class Attack extends Job {

	Entity plan;

	Move subMove;

	public Attack(Unit u, Entity plan) {
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
		return plan.keep();
	}

	@Override
	public void update() {
		if (subMove.getRangeToDest() > u.getCombatRange() || !UMI.gs.terrain.isInLOS(u.getPosition(), plan.getPosition(), .25f, false)) {
			subMove.update();
		} else {
			if (u.getPosition().dst(plan.getPosition()) <= u.getCombatRange()) {
				u.attack(plan);
			} else {
				subMove = new Move(u, plan.getPosition());
			}
		}
	}

}
