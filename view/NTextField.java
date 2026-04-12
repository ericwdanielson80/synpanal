package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

/**
 * Custom JTextField that starts in a non-editable state and toggles into edit
 * mode on a single click, selecting all of its text so the user can type
 * over it. A double-click or Enter keypress or a second click commits the
 * value by invoking doCommand, which in turn notifies the attached
 * NTextFieldListener, reverts the field to read-only and transfers focus
 * away. Used throughout the options panels for numeric fields.
 */
public class NTextField extends JTextField implements MouseListener,
		KeyListener {
NTextFieldListener myListener;
	/**
	 * Builds the field with the given initial text and listener, starting in
	 * read-only mode with a mouse listener attached. The l parameter is the
	 * object notified on commit (may be null when subclasses override
	 * doCommand directly), and s is the initial string shown.
	 */
	public NTextField(NTextFieldListener l, String s)
	{
		super(s);
		myListener = l;
		this.setEditable(false);		
		addMouseListener(this);
	}
	/**
	 * Handles a mouse click by either committing (on double click or when
	 * already editable) via doCommand, or switching the field into editable
	 * mode, attaching the key listener and selecting all current text. The e
	 * parameter is the Swing MouseEvent carrying click count and button.
	 */
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(e.getClickCount() == 2)
		{
			doCommand();
			return;
		}
		
		if(this.isEditable())
		{			
			doCommand();
		}
		else
		{
			this.setEditable(true);
			addKeyListener(this);			
			//this.setCaretPosition(this.getText().length());
			this.setSelectionStart(0);
			this.setSelectionEnd(this.getText().length());
		}
		
	}
	
	/**
	 * Fires the commit callback by calling myListener.fireNTextFieldEvent on
	 * this field, then reverts to read-only, detaches the key listener and
	 * transfers focus away. Subclasses may override this to customize the
	 * commit behaviour (for example ZoomLabel routes the value into the
	 * controller directly).
	 */
	public void doCommand()
	{
		myListener.fireNTextFieldEvent(this);
		this.setEditable(false);
		this.removeKeyListener(this);
		this.transferFocus();
	}

	/** No-op mouse-entered hook; required by the MouseListener interface. */
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op mouse-exited hook; required by the MouseListener interface. */
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op mouse-pressed hook; required by the MouseListener interface. */
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op mouse-released hook; required by the MouseListener interface. */
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * Commits the field when the Enter key is pressed by delegating to
	 * doCommand; other keys are ignored. The e parameter carries the key
	 * code examined by the switch.
	 */
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_ENTER: doCommand(); return;
		}
	}

	/** No-op key-released hook; required by the KeyListener interface. */
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/** No-op key-typed hook; required by the KeyListener interface. */
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
