package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Position;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Unit.Faction;

public class MiningPlan extends Entity {

	public Faction creatingFaction;

	@Override
	public boolean canBeBuiltInWalls() {
		return true;
	}

	@Override
	protected short getCategoryBits() {
		return 8;
	}

	@Override
	protected String getGraphic() {
		return "mine_icon";
	}

	@Override
	public float getHeight() {
		return 1;
	}

	@Override
	protected short getMaskBits() {
		return 0;
	}

	@Override
	protected float getWidth() {
		return 1;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean isControllableByPlayer() {
		return creatingFaction == Faction.PLAYER;
	}

	@Override
	public boolean keep() {
		Position p = new Position(getPosition());
		return super.keep() && !UMI.gs.terrain.isMined(p.x, p.y);
	}

	@Override
	public void render() {
		if (creatingFaction == Faction.PLAYER)
			super.render();
	}

	@Override
	public void setPosition(Vector2 position) {
		this.position = new Position(position).vec2();

	}

	@Override
	public String toString() {
		return "MP " + this.getPosition().toString();
	}

}
