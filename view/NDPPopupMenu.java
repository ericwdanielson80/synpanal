package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Right-click popup attached to a NeuronDataPanel that lets the user switch
 * which category of measurements the panel displays: puncta, spine, cell
 * (regions) or colocalization. Selecting an item updates the controller's
 * data mode and rebuilds the panel's labels accordingly.
 */
public class NDPPopupMenu extends JPopupMenu implements ActionListener, MouseListener {
functionListener fL;
JMenuItem puncta;
JMenuItem spine;
JMenuItem cell;
JMenuItem colocalization;
NeuronDataPanel np;
	/**
	 * Builds the popup with "puncta", "spine", "regions" and "colocalization"
	 * items. The NP parameter is the owning NeuronDataPanel; its fL is
	 * captured for state changes and np is retained so the menu can call
	 * removeLabels / addLabels during mode switches.
	 */
	public NDPPopupMenu(NeuronDataPanel NP)
	{
		super();
		fL = NP.fL;
		np = NP;
		puncta = new JMenuItem("puncta");
		spine = new JMenuItem("spine");
		cell = new JMenuItem("regions");		
		colocalization = new JMenuItem("colocalization");
		add(puncta);
		add(spine);
		add(cell);
		add(colocalization);		
		puncta.addActionListener(this);
		spine.addActionListener(this);
		cell.addActionListener(this);
		colocalization.addActionListener(this);		
	}
	/**
	 * Handles menu selection by first removing the labels belonging to the
	 * current data mode, updating fL.setDataMode with the newly selected
	 * value (0 puncta, 1 spine, 2 cell/regions, 3 colocalization), then
	 * adding the labels for that new mode and triggering a data-pane
	 * repaint. The e parameter is the ActionEvent whose source identifies
	 * the chosen item.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		np.removeLabels(fL.getDataMode());
		
		if(e.getSource() == puncta)
			fL.setDataMode(0);
		if(e.getSource() == spine)
			fL.setDataMode(1);
		if(e.getSource() == cell)
			fL.setDataMode(2);
		if(e.getSource() == colocalization)
			fL.setDataMode(3);		
		
		np.addLabels(fL.getDataMode());
		fL.repaintDataPane();
	}
	
	/** No-op mouse-clicked hook; required by the MouseListener interface. */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op mouse-entered hook; required by the MouseListener interface. */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op mouse-exited hook; required by the MouseListener interface. */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Shows the popup when the right mouse button is pressed on the parent
	 * NeuronDataPanel, centering it horizontally on the click point via
	 * e.getX() minus half the menu width. Other buttons are ignored.
	 */
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.show(np,e.getX() - this.getWidth()/2,e.getY());
		}

	}

	/**
	 * Hides the popup on right-mouse release. Other buttons are ignored.
	 */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.setVisible(false);
		}
	}
		

}
