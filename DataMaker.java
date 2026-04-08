package neuron_analyzer;

public class DataMaker {
PunctaCounter p;
	public DataMaker(PunctaCounter pC)
	{
		p = pC;
	}
	
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
	
	public void getCellBodyData()
	{
		/*
		 * will take an CellBody list / area list, color watch and thresholds and marker and raster
		 * will measure area, and intensity
		 */
	}
	
	public void getTotalIntensity()
	{
		/*
		 * will take a marker and an intensity and a raster
		 * will return the total value under the marker and the area of the marker
		 */
	}
}
