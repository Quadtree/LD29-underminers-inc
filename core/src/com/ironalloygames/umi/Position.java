package com.ironalloygames.umi;

import com.badlogic.gdx.math.Vector2;

public class Position {
	public int x;
	public int y;

	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Position(Vector2 v2) {
		this.x = (int) (v2.x + .5f);
		this.y = (int) (v2.y + .5f);
	}

	public Position cpy() {
		return new Position(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			return x == ((Position) obj).x && y == ((Position) obj).y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return y * 100000 + x;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}

	public Vector2 vec2() {
		return new Vector2(x, y);
	}
}
