package neuron_analyzer;

import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ColorMenu extends JMenu implements ItemListener{
JCheckBoxMenuItem Red;
JCheckBoxMenuItem Green;
JCheckBoxMenuItem Blue;
LogInfo logInfo;
	public ColorMenu(LogInfo lI)
	{		
		super("Data Output Colors");
		logInfo = lI;
		Red = new JCheckBoxMenuItem("Red Channel");
		Red.addItemListener(this);
		Green = new JCheckBoxMenuItem("Green Channel");
		Green.addItemListener(this);
		Blue = new JCheckBoxMenuItem("Blue Channel");
		Blue.addItemListener(this);
		add(Red);
		add(Green);
		add(Blue);
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource() == Red)
			logInfo.red = Red.isSelected();
		if(e.getSource() == Green)
			logInfo.green = Green.isSelected();
		if(e.getSource() == Blue)
			logInfo.blue = Blue.isSelected();
		
	}
}
