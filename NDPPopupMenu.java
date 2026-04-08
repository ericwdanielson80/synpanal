package neuron_analyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class NDPPopupMenu extends JPopupMenu implements ActionListener, MouseListener {
functionListener fL;
JMenuItem puncta;
JMenuItem spine;
JMenuItem cell;
JMenuItem colocalization;
NeuronDataPanel np;
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
	
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.show(np,e.getX() - this.getWidth()/2,e.getY());
		}

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getButton() == MouseEvent.BUTTON3)
		{
			this.setVisible(false);
		}
	}
		

}
