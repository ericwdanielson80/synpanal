package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;

/**
 * Mutable holder pairing two independent boolean flags (selected and
 * ignored) for a single neuron-analyzer object such as a spine, dendrite,
 * or punctum. A dedicated class lets the UI and the analysis code share
 * state by reference so a toggle made in a list or popup immediately
 * affects all views that hold the same container.
 */
public class BooleanContainer {

boolean isSelected;
public boolean isIgnored;

	/**
	 * Creates a container with the given initial flag values. The
	 * {@code iS} parameter seeds the {@code isSelected} field and
	 * {@code iI} seeds the {@code isIgnored} field, establishing the
	 * starting selection and ignore state of the owning object.
	 */
	BooleanContainer(boolean iS,boolean iI)
	{
		isSelected = iS;
		isIgnored = iI;
	}

	/**
	 * Returns whether the owning object is currently marked as selected,
	 * i.e. the current value of the {@code isSelected} flag.
	 */
	public boolean isSelected()
	{
		return isSelected;
	}

	/**
	 * Returns whether the owning object is currently marked as ignored,
	 * i.e. the current value of the {@code isIgnored} flag.
	 */
	public boolean isIgnored()
	{
		return isIgnored;
	}

	/**
	 * Toggles the selected flag: if {@code isSelected} is true it becomes
	 * false, otherwise it becomes true. Used as a UI-click handler to flip
	 * selection state without the caller having to read the current value.
	 */
	public void pushSelected()
	{
		if(isSelected)
			isSelected = false;
		else
			isSelected = true;
	}

	/**
	 * Toggles the ignored flag in the same way {@link #pushSelected()}
	 * toggles selection: {@code isIgnored} is flipped from true to false
	 * or from false to true on each call.
	 */
	public void pushIgnored()
	{
		if(isIgnored)
			isIgnored = false;
		else
			isIgnored = true;
	}

}
