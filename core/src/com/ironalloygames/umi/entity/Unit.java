package com.ironalloygames.umi.entity;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.job.Attack;
import com.ironalloygames.umi.job.Construct;
import com.ironalloygames.umi.job.Job;
import com.ironalloygames.umi.job.Mine;
import com.ironalloygames.umi.job.Move;
import com.ironalloygames.umi.job.Retrieve;

public class Unit extends Entity {
	public enum Faction {
		AI,
		PLAYER
	}

	public enum UnitType {
		BUZZSAW,
		CONSTRUCTOR,
		DRILLER,
		FABRICATOR,
		PELTAST,
		PORTER,
		REFINERY,
		SENTINEL
	}

	public static final float CONSTRUCTION_BEAM_RANGE = 2.5f;

	float constructed = 1;
	Faction faction = Faction.PLAYER;

	boolean gettingUnstuck = false;
	float health;

	ArrayList<Job> jobQueue = new ArrayList<Job>();
	float stuckTime = 0;

	public boolean approach(Vector2 point) {

		Vector2 delta = point.cpy().sub(getPosition());

		if (delta.len2() > .05f * .05f) {

			if (delta.len2() > .6f * .6f) {
				setRotation(delta.angle());
			}

			delta.nor();

			if (stuckTime > 30) {
				gettingUnstuck = true;
				jobQueue.clear();
			}

			if (gettingUnstuck) {
				// System.out.println("I AM STUCK");
				delta.rotate(25);
				stuckTime--;

				if (stuckTime <= 0) {
					gettingUnstuck = false;
				}
			}

			delta.scl(getEnginePower());

			body.applyLinearImpulse(delta, body.getWorldCenter(), true);

			// System.out.println(body.getLinearVelocity().len());

			if (!gettingUnstuck) {
				if (body.getLinearVelocity().len() < 2f) {
					stuckTime++;
				} else {
					stuckTime = 0;
				}
			}

			return true;
		} else {

			delta.nor();
			delta.scl(-Math.min(getEnginePower(), body.getLinearVelocity().len() / 2));
			body.applyLinearImpulse(delta, body.getWorldCenter(), true);

			return false;
		}
	}

	public void attack(Entity target) {
		if (target instanceof Unit) {
			Unit u = (Unit) target;
			if (u.jobQueue.size() == 0 && u.isCombatType()) {
				u.order(new Attack(u, this));
			}
		}
	}

	public void clearJobQueue() {
		jobQueue.clear();
	}

	@Override
	public void created() {
		super.created();

		body.setGravityScale(0);

		this.health = getMaxHealth();
	}

	@Override
	public void destroyed() {
		super.destroyed();
		UMI.a.getSound("unit_die").play();
	}

	@Override
	protected short getCategoryBits() {
		if (constructed > .01f) {
			return (short) (faction == Faction.PLAYER ? 1 : 4);
		} else {
			return 8;
		}
	}

	public float getCombatRange() {
		return 0;
	}

	public float getConstructed() {
		return constructed;
	}

	public float getConstructionTime() {
		return 1;
	}

	public float getCost() {
		return 0;
	}

	public float getEnginePower() {
		return .15f * .75f;
	}

	public Faction getFaction() {
		return faction;
	}

	public float getHealth() {
		return health;
	}

	public String getMarkingsGraphic() {
		return getGraphic() + "_markings";
	}

	public float getMaxHealth() {
		return 1;
	}

	public UnitType getUnitType() {
		return null;
	}

	public boolean hasOrders() {
		return jobQueue.size() > 0;
	}

	@Override
	public boolean isCollidable() {
		return constructed > .01f;
	}

	@Override
	public boolean isControllableByPlayer() {
		return faction == Faction.PLAYER;
	}

	public boolean isFullyConstructed() {
		return constructed > .9999f;
	}

	@Override
	protected boolean isImmobile() {
		return constructed < .01f;
	}

	@Override
	public boolean keep() {
		return super.keep() && health > 0;
	}

	public void order(Job job) {
		if (job instanceof Move && isImmobile())
			return;
		jobQueue.add(job);
	}

