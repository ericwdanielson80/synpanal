package neuron_analyzer;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Font;
import java.text.DecimalFormat;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.Color;


public class ZoomLabel extends NTextField{
functionListener fL;
DecimalFormat dF = new DecimalFormat("#######.##");
//ImageIcon ZONIcon;
	public ZoomLabel(functionListener f)
	{
		super(null,"100");		
		fL = f;
		//ZONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Zoom.gif"));
		//Font font = this.getFont();
		//font = font.deriveFont(Font.BOLD);
		//this.setFont(font);
		//this.setForeground(Color.white);
		//this.setOpaque(false);
	}
	
	public void updateZoom()
	{
		setText(null);
	}
	
	public void doCommand()
	{
		fL.setZoom(new Integer(getText()).intValue());		
		this.setEditable(false);
		this.removeKeyListener(this);
		this.transferFocus();
	}
	
	public void setText(String text)
	{
		if(fL != null)			
		super.setText(dF.format(fL.getZoom()));
		else
			super.setText(new Integer(100).toString());	
	}
	/*public void paint(Graphics g)
	{
		ZONIcon.paintIcon(this,g,0,0);
		super.paint(g);
	}*/
	
	
}
