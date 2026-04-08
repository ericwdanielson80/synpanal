package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

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
public class DataShowHidePanel extends JPanel {
    JButton SHButton;
    FlowLayout flowLayout1 = new FlowLayout();
    ImageIcon Show;
    ImageIcon Pressed;

    public DataShowHidePanel() {
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

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

    public void SHButton_actionPerformed(ActionEvent e) {

    }

    public void SHButton_mousePressed(MouseEvent e) {
SHButton.setIcon(Pressed);
    }

    public void SHButton_mouseReleased(MouseEvent e) {
        SHButton.setIcon(Show);

    }
}


class DataShowHidePanel_SHButton_actionAdapter implements ActionListener {
    private DataShowHidePanel adaptee;
    DataShowHidePanel_SHButton_actionAdapter(DataShowHidePanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        adaptee.SHButton_actionPerformed(e);
    }
}


class DataShowHidePanel_SHButton_mouseAdapter extends MouseAdapter {
    private DataShowHidePanel adaptee;
    DataShowHidePanel_SHButton_mouseAdapter(DataShowHidePanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mousePressed(MouseEvent e) {
        adaptee.SHButton_mousePressed(e);
    }
    public void mouseReleased(MouseEvent e) {
        adaptee.SHButton_mouseReleased(e);
    }

}
