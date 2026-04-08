package neuron_analyzer;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DendritePopupMenu extends JPopupMenu implements ActionListener{
TiffPanelMouseFunctions tpmf;
functionListener fL;
JMenuItem ignore;
JMenuItem restore;
JMenuItem invert;
JMenuItem delete;
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
