package com.ironalloygames.umi.entity;

public class Driller extends Unit {

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 8;
	}

	@Override
	public float getCost() {
		return 115;
	}

	@Override
	public float getMiningRate() {
		return 1;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.DRILLER;
	}
}
