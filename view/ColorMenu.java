package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Menu that lets the user choose which color channels (Red, Green, Blue)
 * will be written to the analyzer's data log. The menu exposes three
 * checkbox items and pushes their selection state into a shared LogInfo
 * instance so downstream output code can decide, per channel, whether to
 * emit that channel's measurements.
 */
public class ColorMenu extends JMenu implements ItemListener{
JCheckBoxMenuItem Red;
JCheckBoxMenuItem Green;
JCheckBoxMenuItem Blue;
LogInfo logInfo;
	/**
	 * Builds the "Data Output Colors" menu. The constructor stores the
	 * shared LogInfo reference lI in logInfo so selection changes can
	 * reach it later, creates a JCheckBoxMenuItem per RGB channel, wires
	 * this object as the item listener on each one, and finally adds all
	 * three items to the menu.
	 */
	public ColorMenu(LogInfo lI)
	{
		super("Data Output Colors");
		logInfo = lI;
		Red = new JCheckBoxMenuItem("Red Channel");
		Red.addItemListener(this);
		Green = new JCheckBoxMenuItem("Green Channel");
		Green.addItemListener(this);
		Blue = new JCheckBoxMenuItem("Blue Channel");
		Blue.addItemListener(this);
		add(Red);
		add(Green);
		add(Blue);
	}

	/**
	 * Responds to changes in any of the three channel checkboxes by
	 * copying the corresponding item's selected state into the matching
	 * logInfo flag (red, green, or blue). The event's source is compared
	 * against each of the three menu items to identify which channel
	 * toggled.
	 */
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource() == Red)
			logInfo.red = Red.isSelected();
		if(e.getSource() == Green)
			logInfo.green = Green.isSelected();
		if(e.getSource() == Blue)
			logInfo.blue = Blue.isSelected();

	}
}
