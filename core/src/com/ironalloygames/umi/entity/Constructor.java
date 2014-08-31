package com.ironalloygames.umi.entity;

public class Constructor extends Unit {

	@Override
	public float getConstructionRate() {
		return .6f;
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 20;
	}

	@Override
	public float getCost() {
		return 150;
	}

	@Override
	protected String getGraphic() {
		return "constructor";
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.CONSTRUCTOR;
	}
}
