package com.github.vegeto079.PoESparkDPSCalculator.objects;

public class Hit {
	public long time = System.currentTimeMillis();
	public double damage;

	public Hit(double damage) {
		this.damage = damage;
	}
}