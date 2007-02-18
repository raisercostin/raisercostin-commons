/*
 * Created on Oct 28, 2003
 */
package raiser.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.Icon;

/**
 * @author raiser
 */
public class RandomizeIcon implements Icon {
	private int width;

	private int height;

	private Shape[] shapes;

	private Color[] colors;

	public RandomizeIcon(int width, int height, int shapeCount, int colorCount) {
		this.width = width;
		this.height = height;
		colors = new Color[shapeCount];
		Color[] colors2 = new Color[colorCount];
		for (int i = 0; i < colors2.length; i++) {
			colors2[i] = new Color((int) (Math.random() * Integer.MAX_VALUE));
		}
		shapes = new Shape[shapeCount];
		for (int i = 0; i < shapes.length; i++) {
			Shape shape = null;
			switch ((int) (Math.random() * 2)) {
			case 0:
				shape = new Rectangle(randomWidth(), randomHeight(),
						randomWidth(), randomHeight());
				break;
			case 1:
				shape = new Ellipse2D.Double(randomWidth(), randomHeight(),
						randomWidth(), randomHeight());
			}
			shapes[i] = shape;
			colors[i] = colors2[(int) (Math.random() * colorCount)];
		}
	}

	private int randomHeight() {
		return (int) (Math.random() * height);
	}

	private int randomWidth() {
		return (int) (Math.random() * width);
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		Shape oldClip = g2.getClip();
		g2.setClip(x, y, width, height);
		g2.translate(x, y);
		for (int i = 0; i < shapes.length; i++) {
			g2.setColor(colors[i]);
			g2.fill(shapes[i]);
		}
		g2.translate(-x, -y);
		g2.setColor(Color.black);
		g2.drawRect(x, y, width - 1, height - 1);
		g2.setClip(oldClip);
	}
}
