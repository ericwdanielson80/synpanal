package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Immutable bundle of colocalization statistics describing how two color
 * channels (labeled A and B) overlap within a measured region. It records
 * the raw pixel counts for each channel plus the overlap, and the
 * integrated intensity sums on each channel both in isolation and when
 * restricted to pixels that are positive in the other channel. Downstream
 * reports derive Manders/Pearson-style coefficients from these values.
 */
public class ColocalizationInfo {
double totalPixelsA;
double totalPixelsB;
double totalPixelsAB;

double integratedA;
double integratedB;
double integratedAwithB;
double integratedBwithA;

	/**
	 * Captures a complete set of colocalization counts and sums. Parameters
	 * {@code tA}, {@code tB}, and {@code AB} are the number of pixels above
	 * threshold on channel A, on channel B, and on both channels
	 * simultaneously; {@code sA} and {@code sB} are the integrated
	 * intensity sums on each channel over the region; {@code sAB} is the
	 * channel-A intensity summed only over pixels where B is also above
	 * threshold, and {@code sBA} is the mirror-image channel-B sum over
	 * pixels where A is above threshold. The ints and longs are stored as
	 * doubles in the corresponding {@code totalPixels*} and
	 * {@code integrated*} fields for later arithmetic.
	 */
	public ColocalizationInfo(int tA, int tB, int AB, long sA, long sB, long sAB, long sBA)
	{
		totalPixelsA = tA;
		totalPixelsB = tB;
		totalPixelsAB = AB;

		integratedA = sA;
		integratedB = sB;

		integratedAwithB = sAB;
		integratedBwithA = sBA;
	}

}
