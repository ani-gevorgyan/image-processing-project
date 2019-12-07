import ij.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class compute_Histogram implements PlugInFilter {

  public int setup(String arg, ImagePlus imp) {
    return DOES_RGB;
  }

  public void run(ImageProcessor ip) {
    double[][] refHist = histogram(ip);

    ImagePlus imp2 = IJ.openImage("C:\\Users\\Ani\\Desktop\\images\\25-11.jpg");
		ImageProcessor ip2 = imp2.getProcessor();
		double [][] actualHistogram = histogram(ip2);

    matchHistogram(ip2, actualHistogram, refHist);
  }

  double[][] histogram(ImageProcessor I) {
    double[] redHist = new double[256];
    double[] greenHist = new double[256];
    double[] blueHist = new double[256];

    Color color;
    for(int i = 0; i < I.getWidth(); i++) {
      for(int j = 0; j< I.getHeight(); j++) {
        color = new Color(I.getPixel(i, j));
        int red = color.getRed();
        redHist[red]++;
        int green = color.getGreen();
        greenHist[green]++;
        int blue = color.getBlue();
        blueHist[blue]++;
      }
    }
    for(int i = 1; i < 256; i++) {
      redHist[i] += redHist[i-1];
      greenHist[i] += greenHist[i-1];
      blueHist[i] += blueHist[i-1];
    }

    for(int i = 0; i < 256; i++) {
      redHist[i] /= redHist[255];
      IJ.log(redHist[i] + "");
      greenHist[i] /= greenHist[255];
      blueHist[i] /= blueHist[255];
    }

    double[][] result = new double[3][256];
    result[0] = redHist;
    result[1] = greenHist;
    result[2] = blueHist;
    return result;
  }

  void matchHistogram(ImageProcessor I, double[][] hist1, double[][] hist2) {
    Color color;
    for(int i = 0; i < I.getWidth(); i++) {
      for(int j = 0; j< I.getHeight(); j++) {
        color = new Color(I.getPixel(i, j));
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        int redNew = 255;
        while(hist1[0][red] < hist2[0][redNew]){
          redNew--;
        }
        int greenNew = 255;
        while(hist1[1][green] < hist2[1][greenNew]){
          greenNew--;
        }
        int blueNew = 255;
        while(hist1[2][blue] < hist2[2][blueNew]){
          blueNew--;
        }

        int[] newColor = {redNew, greenNew, blueNew};
        I.putPixel(i, j, newColor);
      }
    }

    new ImagePlus("Changed Hist", I).show(); 
  }
}
