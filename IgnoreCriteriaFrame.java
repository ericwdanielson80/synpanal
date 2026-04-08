package neuron_analyzer;

import javax.swing.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;

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
	
	public void createIgnoreCriteria()
	{
		iC = new IgnoreCriteria[3];
		iC[0] = new IgnoreCriteria(100,0,0);
		iC[1] = new IgnoreCriteria(0,100,0);
		iC[2] = new IgnoreCriteria(0,0,100);
	}
	
	public void loadIgnoreCriteria()
	{
		iC[0].load(getInt(redValues));
		iC[1].load(getInt(greenValues));
		iC[2].load(getInt(blueValues));
		iC[0].autoIgnorePercentages(autoIgnore.isSelected());
		iC[1].autoIgnorePercentages(autoIgnore.isSelected());
		iC[2].autoIgnorePercentages(autoIgnore.isSelected());
	}
		
	public int[] getInt(JTextField[] r)
	{
		int[] out = new int[3];
		out[0] = getInfo(r[0].getText());
		out[1] = getInfo(r[1].getText());
		out[2] = getInfo(r[2].getText());
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
			iC[0].activateP();
			iC[1].activateP();
			iC[2].activateP();
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
