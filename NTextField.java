package neuron_analyzer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class NTextField extends JTextField implements MouseListener,
		KeyListener {
NTextFieldListener myListener;
	public NTextField(NTextFieldListener l, String s)
	{
		super(s);
		myListener = l;
		this.setEditable(false);		
		addMouseListener(this);
	}
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
	
	public void doCommand()
	{
		myListener.fireNTextFieldEvent(this);
		this.setEditable(false);
		this.removeKeyListener(this);
		this.transferFocus();
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_ENTER: doCommand(); return;
		}
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
