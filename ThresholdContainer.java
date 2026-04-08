package neuron_analyzer;
import java.awt.Color;
import java.awt.image.*;

public class ThresholdContainer {
int[][] thresholdIndexes;
int oldTh; 
int band;
BufferedImage thresholdImage; //the image for thresholding
Color c;



//possible modify so that this is a slider listener and a combobox listener

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
	
		
	public void setColor(Color Color)
	{
		c = Color;
	}

}
