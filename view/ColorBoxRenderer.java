package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import javax.swing.JLabel;

/**
 * Swing list-cell renderer that draws each ColorLabel value as a JLabel whose
 * text foreground matches the ColorLabel's own color. It lets the analyzer
 * display lists of channel or category labels (Red/Green/Blue, etc.) using
 * the color that each entry represents, giving users an at-a-glance visual
 * cue for which channel or grouping a list item belongs to.
 */
public class ColorBoxRenderer extends JLabel implements ListCellRenderer {

	/*public ColorBoxRenderer()
	{
		super();
	}*/

	/**
	 * Prepares this label to render a single cell of the JList. The value is
	 * converted to its string form and placed into the label via setText, the
	 * label's foreground color is set to the color provided by the value cast
	 * to ColorLabel, and the configured label itself is returned as the cell
	 * component. The list, index, isSelected, and cellHasFocus parameters are
	 * standard Swing rendering context but are not consulted here, so the
	 * same styling is applied regardless of selection state.
	 */
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		super.setText(value.toString());
		setForeground(((ColorLabel)value).getColor());
		return this;
	}

}
