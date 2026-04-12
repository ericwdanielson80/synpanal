package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

/**
 * Right-click popup menu for the TiffPanel (the image display surface). It
 * exposes commands that act on the puncta and dendrites overlaid on the
 * current image, including restoring all previously ignored puncta,
 * hiding/showing dendrite overlays, setting auto-ignore criteria, inverting
 * the ignored-puncta set, swapping cell regions between groups, and setting
 * a puncta limit. The menu holds a reference to the TiffPanel it acts on
 * and forwards each menu selection to the matching operation there.
 */
public class TiffPanelPopupMenu extends JPopupMenu implements ActionListener {
TiffPanel tp;

JMenuItem countPuncta;
JMenuItem restoreAllPuncta;
JMenuItem showhideDendrites;
JMenuItem setAutoIgnoreCriteriaPercentage;
JMenuItem setAutoIgnoreCriteriaOverlap;
JMenuItem cycleInfoPanel; //possible change the panels from dendrite to spine to cell to whatever...
JMenuItem showhideThresholdPanel; //maybe
JMenuItem invertIgnored;
JMenuItem SwapCellRegions;
JMenuItem setPunctaLimit;

	/**
	 * Builds the popup menu, creating each JMenuItem, wiring it to this
	 * class as its ActionListener, and adding it to the menu in display
	 * order. The parameter p is the TiffPanel instance whose puncta and
	 * overlay behavior the menu items will control.
	 */
	public TiffPanelPopupMenu(TiffPanel p)
	{
		tp = p;
		restoreAllPuncta = new JMenuItem("Restore all Puncta");
		showhideDendrites = new JMenuItem("Hide Dendrites");
		invertIgnored = new JMenuItem("Invert Ignored Puncta");
		setAutoIgnoreCriteriaPercentage = new JMenuItem("Auto Ignore Criteria (%)");
		setAutoIgnoreCriteriaOverlap = new JMenuItem("Auto Ignore Criteria (Overlaping)");
		SwapCellRegions = new JMenuItem("Swap Cell Regions");
		setPunctaLimit = new JMenuItem("Set Puncta Limit");
		restoreAllPuncta.addActionListener(this);
		showhideDendrites.addActionListener(this);
		invertIgnored.addActionListener(this);		
		setAutoIgnoreCriteriaPercentage.addActionListener(this);
		setAutoIgnoreCriteriaOverlap.addActionListener(this);
		SwapCellRegions.addActionListener(this);		
		setPunctaLimit.addActionListener(this);
		add(restoreAllPuncta);
		add(showhideDendrites);
		add(invertIgnored);
		add(setAutoIgnoreCriteriaPercentage);
		//add(setAutoIgnoreCriteriaOverlap);
		add(SwapCellRegions);
		add(setPunctaLimit);
		
	}
	
	/**
	 * Dispatches menu selections to the TiffPanel. The event source is
	 * compared against each stored JMenuItem: restoreAllPuncta calls
	 * restoreIgnored on the panel; showhideDendrites toggles the "speed"
	 * drawing mode via functionListener and updates the item label
	 * accordingly; setAutoIgnoreCriteriaPercentage/Overlap open their
	 * respective criteria frames; invertIgnored flips the ignored flag
	 * across all puncta; SwapCellRegions swaps cell regions across all
	 * groups fetched from the functionListener; and setPunctaLimit opens
	 * the puncta-limit dialog. The parameter e carries the event source
	 * used for the dispatch.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == restoreAllPuncta)
		{
			tp.restoreIgnored();
			return;
		}
		if(e.getSource() == showhideDendrites)
		{
			tp.fL.toggleSpeed();
			
			if(tp.speed)
				showhideDendrites.setText("Show Dendrites");
			else
				showhideDendrites.setText("Hide Dendrites");
			
			return;
		}
		if(e.getSource() == setAutoIgnoreCriteriaPercentage)
		{
			IgnoreCriteriaFrame f = new IgnoreCriteriaFrame(tp.ignoreCriteria,tp.fL);
			return;
		}
		if(e.getSource() == setAutoIgnoreCriteriaOverlap)
		{
			IgnoreCriteriaFrame2 f2 = new IgnoreCriteriaFrame2(tp.ignoreCriteria,tp.fL);
			return;
		}
		if(e.getSource() == invertIgnored)
		{
			tp.invertIgnored();
		}
		if(e.getSource() == SwapCellRegions)
		{
			tp.SwapCellRegions(tp.fL.getGroupList());
		}
		if(e.getSource() == setPunctaLimit)
		{
			tp.setPunctaLimit();
		}

	}

}
