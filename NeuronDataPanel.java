package neuron_analyzer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;
import java.io.PrintWriter;

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

    
    public void ND_mouseClicked(MouseEvent e) {
        
            removeLabels(type);
            addLabels(type);
            fL.setDataMode(type);
            fL.repaintDataPane();

    }

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
    
    public void addCellLabels()
    {
    	this.add(CellAveIntensityLabel);
    	this.add(CAI);
    	this.add(CellTotalIntensityLabel);
    	this.add(CTI);
    	this.add(CellNumberLabel);
    	this.add(CN);
    }
    
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
   
   public String[] getNeuronDendriteData()
   {
	   //Integrated Intensity / Length,Integrated Intensity / Area .
	   String[] s = new String[2];
	   s[0] = dF.format(this.getAveDendriteIntensity(groupMember.getValue(),watchColor));
	   s[1] = dF.format(this.getAveDendriteAveIntensity(groupMember.getValue(), watchColor));	   
	   return s;
   }
   
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
   
   public String[] getNeuronCellData()
   {
	   //Cell Integrated Intensity, Cell Ave Intensity
	   String[] s = new String[2];	 	  
	   s[1] = dF.format(fL.getAveCellIntensity(watchColor, groupMember.getValue()));
	   s[0] = dF.format(fL.getAveCellAveIntensity(watchColor, groupMember.getValue()));	  
	   return s;
   }
   
   public void checkSize()
   {
	   
   }
    
    
    	
}





class NeuronDataPanel_ND_mouseAdapter extends MouseAdapter {
    private NeuronDataPanel adaptee;
    NeuronDataPanel_ND_mouseAdapter(NeuronDataPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void mouseClicked(MouseEvent e) {
        adaptee.ND_mouseClicked(e);
    }
}
