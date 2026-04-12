package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.awt.Rectangle;
import java.awt.Shape;
/**
 * Pixel-level spatial index that tracks which puncta from each of the red,
 * green, and blue channels overlap within a bounding rectangle. The two 2-D
 * arrays are a grid of OverlapObject references (one per pixel inside the
 * rectangle) plus an ownershipArray used by the spine restoration logic to
 * give the closest spine priority over a shared pixel. Together these
 * structures power auto-ignore decisions and the spine-area puncta
 * restoration used when reassigning puncta back to the nearest dendrite
 * spine shape.
 */
public class OverlapObjectArray {
OverlapObject[][] overlapArray;
int[][] ownershipArray;
int x,y;
Puncta[][] rgb = new Puncta[3][];

	/**
	 * Allocates the per-pixel overlap and ownership arrays sized to the
	 * supplied bounding rectangle r. The rectangle's top-left corner is
	 * stored in fields x and y so that absolute puncta coordinates can be
	 * normalized to array indices later.
	 */
	public OverlapObjectArray(Rectangle r)
	{
		overlapArray = new OverlapObject[r.width][r.height];
		ownershipArray = new int[r.width][r.height];
		x = r.x; //need this to normalize puncta positions
		y = r.y; //need this to normalize puncta positions
	}
	
	/**
	 * Loads the red-channel puncta into the overlap grid, optionally marking
	 * them as ignored as they are added. The parameter p is the puncta list
	 * and ignore forces bC.isIgnored to true on each puncta when set.
	 */
	public void loadRed(Puncta[] p,boolean ignore)
	{
		loadPuncta(0,p,ignore);
		rgb[0] = p;
	}
	
	/** Loads the green-channel puncta into the overlap grid; see loadRed for parameter semantics. */
	public void loadGreen(Puncta[] p,boolean ignore)
	{
		loadPuncta(1,p,ignore);
		rgb[1] = p;
	}
	
	/** Loads the blue-channel puncta into the overlap grid; see loadRed for parameter semantics. */
	public void loadBlue(Puncta[] p,boolean ignore)
	{
		loadPuncta(2,p,ignore);
		rgb[2] = p;
	}
	
	/**
	 * Clears the per-puncta ownership bookkeeping for the requested color so
	 * that spine assignments can be recomputed; each surviving puncta is
	 * given a fresh OwnershipObject.
	 */
	private void resetOwnership(int color)
	{
		for(int k = 0; k < rgb[color].length; k++)
		{
			if(rgb[color][k]!=null)
				rgb[color][k].oO = new OwnershipObject();
		}
	}
	
	/**
	 * Walks the puncta array for one color channel, and for each puncta
	 * iterates the bounding rectangle of its border and tags every pixel
	 * inside the border shape in the overlap grid. The parameters are the
	 * color index, the puncta array p, and the ignore flag that optionally
	 * marks puncta as ignored; the local r is the per-puncta bounding
	 * rectangle used for the pixel scan.
	 */
	private void loadPuncta(int color, Puncta[] p, boolean ignore)
	{
		Rectangle r;
		for(int k = 0; k < p.length; k++)
		{			
			if(p[k] != null)
			{
				if(ignore)
					p[k].bC.isIgnored = true;
				
				r = p[k].border.getBounds();
				for(int j = r.y; j < r.y + r.height; j++)
				{
					for(int l = r.x ; l < r.x + r.width; l++)
					{
						if(p[k].border.contains(l,j))
						{
							if(overlapArray[l - x][j - y] == null)
							overlapArray[l - x][j - y] = new OverlapObject();
							
							overlapArray[l-x][j-y].rgb[color] = p[k];
						}
					}
				}
			}
		}
	}
	
	/**
	 * Applies the per-pixel overlap-restoration rules to every populated cell
	 * of the overlapArray. For each cell it delegates to OverlapObject.restore
	 * passing the three restore flags (which colors are allowed to be
	 * restored) and the three ifRed/ifGreen/ifBlue flags (which overlapping
	 * colors trigger restoration).
	 */
	public void autoIgnore(boolean restoreRed, boolean restoreGreen, boolean restoreBlue, boolean ifRed, boolean ifGreen, boolean ifBlue)
	{
		for(int k = 0; k < overlapArray[0].length; k++)
		{
			for(int j = 0; j < overlapArray.length; j++)
			{
				 if(overlapArray[j][k] != null)
				 {
					 overlapArray[j][k].restore(restoreRed, restoreGreen, restoreBlue, ifRed, ifGreen, ifBlue);
				 }
			}
		}
	}
	
