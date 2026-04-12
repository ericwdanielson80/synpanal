package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Contains the TiffScrollPane widget (a JScrollPane that hosts a TiffPanel)
 * together with three small adapter classes that forward mouse events from
 * the scroll pane back to its outer instance:
 * <ul>
 *   <li>TiffScrollPane - the scroll pane that always shows both scroll bars
 *   and captures the TiffPanel's viewport for later use.</li>
 *   <li>TiffScrollPane_this_mouseMotionAdapter - forwards mouseMoved events
 *   to the enclosing TiffScrollPane.</li>
 *   <li>TiffScrollPane_this_mouseAdapter - forwards mouseClicked events.</li>
 *   <li>TiffScrollPane_this_mouseWheelAdapter - forwards mouse-wheel events.</li>
 * </ul>
 */
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
 * JScrollPane subclass that hosts a TiffPanel image view. It forces both
 * scroll bars to be permanently visible and exposes hooks for mouse
 * events used by the adapter classes in this file.
 */
public class TiffScrollPane extends JScrollPane {
    /** No-arg constructor that simply runs jbInit to install the scroll bar policies. */
    public TiffScrollPane() {
        try {
           jbInit();
       } catch (Exception ex) {
           ex.printStackTrace();
       }


    }


    /** Constructs a scroll pane around the given TiffPanel and records its viewport on tp.myView. */
    public TiffScrollPane(TiffPanel tp) {
        super(tp);
        try {
           jbInit();
       } catch (Exception ex) {
           ex.printStackTrace();
       }
       tp.myView = super.getViewport();




    }

    /** Sets both horizontal and vertical scroll-bar policies to always-shown. */
    private void jbInit() throws Exception {

        this.setHorizontalScrollBarPolicy(JScrollPane.
                                          HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


    }

    /** Empty mouse-clicked callback; intended to be overridden or supplied by subclasses. */
    public void this_mouseClicked(MouseEvent e) {


    }

    /** Empty mouse-moved callback; intended to be overridden or supplied by subclasses. */
    public void this_mouseMoved(MouseEvent e) {

    }

    /** Empty mouse-wheel callback; intended to be overridden or supplied by subclasses. */
    public void this_mouseWheelMoved(MouseWheelEvent e) {

    }
}
