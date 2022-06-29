package com.github.vegeto079.PoESparkDPSCalculator.areas;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;

import com.github.vegeto079.PoESparkDPSCalculator.SparkDPSCalculator.Spark;

public class Area {
	private Polygon polygon = new Polygon();
	private Color color;

	public void addPoint(int x, int y) {
		polygon.addPoint(x, y);
	}

	public void addPoint(Point p) {
		addPoint(p.x, p.y);
	}

	public void draw(Graphics2D g) {
		Color before = g.getColor();
		g.setColor(color);
		g.draw(polygon);
		g.setColor(before);
	}

	public boolean contains(Spark spark) {
		return polygon.contains(spark.getPoint());
	}

	public boolean contains(Point p) {
		return polygon.contains(p);
	}

	public boolean contains(double x, double y) {
		return polygon.contains(new Point((int) x, (int) y));
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
}