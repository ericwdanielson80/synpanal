package neuron_analyzer;

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
	
	public void loadIgnoreCriteria()
	{
		iC[0].load(getBoolean(redValues));
		iC[1].load(getBoolean(greenValues));
		iC[2].load(getBoolean(blueValues));
		iC[0].autoIgnoreOverlap(autoIgnore.isSelected());
		iC[1].autoIgnoreOverlap(autoIgnore.isSelected());
		iC[2].autoIgnoreOverlap(autoIgnore.isSelected());
	}
		
	public int[] getInt(JTextField[] r)
	{
		int[] out = new int[3];
		out[0] = getInfo(r[0].getText());
		out[1] = getInfo(r[1].getText());
		out[2] = getInfo(r[2].getText());
		return out;
	}
	
	public boolean[] getBoolean(JCheckBox[] r)
	{
		boolean[] out = new boolean[3];
		out[0] = r[0].isSelected();
		out[1] = r[1].isSelected();
		out[2] = r[2].isSelected();
		return out;
	}
	
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
	
	public void mousePressed(MouseEvent e)
	{
		
	}
	
	public void mouseReleased(MouseEvent e)
	{
		
	}
	
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
	
	public void mouseEntered(MouseEvent e)
	{
		
	}
	
	public void mouseExited(MouseEvent e)
	{
		
	}

}
