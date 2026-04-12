package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;
import java.io.PrintWriter;

/**
 * Panel displaying aggregated measurement data for a single neuron or group
 * in the neuron_analyzer application. The file defines two classes:
 * <ul>
 *   <li>{@code NeuronDataPanel} — a JPanel that displays puncta, spine, and
 *       cell-body metrics for one group/color, switches between three label
 *       sets based on the active data mode, and provides tab-separated output
 *       to a PrintWriter for log export.</li>
 *   <li>{@code NeuronDataPanel_ND_mouseAdapter} — a MouseAdapter that
 *       forwards click events from the header label to the panel's
 *       ND_mouseClicked handler, which toggles data modes.</li>
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
 * Swing panel that aggregates and displays per-neuron or per-group
 * measurements for a single color channel. Hosts three exchangeable label
 * sets (puncta, spine, cell-body), computes averages across the dendrites
 * belonging to the group, and writes formatted rows to a PrintWriter when
 * exporting log data. The panel also supplies string arrays used by table
 * models that render the same data in JTable views.
 */
public class NeuronDataPanel extends JPanel {
    JLabel ND;
    JLabel PunctaNumLabel;
    JLabel PunctaILabel;
    JLabel PunctaALabel;
    JLabel TotalILengthLabel;
    JLabel PN;
    JLabel PI;
    JLabel PA;
    JLabel TIL;
    
    JLabel SpineNumLabel;
    JLabel SpineWidthLabel;
    JLabel SpineLengthLabel;
    JLabel SN;
    JLabel SW;
    JLabel SL;
    JLabel MushroomSpine;
    JLabel ThinSpine;
    JLabel StubbySpine;
    JLabel Filopodia;
    JLabel MS;
    JLabel TS;
    JLabel SS;
    JLabel FS;
    
    JLabel CellAveIntensityLabel;
    JLabel CAI;
    JLabel CellTotalIntensityLabel;
    JLabel CTI;
    JLabel CellNumberLabel;
    JLabel CN;
    
    Font mainFont = new java.awt.Font("Times New Roman", Font.PLAIN,11);
    
    
    int click = 1;
    int type = 0;
    //DendriteGroupData neuronData;
    DecimalFormat dF = new DecimalFormat("#######.##");
    
    Group groupMember;
    int watchColor;
    
    functionListener fL;

    NDPPopupMenu nPop;


