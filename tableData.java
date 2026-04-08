package neuron_analyzer;
import javax.swing.JPanel;
import java.awt.Font;
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
public interface tableData {

    public String getData(int row, int column );
    public int getRows();
    public boolean isIgnored(int row);
    public boolean isSelected(int row);
    public void resetSelected();
    public void resetIgnored();
    public void pushSelected(int row);
    public void pushIgnored(int row);
    public boolean[] getIgnoredArray();    
    public String[] getTitles();
    public void setColumnDisplay(boolean[] b);
    public int[] getLayout();
    public void setFont(Font f);
    public boolean[] getPrintList();
    //public void getTableLayouts;
    //public void setListener(JPanel listener);
    //public void printData();
}
