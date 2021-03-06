package com.github.vegeto079.PoESparkDPSCalculator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.github.vegeto079.PoESparkDPSCalculator.areas.Area;
import com.github.vegeto079.PoESparkDPSCalculator.areas.PathOfExileMap;
import com.github.vegeto079.PoESparkDPSCalculator.objects.Hit;
import com.github.vegeto079.PoESparkDPSCalculator.objects.Spark;
import com.github.vegeto079.PoESparkDPSCalculator.objects.SparkParent;
import com.github.vegeto079.ngcommontools.main.Game;
import com.github.vegeto079.ngcommontools.main.Logger;

public class SparkDPSCalculator extends Game {
	private static final long serialVersionUID = 3419048634045147821L;
	
	public SparkOptions options = new SparkOptions();

	public SparkDPSCalculator(Logger logger, String[] args, int ticksPerSecond, int paintTicksPerSecond, String title,
			int width, int height) {
		super(logger, args, ticksPerSecond, paintTicksPerSecond, title, width, height);
		
        SwingUtilities.invokeLater(() -> options.setVisible(true));
	}

	static SparkDPSCalculator dps = null;

	public static void main(String[] args) {
		dps = new SparkDPSCalculator(new Logger(true), args, 100, 120, "Spark DPS Calculator", 800, 800);
		dps.startThread();
	}

	public ArrayList<Spark> sparks = new ArrayList<Spark>();

	double expectedDamage = 0;

	int count = 0;
	double maxSparks = 1;
	public Area area = null;
	public Area badGuy = null;
	Point badGuyPoint = new Point(600, 600);
	PathOfExileMap loadedMap = null;

	long start = System.currentTimeMillis();
	ArrayList<Hit> hits = new ArrayList<Hit>();
	double avgHitsPerSecond = -1;
	double avgDamagePerSecond = -1;
	DecimalFormat decimalFormat = new DecimalFormat("#");
	DecimalFormat decimalFormat2 = new DecimalFormat("#.#");
	boolean shootNow = true;
	
	public final int sparkLoopPrevention = 50; // If a Spark hits a wall more than this many times, kill it. It's probably stuck in a wall
	final long sparkHitTimeout = 660; // If a Spark hits an enemy, all Sparks in that cast cannot hit for 0.66s

	@Override
	public void paintTick(Graphics2D g) {
		super.paintTick(g);
		if (area != null) {
			if (mousePoint != null)
				g.drawOval(mousePoint.x - 10, mousePoint.y - 10, 20, 20);
			area.draw(g);
			if(badGuy != null) badGuy.draw(g);

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
			long done = start + options.timeToAverageBy - System.currentTimeMillis();
			if (done < 0) {
				start = System.currentTimeMillis();
			}
			g.drawString("fps: " + this.getFps() + ", ups: " + this.getUps(), 25, y += 20);
			g.drawString("castRate: " + options.castRate + ", projectiles: " + options.proj, 25, y += 40);
			g.drawString("pierce: " + options.pierce + ", fork: " + options.fork, 25, y += 20);
			g.drawString("duration: " + options.duration, 25, y += 20);
		}
	}

	@Override
	public void gameTick() {
		super.gameTick();
		expectedDamage = options.damagePerHit * options.castRate;
		maxSparks = 100 / options.castRate;
		if (area == null || loadedMap != options.selectedMap) {
			sparks.clear();
			badGuy = createBadGuy(badGuyPoint);
			area = createAreaByMap(options.selectedMap);
			mousePoint = new Point(getWidth() / 2, getHeight() / 2);
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);
			decimalFormat2.setGroupingUsed(true);
			decimalFormat2.setGroupingSize(3);
			loadedMap = options.selectedMap;
		}
		if (badGuy == null) badGuy = createBadGuy(badGuyPoint);
		if (shootNow) {
			count++;
			if (count >= maxSparks) {
				addSparks(options.proj);
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
					if (hits.get(i).time + options.timeToAverageBy <= System.currentTimeMillis()) {
						hits.remove(i);
						i--;
					} else {
						newAvgHitsPerSecond++;
						newAvgDpsPerSecond += hits.get(i).damage;
					}
				}
				try {
					avgHitsPerSecond = newAvgHitsPerSecond / ((double) options.timeToAverageBy / 1000d);
				} catch (Exception e) {
					avgHitsPerSecond = 0;
				}
				try {
					avgDamagePerSecond = newAvgDpsPerSecond / ((double) options.timeToAverageBy / 1000d);
				} catch (Exception e) {
					avgDamagePerSecond = 0;
				}
			}
		}
	}

	void addSparks(int amount) {
		if (mousePoint != null)
			synchronized (sparks) {
				SparkParent parent = new SparkParent(mousePoint, options.duration, sparkHitTimeout);
				int amt = 360 / amount;
				int currentAngle = 0;
				while (amount > 0) {
					Spark spark = new Spark(this, parent);
					spark.setAngle(currentAngle);
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
	
	void mouseAction(int x, int y, int button) {
		if (button == 1) // Left Click
			mouseAction(x, y);
		else if (button == 2)
			shootNow = !shootNow;
		else if (button == 3) { // Right click
			badGuyPoint = new Point(x, y);
			badGuy = null;
		}
	}

	@Override
	public void mouseDragged(int x, int y, int button, int xOrigin, int yOrigin) {
		mouseAction(x, y, button);
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
		mouseAction(x, y, button);
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


	public Area createAreaByMap(PathOfExileMap map) {
		Area area = new Area();
		switch (map) {
		case ORIATH_DOCKS:
			area.addPoint(-1500, 310);
			area.addPoint(dps.getWidth() - 300, 310);
			area.addPoint(dps.getWidth() - 300, -200);
			area.addPoint(dps.getWidth() - 100, -200);
			area.addPoint(dps.getWidth() - 100, dps.getHeight() + 300);
			area.addPoint(dps.getWidth() - 300, dps.getHeight() + 300);
			area.addPoint(dps.getWidth() - 300, 450);
			area.addPoint(-1500, 450);
			break;
		case SQUARE:
			area.addPoint(20, 20);
			area.addPoint(20, dps.getHeight() - 20);
			area.addPoint(dps.getWidth() - 20, dps.getHeight() - 20);
			area.addPoint(dps.getWidth() - 20, 20);
			break;
		}
		return area;
	}
	
	public Area createBadGuy(Point startLocation) {
		Area area = new Area();
		int startX = startLocation.x;
		int startY = startLocation.y;
		int size = 50;
		area.addPoint(startX - size / 2, startY - size / 2);
		area.addPoint(startX - size / 2, startY + size / 2);
		area.addPoint(startX + size / 2, startY + size / 2);
		area.addPoint(startX + size / 2, startY - size / 2);
		area.setColor(Color.GREEN);
		return area;
	}
	
	public void AddHit() {
		synchronized (hits) {
			hits.add(new Hit(options.damagePerHit));
		}
	}
}