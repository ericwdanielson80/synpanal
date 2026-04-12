package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import javax.swing.JPanel;
import java.awt.Font;
/**
 * Abstraction for any tabular data source the analyzer needs to display
 * and optionally export. Implementations expose cell values, row counts,
 * per-row "selected" and "ignored" flags, column titles and layout
 * metadata, and hooks for tweaking presentation (fonts, visible columns).
 * This interface decouples the generic table UI from the specific data
 * source (for example dendrite statistics vs puncta statistics).
 */
public interface tableData {

    /**
     * Returns the string-formatted value at the given row and column.
     */
    public String getData(int row, int column );
    /**
     * Returns the number of rows currently held by the data source.
     */
    public int getRows();
    /**
     * Indicates whether the row should be ignored when data is exported
     * or aggregated.
     */
    public boolean isIgnored(int row);
    /**
     * Indicates whether the row is currently marked as selected by the
     * user.
     */
    public boolean isSelected(int row);
    /**
     * Clears the selected state for all rows.
     */
    public void resetSelected();
    /**
     * Clears the ignored state for all rows.
     */
    public void resetIgnored();
    /**
     * Toggles/records the given row's selected state.
     */
    public void pushSelected(int row);
    /**
     * Toggles/records the given row's ignored state.
     */
    public void pushIgnored(int row);
    /**
     * Returns a boolean array, one entry per row, exposing which rows
     * are flagged as ignored.
     */
    public boolean[] getIgnoredArray();
    /**
     * Returns the column header titles in display order.
     */
    public String[] getTitles();
    /**
     * Supplies a per-column visibility mask so callers can hide or show
     * columns without rebuilding the data source.
     */
    public void setColumnDisplay(boolean[] b);
    /**
     * Returns column layout metadata (for example column widths or
     * grouping) used by the table renderer.
     */
    public int[] getLayout();
    /**
     * Applies the given font to the renderer associated with this data.
     */
    public void setFont(Font f);
    /**
     * Returns a mask indicating which rows should actually be emitted
     * when the table is printed or exported.
     */
    public boolean[] getPrintList();
    //public void getTableLayouts;
    //public void setListener(JPanel listener);
    //public void printData();
}
