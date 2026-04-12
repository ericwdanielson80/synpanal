package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Holds the rule set that decides whether a puncta or overlap should be
 * auto-ignored. Two styles of criteria are supported: a per-channel
 * percentage cutoff (the puncta is ignored when the fraction of
 * above-threshold pixels in any channel falls below the cutoff) and a
 * per-channel boolean overlap pattern (the puncta is ignored when the
 * channel-presence pattern matches). The class also carries flags that
 * enable or disable each mode.
 */
public class IgnoreCriteria {
int[] percentages;
public boolean[] overlap;
public boolean AutoIgnorePercentages;
public boolean AutoIgnoreOverlap;

public boolean activeP = false;
public boolean activeO = false;

	/**
	 * Constructs a percentage-mode criteria using the supplied three-element
	 * array of cutoff values for red, green and blue.
	 */
	public IgnoreCriteria(int[] Percentages)
	{
		percentages = Percentages;
	}

	/**
	 * Constructs an overlap-mode criteria using the supplied three-element
	 * boolean array representing the target channel-presence pattern.
	 */
	public IgnoreCriteria(boolean[] Overlap)
	{
		overlap = Overlap;
	}

	/**
	 * Convenience constructor that builds a percentage-mode criteria from
	 * three scalar red/green/blue cutoff values.
	 */
	public IgnoreCriteria(int r, int g, int b)
	{
		percentages = new int[] {r,g,b};
	}

	/**
	 * Convenience constructor that builds an overlap-mode criteria from
	 * three scalar red/green/blue booleans.
	 */
	public IgnoreCriteria(boolean r, boolean g, boolean b)
	{
		overlap = new boolean[] {r,g,b};
	}

	/**
	 * Replaces the percentage cutoffs with the supplied array t.
	 */
	public void load(int[] t)
	{
		percentages = t;
	}

	/**
	 * Replaces the overlap pattern with the supplied array t.
	 */
	public void load(boolean[] t)
	{
		overlap = t;
	}


	/**
	 * Returns true when the observed red percentage r is strictly less than
	 * the red cutoff stored in percentages[0], triggering an ignore.
	 */
	private boolean testRed(int r)
	{
		//if r (percent) is less than cutoff the ignore this puncta		
		return (r < percentages[0]);
	}
	
	/**
	 * Returns true when the observed green percentage r is below
	 * percentages[1].
	 */
	private boolean testGreen(int r)
	{
		return (r < percentages[1]);
	}

	/**
	 * Returns true when the observed blue percentage r is below
	 * percentages[2].
	 */
	private boolean testBlue(int r)
	{
		return (r < percentages[2]);
	}

	/**
	 * Overlap-mode red test; returns true when the observed presence r of the
	 * red channel matches the target pattern overlap[0].
	 */
	private boolean testRed(boolean r)
	{
		//if r (percent) is less than cutoff the ignore this puncta		
		return (r == overlap[0]);
	}
	
	/**
	 * Overlap-mode green test; returns true when r equals overlap[1].
	 */
	private boolean testGreen(boolean r)
	{
		return (r == overlap[1]);
	}

	/**
	 * Overlap-mode blue test; returns true when r equals overlap[2].
	 */
	private boolean testBlue(boolean r)
	{
		return (r == overlap[2]);
	}

	/**
	 * Evaluates the percentage-mode rule on the three-element observed
	 * percentages in input, returning true if any of the red, green or blue
	 * tests matches, i.e. the puncta should be ignored.
	 */
	public boolean testCriteria(int[] input)
	{
		return testRed(input[0]) || testGreen(input[1]) || testBlue(input[2]);
	}

	/**
	 * Evaluates the overlap-mode rule on the three-element observed presence
	 * booleans in input, returning true when any channel test matches.
	 */
	public boolean testCriteria(boolean[] input)
	{
		return testRed(input[0]) || testGreen(input[1]) || testBlue(input[2]);
	}

	/**
	 * Returns the percentage cutoff for the i-th channel as a string for
	 * display in option widgets.
	 */
	public String getText(int i)
	{
		return new Integer(percentages[i]).toString();
	}

	/**
	 * Returns the overlap-mode boolean for the i-th channel as a string.
	 */
	public String getText2(int i)
	{
		return new Boolean(overlap[i]).toString();
	}

	/**
	 * Sets the AutoIgnorePercentages flag to b, controlling whether the
	 * percentage-mode rule is applied during auto-ignore passes.
	 */
	public void autoIgnorePercentages(boolean b)
	{
		AutoIgnorePercentages = b;
	}

	/**
	 * Sets the AutoIgnoreOverlap flag to b, controlling whether the overlap
	 * rule is applied during auto-ignore passes.
	 */
	public void autoIgnoreOverlap(boolean b)
	{
		AutoIgnoreOverlap = b;
	}

	/**
	 * Activates the percentage-mode rule by setting activeP to true.
	 */
	public void activateP()
	{
		activeP = true;
	}

	/**
	 * Deactivates the overlap-mode rule by setting activeO to false.
	 */
	public void deactivateO()
	{
		activeO = false;
	}

	/**
	 * Deactivates the percentage-mode rule by setting activeP to false.
	 */
	public void deactivateP()
	{
		activeP = false;
	}

	/**
	 * Activates the overlap-mode rule by setting activeO to true.
	 */
	public void activateO()
	{
		activeO = true;
	}
}
