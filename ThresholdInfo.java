package neuron_analyzer;
import java.awt.image.*;
import java.util.Stack;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ThresholdInfo {
int[][] data;
int type;
int currentTh;
int width;
int height;
boolean[] thresholded = new boolean[256];

    public ThresholdInfo()
    {
        type = -1;
    }

    public ThresholdInfo(Raster r, int rgb) {

        reset(r,rgb);

    }
    public void reset(Raster r, int rgb)
    {
        type = rgb;
        int[] counter = new int[256];
        width = r.getWidth();
        height = r.getHeight();
        for(int k = 0; k < r.getHeight(); k++)
        {
            for(int j = 0; j < r.getWidth(); j++)
            {
                counter[r.getSample(k,j,rgb)]++;
            }
        }
        data = new int[256][];
        for(int l = 0; l < data.length; l++)
        {
            data[l] = new int[counter[l]];
        }
        int[] counter2 = new int[256];
        int th;
        for(int k = 0; k < r.getWidth(); k++)
        {
            for(int j = 0; j < r.getHeight(); j++)
            {
                th = r.getSample(k,j,rgb);
                data[th][counter2[th]] = (j*r.getWidth())+k;
                counter2[th]++;
            }

        }
        currentTh = 256;
        System.gc();
    }


    public WritableRaster Threshold(WritableRaster r, int th, int[] color)
    {


        if(th > 256)
           {
               th = 256;
           }
        if(th < 0)
        {
               th = 0;
        }

        int x;
        int y;
        int mult;
        int max = 256;
        if(th < currentTh)
        {
            max = currentTh;
            for (int k = th; k < max; k++) {

                if (data[k] != null) {
                    for (int j = 0; j < data[k].length; j++) {
                        mult = data[k][j];
                        y = (mult / r.getWidth());
                        x = (mult - (y * r.getWidth()));

                        r.setPixel(x, y, color);
                    }
                }
            }

        }
        else
        {
            if(th >= currentTh)
            {

                color[3] = 0;
//System.gc();
                for (int k = currentTh; k < th; k++) {

                if (data[k] != null) {
                    for (int j = 0; j < data[k].length; j++) {
                        mult = data[k][j];
                        y = (mult / r.getWidth());
                        x = (mult - (y * r.getWidth()));

                        r.setPixel(x, y, color);
                    }
                }
            }


            }
        }

        currentTh = th;


        return r;
    }

    public int[] getXY(int th, int index)
    {
     int[] xy = new int[2];
     int mult = data[th][index];
     xy[1] = (mult / width);
     xy[0] = (mult - (xy[1] * height));
     return xy;

    }

}
