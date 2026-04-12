package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Mutable integer wrapper representing the width (in pixels) of a dendrite
 * segment. A dedicated class is used instead of a raw {@code int} so that
 * different UI widgets and model objects can share and update the same
 * width value by reference, with changes made in one place visible
 * everywhere the wrapper is held.
 */
public class dendriteWidth {
public int width;

    /**
     * Creates a new width holder initialized to the given value. The
     * {@code w} parameter is stored in the {@code width} field and becomes
     * the value returned by {@link #intValue()} until changed.
     */
    public dendriteWidth(int w) {
        width = w;
    }

    /**
     * Overwrites the stored dendrite width with a new value. The {@code w}
     * parameter replaces the contents of the {@code width} field,
     * propagating the change to every holder of this same instance.
     */
    public void setValue(int w)
    {
        width = w;
    }

    /**
     * Returns the currently stored integer dendrite width in pixels, as
     * either set at construction or later via {@link #setValue(int)}.
     */
    public int intValue()
    {
        return width;
    }
}
