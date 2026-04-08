package neuron_analyzer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

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
