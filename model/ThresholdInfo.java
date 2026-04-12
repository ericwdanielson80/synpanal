package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.image.*;
import java.util.Stack;
/**
 * Cached per-intensity pixel index for one color channel of an image,
 * used to render threshold overlays incrementally. reset builds a
 * data[intensity][ordinal] table that maps each possible 8-bit value
 * (0-255) to the packed (y*width + x) positions of every pixel with
 * that intensity in the source Raster. Threshold then walks only the
 * bins that cross the previous/new threshold boundary and applies a
 * color to those pixels in a WritableRaster, making repeated threshold
 * changes cheap.
 */
public class ThresholdInfo {
int[][] data;
int type;
int currentTh;
int width;
int height;
boolean[] thresholded = new boolean[256];

    /**
     * Creates an uninitialised ThresholdInfo. The type field is set to
     * -1 so callers know no channel is bound until reset is called.
     */
    public ThresholdInfo()
    {
        type = -1;
    }

    /**
     * Convenience constructor that immediately indexes the pixels of
     * Raster r for the rgb channel by delegating to reset.
     */
    public ThresholdInfo(Raster r, int rgb) {

        reset(r,rgb);

    }
    /**
     * Reindexes this ThresholdInfo against a new raster. It records
     * the channel index and image dimensions, then makes a two-pass
     * scan: the first pass fills counter[v] with the number of pixels
     * that hold intensity v so data[v] can be sized exactly; the
     * second pass fills each data[th] slot, with counter2 tracking the
     * current write index per bucket, and each pixel encoded as
     * (j*width + k). currentTh is set to 256 to represent "nothing
     * thresholded yet". The parameters are the source Raster r and
     * the channel index rgb.
     */
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


    /**
     * Applies a threshold change to the overlay raster r incrementally.
     * The new threshold th is clamped to [0,256]. When th is lower
     * than the cached currentTh the method paints every pixel in bins
     * [th, currentTh) with color, effectively revealing more of the
     * image; when th is greater, it walks bins [currentTh, th) and
     * writes color (with color[3] set to 0 first to clear alpha),
     * effectively hiding pixels now below the threshold. In either
     * case the packed pixel index mult is split back into x=mult%width
     * and y=mult/width before the setPixel call. The parameters are
     * the WritableRaster r being updated, the new threshold th, and
     * the int[] color (RGBA) to stamp.
     */
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

    /**
     * Unpacks the index-th pixel stored in bin th back into an (x,y)
     * coordinate pair. The local mult is the packed value taken from
     * data[th][index], xy[1] is the row (mult/width) and xy[0] the
     * column (mult - row*height). Returns a two-element {x,y} array.
     */
    public int[] getXY(int th, int index)
    {
     int[] xy = new int[2];
     int mult = data[th][index];
     xy[1] = (mult / width);
     xy[0] = (mult - (xy[1] * height));
     return xy;

    }

}
