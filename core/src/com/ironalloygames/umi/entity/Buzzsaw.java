package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.UMI;

public class Buzzsaw extends Unit {

	Vector2 currentLaserTarget = null;

	@Override
	public void attack(Entity target) {
		super.attack(target);

		currentLaserTarget = target.getPosition();
		Vector2 delta = currentLaserTarget.cpy().sub(getPosition());
		body.setTransform(getPosition(), delta.angle());

		target.takeDamage(1.5f / 60);
	}

	@Override
	public float getCombatRange() {
		return 2;
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 10;
	}

	@Override
	public float getCost() {
		return 100;
	}

	@Override
	public float getEnginePower() {
		return super.getEnginePower() * 1.04f;
	}

	@Override
	protected String getGraphic() {
		return "buzzsaw";
	}

	@Override
	public float getMaxHealth() {
		return 5f;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.BUZZSAW;
	}

	@Override
	public boolean isCombatType() {
		return true;
	}

	@Override
	public void render() {
		super.render();

		if (currentLaserTarget != null) {
			Vector2 center = getPosition().cpy().add(currentLaserTarget).scl(.5f);
			Vector2 delta = currentLaserTarget.cpy().sub(getPosition());

			UMI.batch.draw(UMI.a.getSprite("mining_laser"), center.x - .5f, center.y - .5f, .5f, .5f, 1, 1, delta.len(), .6f, delta.angle());
		}

		currentLaserTarget = null;
	}
}
