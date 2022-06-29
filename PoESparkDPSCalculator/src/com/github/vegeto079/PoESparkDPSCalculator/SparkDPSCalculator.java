package com.github.vegeto079.PoESparkDPSCalculator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.vegeto079.ngcommontools.main.Game;
import com.github.vegeto079.ngcommontools.main.Logger;
import com.github.vegeto079.ngcommontools.main.Tools;

public class SparkDPSCalculator extends Game {
	private static final long serialVersionUID = 3419048634045147821L;
	
	// ****************
	// Variables below to change
	double castRate = 5;
	int proj = 10;
	int pierce = 1;
	int fork = 0;
	int duration = 3000; // milliseconds
	int damagePerHit = 655652;
	
	double sparkSpeed = 3; // How many pixels the Spark moves per-tick. I set this arbitrarily
	double sparkSize = 25;
	
	long timeToAverageBy = 6000;
	MapArea.Map selectedMap = MapArea.Map.ORIATH_DOCKS; // ORIATH_DOCKS, SQUARE
	// ****************

	public SparkDPSCalculator(Logger logger, String[] args, int ticksPerSecond, int paintTicksPerSecond, String title,
			int width, int height) {
		super(logger, args, ticksPerSecond, paintTicksPerSecond, title, width, height);
	}

	static SparkDPSCalculator dps = null;

	public static void main(String[] args) {
		dps = new SparkDPSCalculator(new Logger(true), args, 100, 120, "Spark DPS Calculator", 800, 800);
		dps.startThread();
	}

	ArrayList<Spark> sparks = new ArrayList<Spark>();

	double expectedDamage = damagePerHit * castRate;

	int count = 0;
	double MAX_COUNT = 100 / castRate;
	MapArea area = null;
	BadGuy badGuy = null;
	Point badGuyPoint = new Point(600, 600);

	long start = System.currentTimeMillis();
	ArrayList<Hit> hits = new ArrayList<Hit>();
	double avgHitsPerSecond = -1;
	double avgDamagePerSecond = -1;
	DecimalFormat decimalFormat = new DecimalFormat("#");
	DecimalFormat decimalFormat2 = new DecimalFormat("#.#");
	boolean shootNow = true;
	
	int sparkLoopPrevention = 50; // If a Spark hits a wall more than this many times, kill it. It's probably stuck in a wall

	class Hit {
		long time = System.currentTimeMillis();
		double damage;

		public Hit(double damage) {
			this.damage = damage;
		}
	}

	@Override
	public void paintTick(Graphics2D g) {
		super.paintTick(g);
		if (area != null) {
			if (mousePoint != null)
				g.drawOval(mousePoint.x - 10, mousePoint.y - 10, 20, 20);
			area.draw(g);
			badGuy.draw(g);

			synchronized (sparks) {
				for (Spark s : sparks)
					s.draw(g);
			}
			g.setColor(Color.WHITE);
			int y = 35;
			g.drawString("Hits per second: " + decimalFormat2.format(avgHitsPerSecond), 25, y += 20);
			g.drawString("dps: " + decimalFormat.format(avgDamagePerSecond), 25, y += 20);
			g.drawString("effectiveness: " + decimalFormat2.format(avgDamagePerSecond / expectedDamage * 100) + "%", 25,
					y += 20);
			long done = start + timeToAverageBy - System.currentTimeMillis();
			if (done < 0) {
				start = System.currentTimeMillis();
			}
			g.drawString("time left (for averaging): " + done + ", fps: " + this.getFps() + ", ups: " + this.getUps(), 25, y += 20);
			g.drawString("castRate: " + castRate + ", projectiles: " + proj, 25, y += 40);
			g.drawString("pierce: " + pierce + ", fork: " + fork, 25, y += 20);
			g.drawString("duration: " + duration, 25, y += 20);
		}
	}

