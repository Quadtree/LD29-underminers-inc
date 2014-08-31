package com.ironalloygames.umi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import com.ironalloygames.umi.Terrain.TerrainType;

public class Path {
	class Node implements Comparable<Node> {
		int costToHere;
		int distanceToGoal;
		Position pos;
		Node previousNode;

		public Node(int costToHere, Position pos, Node previousNode) {
			super();
			this.costToHere = costToHere;
			this.distanceToGoal = costToHere + Math.abs(pos.x - end.x) + Math.abs(pos.y - end.y);
			this.pos = pos;
			this.previousNode = previousNode;
		}

		@Override
		public int compareTo(Node o) {
			return distanceToGoal - o.distanceToGoal;
		}
	}

	HashMap<Position, Node> closed = new HashMap<Position, Node>();

	HashMap<Position, Node> open = new HashMap<Position, Node>();

	PriorityQueue<Node> openQueue = new PriorityQueue<Node>();

	List<Position> path;

	Position start, end;

	Terrain terrain;
	int totalMoveCost;

	public Path(Terrain terrain, Position start, Position end) {
		this.terrain = terrain;
		this.start = start;
		this.end = end;
	}

	private boolean addAdjacentNode(Node prevNode, int dx, int dy) {
		Position newPos = new Position(prevNode.pos.x + dx, prevNode.pos.y + dy);

		int newCost = 0;
		// terrain.isPassable(newPos.x, newPos.y) ? 1 :
		// (terrain.getTerrainType(newPos.x, newPos.y) == TerrainType.ORE ? 800
		// : 200);

		if (terrain.isPassable(newPos)) {
			newCost = 1;
		} else {
			if (terrain.isExtraImpassable(newPos.x, newPos.y)) {
				newCost = 20;
			} else {
				if (terrain.getTerrainType(newPos.x, newPos.y) == TerrainType.ORE) {
					newCost = 800;
				} else {
					newCost = 200;
				}
			}
		}

		if (newPos.equals(end)) {
			totalMoveCost = prevNode.costToHere + newCost;

			path = new ArrayList<Position>();

			path.add(newPos);

			Node curNode = prevNode;

			while (curNode != null) {
				path.add(0, curNode.pos);
				curNode = curNode.previousNode;
			}

			return true;
		}

		if (closed.containsKey(newPos)) {
			return false;
		}

		if (open.containsKey(newPos)) {
			if (open.get(newPos).previousNode.costToHere > prevNode.costToHere) {
				open.get(newPos).previousNode = prevNode;
				open.get(newPos).costToHere = prevNode.costToHere + newCost;
			}
		} else {
			Node nn = new Node(prevNode.costToHere + newCost, newPos, prevNode);
			open.put(nn.pos, nn);
			openQueue.add(nn);
		}

		return false;
	}

	public List<Position> getPath() {
		if (path != null)
			return path;

		Node fn = new Node(0, start, null);

		open.put(fn.pos, fn);
		openQueue.add(fn);

		while (openQueue.size() > 0) {
			Node topNode = openQueue.poll();
			open.remove(topNode.pos);
			closed.put(topNode.pos, topNode);

			if (addAdjacentNode(topNode, 1, 0) || addAdjacentNode(topNode, -1, 0) || addAdjacentNode(topNode, 0, 1) || addAdjacentNode(topNode, 0, -1))
				break;
		}

		return path;
	}

	public int getTotalMoveCost() {
		getPath();

		return totalMoveCost;
	}
}
