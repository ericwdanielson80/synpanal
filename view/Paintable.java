package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
import java.awt.Graphics;
import java.awt.Dimension;

/**
 * Interface for arbitrary objects that know how to render themselves onto a
 * Swing/AWT {@link Graphics} surface and can report the size of the area
 * they will occupy. Implementations are used by the neuron analyzer to draw
 * custom overlays, legends, and plot elements that do not fit cleanly into
 * the standard component hierarchy.
 */
public interface Paintable {

	/**
	 * Draws this object's visual representation onto the supplied graphics
	 * context. The {@code g} parameter is the destination {@link Graphics}
	 * target (a panel, an off-screen buffer, or a print context); the object
	 * is expected to honor the current transform and clip of that context.
	 */
	public void paint(Graphics g);

	/**
	 * Returns the dimensions this object will occupy when painted on the
	 * given graphics context. The context is supplied because sizing may
	 * depend on font metrics or other graphics-dependent state. The
	 * {@code g} parameter is the {@link Graphics} used to measure against.
	 */
	public Dimension getSize(Graphics g);
}
