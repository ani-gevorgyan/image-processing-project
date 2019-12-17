import ij.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class Compute_Moments implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		return DOES_8G;
	}

	public void run(ImageProcessor ip) {
		double moment = moment(ip, 3, 3);
		double centralMoment = centralMoment(ip, 3, 3);
		double nCentralMoment = nCentralMoment(ip, 3, 3);
		double orientation = orientation(ip);
		double eccentricity = eccentricity(ip);

		IJ.log("Moment: " + moment);
		IJ.log("Central Moment: " + centralMoment);
		IJ.log("Normalized Central Moment: " + nCentralMoment);
		IJ.log("Orientation: " + orientation);
		IJ.log("Eccentricity: " + eccentricity);
	}

public double orientation(ImageProcessor I){
	return Math.toDegrees(1/2 * Math.atan2(centralMoment(I, 2, 0) - centralMoment(I, 0, 2), 2 * centralMoment(I, 1, 1)));
}

public double eccentricity(ImageProcessor I) {
	double numerator = centralMoment(I, 2, 0) + centralMoment(I, 0, 2) + Math.sqrt(Math.pow(centralMoment(I, 2, 0) - centralMoment(I, 0, 2), 2) + 4 * Math.pow(centralMoment(I, 1, 1), 2));
	double denominator = centralMoment(I, 2, 0) + centralMoment(I, 0, 2) - Math.sqrt(Math.pow(centralMoment(I, 2, 0) - centralMoment(I, 0, 2), 2) + 4 * Math.pow(centralMoment(I, 1, 1), 2));
	return numerator/denominator;
}

	
	//taken from the textbook
	double moment(ImageProcessor I, int p, int q) {
		double Mpq = 0.0;
		for (int v = 0; v < I.getHeight(); v++) {
			for (int u = 0; u < I.getWidth(); u++) {
				if (I.getPixel(u, v) > 0) {
					Mpq+= Math.pow(u, p) * Math.pow(v, q);
				}
			}
		}
	 	return Mpq;
	 }
	 // Central moments: taken from the textbook

	 double centralMoment(ImageProcessor I, int p, int q) {
	 	double m00 = moment(I, 0, 0); // region area
	 	double xCtr = moment(I, 1, 0) / m00;
	 	double yCtr = moment(I, 0, 1) / m00;
	 	double cMpq = 0.0;
	 	for (int v = 0; v < I.getHeight(); v++) {
	 		for (int u = 0; u < I.getWidth(); u++) {
	 			if (I.getPixel(u, v) > 0) {
	 				cMpq+= Math.pow(u-xCtr, p) * Math.pow(v-yCtr, q);
	 			}
	 		}
	 	}
	 	return cMpq;
	 }

	 // Normalized central moments: taken from the textbook

	 double nCentralMoment(ImageProcessor I, int p, int q) {
	 	double m00 = moment(I, 0, 0);
	 	double norm = Math.pow(m00, 0.5 * (p + q + 2));

	 	return centralMoment(I, p, q) / norm;
	 }

}
