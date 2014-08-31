package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.GameState;
import com.ironalloygames.umi.entity.Unit.Faction;

public class Bolt extends Entity {
	float damage;
	Faction firingFaction;
	String graphic;
	int life;
	Vector2 source;
	float speed;
	Vector2 target;

	public Bolt(Vector2 source, Vector2 target, float speed, float maxRange, float damage, String graphic, Faction firingFaction) {
		this.source = source.cpy();
		this.target = target.cpy();
		this.life = (int) (maxRange / speed * 60);
		this.damage = damage;
		this.graphic = graphic;
		this.firingFaction = firingFaction;
		this.speed = speed;
	}

	@Override
	public void created() {
		super.created();

		Vector2 delta = target.cpy().sub(source);
		delta.nor();

		body.setTransform(source, delta.angle());
		body.setLinearVelocity(delta.cpy().scl(speed));
		body.setGravityScale(0);
	}

	@Override
	protected short getCategoryBits() {
		if (firingFaction == Faction.PLAYER)
			return GameState.COL_BIT_PLAYER_SHOT;
		else
			return GameState.COL_BIT_AI_SHOT;
	}

	@Override
	protected String getGraphic() {
		return graphic;
	}

	@Override
	public float getHeight() {
		return .5f;
	}

	@Override
	protected short getMaskBits() {
		if (firingFaction == Faction.PLAYER)
			return (short) (GameState.COL_BIT_AI_UNIT | GameState.COL_BIT_WALL);
		else
			return (short) (GameState.COL_BIT_PLAYER_UNIT | GameState.COL_BIT_WALL);
	}

	@Override
	protected float getWidth() {
		return .5f;
	}

	@Override
	public boolean keep() {
		return super.keep() && life > 0;
	}

	@Override
	public void onCollision(Entity other) {
		super.onCollision(other);
		if (other != null)
			other.takeDamage(damage);

		System.out.println(this + " hit " + other);

		life = 0;
	}

	@Override
	public void update() {
		super.update();

		life--;
	}

}
