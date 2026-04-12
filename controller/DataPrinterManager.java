package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

/**
 * Facade that lets the print routines retrieve display-ready data from
 * the nested tab structure (RegionGroupTab -> ColorTabs ->
 * DataPanelContainer -> NDP/DDP/PDP panels) without knowing that
 * layout. It exposes per-group and per-channel lookups for neuron
 * summary strings (puncta, spine, cell, dendrite) and for the various
 * tableData objects (dendrite-puncta, dendrite-spine, individual
 * puncta/spine/cell). It also returns the ordered list of group names
 * as stored in the tab titles.
 */
public class DataPrinterManager {
RegionGroupTab cT;
	
	/** Stores the owning RegionGroupTab so later methods can walk it. */
	public DataPrinterManager(RegionGroupTab RGT)
	{
		cT=RGT;
	}
	
	/**
	 * Returns the ordered list of group tab titles. The local s is
	 * sized to cT.getTabCount() and each entry is copied from
	 * cT.getTitleAt(k); the local ColorTabs c inside the loop is
	 * declared but unused.
	 */
	public String[] getGroupNames()
	{
		String[] s = new String[cT.getTabCount()];
		cT.getTabCount();
		for(int k = 0; k< s.length; k++)
		{	ColorTabs c;		
			s[k]=cT.getTitleAt(k);
		}
		return s;
	}
	
	/**
	 * Returns the neuron-level puncta summary strings for the tab
	 * myGroup in the myColor channel (0 Red, 1 Green, 2 Blue) by
	 * reaching into the matching DataPanelContainer's NDP. Returns
	 * null for unrecognised colors.
	 */
	public String[] getNeuronPunctaData(int myGroup, int myColor)
	{
		switch(myColor)
		{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.NDP.getNeuronPunctaData();
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.NDP.getNeuronPunctaData();
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.NDP.getNeuronPunctaData();
		}
	return null;
	}
	
	/**
	 * Returns the neuron-level spine summary strings for group myGroup
	 * and channel myColor via the NDP panel on the matching color tab.
	 */
	public String[] getNeuronSpineData(int myGroup, int myColor)
	{
		switch(myColor)
		{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.NDP.getNeuronSpineData();
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.NDP.getNeuronSpineData();
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.NDP.getNeuronSpineData();
		}
	return null;
	}
	
	/**
	 * Returns the neuron-level cell-body summary strings for group
	 * myGroup and channel myColor from the NDP panel.
	 */
	public String[] getNeuronCellData(int myGroup, int myColor)
	{
		switch(myColor)
		{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.NDP.getNeuronCellData();
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.NDP.getNeuronCellData();
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.NDP.getNeuronCellData();
		}
	return null;
	}
	
	/**
	 * Returns the neuron-level dendrite summary strings for group
	 * myGroup and channel myColor from the NDP panel.
	 */
	public String[] getNeuronDendriteData(int myGroup, int myColor)
	{
		switch(myColor)
		{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.NDP.getNeuronDendriteData();
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.NDP.getNeuronDendriteData();
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.NDP.getNeuronDendriteData();
		}
	return null;
	}
	
	/** Returns the per-dendrite puncta tableData via getDendriteTableData at index 0. */
	public tableData getDendritePunctaTableData(int myGroup, int myColor)
	{
		return getDendriteTableData(0,myGroup,myColor);
	}
	
	/** Returns the per-dendrite spine tableData via getDendriteTableData at index 1. */
	public tableData getDendriteSpineTableData(int myGroup, int myColor)
	{
		return getDendriteTableData(1,myGroup,myColor);
	}
	
	/** Returns the individual puncta tableData via getIndividualTableData at index 0. */
	public tableData getIndividualPunctaData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(0,myGroup, myColor);
	}
	
	/** Returns the individual spine tableData via getIndividualTableData at index 1. */
	public tableData getIndividualSpineData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(1,myGroup, myColor);
	}
	
	/** Returns the individual cell-body tableData via getIndividualTableData at index 2. */
	public tableData getIndividualCellData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(2,myGroup, myColor);
	}
	
	/**
	 * Shared helper that retrieves one of the dendrite-data table
	 * views from the DDP.dataPanel.tableViews array of the appropriate
	 * color tab. The parameter index selects which tableViews slot
	 * (0 puncta, 1 spine), myGroup picks the tab, and myColor picks
	 * the color channel.
	 */
	private tableData getDendriteTableData(int index,int myGroup, int myColor)
	{
		switch(myColor)
			{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.DDP.dataPanel.tableViews[index];
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.DDP.dataPanel.tableViews[index];
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.DDP.dataPanel.tableViews[index];
			}
		return null;
	}
	
	/**
	 * Shared helper that retrieves one of the individual-object table
	 * views from the PDP.dataPanel.tableViews array of the appropriate
	 * color tab. The parameter index selects the tableViews slot
	 * (0 puncta, 1 spine, 2 cell), myGroup picks the tab, and myColor
	 * the color channel.
	 */
	private tableData getIndividualTableData(int index,int myGroup, int myColor)
	{
		switch(myColor)
			{
		case 0: return ((ColorTabs)cT.getComponentAt(myGroup)).Red.PDP.dataPanel.tableViews[index];
		case 1: return ((ColorTabs)cT.getComponentAt(myGroup)).Green.PDP.dataPanel.tableViews[index];
		case 2: return ((ColorTabs)cT.getComponentAt(myGroup)).Blue.PDP.dataPanel.tableViews[index];
			}
		return null;
	}
}
