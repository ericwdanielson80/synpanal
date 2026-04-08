package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import javax.swing.event.*;
import java.awt.Color;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ThresholdOptionsPanel extends JPanel implements ChangeListener, ItemListener, MouseListener, NTextFieldListener {
    FlowLayout flowLayout1 = new FlowLayout();
    JButton RLabel;
    JButton GLabel;
    JButton BLabel;
    JTextField RField;
    JTextField RField2;
    
    JTextField GField;
    JTextField GField2;
    
    JTextField BField;
    JTextField BField2;
    
    ThresholdPanel redP;
    ThresholdPanel greenP;
    ThresholdPanel blueP;
    
    JPanel thresholdContainer;
    
    
    functionListener fL;
    int r,g,b;
    int str = 256;
    int stg = 256;
    int stb = 256;
   
    long time = System.currentTimeMillis();
    int click = 1;
    JLabel jLabel1 = new JLabel();
    JTextField dendriteWidth = new NTextField(this,"");
    JLabel jLabel2 = new JLabel();
    JTextField calibrationField = new NTextField(this,"");
    
    JSlider RlowerBound = new JSlider(0,256);
    JSlider RupperBound = new JSlider(0,256);   
    
    JSlider GlowerBound = new JSlider(0,256);
    JSlider GupperBound = new JSlider(0,256);
    
    JSlider BlowerBound = new JSlider(0,256);
    JSlider BupperBound = new JSlider(0,256);
    
    JComboBox redColors;
    JComboBox greenColors;
    JComboBox blueColors;
    
    JSlider redAlpha = new JSlider(0,255);
    JSlider greenAlpha = new JSlider(0,255);
    JSlider blueAlpha = new JSlider(0,255);
    
    JSlider redLu = new JSlider(1,256);
    JSlider greenLu = new JSlider(1,256);
    JSlider blueLu = new JSlider(1,256);


    public ThresholdOptionsPanel(functionListener fl) {
        fL = fl;
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(flowLayout1);
        flowLayout1.setAlignment(FlowLayout.LEFT);
        flowLayout1.setHgap(50);
        flowLayout1.setVgap(2);
        
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel1.setText("Width");
        dendriteWidth.setText("20");        
        dendriteWidth.addMouseListener(this);
        dendriteWidth.setToolTipText("fixed dendrite width (pixels). double-click to set");
        calibrationField.addMouseListener(this);
        calibrationField.setToolTipText("calibrate (micron/pixel). double-click to set");
        jLabel2.setText("Calibration");
        calibrationField.setText("0.12726"); 
        
        
        
        addThresholdPanels();
        
        JPanel widthPanel = new JPanel();
        widthPanel.setPreferredSize(new Dimension(150,35));
        widthPanel.add(jLabel1);
        widthPanel.add(dendriteWidth);
        JPanel calibrationPanel = new JPanel();
        calibrationPanel.setPreferredSize(new Dimension(150,35));
        calibrationPanel.add(jLabel2);
        calibrationPanel.add(calibrationField);
        
        JPanel other = new JPanel();
        other.setPreferredSize(new Dimension(150,70));
        other.add(widthPanel);
        other.add(calibrationPanel);
        
        
        /*this.add(jLabel1);
        this.add(dendriteWidth);
        this.add(jLabel2);
        this.add(calibrationField);*/
        this.add(other);

    }
    
    public ColorLabel[] makeColors()
    {
    	ColorLabel[] colors = new ColorLabel[8];
        colors[0] = new ColorLabel("White",Color.WHITE);
        colors[1] = new ColorLabel("Red",Color.RED);
        colors[2] = new ColorLabel("Yellow",Color.YELLOW);
        colors[3] = new ColorLabel("Orange",Color.ORANGE);
        colors[4] = new ColorLabel("Green",Color.GREEN);
        colors[5] = new ColorLabel("Blue",Color.BLUE);
        colors[6] = new ColorLabel("Magenta",Color.MAGENTA);
        colors[7] = new ColorLabel("Pink",Color.PINK);
        return colors;    	
    }
    
    public void makeThresholdFields()
    {
    	RField = new NTextField(this,"256");
    	RField.addMouseListener(this);
    	RField.setToolTipText("enter number 0-256. double-click to set");
        GField = new NTextField(this,"256");
        GField.setToolTipText("enter number 0-256. double click to set");
        GField.addMouseListener(this);
        BField = new NTextField(this,"256");
        BField.setToolTipText("enter number 0-256. double click to set");
        BField.addMouseListener(this);
        
        RField2 = new NTextField(this,"256");
        RField2.setToolTipText("enter number 0-256. double click to set");
        RField2.addMouseListener(this);
        GField2 = new NTextField(this,"256");
        GField2.setToolTipText("enter number 0-256. double click to set");
        GField2.addMouseListener(this);
        BField2 = new NTextField(this,"256");
        BField2.setToolTipText("enter number 0-256. double click to set");
        BField2.addMouseListener(this);        
        
    }
    
    public void makeThresholdSliders()
    {
    	RlowerBound.setValue(256);
    	RlowerBound.setToolTipText("change red low threshold");
    	GlowerBound.setValue(256);
    	GlowerBound.setToolTipText("change green low threshold");
    	BlowerBound.setValue(256);
    	BlowerBound.setToolTipText("change blue low threshold");
    	
    	RupperBound.setValue(256);    	
    	RupperBound.setToolTipText("change red high threshold");
    	GupperBound.setValue(256);
    	GupperBound.setToolTipText("change green high threshold");
    	BupperBound.setValue(256);
    	BupperBound.setToolTipText("change blue high threshold");
    	
    	RlowerBound.addChangeListener(this);
    	GlowerBound.addChangeListener(this);
    	BlowerBound.addChangeListener(this);
    	
    	RlowerBound.addMouseListener(this);
    	GlowerBound.addMouseListener(this);
    	BlowerBound.addMouseListener(this);
    	
    	RupperBound.addChangeListener(this);
    	GupperBound.addChangeListener(this);
    	BupperBound.addChangeListener(this);
    	
    }
    
    public void makeColorBoxes()
    {
    	ColorLabel[] redcolors = makeColors();
        ColorLabel[] greencolors = makeColors();
        ColorLabel[] bluecolors = makeColors();        
        
        redColors = new JComboBox(redcolors);
        redColors.setToolTipText("set red threshold color");
        redColors.setRenderer(new ColorBoxRenderer());
        redColors.addItemListener(this);
        
        greenColors = new JComboBox(greencolors);
        greenColors.setToolTipText("set green threshold color");
        greenColors.setRenderer(new ColorBoxRenderer());
        greenColors.addItemListener(this);
        
        blueColors = new JComboBox(bluecolors);
        blueColors.setToolTipText("set blue threshold color");
        blueColors.setRenderer(new ColorBoxRenderer());
        blueColors.addItemListener(this);
    }
    
    public void makeAlphaSliders()
    {
    	redAlpha.setValue(255);
    	greenAlpha.setValue(255);
    	blueAlpha.setValue(255);
    	redAlpha.setToolTipText("change red threshold transparency");
    	greenAlpha.setToolTipText("change green threshold transparency");
    	blueAlpha.setToolTipText("change blue threshold transparency");
    	
    	redAlpha.addChangeListener(this);
    	greenAlpha.addChangeListener(this);
    	blueAlpha.addChangeListener(this);
    }
    
    public void makeLUTSliders()
    {
    	redLu.setValue(255);
    	greenLu.setValue(255);
    	blueLu.setValue(255);
    	redLu.setToolTipText("change red LUT");
    	greenLu.setToolTipText("change green LUT");
    	blueLu.setToolTipText("change blue LUT");
    	
    	redLu.addChangeListener(this);
    	greenLu.addChangeListener(this);
    	blueLu.addChangeListener(this);
    }
    
    public void makeLabels()
    {
    	RLabel = new JButton("Red");
    	RLabel.setToolTipText("click to cycle color");
    	RLabel.addMouseListener(this);
    	GLabel = new JButton("Green");
    	GLabel.setToolTipText("click to cycle color");
    	GLabel.addMouseListener(this);
    	BLabel = new JButton("Blue");
    	BLabel.setToolTipText("click to cycle color");
    	BLabel.addMouseListener(this);
    }
    public void addThresholdPanels()
    {
    	makeLabels();
    	makeThresholdFields();
    	makeThresholdSliders();
    	makeColorBoxes();
    	makeAlphaSliders();
    	makeLUTSliders();
    	
    	redP = new ThresholdPanel(RLabel,RField,RField2,RlowerBound,RupperBound,redColors,redAlpha,redLu);        
        greenP = new ThresholdPanel(GLabel,GField,GField2,GlowerBound,GupperBound,greenColors,greenAlpha,greenLu);
        blueP = new ThresholdPanel(BLabel,BField,BField2,BlowerBound,BupperBound,blueColors,blueAlpha,blueLu);
        
        thresholdContainer = new JPanel();
        thresholdContainer.add(redP);
        this.add(thresholdContainer);
        //this.add(green);
        //this.add(blue);
    }
    
    public void suspendListeners()
    {
    	RlowerBound.removeChangeListener(this);
    	GlowerBound.removeChangeListener(this);
    	BlowerBound.removeChangeListener(this);
    	
    	RupperBound.removeChangeListener(this);
    	GupperBound.removeChangeListener(this);
    	BupperBound.removeChangeListener(this);
    }
    
    public void reactivateListeners()
    {
    	RlowerBound.addChangeListener(this);
    	GlowerBound.addChangeListener(this);
    	BlowerBound.addChangeListener(this);
    	
    	RupperBound.addChangeListener(this);
    	GupperBound.addChangeListener(this);
    	BupperBound.addChangeListener(this);
    	
    	
    }
    

    public void getColors()
    {
        try{
        	
        	
        	
            r = Integer.parseInt(RField.getText());            
            suspendListeners(); RlowerBound.setValue(r);      
            checkUpperBounds();
            RupperBound.setValue(str);            
            redColors.setSelectedItem(fL.getRedThresholdColor());
            redAlpha.setValue(fL.getRedThresholdColor().getAlpha());
            g = Integer.parseInt(GField.getText());
            GlowerBound.setValue(g);
            GupperBound.setValue(stg);
            greenColors.setSelectedItem(fL.getGreenThresholdColor());
            greenAlpha.setValue(fL.getGreenThresholdColor().getAlpha());
            b = Integer.parseInt(BField.getText());
            BlowerBound.setValue(b);
            BupperBound.setValue(stb);
            blueColors.setSelectedItem(fL.getBlueThresholdColor());
            blueAlpha.setValue(fL.getBlueThresholdColor().getAlpha());            
            reactivateListeners();
        }
        catch(NumberFormatException ex)
        {
            r = 256;
            g = 256;
            b = 256;
            RField.setText("256");
            GField.setText("256");
            BField.setText("256");

        }


    }
          
    public void stateChanged(ChangeEvent e)
    {
    	if(e.getSource().equals(RlowerBound) || e.getSource().equals(GlowerBound) || e.getSource().equals(BlowerBound))
    		triggerLowerBound();
    	if(e.getSource().equals(RupperBound) || e.getSource().equals(GupperBound) || e.getSource().equals(BupperBound))
    		triggerUpperBound();
    	if(e.getSource().equals(redAlpha) || e.getSource().equals(greenAlpha) || e.getSource().equals(blueAlpha))
    		triggerAlpha(e);
    	if(e.getSource().equals(redLu) || e.getSource().equals(greenLu) || e.getSource().equals(blueLu))
    		triggerLu(e);
    }
    
    public void triggerAlpha(ChangeEvent e)
    {
    	if(e.getSource().equals(redAlpha))
		{
    		((ColorLabel)redColors.getSelectedItem()).setAlpha(redAlpha.getValue());
    		Color c = ((ColorLabel)redColors.getSelectedItem()).getColor();      		
			fL.setRedThresholdColor(c);
		}
    	if(e.getSource().equals(greenAlpha))
		{
    		((ColorLabel)greenColors.getSelectedItem()).setAlpha(greenAlpha.getValue());
    		Color c = ((ColorLabel)greenColors.getSelectedItem()).getColor();
			fL.setGreenThresholdColor(c);
		}
    	if(e.getSource().equals(blueAlpha))
		{
    		((ColorLabel)blueColors.getSelectedItem()).setAlpha(blueAlpha.getValue());
    		Color c = ((ColorLabel)blueColors.getSelectedItem()).getColor();
			fL.setBlueThresholdColor(c);
		}
		
			
		
    }
    
    public void triggerLu(ChangeEvent e)
    {
    	fL.setLookUp(redLu.getValue(),greenLu.getValue(),blueLu.getValue());		
    }
    
    public void triggerUpperBound()
    {    	
    	
    	str = RupperBound.getValue();
    	stg = GupperBound.getValue();
    	stb = BupperBound.getValue();
    	
    	checkUpperBounds();
    	
    	RField2.setText(Integer.toString(str));
		GField2.setText(Integer.toString(stg));
		BField2.setText(Integer.toString(stb));
		fL.setstThreshold(str,stg,stb);
		repaint();
    }
    
    public void triggerLowerBound()
    {    	
    	r = RlowerBound.getValue();
    	g = GlowerBound.getValue();
    	b = BlowerBound.getValue();
    	
    	RField.setText(Integer.toString(r));
    	GField.setText(Integer.toString(g));
    	BField.setText(Integer.toString(b));   	
    	
    	suspendListeners();
    	RupperBound.setMinimum(r);    	
    	GupperBound.setMinimum(g);
    	BupperBound.setMinimum(b);
    	checkUpperBounds();
    	reactivateListeners();
    	
    	fL.setThreshold(RlowerBound.getValue(),GlowerBound.getValue(),BlowerBound.getValue());
    	repaint();
    	
    }
    
    public void setCalibration(double d)
    {
    	calibrationField.setText(new Double(d).toString());
    }
    
    public void itemStateChanged(ItemEvent e)
    {
    	if(e.getStateChange() == ItemEvent.SELECTED)
    		{
    		if(e.getSource().equals(redColors))
    			{
    			Color c = ((ColorLabel)redColors.getSelectedItem()).getColor();
    			redAlpha.setValue(c.getAlpha());
    			fL.setRedThresholdColor(c);
    			}
    		if(e.getSource().equals(greenColors))
			{
			Color c = ((ColorLabel)greenColors.getSelectedItem()).getColor();
			greenAlpha.setValue(c.getAlpha());
			fL.setGreenThresholdColor(c);
			}
    		if(e.getSource().equals(blueColors))
			{
			Color c = ((ColorLabel)blueColors.getSelectedItem()).getColor();
			blueAlpha.setValue(c.getAlpha());
			fL.setBlueThresholdColor(c);
			}
    			
    		}
    }
    
    public void mousePressed(MouseEvent e)
    {
    	
    }
    
    public void mouseClicked(MouseEvent e)
    {
    	if(e.getSource() == RLabel)
    	{
    		thresholdContainer.removeAll();
    		thresholdContainer.add(greenP);
    		thresholdContainer.validate();
    		thresholdContainer.repaint();
    	}
    	if(e.getSource() == GLabel)
    	{
    		thresholdContainer.removeAll();
    		thresholdContainer.add(blueP);
    		thresholdContainer.validate();
    		thresholdContainer.repaint();
    	}
    	if(e.getSource() == BLabel)
    	{
    		thresholdContainer.removeAll();
    		thresholdContainer.add(redP);
    		thresholdContainer.validate();
    		thresholdContainer.repaint();
    	}
    }
    
    public void mouseReleased(MouseEvent e)
    {
    	if(e.getSource() == RlowerBound || e.getSource() == GlowerBound || e.getSource() == BlowerBound)
    	{
    		fL.measureColocalization();
    		fL.repaintDataPane();
    	}
    }
    
    public void mouseEntered(MouseEvent e)
    {
    	
    }
    
    public void mouseExited(MouseEvent e)
    {
    	
    }
    
    public void resetThresholdOptionsPanel()
    {
    	//sets the options in TOP as stored in tiff panel
    	r = fL.getRedThreshold();
    	g = fL.getGreenThreshold();
    	b = fL.getBlueThreshold();
    	str = fL.getstRedThreshold();
    	stg = fL.getstGreenThreshold();
    	stb = fL.getstBlueThreshold();
    	
    	RField.setText(Integer.toString(r));
    	GField.setText(Integer.toString(g));
    	BField.setText(Integer.toString(b));
    	
    	RField2.setText(Integer.toString(str));
    	GField2.setText(Integer.toString(stg));
    	BField2.setText(Integer.toString(stb));    
    	
    	getColors();    	
    	repaint();
    	
    }
    
    public void checkUpperBounds()
    {
    	if(str < r)
    		{
    		str = r;
    		RupperBound.setValue(str);
    		RField2.setText(Integer.toString(str));
    		}
    	if(stg < g)
    		{
    		stg = g;
    		GupperBound.setValue(stg);
    		GField2.setText(Integer.toString(stg));
    		
    		}
    	if(stb < b)
    		{
    		stb = b;
    		BupperBound.setValue(stb);
    		BField2.setText(Integer.toString(stb));
    		
    		}
    }
    
    public void fireNTextFieldEvent(NTextField e)
    {
    	if(e == dendriteWidth)
    	{
    		fL.setDendriteWidth(Integer.parseInt(dendriteWidth.getText()));
    		return;
    	}
    	if(e == calibrationField)
    	{
    		fL.setCalibration(Double.parseDouble(calibrationField.getText()));
    		fL.repaintDataPane();
    		return;
    	}
    	if(e == RField)
    	{
    		getColors();            
            fL.setThreshold(r,g,b);
            fL.measureColocalization();
    		
    	}
    	if(e == RField2)
    	{
    		
    	}
    	
    	if(e == GField)
    	{
    		getColors();            
            fL.setThreshold(r,g,b);
            fL.measureColocalization();
    	}
    	
    	if(e == GField2)
    	{
    		
    	}
    	
    	if(e == BField)
    	{
    		getColors();            
            fL.setThreshold(r,g,b);
            fL.measureColocalization();
    	}
    	if(e == BField2)
    	{
    		
    	}
    }

}


