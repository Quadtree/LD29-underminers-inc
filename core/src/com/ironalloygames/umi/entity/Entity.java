package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Shape;
import com.ironalloygames.umi.UMI;

public class Entity {
	Body body;
	public Joint carryJoint;
	Fixture fixture;

	Vector2 position = new Vector2();

	public boolean selfDestruct = false;

	public boolean canBeBuiltInWalls() {
		return false;
	}

	public void created() {
		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.DynamicBody;
		bd.position.set(position);
		bd.fixedRotation = true;

		body = UMI.gs.world.createBody(bd);
		body.setUserData(this);

		FixtureDef fd = new FixtureDef();
		fd.density = 1;
		fd.shape = this.getShape();

		fixture = body.createFixture(fd);
		fixture.setUserData(this);
		fixture.getFilterData().categoryBits = getCategoryBits();
		fixture.getFilterData().maskBits = getMaskBits();

		updateFilter();
	}

	public void destroyed() {
		destroyCarryJoint();

		if (body != null)
			UMI.gs.world.destroyBody(body);

		System.out.println(this + " destryoed");
	}

	protected void destroyCarryJoint() {
		if (carryJoint != null) {
			Joint j = carryJoint;
			((Entity) j.getBodyA().getUserData()).carryJoint = null;
			((Entity) j.getBodyB().getUserData()).carryJoint = null;
			UMI.gs.world.destroyJoint(j);

		}
	}

	public Body getBody() {
		return body;
	}

	public Rectangle getBoundingRectangle() {
		return new Rectangle(getPosition().x - getWidth() / 2, getPosition().y - getHeight() / 2, getWidth(), getHeight());
	}

	protected short getCategoryBits() {
		return 1;
	}

	public float getConstructionRate() {
		return 0;
	}

	protected String getGraphic() {
		return "miner";
	}

	public float getHeight() {
		return .666667f;
	}

	protected short getMaskBits() {
		return Short.MAX_VALUE;
	}

	public float getMiningRate() {
		return 0;
	}

	public Vector2 getPosition() {
		if (body != null) {
			return body.getPosition().cpy();
		} else {
			return position.cpy();
		}
	}

	public float getRotation() {
		if (body != null) {
			return body.getAngle();
		} else {
			return 0;
		}
	}

	protected Shape getShape() {
		CircleShape cs = new CircleShape();
		cs.setRadius(getHeight() / 2 * .75f);

		return cs;
	}

	protected float getWidth() {
		return .666667f;
	}

	public boolean isCollidable() {
		return true;
	}

	public boolean isCombatType() {
		return false;
	}

	public boolean isControllableByPlayer() {
		return false;
	}

	protected boolean isImmobile() {
		return false;
	}

	public boolean isTransportingType() {
		return false;
	}

	public boolean keep() {
		return !selfDestruct;
	}

	public void onCollision(Entity other) {

	}

	public void render() {
		UMI.batch.draw(UMI.a.getSprite(getGraphic()), getPosition().x - .5f, getPosition().y - .5f, .5f, .5f, 1, 1, getWidth(), getHeight(), getRotation());
	}

	public void setPosition(Vector2 position) {
		this.position.set(position);
	}

	public void setRotation(float rotation) {
		if (body != null)
			body.setTransform(body.getPosition(), rotation);
	}

	public void takeDamage(float damage) {

	}

	public void update() {
		updateFilter();
	}

	private void updateFilter() {
		Filter f = new Filter();
		f.categoryBits = getCategoryBits();
		f.maskBits = getMaskBits();
		fixture.setFilterData(f);

		if (!isCollidable() || isImmobile()) {
			body.setType(BodyDef.BodyType.KinematicBody);
		} else {
			body.setType(BodyDef.BodyType.DynamicBody);
		}
	}
}
