package neuron_analyzer;

import javax.swing.*;


public class OptionsFrame{
functionListener fL;
	public OptionsFrame(functionListener f)
	{
		fL = f;
	}
	
	public String chooseCategories(JFrame f)
	{
		pause();
		String s = (String)JOptionPane.showInputDialog(
                f,
                "enter categories seperated by : " + '\n' + "e.g. Ctrl:Transfected",
                "Enter Categories",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "Ctrl:Transfected");	
		unpause();
		return s;
	}
	
	public String newCategories(JFrame f)
	{
		pause();
		String s = (String)JOptionPane.showInputDialog(
                f,
                "enter categories name " + '\n' + "e.g. Ctrl",
                "New Category",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "Ctrl");		
		unpause();
		return s;
	}
	
	public int newPunctaLimit(JFrame f, String color, int currentSize)
	{
		pause();
		String s = (String)JOptionPane.showInputDialog(
                f,
                "set minimum " + color + " puncta size " + '\n' + "must be >= 4",
                "set minimum puncta size",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                new Integer(currentSize).toString());		
		unpause();
		if(s == null)
		{
			return currentSize;
		}
		int out = 4;
		try{
			out = Integer.valueOf(s).intValue();
		}
		catch(NumberFormatException e)
		{
			
		}
		return out;
	}
	
	public String newProfile(JFrame f)
	{
		pause();
		String s = (String)JOptionPane.showInputDialog(
                f,
                "enter profile name " + '\n' + "e.g. Spine density",
                "New Profile",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "New Profile");		
		unpause();
		return s;
	}
	
	public boolean confirmDelete(JFrame f)
	{
		
		pause();
		int k = JOptionPane.showConfirmDialog(f, "Deleting Tab will permanently" + '\n' +
				"destroy all contained regions" + '\n' + "are you sure you want to continue?"
				,"Delete Tab", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		unpause();
		if(k == JOptionPane.OK_OPTION)
			return true;
		else
			return false;
                
	}
	
	public void pause()
	{
		if(fL != null)
			fL.pauseKeyListener(false);
	}
	
	public void unpause()
	{
		if(fL != null)
			fL.pauseKeyListener(true);
	}
}