	@Override
	public void render() {
		if (!this.isFullyConstructed()) {
			UMI.batch.setColor(new Color(constructed, constructed, constructed, 1));
		}
		super.render();
		if (!this.isFullyConstructed()) {
			UMI.batch.setColor(Color.WHITE);
		}

		if (this.getFaction() == Faction.PLAYER)
			UMI.batch.setColor(Color.BLUE);
		else
			UMI.batch.setColor(Color.RED);

		if (UMI.a.getSprite(getMarkingsGraphic()) != null)
			UMI.batch.draw(UMI.a.getSprite(getMarkingsGraphic()), getPosition().x - .5f, getPosition().y - .5f, .5f, .5f, 1, 1, getWidth(), getHeight(), getRotation());

		UMI.batch.setColor(Color.WHITE);

		if (UMI.gs.igis.isSelectedByPlayer(this)) {
			UMI.batch.setColor(Color.BLACK);
			UMI.batch.draw(UMI.a.getSprite("healthbar"), getPosition().x - .5f, getPosition().y + .4f - .5f, .5f, .5f, 1, 1, getWidth() * 1, 3f / 32f, 0);
			UMI.batch.setColor(Color.GREEN);
			UMI.batch.draw(UMI.a.getSprite("healthbar"), getPosition().x - .5f, getPosition().y + .4f - .5f, .5f, .5f, 1, 1, getWidth() * (this.getHealth() / this.getMaxHealth()), 3f / 32f, 0);
			UMI.batch.setColor(Color.WHITE);
		}

		if (jobQueue.size() > 0) {
			if (jobQueue.get(0).keep())
				jobQueue.get(0).render();
		}
	}

	public void setConstructed(float constructed) {
		this.constructed = constructed;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	@Override
	public void setRotation(float rotation) {
		if (!isImmobile())
			super.setRotation(rotation);
	}

	@Override
	public void takeDamage(float damage) {
		super.takeDamage(damage);
		health -= damage;
	}

	@Override
	public void update() {
		super.update();

		if (!this.isFullyConstructed())
			return;

		Collections.sort(jobQueue);

		boolean keepJoint = false;

		if (jobQueue.size() > 0) {
			if (jobQueue.get(0).keep()) {

				jobQueue.get(0).update();
				if (jobQueue.size() > 0 && jobQueue.get(0) instanceof Retrieve) {
					keepJoint = true;
				}
			} else {
				// System.out.println("Removing JOB " + jobQueue.get(0));
				jobQueue.remove(0);
			}
		}

		if (!keepJoint && carryJoint != null) {
			destroyCarryJoint();
		}

		if (isCombatType()) {
			for (Entity e : UMI.gs.getEntitiesWithinLOSOf(getPosition(), getCombatRange() + 2, false)) {
				if (e instanceof Unit) {
					Unit u = (Unit) e;

					if (u.getFaction() != this.getFaction()) {
						this.attack(e);
					}
				}
			}
		}

		if (getConstructionRate() > 0 && jobQueue.size() == 0) {
			for (Entity e : UMI.gs.getEntitiesWithinLOSOf(getPosition(), CONSTRUCTION_BEAM_RANGE, false)) {
				if (e instanceof Unit) {
					Unit u = (Unit) e;

					if (u.getFaction() == this.getFaction() && !u.isFullyConstructed()) {
						order(new Construct(this, u));
					}
				}
			}
		}

		if (jobQueue.size() == 0 && this.getMiningRate() > 0) {
			for (Entity e : UMI.gs.getEntitiesPickedInRectangle(new Rectangle(getPosition().x - 6, getPosition().y - 6, 12, 12))) {
				if (e instanceof MiningPlan && ((MiningPlan) e).creatingFaction == getFaction()) {

					MiningPlan mp = (MiningPlan) e;
					if (mp.creatingFaction == getFaction()) {
						order(new Mine(this, mp));
					}
				}
			}
		}

		body.setLinearVelocity(body.getLinearVelocity().cpy().scl(.9f));
	}

}
