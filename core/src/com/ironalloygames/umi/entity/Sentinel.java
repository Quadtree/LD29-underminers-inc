package com.ironalloygames.umi.entity;

import com.badlogic.gdx.graphics.Color;
import com.ironalloygames.umi.UMI;

public class Sentinel extends Unit {

	int shotCooldown = 0;

	@Override
	public void attack(Entity target) {
		super.attack(target);

		if (shotCooldown <= 0) {
			shotCooldown = 150;

			Bolt b = new Bolt(getPosition(), target.getPosition(), 24, getCombatRange(), 1.8f, "sentinel_bolt", faction);
			UMI.gs.entityAddQueue.add(b);
			UMI.a.getSound("sentinel_fire").play();
		}
	}

	@Override
	public float getCombatRange() {
		return 9;
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 12;
	}

	@Override
	public float getCost() {
		return 110;
	}

	@Override
	public float getEnginePower() {
		return super.getEnginePower() * .75f;
	}

	@Override
	protected String getGraphic() {
		return "sentinel";
	}

	@Override
	public float getMaxHealth() {
		return 2.5f;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.SENTINEL;
	}

	@Override
	public boolean isCombatType() {
		return true;
	}

	@Override
	public void render() {
		super.render();

		UMI.batch.setColor(1, 1, 1, health / getMaxHealth());
		UMI.batch.draw(UMI.a.getSprite("sentinel_shield"), getPosition().x - .5f, getPosition().y - .5f, .5f, .5f, 1, 1, getWidth(), getHeight(), getRotation());
		UMI.batch.setColor(Color.WHITE);
	}

	@Override
	public void update() {
		super.update();

		shotCooldown--;

		health = Math.min(getMaxHealth(), health + .9f / 150);
	}
}
