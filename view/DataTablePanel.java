package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * File-level overview: renders the tabular data views (dendrite data,
 * puncta data, spine data) used throughout the neuron analyzer. Contains
 * two classes: DataTablePanel is the JPanel that paints the table and
 * responds to mouse events (selecting and ignoring rows), and
 * DataTablePanel_this_mouseAdapter is a trivial MouseAdapter bridge that
 * forwards clicks back to the panel.
 */
/**
 * JPanel that paints a data table driven by a pluggable tableData source.
 * It supports up to three swappable views (tableViews), paints titles and
 * rows, shades ignored rows dark gray and selected rows red, and converts
 * mouse clicks into selection or ignore toggles.
 */
public class DataTablePanel extends JPanel {
    BorderLayout borderLayout1 = new BorderLayout();
    String[] tableTitle;
    int[] tableLayout; //Last index sets vertical increment
    Font f;
    tableData tableData;// = new int[50][50];
    // tableLayout.length +0: selected (!=0) +1: Ignore Data(!=0)
    public tableData[] tableViews = new tableData[3];
    int Width;
    functionListener fL;    

    /** Stores the initial table titles, layout, and functionListener, then runs the Swing-style jbInit setup. */
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

    /** Sets up the layout, font, computes the total pixel Width from the per-column widths in tableLayout, and registers the mouse adapter. */
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

    /** Delegates to the superclass for background painting, reconciles the current view via checkView, then paints the table header or empty placeholder. */
    public void paint(Graphics g)
    {
        super.paint(g);        
        checkView();       
        if(tableData == null)
            paintEmpty(g);
        else
          paintData(g);
    }

    /** Draws a simple "Dendrite Info" placeholder when no tableData has been set. */
    public void paintEmpty(Graphics g)
    {
    g.drawString("Dendrite Info",10,15);
    }


    /** Paints the table header row and every data row. It measures each title with the current FontMetrics to size columns, then for each row draws the bounding rect, fills in darkGray for ignored rows and red for selected rows, and renders the cell text. Locals VInc and columns control the row height and column count, xPos/yPos track the current drawing cursor, and tableHeight is the number of rows reported by the tableData. */
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


    /** Computes the panel size based on the current row count and a fixed 15-pixel increment, using the cached Width for the horizontal dimension. */
    public Dimension getSize()
    {
        int rows = 0;
        if(tableData != null)
            rows = tableData.getRows();
        int increment = 15;        
        return new Dimension(Width,increment + (increment * rows));
    }

    /** Returns the same dimension as getSize so Swing layout reflects the data size. */
    public Dimension getPreferredSize()
    {
     return getSize();
    }

    /** Swaps in a new tableData source, syncing the font, titles, and layout from it, then updates the panel size accordingly. */
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

    /** Clears the selection state on the current tableData, if any. */
    public void resetSelected()
    {
        if(tableData != null)
            tableData.resetSelected();
    }

    /** Clears the ignore state on the current tableData, if any. */
    public void resetIgnored()
    {
        if(tableData != null)
           tableData.resetIgnored();
    }

    /** Converts a mouse Y coordinate to a zero-based data row index by subtracting the header offset and dividing by the row height. */
    public int getRow(int mouseY)
    {
        int yPos = 2;
        int vInt = tableLayout[tableLayout.length-1];
        int out = 0;

        out = ((mouseY - yPos) / vInt) - 1;

        return out;
    }

    /** Computes which row was clicked and toggles its selection on the underlying tableData, then repaints. */
    public void selectRow(MouseEvent e)
    {
        int row = getRow(e.getY());
        if(row > -1)
        tableData.pushSelected(row);
        repaint();
        //fL.repaintPane();
    }

    /** Computes which row was clicked and toggles its ignored state on the tableData, then triggers a data-pane repaint via the functionListener. */
    public void ignoreRow(MouseEvent e)
    {
        int row = getRow(e.getY());
        if(row > -1)
        tableData.pushIgnored(row);
        //repaint();
        fL.repaintDataPane();
    }
    
    /** Returns the ignore mask from the underlying tableData, or null when no tableData is set. */
    public boolean[] getIgnoreList()
    {
    	if(tableData == null)
    		return null;
    	return tableData.getIgnoredArray();
    }
    
    /** Reads the current data mode index from the functionListener and swaps the active tableData if the mode changed. */
    public void checkView()
    {
    	int k = fL.getDataMode();
    	if(tableData != tableViews[k])
    	{
    		setTableData(tableViews[k]);    		
    	}
    }
    
    /** Replaces the array of selectable tableData views. */
    public void setViews(tableData[] t)
    {
    	tableViews = t;
    }
    
    /** Resynchronizes the panel size with the current tableData size, if one is set. */
    public void checkSize()
    {
    	if(tableData != null)
    		setSize(getSize());
    		
    }


    /** Handles a mouse click: BUTTON1 selects the clicked row and BUTTON3 ignores it; clicks are discarded when no tableData is installed. */
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
