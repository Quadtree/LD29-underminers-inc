package com.ironalloygames.umi.entity;

import com.ironalloygames.umi.UMI;

public class Peltast extends Unit {

	int shotCooldown = 0;

	@Override
	public void attack(Entity target) {
		super.attack(target);

		if (shotCooldown <= 0) {
			shotCooldown = 25;

			Bolt b = new Bolt(getPosition(), target.getPosition(), 6, getCombatRange(), .15f, "peltast_bolt", faction);
			UMI.gs.entityAddQueue.add(b);
			UMI.a.getSound("peltast_fire").play();
		}
	}

	@Override
	public float getCombatRange() {
		return 4.5f;
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 8;
	}

	@Override
	public float getCost() {
		return 90;
	}

	@Override
	public float getEnginePower() {
		return super.getEnginePower() * 2f;
	}

	@Override
	protected String getGraphic() {
		return "peltast";
	}

	@Override
	public float getMaxHealth() {
		return 2.5f;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.PELTAST;
	}

	@Override
	public boolean isCombatType() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		shotCooldown--;
	}
}
