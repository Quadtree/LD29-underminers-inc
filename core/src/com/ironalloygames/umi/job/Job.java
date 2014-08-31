package com.ironalloygames.umi.job;

import com.ironalloygames.umi.UMI;
import com.ironalloygames.umi.entity.Unit;

public class Job implements Comparable<Job> {
	int tickStarted = UMI.gs.tick;

	Unit u;

	public Job(Unit u) {
		this.u = u;
	}

	@Override
	public int compareTo(Job o) {
		return this.getPriority() - o.getPriority();
	}

	public void destroyed() {

	}

	public int getPriority() {
		return tickStarted;
	}

	public boolean keep() {
		return true;
	}

	public void render() {

	}

	public void update() {

	}
}
