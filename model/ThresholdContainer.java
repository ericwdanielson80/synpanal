package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Color;
import java.awt.image.*;

/**
 * Maintains a per-intensity pixel index for one color band of a TIFF image
 * and efficiently repaints an overlay BufferedImage when the threshold is
 * moved. At construction it bins every pixel index by its intensity in the
 * chosen channel so subsequent thresholdArray calls only have to visit
 * pixels in the intensity range between the old and new threshold, rather
 * than the entire image. The container is typically driven by a slider or
 * combobox in the threshold options UI.
 */
public class ThresholdContainer {
int[][] thresholdIndexes;
public int oldTh; 
int band;
BufferedImage thresholdImage; //the image for thresholding
public Color c;



//possible modify so that this is a slider listener and a combobox listener

	/**
	 * Builds the intensity-to-pixel index. The colors parameter is the flat
	 * 32-bit ARGB pixel array for the source image; bitOp is the channel
	 * mask (for example 0x00FF0000 for red) and shift is the right-shift
	 * needed to reduce the masked value to a 0..255 intensity. The tI
	 * parameter is the overlay BufferedImage whose raster will be mutated
	 * during thresholdArray calls; Band is the channel index used for later
	 * reference; thColor is the Color used to paint through-threshold
	 * pixels. The algorithm first counts how many pixels fall at each
	 * intensity (counter[]), then allocates per-intensity index arrays
	 * thresholdIndexes[k] of that size, then makes a second pass recording
	 * each pixel's flat index into its bin. oldTh is seeded to 256 so the
	 * first thresholdArray call paints every pixel from threshold..255.
	 */
	public ThresholdContainer(int[] colors,int bitOp,int shift,BufferedImage tI,int Band, Color thColor)
	{
		oldTh = 256;
		int[] counter = new int[256];
		int intensity;
		band = Band;
		thresholdImage = tI;
		for(int k = 0; k < colors.length; k++)
		{
			intensity = (bitOp & colors[k])>>shift;
		
			counter[intensity]++;		
		}
		thresholdIndexes = new int[256][];
		for(int k = 0;k < thresholdIndexes.length; k++)
		{
			thresholdIndexes[k] =  new int[counter[k]];
			counter[k] = 0;
		}
		for(int k = 0; k < colors.length; k++)
		{
			intensity = (bitOp & colors[k])>>shift;
			thresholdIndexes[intensity][counter[intensity]] = k;
			counter[intensity]++;
		}		
		c = thColor;
	}
	
	/**
	 * Re-paints the overlay raster so that pixels with intensity at or above
	 * the new threshold are drawn in the configured color and those below
	 * are transparent. When threshold is lower than oldTh, every pixel in
	 * the newly-included range [threshold, oldTh) is painted with the
	 * color's RGBA; when threshold is higher, the pixels in the now-excluded
	 * range [oldTh, threshold) are cleared to transparent. The local t is
	 * the flat pixel index retrieved from the intensity bin, and w/h are
	 * its column and row derived by dividing by the image width. oldTh is
	 * updated at the end so the next call only scans the delta.
	 */
	public void thresholdArray(int threshold)
	{
		WritableRaster r = thresholdImage.getRaster();
		int t;
		int w;
		int h;
		
		
		
		if(threshold < oldTh){
		for(int k = threshold; k < oldTh; k++)
		{
			for(int j = 0; j < thresholdIndexes[k].length; j++)
			{
				t = thresholdIndexes[k][j];
				w = t % thresholdImage.getWidth();
				h = t / thresholdImage.getWidth();
				r.setSample(w, h, 3, c.getAlpha());
				r.setSample(w, h, 0, c.getRed());
				r.setSample(w, h, 1, c.getGreen());
				r.setSample(w, h, 2, c.getBlue());
				
			}
		}		
		
		}
		else if(threshold > oldTh){
		for(int k = oldTh; k < threshold; k++)
		{
			for(int j = 0; j < thresholdIndexes[k].length; j++)
			{
				t = thresholdIndexes[k][j];
				w = t % thresholdImage.getWidth();
				h = t / thresholdImage.getWidth();
				r.setSample(w, h, 3, 0);
				r.setSample(w, h, 0, 0);
				r.setSample(w, h, 1, 0);
				r.setSample(w, h, 2, 0);				
			}
		}
		}
		
		oldTh = threshold;
		
		
	}
	
		
	/**
	 * Changes the color used to paint through-threshold pixels on subsequent
	 * thresholdArray calls.
	 */
	public void setColor(Color Color)
	{
		c = Color;
	}

}
