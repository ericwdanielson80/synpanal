package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Graphics;
import java.awt.Font;
import java.text.DecimalFormat;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.Color;


/**
 * Editable text field that displays and edits the current image zoom
 * percentage. It extends NTextField so that double-clicking toggles it into
 * edit mode, and on commit it applies the new zoom level to the controller
 * via functionListener.setZoom. Numeric output is formatted with a
 * DecimalFormat that keeps two decimal places.
 */
public class ZoomLabel extends NTextField{
functionListener fL;
DecimalFormat dF = new DecimalFormat("#######.##");
//ImageIcon ZONIcon;
	/**
	 * Creates a ZoomLabel initialized to display "100" and wired to the
	 * supplied controller. The f parameter is the shared functionListener
	 * from which the current zoom value is read and to which updated zoom
	 * values are pushed. The superclass NTextField constructor is invoked
	 * with a null NTextFieldListener because this subclass overrides
	 * doCommand directly instead of using the listener callback.
	 */
	public ZoomLabel(functionListener f)
	{
		super(null,"100");		
		fL = f;
		//ZONIcon = new ImageIcon(ImagePropertiesPanel.class.getResource("Zoom.gif"));
		//Font font = this.getFont();
		//font = font.deriveFont(Font.BOLD);
		//this.setFont(font);
		//this.setForeground(Color.white);
		//this.setOpaque(false);
	}
	
	/**
	 * Requests that the label redraw its content. Calling setText(null)
	 * triggers the override below, which re-reads the current zoom from the
	 * functionListener and formats it; the null argument is ignored.
	 */
	public void updateZoom()
	{
		setText(null);
	}

	/**
	 * Applies the edited value. Parses the current text as an integer, pushes
	 * it to the controller via fL.setZoom, then returns the field to read-only
	 * mode by disabling editability, detaching the key listener and
	 * transferring keyboard focus away. Overrides NTextField.doCommand so that
	 * the controller is updated directly instead of via the listener callback.
	 */
	public void doCommand()
	{
		fL.setZoom(new Integer(getText()).intValue());		
		this.setEditable(false);
		this.removeKeyListener(this);
		this.transferFocus();
	}
	
	/**
	 * Overrides JTextField.setText so that the displayed value always reflects
	 * the controller's current zoom rather than an externally supplied string.
	 * The text parameter is ignored; if the functionListener is wired up the
	 * field is set to the formatted zoom value, otherwise it falls back to
	 * the literal "100".
	 */
	public void setText(String text)
	{
		if(fL != null)			
		super.setText(dF.format(fL.getZoom()));
		else
			super.setText(new Integer(100).toString());	
	}
	/*public void paint(Graphics g)
	{
		ZONIcon.paintIcon(this,g,0,0);
		super.paint(g);
	}*/
	
	
}
