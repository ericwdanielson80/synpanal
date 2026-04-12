package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Namespace of constants that enumerate every kind of numeric measurement
 * the neuron analyzer can compute and report. Each constant is an integer
 * ID used as an index or selector (for example when choosing which
 * statistic to plot or export): the groups cover per-puncta metrics,
 * per-dendrite metrics, spine morphology/density metrics, puncta-per-spine
 * distributions, and free (off-dendrite) puncta intensity metrics.
 */
public class DataType {

	static final int PunctaDensity = 0;
	static final int PunctaIntegratedIntensity = 1;
	static final int PunctaAveIntensity = 2;
	static final int PunctaArea = 3;
	
	static final int DendriteLength = 4;
	static final int DendriteArea = 5;
	static final int DendriteIntegratedIntensity = 6;
	static final int DendriteAveIntensity = 7;
	
	static final int SpineDesnity = 8;
	static final int SpineHeadWidth = 9;
	static final int SpineNeckWidth = 10;
	static final int SpineLength = 11;
	static final int MushroomSpineDensity = 12;
	static final int StubbySpineDensity = 13;
	static final int ThinSpineDensity = 14;
	static final int FilopodiaDensity = 15;
	static final int ZeroPunctaPerSpine = 16;
	static final int OnePunctaPerSpine = 17;
	static final int TwoPunctaPerSpine = 18;
	static final int ThreePunctaPerSpine = 19;
		
	static final int FreeTotalIntensity = 20;
	static final int FreeAveIntensity = 21;
	
	
}