	/**
	 * Restores puncta of the given color channel that fall inside any of the
	 * provided spine shapes, and groups them by which spine they belong to.
	 * The method first rebuilds the ownership priority grid, then for every
	 * shape it scans the pixels inside the shape, unignores any puncta found
	 * in those pixels, and records which spine claimed which puncta via the
	 * puncta's OwnershipObject. After the scan it counts how many puncta ended
	 * up owned by each spine, allocates the result sub-arrays sized to match,
	 * and fills them by walking the puncta list again. The parameter shapes
	 * is the list of spine areas (typically small circles centered on spines)
	 * and color is the channel index; the returned out array has one sub-array
	 * per shape containing the puncta restored to that spine.
	 */
	public Puncta[][] spineAreaRestore(Shape[] shapes, int color)
	{
		/*
		 * autoIgnore array must be created
		 * then this function will take a list of (circle) shapes
		 * a for loop will be created for the bounds of each shape (union of both)
		 * if the puncta lies within the shape it will be restored.
		 */
		Rectangle r = new Rectangle(x,y,overlapArray.length,overlapArray[0].length);
		Rectangle un;
		int unX;
		int unY;
		int unW;
		int unH;
		Puncta[] list = new Puncta[10];
		int[] punctaNum = new int[shapes.length];
		Puncta[][] out = new Puncta[punctaNum.length][];
		
		resetOwnership(color);
		generateOwnerShipArray(shapes,r);
		
		for(int k = 0; k < shapes.length; k++)
		{
			un = r.union(shapes[k].getBounds());
			unX = un.x;
			unY = un.y;
			unW = un.width;
			unH = un.height;
			for(int j = 0; j < list.length; j++)
			{
				list[j] = null;
			}
			
			for(int yInc = unY; yInc < unY + unH; yInc++)
			{
				for(int xInc = unX; xInc < unX + unW; xInc++)
				{
					
					if(shapes[k].contains(xInc,yInc))
					{
						if(-1 < xInc - x && xInc - x < ownershipArray.length && -1 < yInc - y && yInc - y < ownershipArray[0].length)				
						{
						if(overlapArray[xInc - x][yInc - y] != null)
						{
							if(overlapArray[xInc - x][yInc - y].rgb[color] != null)
							{
								overlapArray[xInc - x][yInc - y].rgb[color].bC.isIgnored = false;
								/*if(isNewPuncta(list,overlapArray[xInc - x][yInc - y].rgb[color]))
									punctaNum[k]++;*/
								overlapArray[xInc - x][yInc - y].rgb[color].oO.addSpine(k, ownershipArray[xInc - x][yInc - y]);
								
							}
						}
						}
					}
					
				}
			}
			/*out[k] = new Puncta[punctaNum[k]];
			for(int l = 0; l < punctaNum[k]; l++)
			{
				out[k][l] = list[l];
			}*/
		}
		
		int[] counter = new int[shapes.length];
		for(int k = 0; k < rgb[color].length; k++)
		{
			if(rgb[color][k] != null)
			{
				if(rgb[color][k].oO.mySpine != -1)
				{
					counter[rgb[color][k].oO.mySpine]++;
				}
			}
		}
		
		for(int k = 0; k < out.length; k++)
		{
			out[k] = new Puncta[counter[k]];
		}
		
		for(int k = 0; k < rgb[color].length; k++)
		{
			if(rgb[color][k] != null)
			{
				if(rgb[color][k].oO.mySpine != -1)
				{
					counter[rgb[color][k].oO.mySpine]--;
					out[rgb[color][k].oO.mySpine][counter[rgb[color][k].oO.mySpine]] = rgb[color][k];					
				}
			}
		}
		
		return out;
	}
	
	/**
	 * Builds the ownershipArray of Manhattan-distance-like weights used to
	 * break ties when multiple spine shapes would claim the same puncta; for
	 * every pixel inside the union of each shape and the grid rectangle it
	 * stores a value that grows with distance from the shape center so
	 * smaller values indicate proximity and thus higher priority. The
	 * parameters are the array of spine shapes and the grid's enclosing
	 * rectangle r.
	 */
	private void generateOwnerShipArray(Shape[] shapes,Rectangle r)
	{	
		/*generates an array to determine which spine gets ownership of which puncta
		 *effectively shows the distance of the point from the radius
		 *smaller is closer so smaller numbers will get priority over larger numbers 
		 */
		
		Rectangle un;
		int unX;
		int unY;
		int unW;
		int unH;
		for(int k = 0; k < shapes.length; k++)
		{
			un = r.union(shapes[k].getBounds());
			unX = un.x;
			unY = un.y;
			unW = un.width;
			unH = un.height;
			
			for(int yInc = unY; yInc < unY + unH; yInc++)
			{
				for(int xInc = unX; xInc < unX + unW; xInc++)
				{	
					if(-1 < xInc - x && xInc - x < ownershipArray.length && -1 < yInc - y && yInc - y < ownershipArray[0].length)			
					{
						ownershipArray[xInc - x][yInc - y] = Math.abs((yInc - y) - (unH / 2)) + Math.abs((xInc - x) - (unX / 2));
					}
				}				
			}			
		}
	}
	
	/**
	 * Searches list for puncta p; if p is already present returns false, and
	 * if the first null slot is encountered the puncta is inserted there and
	 * true is returned. If the array is full the routine grows it by five
	 * slots (though in practice this should not happen). Used by
	 * spineAreaRestore to de-duplicate per-shape puncta lists.
	 */
	private boolean isNewPuncta(Puncta[] list, Puncta p)
	{
		/*
		 * searches list for p, if p is found returns false if p is not found adds to the list and returns true.
		 * if List is too small it is made larger (shouldn't happen)
		 */
		boolean b = false;
		
		for(int k = 0; k < list.length; k++)
		{
			
			if(list[k] == null)
			{
				list[k] = p;
				return true;
			}
			
			if(list[k].equals(p))
				return false;
			
			if(k == list.length - 1)
			{
				Puncta[] out = new Puncta[list.length + 5];
				System.arraycopy(list,0,out,0,list.length);
				list = out;
			}
			
		}
		
		return false;
	}
	

}
