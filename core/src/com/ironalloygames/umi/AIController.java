package com.ironalloygames.umi;

import java.util.EnumMap;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Terrain.TerrainType;
import com.ironalloygames.umi.entity.Buzzsaw;
import com.ironalloygames.umi.entity.Constructor;
import com.ironalloygames.umi.entity.Driller;
import com.ironalloygames.umi.entity.Entity;
import com.ironalloygames.umi.entity.Fabricator;
import com.ironalloygames.umi.entity.MiningPlan;
import com.ironalloygames.umi.entity.Peltast;
import com.ironalloygames.umi.entity.Porter;
import com.ironalloygames.umi.entity.Refinery;
import com.ironalloygames.umi.entity.Sentinel;
import com.ironalloygames.umi.entity.Unit;
import com.ironalloygames.umi.entity.Unit.Faction;
import com.ironalloygames.umi.entity.Unit.UnitType;
import com.ironalloygames.umi.job.Attack;
import com.ironalloygames.umi.job.Construct;
import com.ironalloygames.umi.job.Mine;

public class AIController {

	Unit.UnitType[] buildOrder = new Unit.UnitType[] {
			Unit.UnitType.DRILLER,
			Unit.UnitType.PORTER,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
			Unit.UnitType.DRILLER,
			Unit.UnitType.PORTER,
			Unit.UnitType.CONSTRUCTOR,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
			Unit.UnitType.PORTER,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
			Unit.UnitType.PORTER,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.PELTAST,
			Unit.UnitType.SENTINEL,
			Unit.UnitType.BUZZSAW,
	};

	Unit findNearestEnemy(Vector2 start) {
		float bestDist = Float.MAX_VALUE;
		Unit bestUnit = null;

		for (Entity e : UMI.gs.entities) {
			if (e instanceof Unit && ((Unit) e).getFaction() == Faction.PLAYER && ((Unit) e).isFullyConstructed()) {
				float dist = start.dst(e.getPosition());

				if (dist < bestDist) {
					bestDist = dist;
					bestUnit = (Unit) e;
				}
			}
		}

		return bestUnit;
	}

	Position findNearestOre(Position start) {

		start = new Position(Terrain.WIDTH - 2, Terrain.HEIGHT / 2);

		int bestDist = Integer.MAX_VALUE;
		Position bestPosition = null;

		for (int x = 0; x < Terrain.WIDTH; x++) {
			for (int y = 0; y < Terrain.HEIGHT; y++) {
				if (UMI.gs.terrain.getTerrainType(x, y) == TerrainType.ORE && !UMI.gs.terrain.isMined(x, y)) {
					int dist = Math.abs(x - start.x) + Math.abs(y - start.y);

					if (dist < bestDist) {
						bestDist = dist;
						bestPosition = new Position(x, y);
					}
				}
			}
		}

		return bestPosition;
	}

