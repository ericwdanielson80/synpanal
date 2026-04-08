package neuron_analyzer;

public class DataPrinterManager {
RegionGroupTab cT;
	
	public DataPrinterManager(RegionGroupTab RGT)
	{
		cT=RGT;
	}
	
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
	
	public tableData getDendritePunctaTableData(int myGroup, int myColor)
	{
		return getDendriteTableData(0,myGroup,myColor);
	}
	
	public tableData getDendriteSpineTableData(int myGroup, int myColor)
	{
		return getDendriteTableData(1,myGroup,myColor);
	}
	
	public tableData getIndividualPunctaData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(0,myGroup, myColor);
	}
	
	public tableData getIndividualSpineData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(1,myGroup, myColor);
	}
	
	public tableData getIndividualCellData(int myGroup, int myColor)
	{
		return this.getIndividualTableData(2,myGroup, myColor);
	}
	
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
