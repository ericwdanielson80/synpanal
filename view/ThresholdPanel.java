package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;

/**
 * Compact UI panel that lays out the threshold controls for a single color
 * channel: a threshold-color button, a color combo box, low and high
 * threshold text fields and matching sliders, plus alpha and LUT sliders
 * with two disabled placeholder fields. The panel arranges the supplied
 * Swing components into three fixed-size sub-panels acting as columns.
 */
public class ThresholdPanel extends JPanel {

	
	/**
	 * Builds the threshold control panel from pre-existing Swing
	 * components. The first sub-panel (thresh) holds the thresholdcolor
	 * button and the colorbox combo; the second (lowhigh) holds labelled
	 * low/high text fields, a disabled alpha placeholder, and a disabled
	 * LUT placeholder; the third (thresholdSliders) stacks the lowslider,
	 * highslider, alphaslider and luslider, each sized to 100x15.
	 * Parameters: thresholdcolor is the button that shows/selects the
	 * threshold overlay color, lowfield/highfield are the numeric fields
	 * for the low and high cutoffs, lowslider/highslider are the matching
	 * sliders, colorbox chooses which channel the panel targets,
	 * alphaslider controls overlay transparency, and luslider controls
	 * look-up-table scaling.
	 */
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
