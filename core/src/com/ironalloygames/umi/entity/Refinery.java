package com.ironalloygames.umi.entity;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Terrain;
import com.ironalloygames.umi.UMI;

public class Refinery extends Unit {

	boolean impassibleSquaresSet = false;

	float oreToProcess = 0;

	@Override
	protected short getCategoryBits() {
		return (short) (super.getCategoryBits() | 2);
	}

	@Override
	public float getConstructionTime() {
		return super.getConstructionTime() * 30;
	}

	@Override
	public float getCost() {
		return 350;
	}

	@Override
	public float getEnginePower() {
		return 0;
	}

	@Override
	protected String getGraphic() {
		return "refinery";
	}

	@Override
	public float getHeight() {
		return 128f / 48f;
	}

	@Override
	public float getMaxHealth() {
		return 3;
	}

	public float getOreToProcess() {
		return oreToProcess;
	}

	@Override
	public UnitType getUnitType() {
		return UnitType.REFINERY;
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
	public void render() {
		super.render();

		if (oreToProcess > 0) {
			UMI.batch.draw(UMI.a.getSprite("refinery_active"), getPosition().x - .5f, getPosition().y - .5f, .5f, .5f, 1, 1, getWidth(), getHeight(), getRotation());
		}
	}

	public void setOreToProcess(float oreToProcess) {
		this.oreToProcess = oreToProcess;
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
		
		if(isFullyConstructed()){

			float oreProcessed = Math.min(oreToProcess, .15f);
			oreToProcess -= oreProcessed;

			if (this.getFaction() == Faction.PLAYER) {
				UMI.gs.playerCredits += oreProcessed;
			} else {
				UMI.gs.aiCredits += oreProcessed;
			}
		}
	}

}
