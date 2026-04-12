package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

/**
 * Menu of JCheckBoxMenuItem toggles that lets the user decide which kinds of
 * log files are produced when data is exported. Each checkbox corresponds to
 * a boolean flag on the supplied LogInfo object, covering per-image,
 * per-dendrite and per-individual variants of puncta, spine and generic cell
 * information. Selections are applied by itemStateChanged directly onto the
 * LogInfo fields.
 */
public class LogFileDataMenu extends JMenu implements ItemListener{
	JCheckBoxMenuItem imageP;
	JCheckBoxMenuItem imageS;
	JCheckBoxMenuItem imageC;
	JCheckBoxMenuItem dendriteP;
	JCheckBoxMenuItem dendriteS;
	JCheckBoxMenuItem individualP;
	JCheckBoxMenuItem individualS;
	JCheckBoxMenuItem individualC;
	LogInfo logInfo;
		/**
		 * Builds the "Data Output Type" submenu and populates it with the
		 * eight checkboxes (image-level puncta/spine/generic, dendrite-level
		 * puncta/spine, individual puncta/spine/generic). Each item is
		 * registered as an ItemListener and added to the menu. The lI
		 * parameter is the LogInfo whose fields the listener updates as the
		 * user toggles items.
		 */
		public LogFileDataMenu(LogInfo lI)
		{
			super("Data Output Type");
			logInfo = lI;
			imageP = new JCheckBoxMenuItem("Log Puncta Info Per Image");
			imageP.addItemListener(this);			
			add(imageP);
			
			imageS = new JCheckBoxMenuItem("Log Spine Info Per Image");
			imageS.addItemListener(this);
			add(imageS);
			
			imageC = new JCheckBoxMenuItem("Log Generic Info Per Image");
			imageC.addItemListener(this);
			add(imageC);
			
			dendriteP = new JCheckBoxMenuItem("Log Puncta Info Per Dendrite");
			dendriteP.addItemListener(this);
			add(dendriteP);
			
			dendriteS = new JCheckBoxMenuItem("Log Spine Info Per Dendrite");
			dendriteS.addItemListener(this);
			add(dendriteS);
			
			individualP = new JCheckBoxMenuItem("Log Individual Puncta Info");
			individualP.addItemListener(this);
			add(individualP);
			
			individualS = new JCheckBoxMenuItem("Log Individual Spine Info");
			individualS.addItemListener(this);
			add(individualS);
			
			individualC = new JCheckBoxMenuItem("Log Individual Generic Info");
			individualC.addItemListener(this);
			add(individualC);
		}
		
		/**
		 * Copies the current selected state of whichever checkbox fired the
		 * event onto the matching LogInfo boolean (pI for imageP, sI for
		 * imageS, cI for imageC, dpI for dendriteP, dsI for dendriteS, ppI
		 * for individualP, ssI for individualS, ccI for individualC). The e
		 * parameter is the Swing ItemEvent whose source identifies the item.
		 */
		public void itemStateChanged(ItemEvent e)
		{
			if(e.getSource() == imageP)
				logInfo.pI = imageP.isSelected();
			if(e.getSource() == imageS)
				logInfo.sI = imageS.isSelected();
			if(e.getSource() == imageC)
				logInfo.cI = imageC.isSelected();
			if(e.getSource() == dendriteP)
				logInfo.dpI = dendriteP.isSelected();
			if(e.getSource() == dendriteS)
				logInfo.dsI = dendriteS.isSelected();
			if(e.getSource() == individualP)
				logInfo.ppI = individualP.isSelected();
			if(e.getSource() == individualS)
				logInfo.ssI = individualS.isSelected();
			if(e.getSource() == individualC)
				logInfo.ccI = individualC.isSelected();			
		}
}
