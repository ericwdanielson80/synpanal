package neuron_analyzer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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
public class TiffScrollPane extends JScrollPane {
    public TiffScrollPane() {
        try {
           jbInit();
       } catch (Exception ex) {
           ex.printStackTrace();
       }


    }


    public TiffScrollPane(TiffPanel tp) {
        super(tp);
        try {
           jbInit();
       } catch (Exception ex) {
           ex.printStackTrace();
       }
       tp.myView = super.getViewport();




    }

    private void jbInit() throws Exception {

        this.setHorizontalScrollBarPolicy(JScrollPane.
                                          HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


    }

    public void this_mouseClicked(MouseEvent e) {


    }

    public void this_mouseMoved(MouseEvent e) {

    }

    public void this_mouseWheelMoved(MouseWheelEvent e) {

    }
}


class TiffScrollPane_this_mouseMotionAdapter extends MouseMotionAdapter {
    private TiffScrollPane adaptee;
    TiffScrollPane_this_mouseMotionAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseMoved(MouseEvent e) {
        adaptee.this_mouseMoved(e);
    }
}


class TiffScrollPane_this_mouseAdapter extends MouseAdapter {
    private TiffScrollPane adaptee;
    TiffScrollPane_this_mouseAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}


class TiffScrollPane_this_mouseWheelAdapter implements MouseWheelListener {
    private TiffScrollPane adaptee;
    TiffScrollPane_this_mouseWheelAdapter(TiffScrollPane adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        adaptee.this_mouseWheelMoved(e);
    }
}