	public void update() {
		int combatUnitCount = 0;
		int minerCount = 0;

		for (Entity e : UMI.gs.entities) {
			if (e instanceof Unit && ((Unit) e).getFaction() == Faction.AI) {
				Unit u = (Unit) e;

				if (u.getUnitType() == UnitType.BUZZSAW || u.getUnitType() == UnitType.SENTINEL || u.getUnitType() == UnitType.PELTAST) {
					combatUnitCount++;
				}
				if (u.getUnitType() == UnitType.DRILLER) {
					minerCount++;
				}
			}
		}

		// 4 2
		if (combatUnitCount >= 4 && minerCount >= 2 && MathUtils.random.nextInt(40) == 0) {
			Unit nearestEnemy = null;
			Path p = null;

			for (Entity e : UMI.gs.entities) {
				if (e instanceof Driller && ((Driller) e).getFaction() == Faction.AI) {
					Driller dr = (Driller) e;

					nearestEnemy = findNearestEnemy(dr.getPosition());

					if (nearestEnemy != null) {

						p = new Path(UMI.gs.terrain, new Position(dr.getPosition()), new Position(nearestEnemy.getPosition()));

						if (p.getTotalMoveCost() > 150) {

							dr.clearJobQueue();

							for (Position pos : p.getPath()) {

								MiningPlan mp = null;

								for (Entity en : UMI.gs.getEntitiesPickedInRectangle(new Rectangle(pos.x, pos.y, .1f, .1f))) {
									if (en instanceof MiningPlan && ((MiningPlan) en).creatingFaction == Faction.AI) {
										mp = (MiningPlan) en;
									}
								}

								if (!UMI.gs.terrain.isMined(pos.x, pos.y) && mp == null) {
									mp = new MiningPlan();
									mp.setPosition(pos.vec2());
									mp.creatingFaction = Faction.AI;
									UMI.gs.entityAddQueue.add(mp);
								}

								if (mp != null) {
									dr.order(new Mine(dr, mp));
								}
							}
						}
					}

					break;
				}

			}

			if (p != null) {
				// System.out.println("TOTAL MOVE COST IS " +
				// p.getTotalMoveCost());
			}

			if (nearestEnemy != null && p != null && p.getTotalMoveCost() < 150) {
				for (Entity e : UMI.gs.entities) {
					if (e instanceof Unit && ((Unit) e).getFaction() == Faction.AI) {
						Unit u = (Unit) e;

						if (u.getUnitType() == UnitType.BUZZSAW || u.getUnitType() == UnitType.SENTINEL || u.getUnitType() == UnitType.PELTAST) {
							u.clearJobQueue();
							u.order(new Attack(u, nearestEnemy));
						}
					}
				}
			}
		}

		for (Entity e : UMI.gs.entities) {
			if (e instanceof Driller) {
				Driller dr = (Driller) e;

				if (dr.getFaction() == Faction.AI && !dr.hasOrders()) {
					Position nearestOre = findNearestOre(new Position(dr.getPosition()));

					Path p = new Path(UMI.gs.terrain, new Position(dr.getPosition()), nearestOre);

					for (Position pos : p.getPath()) {
						if (!UMI.gs.terrain.isMined(pos.x, pos.y)) {
							MiningPlan mp = new MiningPlan();
							mp.setPosition(pos.vec2());
							mp.creatingFaction = Faction.AI;
							UMI.gs.entityAddQueue.add(mp);
							dr.order(new Mine(dr, mp));
						}
					}
				}
			}
			if (e.getConstructionRate() > 0) {
				Unit u = (Unit) e;

				if (u.getFaction() == Faction.AI && !u.hasOrders()) {
					EnumMap<UnitType, Integer> currentQtys = new EnumMap<UnitType, Integer>(UnitType.class);

					for (UnitType ut : Unit.UnitType.values()) {
						currentQtys.put(ut, 0);
					}

					for (Entity e2 : UMI.gs.entities) {
						if (e2 instanceof Unit && ((Unit) e2).getFaction() == Faction.AI) {

							UnitType ut = ((Unit) e2).getUnitType();

							currentQtys.put(ut, currentQtys.get(ut) + 1);
						}
					}

					EnumMap<UnitType, Integer> buildOrderQtys = new EnumMap<UnitType, Integer>(UnitType.class);

					for (UnitType ut : Unit.UnitType.values()) {
						buildOrderQtys.put(ut, 0);
					}

					Unit brush = null;

					int c = 0;

					while (brush == null) {
						for (UnitType bout : buildOrder) {
							buildOrderQtys.put(bout, buildOrderQtys.get(bout) + 1);

							if (buildOrderQtys.get(bout) > currentQtys.get(bout)) {
								switch (bout) {
								case REFINERY:
									brush = new Refinery();
									break;
								case CONSTRUCTOR:
									brush = new Constructor();
									break;
								case DRILLER:
									brush = new Driller();
									break;
								case BUZZSAW:
									brush = new Buzzsaw();
									break;
								case FABRICATOR:
									brush = new Fabricator();
									break;
								case PELTAST:
									brush = new Peltast();
									break;
								case PORTER:
									brush = new Porter();
									break;
								case SENTINEL:
									brush = new Sentinel();
									break;
								}
								break;
							}
						}

						if (c++ > 1000)
							break;
					}

					if (brush != null && UMI.gs.aiCredits >= brush.getCost()) {
						brush.setPosition(u.getPosition().add(0, u.getHeight() / 2));
						UMI.gs.aiCredits -= brush.getCost();
						UMI.gs.entityAddQueue.add(brush);
						brush.setConstructed(0);
						brush.setFaction(Faction.AI);
						u.order(new Construct(u, brush));
					}

				}
			}
		}
	}
}
