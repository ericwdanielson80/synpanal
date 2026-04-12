package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.*;


/**
 * Collection of JOptionPane-based dialogs used to prompt the user for group
 * definitions, profile names, minimum puncta sizes and deletion
 * confirmations. Each method brackets the dialog with pause/unpause calls
 * on the main functionListener's key listener so keyboard shortcuts in the
 * background do not fire while a dialog is open.
 */
public class OptionsFrame{
functionListener fL;
	/**
	 * Stores the supplied functionListener so the pause/unpause helpers can
	 * later toggle the application's main key listener while dialogs are
	 * shown.
	 */
	public OptionsFrame(functionListener f)
	{
		fL = f;
	}

	/**
	 * Shows an input dialog asking the user to enter colon-separated
	 * category names (e.g. "Ctrl:Transfected"). The f parameter is the
	 * parent JFrame used for modality, and the method pauses the key
	 * listener around the dialog so keyboard shortcuts do not fire.
	 * Returns the entered string (possibly null if cancelled).
	 */
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
	
	/**
	 * Shows an input dialog prompting for a single new category name. The f
	 * parameter is the parent frame; the default value "Ctrl" is pre-filled.
	 * Returns the user-entered string or null if cancelled.
	 */
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
	
	/**
	 * Asks the user for a new minimum puncta size for a given color channel
	 * and returns the new value as an integer. The f parameter is the
	 * parent frame, color is the channel name shown in the prompt, and
	 * currentSize is the starting value displayed in the input. If the
	 * user cancels, currentSize is returned unchanged; if parsing fails
	 * the fallback value 4 (assigned to the local out) is returned.
	 */
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
	
	/**
	 * Shows an input dialog asking for a new analysis profile name (for
	 * example "Spine density"). The f parameter is the parent frame;
	 * returns the entered string or null when cancelled.
	 */
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
	
	/**
	 * Prompts for confirmation before deleting a group tab and all of its
	 * contained regions. Returns true only when the user clicks OK on the
	 * warning dialog; false otherwise or when the dialog is dismissed.
	 * The f parameter is the parent frame. The local k captures the option
	 * chosen by the user.
	 */
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
	
	/**
	 * Disables the main key listener while a dialog is open by calling
	 * fL.pauseKeyListener(false). The null check guards against tests or
	 * harness uses that supply no controller.
	 */
	public void pause()
	{
		if(fL != null)
			fL.pauseKeyListener(false);
	}

	/**
	 * Re-enables the main key listener after a dialog closes by calling
	 * fL.pauseKeyListener(true). The null check mirrors pause().
	 */
	public void unpause()
	{
		if(fL != null)
			fL.pauseKeyListener(true);
	}
}

