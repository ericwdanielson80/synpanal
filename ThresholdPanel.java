package neuron_analyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;

public class ThresholdPanel extends JPanel {

	
	public ThresholdPanel(JButton thresholdcolor,JTextField lowfield,JTextField highfield,JSlider lowslider, JSlider highslider,
			JComboBox colorbox,JSlider alphaslider,JSlider luslider)
	{
		super();
		((FlowLayout)getLayout()).setHgap(0);
		
		JPanel thresh = new JPanel();
		thresh.setPreferredSize(new Dimension(80,95));
		//((FlowLayout)thresh.getLayout()).setVgap(0);
		((FlowLayout)thresh.getLayout()).setHgap(0);			
		thresh.add(thresholdcolor);
		thresh.add(colorbox);
		this.add(thresh); //first column
		
		JPanel lowhigh = new JPanel();				
		lowhigh.setPreferredSize(new Dimension(70,95));
		((FlowLayout)lowhigh.getLayout()).setHgap(0);
		
		JLabel low = new JLabel("low");
		low.setPreferredSize(new Dimension(30,15));
		lowhigh.add(low);		
		
		lowfield.setPreferredSize(new Dimension(30,15));
		lowhigh.add(lowfield);
		
		JLabel high = new JLabel("high");
		high.setPreferredSize(new Dimension(30,15));
		lowhigh.add(high);				
		lowhigh.add(highfield);
		
		JLabel alpha = new JLabel("alpha");		
		lowhigh.add(alpha);		
		JTextField blank = new JTextField();
		blank.setEnabled(false);
		blank.setPreferredSize(new Dimension(30,15));
		lowhigh.add(blank);
		
		JLabel lu = new JLabel("LUT");		
		lowhigh.add(lu);		
		JTextField blank2 = new JTextField();
		blank2.setEnabled(false);
		blank2.setPreferredSize(new Dimension(30,15));
		lowhigh.add(blank2);
		
		
		
		this.add(lowhigh); //second column
		
		JPanel thresholdSliders = new JPanel(); 
		((FlowLayout)thresholdSliders.getLayout()).setHgap(0);
		thresholdSliders.setPreferredSize(new Dimension(100,95));	
		lowslider.setPreferredSize(new Dimension(100,15));
		highslider.setPreferredSize(new Dimension(100,15));
		alphaslider.setPreferredSize(new Dimension(100,15));
		luslider.setPreferredSize(new Dimension(100,15));
		thresholdSliders.add(lowslider);
		thresholdSliders.add(highslider);		
		thresholdSliders.add(alphaslider);
		thresholdSliders.add(luslider);
		this.add(thresholdSliders); //third column
		
		/*JPanel colorPanel = new JPanel();
		((FlowLayout)colorPanel.getLayout()).setHgap(0);
		colorPanel.setPreferredSize(new Dimension(80,75));		
		((FlowLayout)colorPanel.getLayout()).setVgap(15);
		colorPanel.add(colorbox);
		this.add(colorPanel); //forth column
		
		JPanel alphaPanel = new JPanel();
		((FlowLayout)alphaPanel.getLayout()).setHgap(0);
		alphaPanel.setPreferredSize(new Dimension(100,75));		
		((FlowLayout)alphaPanel.getLayout()).setVgap(15);
		alphaslider.setPreferredSize(new Dimension(100,15));		
		alphaPanel.add(alphaslider);
		this.add(alphaPanel); //fifth column*/
		
	}
}
