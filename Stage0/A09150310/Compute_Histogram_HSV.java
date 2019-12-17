import ij.*;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.plugin.filter.PlugInFilter;
import java.awt.Color;

public class Compute_Histogram_HSV implements PlugInFilter {

  public int setup(String arg, ImagePlus imp) {
    return DOES_RGB;
  }

  public void run(ImageProcessor ip) {

    float[][] hsvRef = convert2HSV(ip);

    float[] hRef = hsvRef[0];
    float[] sRef = new float[101];
    float[] vRef = new float[101];

    for(int i = 0; i < 100; i++) {
      sRef[i] = hsvRef[1][i];
      vRef[i] = hsvRef[2][i];
    }


    ImagePlus imp2= IJ.openImage("C:\\Users\\Ani\\Documents\\Image Processing\\final_project\\images\\26-11.jpg");
    ImageProcessor ip2 = imp2.getProcessor();

    float[][] hsv = convert2HSV(ip2);

    float[] h = hsv[0];
    float[] s = new float[101];
    float[] v = new float[101];

    for(int i = 0; i < 100; i++) {
      s[i] = hsv[1][i];
      v[i] = hsv[2][i];
    }

    matchHistogramHSV(ip2, h, s, v, hRef, sRef, vRef);

}



float[][] convert2HSV(ImageProcessor I) {
  float[] hHist = new float[361];
  float[] sHist = new float[101];
  float[] vHist = new float[101];
  float[] hsv = new float[3];

  Color color;

  for(int i = 0; i < I.getWidth(); i++) {
    for(int j = 0; j < I.getHeight(); j++) {
      color = new Color(I.getPixel(i, j));
      int red = color.getRed();
      int green = color.getGreen();
      int blue = color.getBlue();
      hsv = Color.RGBtoHSB(red, green, blue, null);

      hHist[(int) (hsv[0] * 360)] ++;
      sHist[(int) (hsv[1] * 100)] ++;
      vHist[(int) (hsv[2] * 100)] ++;
    }
  }


   for(int i = 1; i < 100; i++) {
      sHist[i] += sHist[i-1];
      vHist[i] += vHist[i-1];
  }

  for(int i = 1; i< 360; i++) {
     hHist[i] += hHist[i-1];
  }

  for(int i = 0; i < 100; i++) {
    sHist[i] /= sHist[99];
    // IJ.log(sHist[i] + "");
    vHist[i] /= vHist[99];
    IJ.log(vHist[i] + "");
  }

  for(int i = 0; i < 360; i++) {
    hHist[i] /= hHist[359];
    // IJ.log(hHist[i] + "");
  }

  float[][] result = new float[3][360];
  result[0] = hHist;
  result[1] = sHist;
  result[2] = vHist;

  return result;

}



 void matchHistogramHSV(ImageProcessor I, float[] hist1H, float[] hist1S, float[] hist1V, float[] hist2H,  float[] hist2S, float[] hist2V) {
   Color color;
   for(int i = 0; i < I.getWidth(); i++) {
     for(int j = 0; j < I.getHeight(); j++) {
        color = new Color(I.getPixel(i, j));
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color. getBlue();
        float[] hsv = Color.RGBtoHSB(red, green, blue, null);

        int hNew = 360;
        do {
          hNew--;
        } while (hNew >= 0 && hist1H[(int)(hsv[0]*360)] <= hist2H[hNew]);

        int sNew = 100;
        do {
          sNew--;
        } while (sNew >= 0 && hist1S[(int)(hsv[1]*100)] <= hist2S[sNew]);

        int vNew = 100;
        do {
          vNew--;
        } while (vNew >= 0 && hist1V[(int)(hsv[2] * 100)] <= hist2V[vNew]);

        int rgb = Color.HSBtoRGB((float)(hNew / 360), (float)(sNew / 100), (float) (vNew / 100));

        I.putPixelValue(i, j, rgb);

     }
   }
   new ImagePlus("HSV", I).show();
 }
}
