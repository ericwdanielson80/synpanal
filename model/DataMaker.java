package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Stub aggregator intended to coordinate measurement of puncta, dendrite,
 * cell-body and total-intensity data once a PunctaCounter has produced its
 * raw detections. The method bodies are placeholders that describe the
 * planned algorithms in comments; the class currently only retains the
 * PunctaCounter reference supplied at construction time.
 */
public class DataMaker {
PunctaCounter p;
	/**
	 * Stores the supplied PunctaCounter so the various per-measurement helpers
	 * can later consult it. The pC parameter is the shared puncta-detection
	 * engine providing puncta lists and counts for downstream measurement.
	 */
	public DataMaker(PunctaCounter pC)
	{
		p = pC;
	}

	/**
	 * Intended to produce per-dendrite puncta measurements. Currently
	 * allocates a groupVector named puncta as a placeholder and leaves the
	 * heavy lifting (using PunctaCounter.countPuncta, thresholds, color
	 * selection and marker-restricted measurement) as a TODO described in
	 * the in-line comment.
	 */
	public void getPunctaData()
	{	/*will not be void
		will take dendrites/list of areas, color type and a threshold value and a raster
		will also take a marker 
		will us this to make puncta and measure area and intensity, if marker is present then
		will only measure puncta underneigth the marker
		will return a list of punctaContainers 
		these containers should have a list of areas, intensities and outlines
		
		*/
		groupVector puncta = new groupVector();
		//puncta = p.countPuncta()
	}
	
	/**
	 * Placeholder for computing per-dendrite measurements (length, width,
	 * total intensity and marker-restricted intensity). The body is empty;
	 * the in-line comment captures the planned inputs and outputs.
	 */
	public void getDendriteData()
	{
		/*
		 will not be void
		 will take a list of dendrites and a color watch types and threshold values and raster
		 one value will be for measuring dendrite width, the rest for total intensity
		 will measure dendrite length, dendrite width and total intensity within dendrite region
		 if dendrite marker is present total intensity will be measured only where marker is 
		 present
		 */
	}
	
	/**
	 * Placeholder for computing per-cell-body measurements (area and
	 * intensity) given area lists, color watches, thresholds, marker and
	 * raster. The body is empty.
	 */
	public void getCellBodyData()
	{
		/*
		 * will take an CellBody list / area list, color watch and thresholds and marker and raster
		 * will measure area, and intensity
		 */
	}
	
	/**
	 * Placeholder for summing intensity values under a marker region, to
	 * yield total intensity and marker area. The body is empty.
	 */
	public void getTotalIntensity()
	{
		/*
		 * will take a marker and an intensity and a raster
		 * will return the total value under the marker and the area of the marker
		 */
	}
}
