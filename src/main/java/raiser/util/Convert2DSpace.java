/**
 * @project 17.pixel
 * @author cgrigore
 * @date Jul 26, 2002
 *
 * Copyright (c) 2002 Softwin SRL, Romania, Bucharest, All Rights Reserved.
 */
package raiser.util;

/**
 * @author cgrigore
 */
public class Convert2DSpace {
	public Convert2DSpace(final double realxmin, final double realymin,
			final double realxmax, final double realymax,
			final double devicexmin, final double deviceymin,
			final double devicexmax, final double deviceymax) {
		this.realxmin = realxmin;
		this.realymin = realymin;
		this.realxmax = realxmax;
		this.realymax = realymax;
		this.devicexmin = devicexmin;
		this.deviceymin = deviceymin;
		this.devicexmax = devicexmax;
		this.deviceymax = deviceymax;
	}

	/**
	 * Method x2device.
	 * 
	 * @param d
	 */
	public double x2device(final double realx) {
		return convert(realx, realxmin, realxmax, devicexmin, devicexmax);
	}

	/**
	 * Method y2device.
	 * 
	 * @param d
	 */
	public double y2device(final double realy) {
		return convert(realy, realymin, realymax, deviceymin, deviceymax);
	}

	/**
	 * Method convert.
	 * 
	 * @param realx
	 * @param realxmin
	 * @param realxmax
	 * @param devicexmin
	 * @param devicexmax
	 * @return double
	 */
	private double convert(final double x, final double x1, final double x2,
			final double y1, final double y2) {
		final double y = y1 + (y2 - y1) * (x - x1) / (x2 - x1);
		return y;
	}

	double realxmin;

	double realymin;

	double realxmax;

	double realymax;

	double devicexmin;

	double deviceymin;

	double devicexmax;

	double deviceymax;
}
