package neuron_analyzer;

import java.awt.*;

import javax.swing.*;
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
public class DataTablePanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    String[] tableTitle;
    int[] tableLayout; //Last index sets vertical increment
    Font f;
    tableData tableData;// = new int[50][50];
    // tableLayout.length +0: selected (!=0) +1: Ignore Data(!=0)
    tableData[] tableViews = new tableData[3];
    int Width;
    functionListener fL;    

    public DataTablePanel(String[] TableTitle, int[] TableLayout, functionListener fl) {
        tableTitle = TableTitle;
        tableLayout = TableLayout;
        fL = fl;        
       /* tableTitle = new String[]{"Dendrite","Length","Puncta Density","Ave Intensity", "Ave Area"};
        tableLayout = new int[]{50,50,85,84,50,15};*/
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setLayout(borderLayout1);
        f = new Font("Times New Roman",Font.PLAIN,12);
        Width = 10;        
        for(int k = 0; k < tableTitle.length; k++)
        {
            Width+= tableLayout[k];
        }
        this.setSize(Width,15);
        this.addMouseListener(new DataTablePanel_this_mouseAdapter(this));
    }

    public void paint(Graphics g)
    {
        super.paint(g);        
        checkView();       
        if(tableData == null)
            paintEmpty(g);
        else
          paintData(g);
    }

    public void paintEmpty(Graphics g)
    {
    g.drawString("Dendrite Info",10,15);
    }


    public void paintData(Graphics g)
    {    	
    int VInc = tableLayout[tableLayout.length - 1];
    int columns = tableTitle.length;
    int xPos = 2;
    int yPos = 2;
    Integer tmp = new Integer(0);
    FontMetrics fm = g.getFontMetrics();
    int tableHeight = tableData.getRows();
    for(int k = 0; k <columns; k++)
    {
    	tableLayout[k] = fm.stringWidth(tableTitle[k]) + 15;
        g.drawRect(xPos,yPos,tableLayout[k],VInc);
        g.drawString(tableTitle[k],3 + xPos,-3 + yPos +VInc);
        xPos += tableLayout[k];        
    }
    if(Width != xPos || this.getHeight() != (tableHeight * 15) + 15)
    {
    	Width = xPos;
    	//this.setSize(Width, (tableHeight * 15) + 15);
    }
    yPos+= VInc;
    
    for(int k = 0; k < tableHeight; k++)
    {
        xPos = 2;
        for(int j = 0; j <columns; j++)
    {
        g.drawRect(xPos, yPos, tableLayout[j], VInc);
        if(tableData.isIgnored(k))
        {
            g.setColor(Color.darkGray);
            g.fillRect(xPos+1, yPos+1, tableLayout[j] - 1, VInc -1);
            g.setColor(Color.black);
        }

        if(tableData.isSelected(k))
        {
            g.setColor(Color.red);
            g.fillRect(xPos+1, yPos+1, tableLayout[j] - 1, VInc -1);
            g.setColor(Color.black);
        }

        g.drawString(tableData.getData(k,j), 3 + xPos, -3 + yPos + VInc);
        xPos += tableLayout[j];
    }
    yPos+= VInc;

    }
    }


    public Dimension getSize()
    {
        int rows = 0;
        if(tableData != null)
            rows = tableData.getRows();
        int increment = 15;        
        return new Dimension(Width,increment + (increment * rows));
    }

    public Dimension getPreferredSize()
    {
     return getSize();
    }

    public void setTableData(tableData TableData)
    {
        tableData = TableData;
        if(tableData != null)
        {	
        tableData.setFont(f);
        tableTitle = tableData.getTitles();
        tableLayout = tableData.getLayout();
        }
        setSize(getSize());
        
    }

    public void resetSelected()
    {
        if(tableData != null)
            tableData.resetSelected();
    }

    public void resetIgnored()
    {
        if(tableData != null)
           tableData.resetIgnored();
    }

    public int getRow(int mouseY)
    {
        int yPos = 2;
        int vInt = tableLayout[tableLayout.length-1];
        int out = 0;

        out = ((mouseY - yPos) / vInt) - 1;

        return out;
    }

    public void selectRow(MouseEvent e)
    {
        int row = getRow(e.getY());
        if(row > -1)
        tableData.pushSelected(row);
        repaint();
        //fL.repaintPane();
    }

    public void ignoreRow(MouseEvent e)
    {
        int row = getRow(e.getY());
        if(row > -1)
        tableData.pushIgnored(row);
        //repaint();
        fL.repaintDataPane();
    }
    
    public boolean[] getIgnoreList()
    {
    	if(tableData == null)
    		return null;
    	return tableData.getIgnoredArray();
    }
    
    public void checkView()
    {
    	int k = fL.getDataMode();
    	if(tableData != tableViews[k])
    	{
    		setTableData(tableViews[k]);    		
    	}
    }
    
    public void setViews(tableData[] t)
    {
    	tableViews = t;
    }
    
    public void checkSize()
    {
    	if(tableData != null)
    		setSize(getSize());
    		
    }


    public void this_mouseClicked(MouseEvent e) {
        if(tableData == null)
            return;

        if(e.getButton() == e.BUTTON1)
        selectRow(e);
    else
        if(e.getButton() == e.BUTTON3)
            ignoreRow(e);


    }


}


class DataTablePanel_this_mouseAdapter extends MouseAdapter {
    private DataTablePanel adaptee;
    DataTablePanel_this_mouseAdapter(DataTablePanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.this_mouseClicked(e);
    }
}
