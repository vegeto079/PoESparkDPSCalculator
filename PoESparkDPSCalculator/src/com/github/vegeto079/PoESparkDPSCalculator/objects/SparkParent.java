package com.github.vegeto079.PoESparkDPSCalculator.objects;

import java.awt.Point;

public class SparkParent {
	double x, y;
	double death;
	double lastHit = -1;
	long sparkHitTimeout;

	public SparkParent(Point p, long duration, long sparkHitTimeout) {
		x = p.x;
		y = p.y;
		death = System.currentTimeMillis() + duration;
		this.sparkHitTimeout = sparkHitTimeout;
	}

	boolean canHit() {
		return lastHit + sparkHitTimeout <= System.currentTimeMillis();
	}
	
	void setLastHitNow() {
		lastHit = System.currentTimeMillis();
	}
}