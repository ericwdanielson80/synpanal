package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Swing dialog that lets the user edit the percent-overlap based auto-ignore
 * rules for puncta of the three color channels (red, green, blue). The frame
 * lays out a 4x4 grid where each row corresponds to a puncta color and each
 * column corresponds to a percent-of-other-color threshold; the diagonal
 * cells (a color compared against itself) are fixed at 100% and non-editable.
 * Values entered into the editable text fields are parsed back into the
 * shared IgnoreCriteria array when the user confirms, and the linked
 * functionListener is notified so the data views can be repainted.
 */
public class IgnoreCriteriaFrame extends JFrame implements MouseListener{
JPanel center;
JPanel bottom;

JButton confirm;
JButton close;
JCheckBox autoIgnore;

JLabel[] rowTitles;
JLabel[] columnTitles;

JTextField[] redValues;
JTextField[] greenValues;
JTextField[] blueValues;


functionListener fL;

IgnoreCriteria[] iC; 

	/**
	 * Builds the dialog using the three supplied IgnoreCriteria (one per color
	 * channel) or synthesizes a default set if null is passed in. The
	 * constructor wires up the labels, the 3x3 matrix of JTextFields that
	 * display the current percentage thresholds, the color-coded backgrounds,
	 * and the confirm/close/auto-ignore controls. The parameter ignoreCriteria
	 * is the current rule set (may be null for defaults); fl is the
	 * functionListener that will receive the updated criteria on confirm.
	 * Local fields rowTitles, columnTitles, redValues, greenValues, and
	 * blueValues hold the per-row and per-column widgets, while center and
	 * bottom are the two JPanels laid out inside the content pane.
	 */
	public IgnoreCriteriaFrame(IgnoreCriteria[] ignoreCriteria, functionListener fl)
	{
		fL = fl;
	
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		iC = ignoreCriteria;
		if(iC == null)
			createIgnoreCriteria();
		
		rowTitles = new JLabel[] {new JLabel("Red Puncta"),new JLabel("Green Puncta"),new JLabel("Blue Puncta")};
		columnTitles = new JLabel[] {new JLabel("Percent Red"),new JLabel("Percent Green"),new JLabel("Percent Blue")};
		
		redValues = new JTextField[] {new JTextField(iC[0].getText(0)),new JTextField(iC[0].getText(1)),new JTextField(iC[0].getText(2))};
		greenValues = new JTextField[] {new JTextField(iC[1].getText(0)),new JTextField(iC[1].getText(1)),new JTextField(iC[1].getText(2))};
		blueValues = new JTextField[] {new JTextField(iC[2].getText(0)),new JTextField(iC[2].getText(1)),new JTextField(iC[2].getText(2))};
		
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
		
		redValues[0].setEditable(false);
		greenValues[1].setEditable(false);
		blueValues[2].setEditable(false);
		
		confirm = new JButton("Confirm");
		confirm.addMouseListener(this);
		
		close = new JButton("Close");
		close.addMouseListener(this);
		
		autoIgnore = new JCheckBox("Auto Ignore");
		
		
		center = new JPanel();
		center.setLayout(new GridLayout(4,4));
		
		//first row
		center.add(Box.createRigidArea(new Dimension(20,0)));
		center.add(rowTitles[0]);
		center.add(rowTitles[1]);
		center.add(rowTitles[2]);
		
		//second row
		center.add(columnTitles[0]);
		center.add(redValues[0]);
		center.add(greenValues[0]);
		center.add(blueValues[0]);
		
		//third row
		center.add(columnTitles[1]);
		center.add(redValues[1]);
		center.add(greenValues[1]);
		center.add(blueValues[1]);
		
		//third row
		center.add(columnTitles[2]);
		center.add(redValues[2]);
		center.add(greenValues[2]);
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
	 * Populates the iC array with the default IgnoreCriteria, one per color
	 * channel. Each default requires 100% self-color overlap and 0% overlap
	 * with the other two channels, so that by default puncta are not
	 * auto-ignored on either of the foreign-color axes.
	 */
	public void createIgnoreCriteria()
	{
		iC = new IgnoreCriteria[3];
		iC[0] = new IgnoreCriteria(100,0,0);
		iC[1] = new IgnoreCriteria(0,100,0);
		iC[2] = new IgnoreCriteria(0,0,100);
	}
	
	/**
	 * Reads the current values out of the three rows of text fields, parses
	 * them into int[] arrays via getInt, and pushes them into each
	 * IgnoreCriteria object. It also synchronizes the auto-ignore flag from
	 * the checkbox into all three IgnoreCriteria so they know whether to
	 * actually apply their percentage rules.
	 */
	public void loadIgnoreCriteria()
	{
		iC[0].load(getInt(redValues));
		iC[1].load(getInt(greenValues));
		iC[2].load(getInt(blueValues));
		iC[0].autoIgnorePercentages(autoIgnore.isSelected());
		iC[1].autoIgnorePercentages(autoIgnore.isSelected());
		iC[2].autoIgnorePercentages(autoIgnore.isSelected());
	}
		
	/**
	 * Converts a row of three JTextFields into a length-3 int array by
	 * delegating each text value to getInfo for safe parsing. The returned
	 * array out holds the parsed thresholds in the same index order as the
	 * input row r.
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
	 * Parses a String to an int, returning zero if the input does not
	 * represent a valid integer. The parameter s is the text to parse, and
	 * the local out defaults to zero and is overwritten only on a successful
	 * parse so that malformed user input never throws through to the caller.
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
	
	/** No-op MouseListener callback; mouse press events are not handled by this frame. */
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	/** No-op MouseListener callback; mouse release events are not handled by this frame. */
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
	/**
	 * Dispatches clicks on the two bottom buttons. Clicking close disposes of
	 * the frame; clicking confirm first loads the text-field values into the
	 * IgnoreCriteria via loadIgnoreCriteria, activates the percentage mode on
	 * all three criteria, pushes the updated array back through the
	 * functionListener, triggers a data pane repaint, and finally disposes
	 * of the frame. The parameter e is the standard AWT mouse event used only
	 * to identify which component was clicked.
	 */
	public void mouseClicked(MouseEvent e)
	{
		if(e.getSource() == close)
			this.dispose();
		if(e.getSource() == confirm)
		{
			loadIgnoreCriteria();
			iC[0].activateP();
			iC[1].activateP();
			iC[2].activateP();
			fL.setIgnoreCriteria(iC);
			fL.repaintDataPane();
			this.dispose();
		}
	}
	
	/** No-op MouseListener callback; mouse entry events are not handled by this frame. */
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	/** No-op MouseListener callback; mouse exit events are not handled by this frame. */
	public void mouseExited(MouseEvent e)
	{
		
	}

}