    /**
     * Constructs the panel bound to the supplied function listener, group,
     * and color channel index. Installs the popup menu mouse listener and
     * invokes {@code jbInit} to build the child label components.
     */
    public NeuronDataPanel(functionListener fl, Group Group, int Color) {
    	groupMember = Group;
    	watchColor = Color;
    	fL = fl;
    	nPop = new NDPPopupMenu(this);
    	this.addMouseListener(nPop);
        try {
            jbInit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Builds the panel's layout and default label set. Applies an absolute
     * (null) layout manager and a black line border, constructs the header
     * "Neuron Data" label with its click listener, delegates to the three
     * label-set setup methods, and adds the puncta label group as the
     * initial view.
     */
    private void jbInit() throws Exception {
        this.setLayout(null);
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        ND = new JLabel("Neuron Data");
        this.setToolTipText("data per image");
        ND.setToolTipText("toggles data mode");        
        ND.setFont(new java.awt.Font("Times New Roman", Font.BOLD, 14));
        ND.setBorder(BorderFactory.createLineBorder(Color.black));
        ND.setHorizontalAlignment(SwingConstants.CENTER);
        ND.setBounds(new Rectangle(0, 0,200,15));
        ND.addMouseListener(new NeuronDataPanel_ND_mouseAdapter(this));
        
        setUpPunctaLabels();
        setUpSpineLabels();
        setUpCellLabels();
        
        this.add(ND, null);
        this.add(PunctaILabel, null);
        this.add(PunctaNumLabel, null);
        this.add(PunctaALabel, null);
        this.add(TotalILengthLabel,null);
        this.add(PN,null);
        this.add(PI,null);
        this.add(PA,null);
        this.add(TIL,null);
        //this.addSpineLabels();
       // this.addPunctaLabels();
       // this.addCellLabels();
    }
    
    /**
     * Instantiates the puncta label group (average number, intensity, area,
     * and integrated intensity per length) along with their value labels,
     * and positions and styles each one using the shared font and absolute
     * bounds.
     */
    public void setUpPunctaLabels()
    {
    	PunctaNumLabel = new JLabel("Ave Puncta Number");    	
        PunctaILabel = new JLabel("Ave Puncta Intensity");
        PunctaALabel = new JLabel("Ave Puncta Area");
        TotalILengthLabel = new JLabel("Ave Intensity / Length");
        PN = new JLabel("N/A");
        PI = new JLabel("N/A");
        PA = new JLabel("N/A");
        TIL = new JLabel("N/A");
        PunctaALabel.setFont(mainFont);

        PunctaALabel.setBounds(new Rectangle(10, 70, 135, 15));
        PunctaILabel.setFont(mainFont);
        PunctaILabel.setBounds(new Rectangle(10, 50, 135, 15));
        PunctaNumLabel.setFont(mainFont);
        PunctaNumLabel.setBounds(new Rectangle(10, 30, 135, 15));
        TotalILengthLabel.setFont(mainFont);
        TotalILengthLabel.setBounds(new Rectangle(10, 90, 135, 15));
        PN.setBounds(130,30,80,15);
        PI.setBounds(130,50,80,15);
        PA.setBounds(130,70,80,15);
        TIL.setBounds(130,90,80,15);

        
        PN.setFont(mainFont);
        PI.setFont(mainFont);
        PA.setFont(mainFont);
        TIL.setFont(mainFont);
    }
    
    /**
     * Instantiates the spine label group: overall spine number/width/length
     * descriptors, per-type category labels (mushroom, thin, stubby,
     * filopodia), and their corresponding value labels, then positions and
     * styles each with the shared font.
     */
    public void setUpSpineLabels()
    {
    	SpineNumLabel = new JLabel("Ave Spine Number :");
        SpineWidthLabel = new JLabel("Ave Spine Head Width :");
        SpineLengthLabel= new JLabel("Ave Spine Length :");
        SN = new JLabel("N/A");
        SW = new JLabel("N/A");
        SL= new JLabel("N/A");
        MushroomSpine = new JLabel("Mushroom :");
        ThinSpine = new JLabel("Thin :");
        StubbySpine = new JLabel("Stubby :");
        Filopodia = new JLabel("Filopodia :");
        MS = new JLabel("N/A");
        TS = new JLabel("N/A");
        SS = new JLabel("N/A");
        FS = new JLabel("N/A");
        /*
        SpineNumLabel;
        SpineWidthLabel;
        SpineLengthLabel;
        SN;
        SW;
        SL;
        MushroomSpine;
        ThinSpine;
        StubbySpine;
        Filopodia;
        MS;
        TS;
        SS;
        FS;*/
        SpineNumLabel.setBounds(10, 30, 135, 15);
        SpineWidthLabel.setBounds(10, 50, 135, 15);
        SpineLengthLabel.setBounds(10, 70, 135, 15);
        SN.setBounds(130,30,80,15);
        SW.setBounds(130,50,80,15);
        SL.setBounds(130,70,80,15);
        MushroomSpine.setBounds(10, 90, 135, 15);
        ThinSpine.setBounds(10, 110, 135, 15);
        StubbySpine.setBounds(10, 130, 135, 15);
        Filopodia.setBounds(10, 150, 135, 15);
        MS.setBounds(130,90,80,15);
        TS.setBounds(130,110,80,15);
        SS.setBounds(130,130,80,15);
        FS.setBounds(130,150,80,15);
        
        SpineNumLabel.setFont(mainFont);
        SpineWidthLabel.setFont(mainFont);
        SpineLengthLabel.setFont(mainFont);
        SN.setFont(mainFont);
        SW.setFont(mainFont);
        SL.setFont(mainFont);
        MushroomSpine.setFont(mainFont);
        ThinSpine.setFont(mainFont);
        StubbySpine.setFont(mainFont);
        Filopodia.setFont(mainFont);
        MS.setFont(mainFont);
        TS.setFont(mainFont);
        SS.setFont(mainFont);
        FS.setFont(mainFont);
    }
    
    /**
     * Instantiates the cell-body label group (average intensity, integrated
     * intensity, region count) together with their value labels, and sets
     * their bounds and font.
     */
    public void setUpCellLabels()
    {
    	CellAveIntensityLabel = new JLabel("Ave Intensity");
    	CAI = new JLabel("N/A");
    	
    	CellTotalIntensityLabel = new JLabel("Integrated Intensity");
    	CTI = new JLabel("N/A");
    	
    	CellNumberLabel = new JLabel("Region Number");
    	CN = new JLabel("0");
    	
    	CellAveIntensityLabel.setBounds(10, 30, 135, 15);
    	CAI.setBounds(130,30,80,15);
    	
    	CellTotalIntensityLabel.setBounds(10, 50, 135, 15);
    	CTI.setBounds(130,50,80,15);
    	
    	CellNumberLabel.setBounds(10, 70, 135, 15);
    	CN.setBounds(130,70,80,15);
    	
    	
    	CellAveIntensityLabel.setFont(mainFont);
    	CAI.setFont(mainFont);
    	
    	CellTotalIntensityLabel.setFont(mainFont);
    	CTI.setFont(mainFont);
    	
    	CellNumberLabel.setFont(mainFont);
    	CN.setFont(mainFont);
    	
    	
    }

    
    /**
     * Handles a click on the header label by cycling the displayed label
     * set: removes the current labels, adds the next set, and notifies the
     * function listener so the data pane repaints in the new mode.
     */
    public void ND_mouseClicked(MouseEvent e) {
        
            removeLabels(type);
            addLabels(type);
            fL.setDataMode(type);
            fL.repaintDataPane();

    }

    /**
     * Dispatches to the appropriate remove method based on the current
     * label set index (0 = puncta, 1 = spine, 2 = cell, 3 resets to 0).
     */
    public void removeLabels(int t)
    {
    	switch(t)
    	{
    	case 0: removePunctaLabels(); break;
    	case 1: removeSpineLabels(); break;
    	case 2: removeCellLabels(); break;
    	case 3: type = 0; break;
    	}
    }
    /**
     * Removes the puncta label set from the panel and advances the internal
     * type counter to the spine set.
     */
    public void removePunctaLabels()
    {
    	type = 1;
        this.remove(PunctaNumLabel);
        this.remove(PunctaILabel);
        this.remove(PunctaALabel);
        this.remove(TotalILengthLabel);

        this.remove(PN);
        this.remove(PI);
        this.remove(PA);
        this.remove(TIL);
    }

    /**
     * Dispatches to the appropriate add method based on the supplied label
     * set index (0 = puncta, 1 = spine, 2 = cell, 3 no-op).
     */
    public void addLabels(int t)
    {
    	switch(t)
    	{
    	case 0: addPunctaLabels(); break;
    	case 1: addSpineLabels(); break;    	
    	case 2: addCellLabels(); break;
    	case 3: break;
    	}
    }
    
    /**
     * Adds the puncta label set (description and value labels) to the
     * panel.
     */
    public void addPunctaLabels()
    {
    	
        this.add(PunctaNumLabel);
        this.add(PunctaILabel);
        this.add(PunctaALabel);
        this.add(TotalILengthLabel);

        this.add(PN);
        this.add(PI);
        this.add(PA);
        this.add(TIL);
    }
    
    /**
     * Adds the spine label set, including the per-type category labels, to
     * the panel.
     */
    public void addSpineLabels()
    {    	
    	this.add(SpineNumLabel);
    	this.add(SpineWidthLabel);
    	this.add(SpineLengthLabel);
    	this.add(SN);
    	this.add(SW);
    	this.add(SL);
    	this.add(MushroomSpine);
    	this.add(ThinSpine);
    	this.add(StubbySpine);
    	this.add(Filopodia);
    	this.add(MS);
    	this.add(TS);
    	this.add(SS);
    	this.add(FS);
    }
    
    /**
     * Adds the cell-body label set to the panel.
     */
    public void addCellLabels()
    {
    	this.add(CellAveIntensityLabel);
    	this.add(CAI);
    	this.add(CellTotalIntensityLabel);
    	this.add(CTI);
    	this.add(CellNumberLabel);
    	this.add(CN);
    }
    
    /**
     * Removes the spine label set from the panel and advances the type
     * counter to the cell-body set.
     */
    public void removeSpineLabels()
    {
    	type = 2;
    	this.remove(SpineNumLabel);
    	this.remove(SpineWidthLabel);
    	this.remove(SpineLengthLabel);
    	this.remove(SN);
    	this.remove(SW);
    	this.remove(SL);
    	this.remove(MushroomSpine);
    	this.remove(ThinSpine);
    	this.remove(StubbySpine);
    	this.remove(Filopodia);
    	this.remove(MS);
    	this.remove(TS);
    	this.remove(SS);
    	this.remove(FS);
    }
    
    /**
     * Removes the cell-body label set and rolls the type counter back
     * toward the puncta set on the next cycle.
     */
    public void removeCellLabels()
    {
    	type = 3;
    	this.remove(CellAveIntensityLabel);
    	this.remove(CAI);
    	this.remove(CellTotalIntensityLabel);
    	this.remove(CTI);
    	this.remove(CellNumberLabel);
    	this.remove(CN);
    }
    
    /**
     * Overrides paint so that if the function listener's current data
     * mode differs from the displayed one, the old label set is removed
     * and the new set is added before delegating to the superclass.
     */
    public void paint(Graphics g)
    {    	
    	if(fL.getDataMode() != type)
    	{
    		removeLabels(type);
    		type = fL.getDataMode();
    		addLabels(type);
    	}
    	super.paint(g);
    		
    }

    /**
     * Refreshes the text of every value label from the underlying
     * dendrite data. Pulls puncta averages (number, intensity, area,
     * integrated intensity per length), spine averages (number, width,
     * length), counts of each spine morphology type, and cell-body
     * intensities and region count, formatting all numeric values through
     * the shared {@code DecimalFormat} before invoking the superclass
     * repaint.
     */
    public void repaint()
    {
    	if(PN == null)
    		return;
    	if(fL != null && fL.getDendrites(groupMember.getValue()) != null)
        {          	
            PN.setText(dF.format(getAvePunctaNum(groupMember.getValue(),watchColor)));
            PI.setText(dF.format(getAveIntensity(groupMember.getValue(),watchColor)));
            PA.setText(dF.format(getAveArea(groupMember.getValue(),watchColor)));
            SN.setText(dF.format(getAveSpineNum(groupMember.getValue())));
            SW.setText(dF.format(getAveSpineWidth(groupMember.getValue())));            
            SL.setText(dF.format(getAveSpineLength(groupMember.getValue())));
            MS.setText(dF.format(getSpineTypeNum(0,groupMember.getValue())));
            TS.setText(dF.format(getSpineTypeNum(1,groupMember.getValue())));
            SS.setText(dF.format(getSpineTypeNum(2,groupMember.getValue())));
            FS.setText(dF.format(getSpineTypeNum(3,groupMember.getValue())));    
            TIL.setText(dF.format(getAveTotalPunctaIntegratedIntensityPerLength(groupMember.getValue(),watchColor)));
        }
        if(CAI != null)        	
        	{
        	CAI.setText(dF.format(fL.getAveCellAveIntensity(watchColor, groupMember.getValue())));
        	CTI.setText(dF.format(fL.getAveCellIntensity(watchColor, groupMember.getValue())));
        	CN.setText(dF.format(fL.getCellNumber(groupMember.getValue())));
        	}
        super.repaint();
    }
    
    /**
     * Writes a single log row for this neuron/group to the supplied
     * PrintWriter. Begins each line with a prefix of file name, group
     * identifier, color, and threshold. Based on the {@code LogInfo}
     * flags it optionally appends puncta metrics, spine metrics, and
     * aggregate dendrite intensities on the first line, then emits
     * additional lines for cell-body intensities, per-spine puncta data
     * (delegated to each Dendrite's printSpineData), and per-spine puncta
     * count percentages across all dendrites in the group.
     */
    public void printData(PrintWriter pW,String fileName,String txc, String color, int threshold,LogInfo lI)
    {
    	String prefix = new String(fileName + "\t" + txc + "\t" + color + "\t" +"th = " + threshold);
    	pW.print(prefix);
    	
    	if(lI.pI)
    	pW.print("\t" + 
    			dF.format(getAvePunctaNum(groupMember.getValue(),watchColor)) + 
    			"\t" + dF.format(getAveIntensity(groupMember.getValue(),watchColor)) + "\t" 
    			+ dF.format(getAveArea(groupMember.getValue(),watchColor)) + "\t" + dF.format(getAveTotalPunctaIntegratedIntensityPerLength(groupMember.getValue(),watchColor)));
    	if(lI.sI)
    		pW.print("\t" + dF.format(getAveSpineNum(groupMember.getValue())) + "\t" + dF.format(getAveSpineWidth(groupMember.getValue()))
    			+ "\t" + dF.format(getAveSpineLength(groupMember.getValue())) + "\t" + dF.format(getSpineTypeNum(0,groupMember.getValue())) 
    			+ "\t" + dF.format(getSpineTypeNum(1,groupMember.getValue())) + "\t" + dF.format(getSpineTypeNum(2,groupMember.getValue())) + "\t" + dF.format(getSpineTypeNum(3,groupMember.getValue())));
    	
    	if(lI.dI)
    	{
    		//for(int k = 0; k < neuronData.dendriteNames.length; k++)
    		//{
    			//pW.println(fileName + "\t" + color + "\t" + "th =" + "\t" + threshold + "\t" + neuronData.dendriteNames[k] + "\t" + neuronData.dendriteLengths[k]); //right now will only print dendrite lengths    			                                                                                                                                                                                                                	
    			                                                                                                        
    		//}
    		//temp hikack for total dendrite stuff
    		pW.print("\t" + getAveDendriteIntensity(groupMember.getValue(),watchColor) + "\t" + getAveDendriteAveIntensity(groupMember.getValue(),watchColor) + "\t" + getAveDendriteIntensityPerLength(groupMember.getValue(),watchColor));
    	}
    	
    	
    	pW.print("\n");
    	
    	if(lI.cI)
    	{
    		pW.println(prefix + "\t" + dF.format(fL.getAveCellIntensity(watchColor,groupMember.getValue())) + "\t" + dF.format(fL.getAveCellAveIntensity(watchColor,groupMember.getValue())));
    		/*for(int k = 0; k < fL.getCellNum(); k++)
    		{
    			if(fL.showCell(k,watchColor,groupMember))
    			pW.println(prefix + "\t"+ "Cell " + k + "\t" + dF.format(fL.getCellIntensity(k,watchColor))+ "\t" + dF.format(fL.getCellAveIntensity(k,watchColor)));
    		}*/
    	}
    	if(lI.ppI)
    	{
    		/*for(int k = 0; k < neuronData.punctaData.punctaNames.length; k++)
    		{
    			if(!neuronData.punctaData.bC[k].isIgnored())
    				pW.println(prefix 
    						+ "\t" + neuronData.punctaData.getData(k,0)
    						+ "\t" + neuronData.punctaData.getData(k,1)
    						+ "\t" + neuronData.punctaData.getData(k,2)
    						);
    		}*/
    	}
    	if(lI.ssI)
    	{
    		    		
    		Dendrite[] myDendrites = fL.getDendrites(groupMember.getValue());
    		for(int k = 0; k < myDendrites.length; k++)
    		{	if(!myDendrites[k].isIgnored())
    			myDendrites[k].printSpineData(pW, k, watchColor, groupMember.getValue(), prefix, dF, fL.getCalibration());
    		}
    	}
    			
    	
    	
    	
    	
    	if(lI.ppS)
    	{
    		double [] ppS = new double[4];
    		int[] tmp;
    		int counter = 0;
//    		prefix: filename groupmember color threshold dendrite number
			//after prefix dendrite name, # spines with 0, 1, 2 3 or <
    		Dendrite[] mD = fL.getDendrites(groupMember.getValue());
    		for(int k = 0; k < mD.length; k++)
    		{	if(mD[k]!= null && !mD[k].isIgnored())
    			{
    			tmp = mD[k].getPunctaPerSpineData(watchColor);
    			
    			ppS[0] += ( tmp[0] / mD[k].getLength(fL.getCalibration()) );
    			ppS[1] += ( tmp[1] / mD[k].getLength(fL.getCalibration()) );
    			ppS[2] += ( tmp[2] / mD[k].getLength(fL.getCalibration()) );
    			ppS[3] += ( tmp[3] / mD[k].getLength(fL.getCalibration()) );
    			counter++;
    			}
    			
    		}
    		double[] pC = new double[4];
    		pC[0] = (ppS[0] / counter) * 100;
    		pC[1] = (ppS[1] / counter) * 100;
    		pC[2] = (ppS[2] / counter) * 100;
    		pC[3] = (ppS[3] / counter) * 100;

    		pW.println(prefix 	
    				+ "\t" + dF.format(pC[0]) 
					+ "\t" + dF.format(pC[1]) /*spineData[j].getName()*/ 
					+ "\t" + dF.format(pC[2])
					+ "\t" + dF.format(pC[3])
			);
		}
    	}
    
    
    /**
     * Returns the mean puncta count per dendrite for the given group and
     * color, averaged over dendrites that are not marked ignored.
     */
    public float getAvePunctaNum(int group, int color)
    {
        float sum = 0;
        float count = 0;
        
        Dendrite[] myDendrites = fL.getDendrites(group);                
        for(int k = 0; k < myDendrites.length; k++)
        {
            if(myDendrites[k] != null && !myDendrites[k].isIgnored())
            {
                sum += ((float)myDendrites[k].getPunctaNumber(color, fL.getCalibration()));
                count++;
            }
        }
        if(count == 0)
            return count;
        return sum / count;
    }

    /**
     * Returns the mean per-dendrite average puncta intensity for the
     * group, averaged across non-ignored dendrites (note the method uses
     * {@code watchColor} rather than the {@code color} argument).
     */
    public int getAveIntensity(int group, int color)
    {
        int sum = 0;
        int count = 0;
        Dendrite[] myDendrites = fL.getDendrites(group);
        for(int k = 0; k < myDendrites.length; k++)
        {
            if(myDendrites[k] != null && !myDendrites[k].isIgnored())
            {
                sum += myDendrites[k].getAvePunctaIntensity(watchColor);
                count++;
            }
        }
        if(count == 0)
            return count;
        return sum / count;
    }
    
    /**
     * Returns the mean of each dendrite's total puncta integrated
     * intensity per unit length for the given group and color, averaged
     * over non-ignored dendrites.
     */
    public float getAveTotalPunctaIntegratedIntensityPerLength(int group,int myColor )
    {
 	   int sum = 0;
        int count = 0;
        Dendrite[] myDendrites = fL.getDendrites(group);
        for(int k = 0; k < myDendrites.length; k++)
        {
            if(myDendrites[k] != null && !myDendrites[k].isIgnored())
            {
                sum += myDendrites[k].getTotalPunctaIntegratedIntensityPerLength(myColor,fL.getCalibration());
                count++;
            }
        }
        if(count == 0)
            return count;
        return sum / count;
    }

    /**
     * Returns the mean per-dendrite average puncta area for the given
     * group and color across non-ignored dendrites.
     */
    public float getAveArea(int group, int myColor)
   {
       float sum = 0;
       float count = 0;
       Dendrite[] myDendrites = fL.getDendrites(group);
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += myDendrites[k].getAvePunctaArea(myColor,fL.getCalibration());
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;

   }
    
   /**
    * Returns the total intensity for a single dendrite (by index) in the
    * given group and color, selected from the dendrite's red, green, or
    * blue intensity array.
    */
   public float getDendriteIntensity(int k,int group,int myColor)
   {
 	  float sum = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	  switch(myColor)
 	  {
 	  case 0: 	sum += (float)(myDendrites[k].totalRedIntensity[0]); break;
 	  case 1: 	sum += (float)(myDendrites[k].totalGreenIntensity[0]); break;
 	  case 2: 	sum += (float)(myDendrites[k].totalBlueIntensity[0]); break;	  
 	  }
 	  return sum;
   }
   
   /**
    * Returns the average intensity of a single dendrite normalized by
    * area. Divides the total intensity channel by the pixel count and by
    * the square of the calibration factor.
    */
   public float getDendriteAveIntensity(int k,int group, int myColor)
   {
 	  float sum = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	  switch(myColor)
 	  {
 	  case 0: 	sum += (float)(myDendrites[k].totalRedIntensity[0]/myDendrites[k].totalRedIntensity[1]); break;
 	  case 1: 	sum += (float)(myDendrites[k].totalGreenIntensity[0]/myDendrites[k].totalGreenIntensity[1]); break;
 	  case 2: 	sum += (float)(myDendrites[k].totalBlueIntensity[0]/myDendrites[k].totalBlueIntensity[1]); break;	  
 	  }
 	  return sum / ((float)fL.getCalibration() * (float)fL.getCalibration());
   }
   
   /**
    * Returns the mean of per-dendrite total intensity across non-ignored
    * dendrites in the group and color channel.
    */
   public float getAveDendriteIntensity(int group, int myColor)
   {
       float sum = 0;
       float count = 0;
       Dendrite[] myDendrites = fL.getDendrites(group);
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += getDendriteIntensity(k,group,myColor);
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;

   }
   
   /**
    * Returns the mean of per-dendrite average intensity across non-ignored
    * dendrites in the group and color channel.
    */
   public float getAveDendriteAveIntensity(int group, int myColor)
   {
       float sum = 0;
       float count = 0;
       Dendrite[] myDendrites = fL.getDendrites(group);
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += getDendriteAveIntensity(k,group,myColor);
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;

   }
   
   /**
    * Returns the average spine density over the group's non-ignored
    * dendrites.
    */
   public double getAveSpineNum(int group)
   {
	   Dendrite[] myDendrites = fL.getDendrites(group);
 	  double total = 0;
 	  int counter = 0;
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    		total += myDendrites[k].getSpineNum(fL.getCalibration());	    		
 	    	}
 	    	
 	    }    
 	    if(k == 0)
 	    	return 0;    
 	    
 	    return total / (double)(counter) ;	    
   }
   
