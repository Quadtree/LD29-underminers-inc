package com.ironalloygames.umi.entity;

import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.job.Retrieve;

public class Porter extends Unit {

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 5;
	}

	@Override
	public float getCost() {
		return 60;
	}

	@Override
	public float getEnginePower() {
		return super.getEnginePower() * 1.3f;
	}

	@Override
	protected String getGraphic() {
		return "porter";
	}

	@Override
	public float getMaxHealth() {
		return super.getMaxHealth() * .65f;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.PORTER;
	}

	@Override
	public boolean isTransportingType() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		if (jobQueue.size() == 0) {

			float bestDist = Float.MAX_VALUE;
			Ore bestOre = null;

			for (Entity e : UMI.gs.getEntities()) {
				if (e instanceof Ore) {
					Ore o = (Ore) e;
					if (o.getMinedBy() == this.getFaction() && (o.claimedBy == null || o.claimedBy == this || !o.claimedBy.keep())) {
						float dist = o.getPosition().dst2(this.getPosition());

						if (dist < bestDist) {
							bestDist = dist;
							bestOre = o;
						}
					}
				}
			}

			if (bestOre != null) {
				jobQueue.add(new Retrieve(this, bestOre));
			}
		}
	}

}
