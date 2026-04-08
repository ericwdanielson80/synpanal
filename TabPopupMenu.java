package neuron_analyzer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class TabPopupMenu extends JPopupMenu implements ActionListener {
RegionGroupTab RGT;
JMenuItem add;
JMenuItem delete;

	public TabPopupMenu(RegionGroupTab r)
	{
		super();
		add = new JMenuItem("Add Tab");
		delete = new JMenuItem("Delete Tab");
		add(add);
		add(delete);
		add.addActionListener(this);
		delete.addActionListener(this);
		RGT = r;
	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == add)
			RGT.fL.addTab();
		if(e.getSource() == delete && RGT.getTabCount() > 1)
			RGT.fL.deleteTab(RGT.getSelectedIndex());
	}

}