   /**
    * Returns the average density of the given spine morphology type
    * (mushroom, thin, stubby, or filopodia) across non-ignored dendrites
    * in the group.
    */
   public double getSpineTypeNum(int type, int group)
   {
 	  double total = 0;
 	  int counter = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    	total += myDendrites[k].getSpineTypeNum(type,fL.getCalibration());
 	    	}
 	    }    
 	    if(k == 0)
 	    	return 0;
 	    return total / (double)(counter) ;	  
   }
   
   /**
    * Returns the mean spine head width across non-ignored dendrites in
    * the group.
    */
   public double getAveSpineWidth(int group)
   {
 	  double total = 0;
 	  int counter = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    	total += myDendrites[k].getAveSpineWidth(fL.getCalibration());
 	    	}
 	    }    
 	    if(k == 0)
 	    	return 0;
 	    return total / (double)(counter) ;	   
   }
   
   /**
    * Returns the mean spine neck width across non-ignored dendrites in
    * the group.
    */
   public double getAveSpineNeckWidth(int group)
   {
 	  double total = 0;
 	  int counter = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    	total += myDendrites[k].getAveSpineNeckWidth(fL.getCalibration());
 	    	}
 	    }    
 	    if(k == 0)
 	    	return 0;
 	    return total / (double)(counter) ;	   
   }
   
   /**
    * Returns the average puncta-per-spine value for a given bucket index
    * (0, 1, 2, or 3+) across non-ignored dendrites in the group, using
    * the panel's watched color channel.
    */
   public double getAvePunctaPerSpine(int index,int group)
   {
 	  double total = 0;
 	  int counter = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    	total += myDendrites[k].getPunctaPerSpineData(index, watchColor,fL.getCalibration());
 	    	}
 	    }    
 	    if(k == 0)
 	    	return 0;
 	    return total / (double)(counter) ;	   
   }
   
   /**
    * Returns the mean spine length across non-ignored dendrites in the
    * group.
    */
   public double getAveSpineLength(int group)
   {
 	  int counter = 0;
 	  double total = 0;
 	 Dendrite[] myDendrites = fL.getDendrites(group);
 	    int k = 0;
 	    for(k = 0; k < myDendrites.length; k++)
 	    {
 	    	if(myDendrites[k] != null && !myDendrites[k].isIgnored())
 	    	{
 	    		counter++;
 	    	total += myDendrites[k].getAveSpineLength(fL.getCalibration());
 	    	}
 	    }    
 	    if(k == 0)
 	    	return 0;
 	    return total / (double)(counter) ;	 	  
   }
   
   /**
    * Returns the mean intensity per unit length across non-ignored
    * dendrites, computed by dividing each dendrite's intensity by its
    * calibrated length before averaging.
    */
   public float getAveDendriteIntensityPerLength(int group,int myColor)
   {
       float sum = 0;
       float count = 0;
       Dendrite[] myDendrites = fL.getDendrites(group);
       for(int k = 0; k < myDendrites.length; k++)
       {
           if(myDendrites[k] != null && !myDendrites[k].isIgnored())
           {
               sum += (getDendriteIntensity(k,group,myColor) / myDendrites[k].getLength(fL.getCalibration()));
               count++;
           }
       }
       if(count == 0)
           return count;
       return sum / count;

   } 
   
   /**
    * Returns a four-element array of formatted puncta metrics (density,
    * intensity, area, intensity per length) for use by table models.
    */
   public String[] getNeuronPunctaData()
   {
	   //Puncta Density, Puncta Intensity, Puncta Area, Puncta Intensity / Length.
	   String[] s = new String[4];
	   s[0] = dF.format(this.getAvePunctaNum(groupMember.getValue(), watchColor));
	   s[1] = dF.format(this.getAveIntensity(groupMember.getValue(), watchColor));
	   s[2] = dF.format(this.getAveArea(groupMember.getValue(), watchColor));
	   s[3] = dF.format(this.getAveTotalPunctaIntegratedIntensityPerLength(groupMember.getValue(), watchColor));	   
	   return s;
   }
   
   /**
    * Returns a two-element array of formatted dendrite metrics
    * (integrated intensity and average intensity) for use by table models.
    */
   public String[] getNeuronDendriteData()
   {
	   //Integrated Intensity / Length,Integrated Intensity / Area .
	   String[] s = new String[2];
	   s[0] = dF.format(this.getAveDendriteIntensity(groupMember.getValue(),watchColor));
	   s[1] = dF.format(this.getAveDendriteAveIntensity(groupMember.getValue(), watchColor));	   
	   return s;
   }
   
   /**
    * Returns a twelve-element array of formatted spine metrics covering
    * density, head width, neck width, length, per-type densities, and
    * per-spine puncta counts, for use by table models.
    */
   public String[] getNeuronSpineData()
   {
	   //Spine Density, Spine Head Width,Spine Neck Width, Spine Length,Mushroom Density,Thin Density,Stubby Density,Filopodia Density,Spines with 0, Spines with 1, Spines with 2, Spines with >3
	   String[] s = new String[12];
	   s[0] = dF.format(this.getAveSpineNum(groupMember.getValue()));
	   s[1] = dF.format(this.getAveSpineWidth(groupMember.getValue()));
	   s[2] = dF.format(this.getAveSpineNeckWidth(groupMember.getValue()));
	   s[3] = dF.format(this.getAveSpineLength(groupMember.getValue()));
	   s[4] = dF.format(this.getSpineTypeNum(0,groupMember.getValue()));
	   s[5] = dF.format(this.getSpineTypeNum(1,groupMember.getValue()));
	   s[6] = dF.format(this.getSpineTypeNum(2,groupMember.getValue()));
	   s[7] = dF.format(this.getSpineTypeNum(3,groupMember.getValue()));
	   s[8] = dF.format(this.getAvePunctaPerSpine(0,groupMember.getValue()));
	   s[9] = dF.format(this.getAvePunctaPerSpine(1,groupMember.getValue()));
	   s[10] = dF.format(this.getAvePunctaPerSpine(2,groupMember.getValue()));
	   s[11] = dF.format(this.getAvePunctaPerSpine(3,groupMember.getValue()));
	   return s;
   }
   
   /**
    * Returns a two-element array of formatted cell-body metrics
    * (integrated and average intensity) for use by table models.
    */
   public String[] getNeuronCellData()
   {
	   //Cell Integrated Intensity, Cell Ave Intensity
	   String[] s = new String[2];	 	  
	   s[1] = dF.format(fL.getAveCellIntensity(watchColor, groupMember.getValue()));
	   s[0] = dF.format(fL.getAveCellAveIntensity(watchColor, groupMember.getValue()));	  
	   return s;
   }
   
   /**
    * Placeholder hook for later size adjustment logic; currently a
    * no-op.
    */
   public void checkSize()
   {
	   
   }
    
    
    	
}
