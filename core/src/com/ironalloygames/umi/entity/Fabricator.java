package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Terrain;
import com.ironalloygames.umi.UMI;

public class Fabricator extends Unit {
	boolean impassibleSquaresSet = false;

	@Override
	protected short getCategoryBits() {
		return (short) (super.getCategoryBits() | 2);
	}

	@Override
	public float getConstructionRate() {
		return 6;
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 35;
	}

	@Override
	public float getCost() {
		return 300;
	}

	@Override
	public float getEnginePower() {
		return 0;
	}

	@Override
	protected String getGraphic() {
		return "fabricator";
	}

	@Override
	public float getHeight() {
		return 128f / 48f;
	}

	@Override
	public float getMaxHealth() {
		return 10;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.FABRICATOR;
	}

	@Override
	protected float getWidth() {
		return 128f / 48f;
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public void update() {
		super.update();

		if (isFullyConstructed() && !impassibleSquaresSet) {
			for (int x = 0; x < Terrain.WIDTH; x++) {
				for (int y = 0; y < Terrain.HEIGHT; y++) {
					float dist = new Vector2(x, y).dst(this.getPosition());

					if (dist < getHeight() / 2 + .5f) {
						UMI.gs.terrain.setExtraImpassibleSquare(x, y, true);
					}
				}
			}
		}
	}
}
