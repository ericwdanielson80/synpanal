package neuron_analyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

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
