package com.ironalloygames.umi.job;

import com.badlogic.gdx.math.Vector2;
import com.ironalloygames.umi.Path;
import com.ironalloygames.umi.Position;
import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Unit;

public class Move extends Job {
	int blockedFor = 0;
	Vector2 destination;
	boolean keep = true;

	Path path;

	int pathPoi = 0;

	public Move(Unit u, Vector2 destination) {
		super(u);
		this.destination = destination.cpy();
	}

	public float getRangeToDest() {
		return u.getPosition().dst(destination);
	}

	@Override
	public boolean keep() {
		return keep;
	}

	@Override
	public void update() {
		if (path == null) {
			path = new Path(UMI.gs.terrain, new Position(u.getPosition()), new Position(destination));
		}

		/*
		 * ShapeRenderer sr = new ShapeRenderer();
		 * sr.setProjectionMatrix(UMI.gs.worldCam.combined);
		 * sr.begin(ShapeType.Line);
		 * 
		 * for (Position p : path.getPath()) { sr.circle(p.x, p.y, .5f, 32); }
		 * sr.end();
		 */

		// System.out.println("DEST " + destination);

		Vector2 nextPos = pathPoi < path.getPath().size() - 1 ? path.getPath().get(pathPoi + 1).vec2() : destination;

		if (blockedFor < 15) {
			if (UMI.gs.terrain.isInLOS(u.getPosition(), nextPos, .25f)) {
				pathPoi++;
			}
		} else {
			if (UMI.gs.terrain.isInLOS(u.getPosition(), nextPos)) {
				pathPoi++;
			}
		}

		if (pathPoi < path.getPath().size()) {
			if (!u.approach(path.getPath().get(pathPoi).vec2())) {
				blockedFor++;
			} else {
				blockedFor = 0;
			}
		} else {
			if (!u.approach(destination)) {
				keep = false;
			}
		}

		if (blockedFor > 30)
			keep = false;

		/*
		 * if (pathPoi < path.getPath().size()) { if
		 * (!u.approach(path.getPath().get(pathPoi).vec2())) { pathPoi++; } }
		 * else { if (!u.approach(destination)) keep = false; }
		 */

	}
}
