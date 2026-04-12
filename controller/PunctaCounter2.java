package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;

/**
 * Scaffolding for a second-generation puncta counter that works directly
 * with AWT geometry (an Area describing a region of interest) and a
 * Marker (which supplies pixel data and channel metadata). The current
 * implementation is a skeleton: the storage for grouped puncta IDs lives
 * in the groupList two-dimensional array, and countPuncta merely derives
 * the bounding rectangle of the supplied Area without performing the
 * count yet.
 */
public class PunctaCounter2 {
int[][] groupList;

	/**
	 * No-argument constructor that leaves the counter in its default
	 * state. The groupList field is left at its default null value until
	 * a future implementation populates it.
	 */
	public PunctaCounter2()
	{

	}

	/**
	 * Entry point for counting puncta inside the Area a for the given
	 * Marker m using the supplied per-channel threshold array. The method
	 * starts by fetching the bounding rectangle of a via getBounds into
	 * the local r, which a full implementation would use to iterate over
	 * candidate pixels; the remainder of the algorithm is not yet
	 * implemented in this scaffold.
	 */
	public void countPuncta(Area a, Marker m, int[] threshold){
		Rectangle2D r = a.getBounds();



	}

}
