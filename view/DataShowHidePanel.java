package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * This file provides a small toolbar panel with a toggle-style "show/hide"
 * button used to collapse/expand a neighboring data view.
 * <br>
 * Classes in this file:
 * <ul>
 * <li>DataShowHidePanel: the JPanel host, holding the icon-backed JButton and
 * swapping its icon based on mouse state.</li>
 * <li>DataShowHidePanel_SHButton_actionAdapter: ActionListener adapter that
 * routes button clicks back to the host panel.</li>
 * <li>DataShowHidePanel_SHButton_mouseAdapter: MouseAdapter that routes
 * mouse press/release events back to the host panel.</li>
 * </ul>
 */
/**
 * Panel containing the show/hide toggle button with its two icons
 * (normal and pressed). Responsible for layout and event wiring.
 */
public class DataShowHidePanel extends JPanel {
    JButton SHButton;
    FlowLayout flowLayout1 = new FlowLayout();
    ImageIcon Show;
    ImageIcon Pressed;

    /** Builds the panel by invoking jbInit, printing any exceptions. */
    public DataShowHidePanel() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /** Sets up the layout, loads the two icons, and wires up listeners on the button. */
    private void jbInit() throws Exception {
        this.setLayout(flowLayout1);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setPreferredSize(new Dimension(34, 564));
        Show = new ImageIcon(ImagePropertiesPanel.class.getResource("ShowHide.gif"));
        Pressed = new ImageIcon(ImagePropertiesPanel.class.getResource("ShowHidePressed.gif"));
        SHButton = new JButton(Show);
        SHButton.setPreferredSize(new Dimension(30,120));
        SHButton.setBorderPainted(false);
        SHButton.addMouseListener(new DataShowHidePanel_SHButton_mouseAdapter(this));
        SHButton.addActionListener(new DataShowHidePanel_SHButton_actionAdapter(this));
        this.add(SHButton, null);
    }

    /** Action callback for the show/hide button; currently a no-op hook. */
    public void SHButton_actionPerformed(ActionEvent e) {

    }

    /** Switches the button to the pressed icon while the mouse is held down. */
    public void SHButton_mousePressed(MouseEvent e) {
SHButton.setIcon(Pressed);
    }

    /** Restores the default show/hide icon when the mouse is released. */
    public void SHButton_mouseReleased(MouseEvent e) {
        SHButton.setIcon(Show);

    }
}
