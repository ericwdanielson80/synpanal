package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Right-click popup shown when a spine is selected in the image pane. Exposes
 * commands to set the selected spine's morphological type to auto, mushroom,
 * thin, stubby or filopodia and triggers both data-pane and image-pane
 * repaints so the new classification is visible immediately.
 */
public class SpinePopupMenu extends JPopupMenu implements ActionListener{
TiffPanelMouseFunctions tpmf;
functionListener fL;
JMenuItem auto;
JMenuItem mushroom;
JMenuItem thin;
JMenuItem stubby;
JMenuItem filopodia;
	
	/**
	 * Builds the popup with items for each spine type and wires this instance
	 * as their ActionListener. The Tpmf parameter is the mouse dispatcher
	 * that owns the currently selected spine (used to apply the type change)
	 * and FL is the controller used to refresh the data and image panes.
	 */
	public SpinePopupMenu(TiffPanelMouseFunctions Tpmf, functionListener FL)
	{
		super();
		tpmf = Tpmf;
		fL = FL;
		auto = new JMenuItem("auto type");
		mushroom = new JMenuItem("mushrrom");
		thin = new JMenuItem("thin");
		stubby = new JMenuItem("stubby");
		filopodia = new JMenuItem("filopodia");
		
		
		add(auto);
		add(mushroom);
		add(thin);
		add(stubby);
		add(filopodia);
		auto.addActionListener(this);
		mushroom.addActionListener(this);
		thin.addActionListener(this);
		stubby.addActionListener(this);
		filopodia.addActionListener(this);
		
	}
	
	/**
	 * Routes menu selections to TiffPanelMouseFunctions.setSelectedSpineType
	 * with the type index 0..3 and an auto-mode boolean; "auto" passes
	 * (0, true) to use automatic classification, while the other four items
	 * pass their explicit type index with false. Every branch repaints the
	 * data pane and image pane. The e parameter is the Swing ActionEvent
	 * whose source identifies which menu item fired.
	 */
	public void actionPerformed(ActionEvent e)
	{
		
		if(e.getSource() == auto)
			{
			tpmf.setSelectedSpineType(0,true);			
			fL.repaintDataPane();
			fL.repaintPane();
			}
		
		if(e.getSource() == mushroom)
		{
		tpmf.setSelectedSpineType(0,false);
		fL.repaintDataPane();
		fL.repaintPane();
		}
		
		if(e.getSource() == thin)
		{
		tpmf.setSelectedSpineType(1,false);
		fL.repaintDataPane();
		fL.repaintPane();
		}
		
		if(e.getSource() == stubby)
		{
		tpmf.setSelectedSpineType(2,false);		
		fL.repaintDataPane();
		fL.repaintPane();
		}
		
		if(e.getSource() == filopodia)
		{
		tpmf.setSelectedSpineType(3,false);	
		fL.repaintDataPane();
		fL.repaintPane();
		}
		
		
	}
}
