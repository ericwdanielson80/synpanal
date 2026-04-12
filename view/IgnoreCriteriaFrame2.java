package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Swing dialog that lets the user edit the overlap-based auto-ignore rules
 * for puncta of the three color channels. Unlike its sibling
 * IgnoreCriteriaFrame (which uses percentage text fields), this variant
 * exposes a 3x3 matrix of JCheckBoxes asking "keep a puncta of color X if
 * it overlaps color Y?". A separate row of restoreColors checkboxes toggles
 * whether each color should run the restoration step. On confirm the
 * selections are pushed back into the IgnoreCriteria array via the
 * functionListener.
 */
public class IgnoreCriteriaFrame2 extends JFrame implements MouseListener {
	JPanel center;
	JPanel bottom;

	JButton confirm;
	JButton close;
	JCheckBox autoIgnore;

	JLabel[] rowTitles;
	JLabel[] columnTitles;

	JCheckBox[] redValues;
	JCheckBox[] greenValues;
	JCheckBox[] blueValues;
	
	JCheckBox[] restoreColors;


	functionListener fL;

	IgnoreCriteria[] iC; 
	
	/**
	 * Builds the dialog using the three supplied IgnoreCriteria or falls back
	 * to default rules when null. It lays out a 5x4 grid containing the
	 * restore-colors row, the row-color titles, the column-color titles, and
	 * the 3x3 matrix of overlap checkboxes with color-coded backgrounds, plus
	 * a confirm/close/auto-ignore button row at the bottom. The parameter
	 * ignoreCriteria is the starting rule set and fl is the functionListener
	 * that will receive the updated criteria on confirm.
	 */
	public IgnoreCriteriaFrame2(IgnoreCriteria[] ignoreCriteria, functionListener fl)
	{
		fL = fl;
	
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		iC = ignoreCriteria;
		if(iC == null)
			createIgnoreCriteria();
		
		rowTitles = new JLabel[] {new JLabel("Red Puncta"),new JLabel("Green Puncta"),new JLabel("Blue Puncta")};
		columnTitles = new JLabel[] {new JLabel("Keep if overlaps Red"),new JLabel("Keep if overlaps Green"),new JLabel("Keep if overlaps Blue")};
		
		redValues = new JCheckBox[] {new JCheckBox("",iC[0].overlap[0]),new JCheckBox("",iC[0].overlap[1]),new JCheckBox("",iC[0].overlap[2])};
		greenValues = new JCheckBox[] {new JCheckBox("",iC[1].overlap[0]),new JCheckBox("",iC[1].overlap[1]),new JCheckBox("",iC[1].overlap[2])};
		blueValues = new JCheckBox[] {new JCheckBox("",iC[2].overlap[0]),new JCheckBox("",iC[2].overlap[1]),new JCheckBox("",iC[2].overlap[2])};
		
		rowTitles[0].setForeground(Color.red);
		rowTitles[1].setForeground(Color.green);
		rowTitles[2].setForeground(Color.blue);
		
		columnTitles[0].setForeground(Color.red);
		columnTitles[1].setForeground(Color.green);
		columnTitles[2].setForeground(Color.blue);
		
		redValues[0].setBackground(Color.red);
		redValues[1].setBackground(Color.red);
		redValues[2].setBackground(Color.red);
		
		greenValues[0].setBackground(Color.green);
		greenValues[1].setBackground(Color.green);
		greenValues[2].setBackground(Color.green);
		
		blueValues[0].setBackground(Color.blue);
		blueValues[1].setBackground(Color.blue);
		blueValues[2].setBackground(Color.blue);
		
		blueValues[0].setForeground(Color.white);
		blueValues[1].setForeground(Color.white);
		blueValues[2].setForeground(Color.white);
		
		redValues[0].setEnabled(false);
		greenValues[1].setEnabled(false);
		blueValues[2].setEnabled(false);
		
		confirm = new JButton("Confirm");
		confirm.addMouseListener(this);
		
		close = new JButton("Close");
		close.addMouseListener(this);
		
		restoreColors = new JCheckBox[3];
		restoreColors[0] = new JCheckBox(" ",false);
		restoreColors[1] = new JCheckBox(" ",false);
		restoreColors[2] = new JCheckBox(" ",false);
		
		autoIgnore = new JCheckBox("Auto Ignore");
		
		
		center = new JPanel();
		center.setLayout(new GridLayout(5,4));
		
		//0 row
		center.add(Box.createRigidArea(new Dimension(20,0)));
		center.add(restoreColors[0]);
		center.add(restoreColors[1]);
		center.add(restoreColors[2]);
		
		//first row
		center.add(Box.createRigidArea(new Dimension(20,0)));
		center.add(rowTitles[0]);
		center.add(rowTitles[1]);
		center.add(rowTitles[2]);
		
		//second row
		center.add(columnTitles[0]);
		center.add(redValues[0]);
		center.add(redValues[1]);
		center.add(redValues[2]);
		
		//third row
		center.add(columnTitles[1]);
		center.add(greenValues[0]);
		center.add(greenValues[1]);
		center.add(greenValues[2]);
		
		//third row
		center.add(columnTitles[2]);
		center.add(blueValues[0]);
		center.add(blueValues[1]);
		center.add(blueValues[2]);
				
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(center,BorderLayout.CENTER);
		
		bottom = new JPanel();
		bottom.add(confirm);
		bottom.add(close);
		bottom.add(autoIgnore);
		
		getContentPane().add(bottom,BorderLayout.SOUTH);
		
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		
	}
	
