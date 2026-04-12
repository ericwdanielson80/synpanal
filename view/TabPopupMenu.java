package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Right-click context menu attached to the tabbed view of region groups.
 * It exposes two actions, "Add Tab" and "Delete Tab", that forward to the
 * owning RegionGroupTab's functionListener so the user can manage tab
 * entries without leaving the canvas.
 */
public class TabPopupMenu extends JPopupMenu implements ActionListener {
RegionGroupTab RGT;
JMenuItem add;
JMenuItem delete;

	/**
	 * Builds the popup menu for the given RegionGroupTab. The constructor
	 * creates two JMenuItem entries labeled "Add Tab" and "Delete Tab",
	 * attaches them to this popup, and registers this object as the
	 * ActionListener for both so their clicks are routed to
	 * actionPerformed. The parameter r is stored in RGT so the listener
	 * can later call its functionListener to mutate the tab set.
	 */
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
	/**
	 * Dispatches menu item clicks. If the event originates from the add
	 * item, the tab container's functionListener is asked to add a new
	 * tab. If it comes from the delete item, a new tab is removed only
	 * when the container still has more than one tab (preventing removal
	 * of the last remaining tab) and the currently selected index is the
	 * one deleted.
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == add)
			RGT.fL.addTab();
		if(e.getSource() == delete && RGT.getTabCount() > 1)
			RGT.fL.deleteTab(RGT.getSelectedIndex());
	}

}
