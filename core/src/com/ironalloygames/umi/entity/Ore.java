package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.MathUtils;
import com.ironalloygames.umi.entity.Unit.Faction;

public class Ore extends Entity {

	public Unit claimedBy = null;

	boolean consumed = false;

	Faction minedBy;

	@Override
	public void created() {
		super.created();

		body.setLinearVelocity(MathUtils.random(-2, 2), MathUtils.random(-2, 2));
	}

	@Override
	protected String getGraphic() {
		return "ore_chunk";
	}

	@Override
	public float getHeight() {
		return .5f;
	}

	@Override
	protected short getMaskBits() {
		return 2;
	}

	public Faction getMinedBy() {
		return minedBy;
	}

	@Override
	protected float getWidth() {
		return .5f;
	}

	public boolean isConsumed() {
		return consumed;
	}

	@Override
	public boolean keep() {
		return !consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	public void setMinedBy(Faction minedBy) {
		this.minedBy = minedBy;
	}

}