	/**
	 * Builds a default IgnoreCriteria array when none is provided: each
	 * channel is set to 100% self-overlap, 0% foreign-overlap, and its
	 * own-color overlap checkbox is pre-selected so that by default puncta
	 * of each color are simply kept.
	 */
	public void createIgnoreCriteria()
	{
		iC = new IgnoreCriteria[3];
		iC[0] = new IgnoreCriteria(100,0,0);
		iC[1] = new IgnoreCriteria(0,100,0);
		iC[2] = new IgnoreCriteria(0,0,100);
		iC[0].load(new boolean[] {true,false,false});
		iC[1].load(new boolean[] {false,true,false});
		iC[2].load(new boolean[] {false,false,true});
		
		
	}
	
	/**
	 * Harvests the checkbox state from each row of overlap checkboxes and
	 * pushes the resulting boolean arrays into the IgnoreCriteria objects,
	 * then synchronizes the "auto ignore overlap" flag on every criterion
	 * based on the autoIgnore checkbox.
	 */
	public void loadIgnoreCriteria()
	{
		iC[0].load(getBoolean(redValues));
		iC[1].load(getBoolean(greenValues));
		iC[2].load(getBoolean(blueValues));
		iC[0].autoIgnoreOverlap(autoIgnore.isSelected());
		iC[1].autoIgnoreOverlap(autoIgnore.isSelected());
		iC[2].autoIgnoreOverlap(autoIgnore.isSelected());
	}
		
	/**
	 * Unused helper retained for API symmetry; converts a row of three
	 * JTextFields into an int array by parsing each via getInfo.
	 */
	public int[] getInt(JTextField[] r)
	{
		int[] out = new int[3];
		out[0] = getInfo(r[0].getText());
		out[1] = getInfo(r[1].getText());
		out[2] = getInfo(r[2].getText());
		return out;
	}
	
	/**
	 * Reads a length-3 row of checkboxes into a boolean array mirroring the
	 * selected state of each, so the results can be fed into IgnoreCriteria.
	 */
	public boolean[] getBoolean(JCheckBox[] r)
	{
		boolean[] out = new boolean[3];
		out[0] = r[0].isSelected();
		out[1] = r[1].isSelected();
		out[2] = r[2].isSelected();
		return out;
	}
	
	/**
	 * Parses a String into an int, catching NumberFormatException and
	 * returning zero so malformed input is treated as a default.
	 */
	public int getInfo(String s)
	{
		int out = 0;
		try{
			out = Integer.parseInt(s);			
			}
		catch(NumberFormatException ex)
		{
			out = 0;			
		}
		return out;
	}
	
	/** Unused MouseListener callback. */
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	/** Unused MouseListener callback. */
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	/**
	 * Routes clicks on the two bottom buttons. Clicking close disposes of
	 * the frame; clicking confirm pulls the checkbox state into the
	 * IgnoreCriteria array, copies the restoreColors checkbox state into
	 * each criterion's activeO flag, pushes the updated array through the
	 * functionListener, triggers a data pane repaint, and disposes of the
	 * frame. The parameter e identifies which button was clicked.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if(e.getSource() == close)
			this.dispose();
		if(e.getSource() == confirm)
		{
			loadIgnoreCriteria();
			iC[0].activeO = restoreColors[0].isSelected();
			iC[1].activeO = restoreColors[1].isSelected();
			iC[2].activeO = restoreColors[2].isSelected();
			fL.setIgnoreCriteria(iC);
			fL.repaintDataPane();
			this.dispose();
		}
	}
	
	/** Unused MouseListener callback. */
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	/** Unused MouseListener callback. */
	public void mouseExited(MouseEvent e)
	{
		
	}

}
