package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Right-click popup menu shown over a dendrite in the TIFF panel. Lets the
 * user bulk-toggle the ignore state of all puncta on the currently selected
 * dendrite, invert that state, or delete the dendrite entirely. Action
 * callbacks are routed through TiffPanelMouseFunctions to operate on the
 * selected dendrite and through the functionListener to refresh both the
 * image pane and the data pane.
 */
public class DendritePopupMenu extends JPopupMenu implements ActionListener{
TiffPanelMouseFunctions tpmf;
functionListener fL;
JMenuItem ignore;
JMenuItem restore;
JMenuItem invert;
JMenuItem delete;
	/**
	 * Builds the popup with "Ignore all puncta", "Restore all puncta",
	 * "Invert all puncta" and "Delete Dendrite" items and registers this
	 * instance as ActionListener for each. The Tpmf parameter is the mouse
	 * dispatcher that owns the currently selected dendrite, and FL is the
	 * controller used to trigger repaints of the data pane and image pane
	 * after an action runs.
	 */
	public DendritePopupMenu(TiffPanelMouseFunctions Tpmf, functionListener FL)
	{
		super();
		tpmf = Tpmf;
		fL = FL;
		ignore = new JMenuItem("Ignore all puncta");
		restore = new JMenuItem("Restore all puncta");
		invert = new JMenuItem("Invert all puncta");
		delete = new JMenuItem("Delete Dendrite");
		add(ignore);
		add(restore);
		add(invert);
		add(delete);
		ignore.addActionListener(this);
		restore.addActionListener(this);
		invert.addActionListener(this);
		delete.addActionListener(this);
	}
	
	/**
	 * Dispatches the four menu actions. When "ignore" is chosen every puncta
	 * on the selected dendrite is marked ignored for the current color group;
	 * "restore" clears that state; "invert" flips the ignored flag of each
	 * puncta on the dendrite; "delete" removes the dendrite via
	 * TiffPanelMouseFunctions.deleteDendrite. Every branch triggers
	 * repaintDataPane and repaintPane so the UI stays consistent. The e
	 * parameter is the Swing ActionEvent whose source identifies which menu
	 * item fired.
	 */
	public void actionPerformed(ActionEvent e)
	{
		
		if(e.getSource() == ignore)
			{
			tpmf.selectedDendrite.ignoreAllPuncta(fL.getCurrentColorGroup());			
			fL.repaintDataPane();
			fL.repaintPane();
			}
		
		if(e.getSource() == restore)
		{
		tpmf.selectedDendrite.resetAllPuncta(fL.getCurrentColorGroup());
		fL.repaintDataPane();
		fL.repaintPane();
		}
		
		if(e.getSource() == invert)
		{
		tpmf.selectedDendrite.invertIgnored();
		fL.repaintDataPane();
		fL.repaintPane();
		}
		if(e.getSource() == delete)
		{
			tpmf.deleteDendrite(tpmf.selectedDendrite);		
			fL.repaintDataPane();
			fL.repaintPane();
		}
	}
}