	@Override
	public void gameTick() {
		super.gameTick();
		if (area == null) {
			badGuy = new BadGuy(badGuyPoint);
			area = new MapArea(selectedMap);
			mousePoint = new Point(getWidth() / 2, getHeight() / 2);
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);
			decimalFormat2.setGroupingUsed(true);
			decimalFormat2.setGroupingSize(3);
		}
		badGuy = new BadGuy(badGuyPoint);
		if (shootNow) {
			count++;
			if (count >= MAX_COUNT) {
				addSparks(proj);
				count = 0;
			}
		}
		synchronized (sparks) {
			for (int i = 0; i < sparks.size(); i++) {
				if (!sparks.get(i).tick()) {
					sparks.remove(i);
					i--;
				}
			}
		}
		synchronized (hits) {
			if (!hits.isEmpty()) {
				double newAvgHitsPerSecond = 0;
				double newAvgDpsPerSecond = 0;
				for (int i = 0; i < hits.size(); i++) {
					if (hits.get(i).time + timeToAverageBy <= System.currentTimeMillis()) {
						hits.remove(i);
						i--;
					} else {
						newAvgHitsPerSecond++;
						newAvgDpsPerSecond += hits.get(i).damage;
					}
				}
				try {
					avgHitsPerSecond = newAvgHitsPerSecond / ((double) timeToAverageBy / 1000d);
				} catch (Exception e) {
					avgHitsPerSecond = 0;
				}
				try {
					avgDamagePerSecond = newAvgDpsPerSecond / ((double) timeToAverageBy / 1000d);
				} catch (Exception e) {
					avgDamagePerSecond = 0;
				}
			}
		}
	}

	void addSparks(int amount) {
		if (mousePoint != null)
			synchronized (sparks) {
				SparkParent parent = new SparkParent(mousePoint);
				int amt = 360 / amount;
				int currentAngle = 0;
				while (amount > 0) {
					Spark spark = new Spark(parent);
					spark.angle = currentAngle;
					currentAngle += amt;
					sparks.add(spark);
					amount--;
				}
			}
	}

	Point mousePoint = null;

	void mouseAction(int x, int y) {
		if (area.contains(x, y))
			mousePoint = new Point(x, y);
	}

	void mouseUnaction() {
		// mousePoint = null;
	}

	@Override
	public void mouseDragged(int x, int y, int button, int xOrigin, int yOrigin) {
		if (button == 1) // Left Click
			mouseAction(x, y);
		else if (button == 2)
			shootNow = !shootNow;
		else if (button == 3) // Right click
			badGuyPoint = new Point(x, y);
	}

	@Override
	public void mouseMoved(int x, int y, int button) {
	}

	@Override
	public void mouseClicked(int x, int y, int button) {
		mouseUnaction();
	}

	@Override
	public void mousePressed(int x, int y, int button) {
		if (button == 1) // Left Click
			mouseAction(x, y);
		else if (button == 2)
			shootNow = !shootNow;
		else if (button == 3) // Right click
			badGuyPoint = new Point(x, y);
	}

	@Override
	public void mouseReleased(int x, int y, int button) {
		mouseUnaction();
	}

	@Override
	public void mouseEntered(int x, int y, int button) {
		mouseUnaction();
	}

	@Override
	public void mouseExited(int x, int y, int button) {
		mouseUnaction();
	}

	class SparkParent {
		double x, y;
		double death;
		double lastHit = -1;

		SparkParent(Point p) {
			x = p.x;
			y = p.y;
			death = System.currentTimeMillis() + duration;
		}

		boolean canHit() {
			return lastHit + 600 <= System.currentTimeMillis();
		}

		void hit(double damage) {
			lastHit = System.currentTimeMillis();
			synchronized (hits) {
				hits.add(new Hit(damage));
			}
		}
	}

	class Spark {
		double speed, x, y, angle, size, sizeView;
		Color color;
		SparkParent parent;
		int pierceLeft, forkLeft;
		double damage = damagePerHit;

		Spark(SparkParent parent) {
			this.parent = parent;
			x = parent.x;
			y = parent.y;
			size = sparkSize;
			sizeView = 5;
			speed = sparkSpeed;
			pierceLeft = pierce;
			forkLeft = fork;
			color = Color.BLUE;
		}

		void draw(Graphics2D g) {
			Color before = g.getColor();
			g.setColor(color);
			g.drawRect((int) (x - sizeView / 2), (int) (y - sizeView / 2), (int) (sizeView), (int) (sizeView));
			g.setColor(before);
		}

		boolean tick() {
			return tick(0);
		}

		boolean tick(int loopPrevention) {
			if (System.currentTimeMillis() >= parent.death)
				return false;
			else if (loopPrevention > sparkLoopPrevention)
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

			if (canHit() && (badGuy.contains(minX, minY) || badGuy.contains(maxX, minY) || badGuy.contains(minX, maxY)
					|| badGuy.contains(maxX, maxY))) {
				hit();
			}
			if (!area.contains(minX, minY) || !area.contains(maxX, minY) || !area.contains(minX, maxY)
					|| !area.contains(maxX, maxY)) {
				setRandomDirection();
				tick(++loopPrevention);
			} else {
				x = newX;
				y = newY;
			}
			return true;
		}

		void setRandomDirection() {
			angle = Tools.random(dps, 0, 360, "Spark Angle");
		}

		Point getPoint() {
			return new Point((int) x, (int) y);
		}

		boolean canHit() {
			return parent.canHit();
		}

		void hit() {
			parent.hit(damage);
			pierceLeft--;
			if (pierceLeft < 0 && forkLeft >= 0) {
				forkLeft--;
				fork();
				// Kill self as we have forked
				forkLeft = -1;
			}
		}

		void fork() {
			Spark spark = new Spark(parent);
			spark.pierceLeft = 0;
			spark.forkLeft = forkLeft;
			spark.x = x;
			spark.y = y;
			spark.angle = angle - 120;
			sparks.add(spark);

			Spark spark2 = new Spark(parent);
			spark2.pierceLeft = 0;
			spark2.forkLeft = forkLeft;
			spark2.setRandomDirection();
			spark2.x = x;
			spark2.y = y;
			spark2.angle = angle + 120;
			sparks.add(spark2);
		}
	}

	static class MapArea extends Area {
		enum Map {
			ORIATH_DOCKS, SQUARE;
		}

		MapArea(Map map) {
			switch (map) {
			case ORIATH_DOCKS:
				addPoint(-1500, 310);
				addPoint(dps.getWidth() - 300, 310);
				addPoint(dps.getWidth() - 300, -200);
				addPoint(dps.getWidth() - 100, -200);
				addPoint(dps.getWidth() - 100, dps.getHeight() + 300);
				addPoint(dps.getWidth() - 300, dps.getHeight() + 300);
				addPoint(dps.getWidth() - 300, 450);
				addPoint(-1500, 450);
				break;
			case SQUARE:
				addPoint(20, 20);
				addPoint(20, dps.getHeight() - 20);
				addPoint(dps.getWidth() - 20, dps.getHeight() - 20);
				addPoint(dps.getWidth() - 20, 20);
				break;
			}
		}
	}

	static class BadGuy extends Area {
		BadGuy(Point startLocation) {
			int startX = startLocation.x;
			int startY = startLocation.y;
			int size = 50;
			addPoint(startX - size / 2, startY - size / 2);
			addPoint(startX - size / 2, startY + size / 2);
			addPoint(startX + size / 2, startY + size / 2);
			addPoint(startX + size / 2, startY - size / 2);
			color = Color.GREEN;
		}
	}

	static class Area {
		Polygon polygon = new Polygon();
		Color color;

		void addPoint(int x, int y) {
			polygon.addPoint(x, y);
		}

		void addPoint(Point p) {
			addPoint(p.x, p.y);
		}

		void draw(Graphics2D g) {
			Color before = g.getColor();
			g.setColor(color);
			g.draw(polygon);
			g.setColor(before);
		}

		boolean contains(Spark spark) {
			return polygon.contains(spark.getPoint());
		}

		boolean contains(Point p) {
			return polygon.contains(p);
		}

		boolean contains(double x, double y) {
			return polygon.contains(new Point((int) x, (int) y));
		}
	}
}