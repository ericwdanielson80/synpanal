package neuron_analyzer;
import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SpinePopupMenu extends JPopupMenu implements ActionListener{
TiffPanelMouseFunctions tpmf;
functionListener fL;
JMenuItem auto;
JMenuItem mushroom;
JMenuItem thin;
JMenuItem stubby;
JMenuItem filopodia;
	
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
