package neuron_analyzer;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Color;
import javax.swing.JLabel;

public class ColorBoxRenderer extends JLabel implements ListCellRenderer {

	/*public ColorBoxRenderer()
	{
		super();
	}*/
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		super.setText(value.toString());
		setForeground(((ColorLabel)value).getColor());			
		return this;
	}

}
