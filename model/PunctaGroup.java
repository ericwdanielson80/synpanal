package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Lightweight aggregator that accumulates statistics over a group of puncta
 * (bright synaptic-marker spots detected in an image channel). Each instance
 * tracks a running total of integrated intensity and a count of puncta
 * contributed so far, which downstream reporting code uses to compute
 * averages and totals for a particular grouping (e.g. per dendrite or per
 * spine).
 */
public class PunctaGroup {
//keeps info for integrated intensity for the puncta group and the total number of puncta
int intensity;
int counter;


    /**
     * Creates an empty group with both the intensity total and the puncta
     * count initialized to zero. Callers then populate the group by invoking
     * addPuncta repeatedly.
     */
    public PunctaGroup() {
        intensity = 0;
        counter = 0;
    }

    /**
     * Records one additional puncta in this group by adding its integrated
     * intensity i to the running total and incrementing the member count by
     * one. No normalization or averaging is performed here; callers divide
     * intensity by counter themselves when they want a mean.
     */
    public void addPuncta(int i)
    {
        intensity += i;
        counter++;
    }
}
