/*
 * @project common
 * @author raiser
 * @creation time Oct 13, 2003.11:25:25 AM
 */
package raiser.util;

import javax.swing.Icon;

/**
 * Util class.
 */
public class Randomizer {
	public static String generateWord(int minSize, int maxSize) {
		return "randomWord" + Math.random();
	}

	public static Icon generateIcon(int width, int height, int shapeCount,
			int colorCount) {
		return new RandomizeIcon(width, height, shapeCount, colorCount);
	}

	/**
	 * Generates a unique long for this virtual machine.
	 */
	public static long generateUniqueId() {
		return uniqueId++;
	}

	private static long uniqueId = 0;
}
