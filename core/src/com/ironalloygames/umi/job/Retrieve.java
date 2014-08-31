package com.ironalloygames.umi.job;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.Ore;
import com.ironalloygames.umi.entity.Refinery;
import com.ironalloygames.umi.entity.Unit;

public class Retrieve extends Job {

	boolean isComplete = false;

	Move subMove;
	Move subMove2;

	Entity target;

	Refinery targetRefinery;

	public Retrieve(Unit u, Entity e) {
		super(u);
		target = e;
		subMove = new Move(u, target.getPosition());
	}

	@Override
	public int getPriority() {
		return super.getPriority() + (int) (target.getPosition().dst(u.getPosition()) * 100);
	}

	@Override
	public boolean keep() {
		return target.keep() && u.isTransportingType() && !isComplete && (target.carryJoint == null) == (u.carryJoint == null);
	}

	@Override
	public void render() {
		super.render();

		if (u.carryJoint != null) {
			Vector2 center = u.getPosition().cpy().add(target.getPosition()).scl(.5f);
			Vector2 delta = target.getPosition().cpy().sub(u.getPosition());

			UMI.batch.draw(UMI.a.getSprite("tow_beam"), center.x - .5f, center.y - .5f, .5f, .5f, 1, 1, delta.len(), .4f, delta.angle());
		}
	}

	@Override
	public void update() {

		((Ore) target).claimedBy = u;

		if (subMove.getRangeToDest() > 1.5f && subMove2 == null) {
			subMove.update();
		} else if (u.carryJoint == null) {
			if (u.getPosition().dst(target.getPosition()) > 1.6f) {
				subMove = new Move(u, target.getPosition());
			} else {
				RopeJointDef jd = new RopeJointDef();
				jd.bodyA = u.getBody();
				jd.bodyB = target.getBody();
				jd.maxLength = u.getPosition().dst(target.getPosition());
				jd.collideConnected = false;
				jd.localAnchorA.set(new Vector2(0, 0));
				jd.localAnchorB.set(new Vector2(0, 0));

				u.carryJoint = UMI.gs.world.createJoint(jd);
				target.carryJoint = u.carryJoint;
			}
		} else {
			if (targetRefinery == null || !targetRefinery.keep()) {

				float bestQty = Float.MAX_VALUE;
				Refinery bestRefinery = null;

				for (Entity e : UMI.gs.getEntities()) {
					if (e instanceof Refinery) {
						Refinery r = (Refinery) e;

						if (r.getFaction() == u.getFaction() && r.isFullyConstructed()) {
							if (r.getOreToProcess() < bestQty) {
								bestQty = r.getOreToProcess();
								bestRefinery = r;
							}
						}
					}
				}

				this.targetRefinery = bestRefinery;

				System.out.println("Chose " + targetRefinery);

				subMove2 = null;
			} else {
				if (subMove2 == null) {
					subMove2 = new Move(u, targetRefinery.getPosition());
				}

				if (subMove2.getRangeToDest() > 2.5f) {
					subMove2.update();
				} else {
					System.out.println("Command completed");
					// UMI.gs.world.destroyJoint(u.carryJoint);
					// u.carryJoint = null;
					isComplete = true;
					((Ore) target).setConsumed(true);
					targetRefinery.setOreToProcess(targetRefinery.getOreToProcess() + 20);
					UMI.a.getSound("drop").play(.3f);
				}
			}
		}
	}
}
