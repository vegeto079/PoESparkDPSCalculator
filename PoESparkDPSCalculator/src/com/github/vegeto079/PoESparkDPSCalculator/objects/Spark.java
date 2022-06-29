package com.github.vegeto079.PoESparkDPSCalculator.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import com.github.vegeto079.PoESparkDPSCalculator.SparkDPSCalculator;
import com.github.vegeto079.ngcommontools.main.Tools;

public class Spark {
	double speed, x, y, angle, size, sizeView;
	Color color;
	SparkParent parent;
	int pierceLeft, forkLeft;
	SparkDPSCalculator main;

	public Spark(SparkDPSCalculator main, SparkParent parent) {
		this.main = main;
		this.parent = parent;
		x = parent.x;
		y = parent.y;
		size = main.options.sparkSize;
		sizeView = 5;
		speed = main.options.sparkSpeed;
		pierceLeft = main.options.pierce;
		forkLeft = main.options.fork;
		color = Color.BLUE;
	}

	public void draw(Graphics2D g) {
		Color before = g.getColor();
		g.setColor(color);
		g.drawRect((int) (x - sizeView / 2), (int) (y - sizeView / 2), (int) (sizeView), (int) (sizeView));
		g.setColor(before);
	}

	public boolean tick() {
		return tick(0);
	}

	public boolean tick(int loopPrevention) {
		if (System.currentTimeMillis() >= parent.death)
			return false;
		else if (loopPrevention > main.sparkLoopPrevention)
			return false;
		else if (pierceLeft < 0 && forkLeft == 0)
			return false;
		else if (forkLeft < 0)
			return false;

		if (canHit()) {
			color = Color.BLUE;
		} else {
			color = Color.RED;
		}

		double newX = x + speed * Math.cos(angle);
		double newY = y + speed * Math.sin(angle);

		double minX = newX - size / 2;
		double minY = newY - size / 2;
		double maxX = newX + size / 2;
		double maxY = newY + size / 2;

		if (canHit() && main.badGuy != null && (main.badGuy.contains(minX, minY) || main.badGuy.contains(maxX, minY) || main.badGuy.contains(minX, maxY)
				|| main.badGuy.contains(maxX, maxY))) {
			hit();
		}
		if (!main.area.contains(minX, minY) || !main.area.contains(maxX, minY) || !main.area.contains(minX, maxY)
				|| !main.area.contains(maxX, maxY)) {
			setRandomDirection();
			tick(++loopPrevention);
		} else {
			x = newX;
			y = newY;
		}
		return true;
	}

	public void setRandomDirection() {
		angle = Tools.random(main, 0, 360, "Spark Angle");
	}

	public Point getPoint() {
		return new Point((int) x, (int) y);
	}

	public boolean canHit() {
		return parent.canHit();
	}

	public void hit() {
		parent.setLastHitNow();
		main.AddHit();
		pierceLeft--;
		if (pierceLeft < 0 && forkLeft >= 0) {
			forkLeft--;
			fork();
			// Kill self as we have forked
			forkLeft = -1;
		}
	}

	public void fork() {
		Spark spark = new Spark(main, parent);
		spark.pierceLeft = 0;
		spark.forkLeft = forkLeft;
		spark.x = x;
		spark.y = y;
		spark.angle = angle - 120;
		main.sparks.add(spark);

		Spark spark2 = new Spark(main, parent);
		spark2.pierceLeft = 0;
		spark2.forkLeft = forkLeft;
		spark2.setRandomDirection();
		spark2.x = x;
		spark2.y = y;
		spark2.angle = angle + 120;
		main.sparks.add(spark2);
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
}