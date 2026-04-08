package neuron_analyzer;
import java.awt.event.*;
import java.awt.Point;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.awt.Color;

public class TiffPanelMouseFunctions {
int mode = 0;
int mouseWheelMode = 0;

long mouseTimer = 0;
/*
 0: 
 1: Add Dendrites
 2: Make Complex Dendrite
 3: Finish Complex Area
 4: Finish Cell
 5: Enter Add Spine Mode
 */
TiffPanel tp;
Point p;
Dendrite selectedDendrite;
SpineInfo selectedSpine;

DendritePopupMenu p2;
TiffPanelPopupMenu p3;
SpinePopupMenu p4;

lineTools lT = new lineTools();

	public TiffPanelMouseFunctions(TiffPanel t)
	{
		tp = t;
		p = new Point(0,0);
		p2 = new DendritePopupMenu(this,tp.fL);	
		p3 = new TiffPanelPopupMenu(tp);	
		p4 = new SpinePopupMenu(this,tp.fL);
	}
	
	public void setMode(int t)
	{
		mode = t;
	}
	
	public void setWheelMode(int t)
	{
		mouseWheelMode = t;
	}
	
	public void mousePressed(MouseEvent e)
	{		
		//mouseTimer = System.currentTimeMillis() - mouseTimer;
		
		//mouseTimer = System.currentTimeMillis();
		
		switch (mode){		
		case 0:	mouseClickedNormal(e); break;
		case 1:	mouseClickedAddDendrites(e); break;
		case 2: mouseClickedAddComplexDendrite(e); break;
		case 3: mouseClickedFinishArea(e); break;		
		case 4: mouseClickedFinishCell(e); break;
		case 5: mouseClickedAddSpineLength(e); break;
		case 6: mouseClickedAddSpineWidth(e); break;
		case 7: mouseClickedAddSpineNeck(e); break;
		case 8: mouseClickedAddSpine(e); break;		
		case 9: mouseClickedRemoveSpine(e); break;
		case 10: mouseClickedRemoveCell(e); break;
		case 11: mouseClickedRemoveDendrite(e); break;
		case 12: mouseClickedAddSpineShaft(e);break;
		case 13: mouseClickedAddShaftPosition(e); break;
		case 14: mouseClickedAddShollAnalysis(e); break;
		case 15: mouseClickedAddChildDendrite(e); break;
		default: break;
		}
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		switch (mode){	
		case 1: Zoom(e); break;
		case 2: ChangeSpineWidth(e); break;
		default: break;
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
		/*switch (mode){		
		case 0:	mouseClickedNormal(e); break;
		case 1:	mouseClickedAddDendrites(e); break;
		case 2: mouseClickedAddComplexDendrite(e); break;
		case 3: mouseClickedFinishArea(e); break;		
		case 4: mouseClickedFinishCell(e); break;
		case 5: mouseClickedAddSpineLength(e); break;
		case 6: mouseClickedAddSpineWidth(e); break;
		case 7: mouseClickedAddSpineNeck(e); break;
		case 8: mouseClickedAddSpine(e); break;		
		case 9: mouseClickedRemoveSpine(e); break;
		case 10: mouseClickedRemoveCell(e); break;
		case 11: mouseClickedRemoveDendrite(e); break;
		default: break;
		}*/
	}
	
	public void mouseReleased(MouseEvent e)
	{
		switch (mode){		
		case 0:	mouseReleasedNormal(e); break;
		case 1:	break;
		case 2: break;
		case 3: break;		
		case 4: mouseReleasedFinishCell(e); break;
		case 5: break;
		case 6: break;
		case 7: break;
		case 8: break;		
		case 9: break;
		case 10: break;
		case 11: break;
		default: break;
		}
	}
	
	public void mouseMoved(MouseEvent e)
	{
		switch (mode){		
		case 0:	break;
		case 1: mouseMovedAddDendrite(e); break;
		case 2: mouseMovedAddDendrite(e); break;
		case 5: mouseMovedAddSpine(e,0); break;
		case 6: mouseMovedAddSpine(e,1); break;
		case 7: mouseMovedAddSpine(e,2); break;
		case 12: mouseMovedAddSpine(e,0); break;
		case 13: mouseMovedAddShaft(e); break;
		case 15: mouseMovedAddChildDendrite(e); break;
		default: break;
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{
		switch (mode){		
		case 0:	break;
		case 1:	break;
		case 3: mouseDraggedAddComplexDendrite(e); break;
		case 4: mouseDraggedAddCell(e); break;
		default: break;
		}
	}
	
	public void mouseReleasedNormal(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON3)
		{
		if(p2.isVisible())
		{
			p2.setVisible(false);			
			selectedDendrite = null;
		}
		if(p3.isVisible())
		{
			p3.setVisible(false);
		}
		}
	}
	
	public void mouseClickedNormal(MouseEvent e)
	{
		int k = 0;
		int j = 0;
		int pC = 0;
		int start;
		boolean foundPuncta = false;
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		
		if(myDendrites[0] != null)
		{	
		
		while(k < myDendrites.length && myDendrites[k] != null)
		{
			
			if(myDendrites[k].dendriteArea.contains(p))
			{
				if(e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() ==2)
				{
					myDendrites[k].ignoreAllPuncta(tp.fL.getCurrentColorGroup());
					tp.fL.repaintDataPane();
					tp.createOverlayImage();
					tp.repaint();
					return;
				}
				
				if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() ==2)
				{
					myDendrites[k].resetAllPuncta(tp.fL.getCurrentColorGroup());
					tp.fL.repaintDataPane();
					tp.createOverlayImage();
					tp.repaint();
					return;
				}
				
				//for(j = 1; j< myDendrites[k].puncta[tp.fL.getCurrentColorGroup()].vector.length; j++ )
				for(j = 0; j< myDendrites[k].myPuncta[tp.fL.getCurrentColorGroup()].myPuncta.length; j++ )
				{				
				if(myDendrites[k].myPuncta[tp.fL.getCurrentColorGroup()].myPuncta[j].border.contains(p))
					{
					//start = myDendrites[k].punctaStart[tp.fL.getCurrentColorGroup()];
					if(e.getButton() == MouseEvent.BUTTON1)
						{
						myDendrites[k].myPuncta[tp.fL.getCurrentColorGroup()].myPuncta[j].pushSelected();	
						foundPuncta = true;
						}
					else if(e.getButton() == MouseEvent.BUTTON3) 
						{
						myDendrites[k].myPuncta[tp.fL.getCurrentColorGroup()].myPuncta[j].pushIgnored();
						foundPuncta = true;
						}
						
					}
				
				}
			}
			if(myDendrites[k].myPuncta != null)
			pC += myDendrites[k].myPuncta[tp.fL.getCurrentColorGroup()].myPuncta.length;
			k++;			
		}
		}
		if(e.getButton() == MouseEvent.BUTTON3 && !foundPuncta)
		{	
		selectedDendrite = findDendrite(e);
		findSpine(selectedDendrite,p);
		if(selectedDendrite != null && selectedSpine == null)
		p2.show(tp, e.getX(), e.getY());		
		else if(selectedDendrite != null && selectedSpine != null)
			p4.show(tp,e.getX(),e.getY());
		else
		p3.show(tp, e.getX(), e.getY());			
		}
		
				
		tp.fL.repaintDataPane();
		tp.createOverlayImage();
		tp.repaint();
	}
	
	public void mouseClickedAddDendrites(MouseEvent e)
	{
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		if(e.getClickCount() == 2)
        {
            tp.newDendrite.remove();
            tp.countPuncta(tp.newDendrite);
            for(int k = 0;k < myDendrites.length; k++)
            {
                if(myDendrites[k] == null)
                    {
                	myDendrites[k] = tp.newDendrite;       
                	tp.countPuncta(myDendrites[k]);
                	tp.newDendrite = null;
                        break;
                    }
                 if(k == myDendrites.length - 1)
                 {
                     Dendrite[] tmp = new Dendrite[myDendrites.length*2];
                     System.arraycopy(myDendrites,0,tmp,0,myDendrites.length);
                     tp.myDendritesGroups[tp.fL.getCurrentDendriteGroup().getValue()].myDendrites = tmp;                     
                     myDendrites = tp.getCurrentDendriteGroup();
                }
                 

            }
            
            tp.setMouseMode(0);      
         
            

        }
        else
        if(e.getClickCount() == 1)
        {
        	/*
        	 * new stuff for selected dendrite
        	 */
        Dendrite d = findDendrite(e);
        if(d != null)
        {
        	System.out.println("Inside dendrite");
        	selectedDendrite = d;
        	this.setMode(15);
        	tp.repaint();
        	return;
        }
        	
        if(tp.newDendrite == null)
            {        	
        	tp.newDendrite = new Dendrite(tp.dendriteWidth,tp.dendriteWatch,tp.fL.getCurrentDendriteGroup());        	
        	tp.newDendrite.add((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
        	
            }
        if(e.getButton() == e.BUTTON1)
        	tp.newDendrite.add((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
        if(e.getButton() == e.BUTTON3)
            if(tp.newDendrite.remove())
            	tp.newDendrite = null;
        }


        //  RGT.Ctrl.Red.setData(TP.getData(1.0));
            //
		tp.repaint();
	}
	
	public void mouseClickedAddComplexDendrite(MouseEvent e)
	{
		if(e.getClickCount() == 2)
        {			
            tp.setMouseMode(3);
        }
        else
        if(e.getClickCount() == 1)
        {
        if(tp.newDendrite == null)
            {
        	tp.newDendrite = new ComplexDendrite(tp.dendriteWidth,tp.dendriteWatch,tp.fL.getCurrentDendriteGroup());
        	tp.newDendrite.add((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
        	
            }
        if(e.getButton() == e.BUTTON1)
        	tp.newDendrite.add((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
        if(e.getButton() == e.BUTTON3)
            if(tp.newDendrite.remove())
            	tp.newDendrite = null;
        }


        //  RGT.Ctrl.Red.setData(TP.getData(1.0));
            //
		tp.repaint();
	
	}
	
	public void mouseDraggedAddComplexDendrite(MouseEvent e)
	{
		((ComplexDendrite)tp.newDendrite).addAreaPoints((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		tp.repaint();
	}
	
	public void mouseClickedFinishArea(MouseEvent e)
	{
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		if(e.getClickCount() == 2)
		{
			((ComplexDendrite)(tp.newDendrite)).finishArea();
			tp.newDendrite.remove();
			
            for(int k = 0;k < myDendrites.length; k++)
            {
                if(myDendrites[k] == null)
                    {
                	myDendrites[k] = tp.newDendrite;
                	tp.countPuncta(myDendrites[k]);
                	tp.newDendrite = null;
                        break;
                    }
                 if(k == myDendrites.length - 1)
                 {
                     Dendrite[] tmp = new Dendrite[myDendrites.length*2];
                     System.arraycopy(myDendrites,0,tmp,0,myDendrites.length);
                     myDendrites = tmp;
                     tp.myDendritesGroups[tp.fL.getCurrentDendriteGroup().getValue()].myDendrites = myDendrites;
                 }
            

            }
            
            tp.createOverlayImage();
            tp.setMouseMode(0);
   		 	tp.fL.repaintDataPane();            
            tp.repaint();
		}
	}
	
	public void mouseMovedAddDendrite(MouseEvent e) {
        if(tp.newDendrite != null)
        {
            tp.newDendrite.editLast(((e.getX())*100)/tp.zoom,((e.getY())*100)/tp.zoom);
            tp.repaint();
        }
     }
		
	
	public void mouseClickedFinishCell(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && tp.newCell == null)
		{
			tp.setMouseMode(0);
		}
		
		 if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && tp.newCell != null)
		 {			 
			 tp.newCell.finishArea();
			 			 
		 
			 for(int k = 0;k < tp.myCells.length; k++)
	            {
	                if(tp.myCells[k] == null)
	                    {
	                	tp.myCells[k] = tp.newCell;
	                	tp.newCell = null;
	                        break;
	                    }
	                 if(k == tp.myCells.length - 1)
	                 {
	                	 CellBody[] tmp = new CellBody[tp.myCells.length*2];
	                     System.arraycopy(tp.myCells,0,tmp,0,tp.myCells.length);
	                     tp.myCells = tmp;
	                 }

	            }
			 //tp.newCell = null;
			 tp.setMouseMode(4);
			 tp.createOverlayImage();
			 mouseTimer = e.getWhen();
			 
		 }
		 tp.CalcCellIntesity();
		 tp.fL.notifyCellEventListeners();
		 tp.fL.repaintDataPane();		 
		 tp.repaint();
	}
	
	public void mouseReleasedFinishCell(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1 && tp.newCell == null)
		{
			tp.setMouseMode(0);
		}
		
		 if(e.getButton() == MouseEvent.BUTTON1 && tp.newCell != null)
		 {			 
			 tp.newCell.finishArea();			
		  
			 for(int k = 0;k < tp.myCells.length; k++)
	            {
	                if(tp.myCells[k] == null)
	                    {
	                	tp.myCells[k] = tp.newCell;
	                	tp.newCell = null;
	                        break;
	                    }
	                 if(k == tp.myCells.length - 1)
	                 {
	                	 CellBody[] tmp = new CellBody[tp.myCells.length*2];
	                     System.arraycopy(tp.myCells,0,tmp,0,tp.myCells.length);
	                     tp.myCells = tmp;
	                 }

	            }
			 //tp.newCell = null;
			 tp.setMouseMode(4);
			 tp.createOverlayImage();
			 mouseTimer = e.getWhen();
			 
		 }
		 tp.CalcCellIntesity();
		 tp.fL.notifyCellEventListeners();
		 tp.fL.repaintDataPane();
		 tp.repaint();
	}
	
	public void mouseDraggedAddCell(MouseEvent e)	
	{	
		if((e.getWhen() - mouseTimer) > 500)
		{
		
		if(tp.newCell == null)
		tp.newCell = new CellBody(tp.dendriteWatch,tp.fL.getCurrentDendriteGroup());
	
		tp.newCell.addAreaPoints((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		tp.repaint();
		}
		
	}
	
	public void mouseClickedAddSpineLength(MouseEvent e)
	{
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		if(tp.newLines == null)
		{
			for(int k = 0; k < myDendrites.length; k++)
			{
				if(myDendrites[k] == null)
					{
					tp.setMouseMode(0);
					return;
					}
				if(myDendrites[k].dendriteArea.contains(p))
					{
					selectedDendrite = myDendrites[k];
					break;
					}
			}
			if(selectedDendrite == null)
				{
				// if clicked outside of a dendrite then exit addspine mode
				tp.setMouseMode(0);
				return;
				}
			//if dendrtie is found then make a new spine length object
			tp.resetNewLines(3,new Color[] {Color.BLUE,Color.CYAN, Color.MAGENTA});
			tp.newLines[0] = new int[4];
			tp.newLines[0][0] = (int)p.getX();
			tp.newLines[0][1] = (int)p.getY();
			tp.newLines[0][2] = (int)p.getX();
			tp.newLines[0][3] = (int)p.getY();
			}
		else
		{
			tp.newLines[0][2] = (int)p.getX();
			tp.newLines[0][3] = (int)p.getY();
			tp.setMouseMode(6); //add spine head
			
		}
		tp.repaint();
				
		
	}
		

	public void mouseClickedAddSpineWidth(MouseEvent e)
	{
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		if(tp.newLines[1] == null)
		{
			tp.newLines[1] = new int[4];
			tp.newLines[1][0] = (int)p.getX();
			tp.newLines[1][1] = (int)p.getY();
			tp.newLines[1][2] = (int)p.getX();
			tp.newLines[1][3] = (int)p.getY();
		}
		else
		{
			tp.newLines[1][2] = (int)p.getX();
			tp.newLines[1][3] = (int)p.getY();
			tp.setMouseMode(7);	//add spine neck		
		}
		tp.repaint();
	}
	public void mouseClickedAddSpineNeck(MouseEvent e)
	{
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		if(tp.newLines[2] == null)
		{
			tp.newLines[2] = new int[4];
			tp.newLines[2][0] = (int)p.getX();
			tp.newLines[2][1] = (int)p.getY();
			tp.newLines[2][2] = (int)p.getX();
			tp.newLines[2][3] = (int)p.getY();
		}
		else
		{
			tp.newLines[2][2] = (int)p.getX();
			tp.newLines[2][3] = (int)p.getY();
			selectedDendrite.addSpine(new SpineInfo(tp.newLines[0],tp.newLines[1],tp.newLines[2],tp.fL.getCalibration()));
			selectedDendrite = null;			
			tp.delNewLines();
			tp.setMouseMode(5); //add more spines (or exit by clicking outside a dendrite)
			tp.createOverlayImage();
		}
		tp.fL.notifySpineEventListeners();
		repaintAll();
		
	}
	
	public void mouseMovedAddSpine(MouseEvent e, int row)
	{
		if(tp.newLines == null )
			return;
		
		if(tp.newLines[row] == null)
			return;
		
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		tp.newLines[row][2] = (int)p.getX();
		tp.newLines[row][3] = (int)p.getY();
		tp.repaint();
	}
	
	public void mouseClickedAddSpine(MouseEvent e)
	{
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		
			for(int k = 0; k < myDendrites.length; k++)
			{
				if(myDendrites[k] == null)
					{
					tp.setMouseMode(0);
					return;
					}
				if(myDendrites[k].dendriteArea.contains(p))
					{
					selectedDendrite = myDendrites[k];
					break;
					}
			}
			if(selectedDendrite == null)
				{
				// if clicked outside of a dendrite then exit addspine mode
				tp.setMouseMode(0);
				return;
				}
			//if dendrtie is found then make a new spine length object
			tp.resetNewLines(3,null);
			tp.newLines[0] = new int[4];
			tp.newLines[0][0] = (int)p.getX() - 3;
			tp.newLines[0][1] = (int)p.getY() - 3;
			tp.newLines[0][2] = (int)p.getX();
			tp.newLines[0][3] = (int)p.getY() + 3;
			
			tp.newLines[1] = new int[4];
			tp.newLines[1][0] = (int)p.getX();
			tp.newLines[1][1] = (int)p.getY() + 3;
			tp.newLines[1][2] = (int)p.getX() + 3;
			tp.newLines[1][3] = (int)p.getY() - 3;
			
			tp.newLines[2] = new int[4];
			tp.newLines[2][0] = (int)p.getX() + 3;
			tp.newLines[2][1] = (int)p.getY() - 3;
			tp.newLines[2][2] = (int)p.getX() - 3;
			tp.newLines[2][3] = (int)p.getY() - 3;
			
			selectedDendrite.addSpine(new SpineInfo(tp.newLines[0],tp.newLines[1],tp.newLines[2],tp.fL.getCalibration()));
			selectedDendrite = null;
			tp.delNewLines();
			tp.setMouseMode(8);
			
			
		tp.fL.notifySpineEventListeners();
		repaintAll();
				
		
	}
	
	public void mouseClickedRemoveCell(MouseEvent e)
	{
		
		int k = findCell(e);
		if(k == -1)
			return;
		tp.myCells[k] = null;
		mode = 0;
		
		for(int j = tp.myCells.length - 1; j >= 0; j--)
		{
			if(j == k)
				{		
				tp.CalcCellIntesity();
				tp.fL.notifyCellEventListeners();
				 tp.fL.repaintDataPane();
				 tp.repaint();
				return;
				}
			if(tp.myCells[j] != null)
				{
				tp.myCells[k] = tp.myCells[j];
				tp.myCells[j] = null;
				tp.CalcCellIntesity();
				tp.fL.notifyCellEventListeners();
				 tp.fL.repaintDataPane();
				 tp.repaint();
				return;
				}
		}
		
		
	}
	
	public int findCell(MouseEvent e)
	{
		int k = 0;
		int j = 0;
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		if(tp.myCells == null)
			return -1;
		
		while(k < tp.myCells.length && tp.myCells[k] != null)
		{
			
			if(tp.myCells[k].cellArea.contains(p) && tp.myCells[k].groupMember == tp.fL.getCurrentDendriteGroup())
			{
				return k;				
			}
			k++;
			
		}
		return -1;
	}	
	
	public void mouseClickedRemoveDendrite(MouseEvent e)
	{		
		
	}
	
	public Dendrite findDendrite(MouseEvent e)
	{
		int k = 0;
		int j = 0;
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		if(myDendrites == null)
			return null;
		
		while(k < myDendrites.length && myDendrites[k] != null)
		{
			
			if(myDendrites[k].dendriteArea.contains(p))
			{
				return myDendrites[k];				
			}
			k++;
			
		}
		return null;
	}	
	
	public void mouseClickedRemoveSpine(MouseEvent e)
	{
		Dendrite d = findDendrite(e);
		if(d == null)
			{
			mode = 0;
			return;
			}
		
		
		SpineInfo[] spines = d.spineData;
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		int length = d.spineNumber;
		int found = -1;		
		for(int k = 0; k < length; k++)
		{
			
			if(spines[k].myBounds.contains(p))
				{				
				found = k;
				break;
				}
				
		}
		
		if(found == -1)
			{
			mode = 0;			
			return;
			}
		
		if(found == 0 && length == 1)
		{
			d.spineData = null;
			d.spineNumber = 0;
			tp.fL.notifySpineEventListeners();
			tp.fL.repaintDataPane();
			tp.createOverlayImage();
			tp.repaint();
			return;
		}
		
		if(found == length - 1)
		{
			spines[found] = null;
			d.spineNumber--;
			tp.fL.notifySpineEventListeners();
			repaintAll();
			return;
		}
				
		spines[found] = null;
		spines[found] = spines[length - 1];
		spines[length - 1] = null;
		d.spineNumber--;
		tp.fL.notifySpineEventListeners();		
		tp.fL.repaintDataPane();
		tp.createOverlayImage();
		tp.repaint();
		
	}
	
	public void mouseClickedAddSpineShaft(MouseEvent e)
	{
		/*
		 * first click adds a point at the base of the shaft
		 * as mouse moves a line is drawn from the base to the muse pointer tip
		 * second click adds a point at the tip of the spine
		 * two new lines are generated perpendicular to the segment drawn one intersecting the base
		 * one intersecting the tip
		 * mouse wheel movements are altered to change the width of these new segments
		 * a third line is drawn also perpendicular to the first line segment and moves along the first line
		 * line as the mouse moves this line will be the seperating line, when clicked this line will be permanent
		 * apon clicking lines are finalized, a new spine is generated with lines 2,3,4 becoming sL, sW and sN respectively
		 * line one is discarded
		 */
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		if(tp.newLines == null)
		{
			for(int k = 0; k < myDendrites.length; k++)
			{
				if(myDendrites[k] == null)
					{
					tp.setMouseMode(0);
					return;
					}
				if(myDendrites[k].dendriteArea.contains(p))
					{
					selectedDendrite = myDendrites[k];
					break;
					}
			}
			if(selectedDendrite == null)
				{
				// if clicked outside of a dendrite then exit addspine mode
				tp.setMouseMode(0);
				return;
				}
			//if dendrtie is found then make a new spine length object
			tp.resetNewLines(4, new Color[] {Color.BLUE,Color.BLUE,Color.BLUE,Color.BLUE});
			tp.newLines[0] = new int[4];
			tp.newLines[0][0] = (int)p.getX();
			tp.newLines[0][1] = (int)p.getY();
			tp.newLines[0][2] = (int)p.getX();
			tp.newLines[0][3] = (int)p.getY();
			}
		else
		{
			tp.newLines[0][2] = (int)p.getX();
			tp.newLines[0][3] = (int)p.getY();
			/*
			 * add code for perpendicular lines
			 */
			int[][] perp = lT.xyListstoLines(lT.getPerpendicularLine(tp.newLines[0][0], tp.newLines[0][1], tp.newLines[0][2], tp.newLines[0][3], tp.newSpineWidth));
			tp.newLines[1] = perp[0];
			tp.newLines[2] = perp[1];
			tp.setMouseMode(13); //add Shaft
			
		}
		tp.repaint();
	}
	
	public void mouseClickedAddShaftPosition(MouseEvent e)
	{
		/*
		 * as mouse moves calculate given x where on the line it is and given y where on the line it is
		 * if one is off the line use the other
		 * if both are off the line set at max of x or y (depending on which is greater)
		 * if neither are off the line then ise x if dx>dy and y if dy>dx
		 * 
		 */
		int[] nL = new int[4];
		nL[0] = tp.newLines[0][0];
		nL[1] = tp.newLines[0][1];
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		int[] xy = lT.getXY(tp.newLines[0], p.x, p.y);
		nL[2] = xy[0];
		nL[3] = xy[1];
		
		int[][] perp = lT.xyListstoLines(lT.getPerpendicularLine(nL[0],nL[1],nL[2],nL[3], tp.newSpineWidth));
		//nL is new segment that shows where shaft should be
		tp.addTempSpineBase(perp[1][0],perp[1][1],perp[1][2],perp[1][3]);
		
		selectedDendrite.addSpine(new SpineInfo(tp.newLines[1],tp.newLines[2],tp.newLines[3],tp.fL.getCalibration()));
		selectedDendrite = null;
		tp.delNewLines();
		tp.setMouseMode(12);
		tp.repaint();
		
	}
	
	public void mouseMovedAddShaft(MouseEvent e)
	{
		int[] nL = new int[4];
		nL[0] = tp.newLines[0][0];
		nL[1] = tp.newLines[0][1];
		nL[2] = tp.newLines[0][2];
		nL[3] = tp.newLines[0][3];
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		int[] xy = lT.getXY(nL, p.x, p.y);
		
		int[][] perp = lT.xyListstoLines(lT.getPerpendicularLine(nL[0],nL[1],xy[0],xy[1], tp.newSpineWidth));
		//nL is new segment that shows where shaft should be
		tp.addTempSpineBase(perp[1][0],perp[1][1],perp[1][2],perp[1][3]);
		tp.repaint();
		
	}
	
	public void repaintAll()
	{
		tp.fL.repaintDataPane();
		tp.createOverlayImage();
		tp.repaint();
	}
	
	public void Zoom(MouseWheelEvent e)
	{
		tp.setZoom(tp.getZoom() + ((tp.getZoom() * e.getWheelRotation()*10)/100),e.getX(),e.getY());
        tp.repaint();
	}
	
	public void ChangeSpineWidth(MouseWheelEvent e)
	{
		tp.newSpineWidth =+ e.getWheelRotation();
	}
	
	public void recalcSpineLines()
	{
		int[][] perp = lT.xyListstoLines(lT.getPerpendicularLine(tp.newLines[0][0], tp.newLines[0][1], tp.newLines[0][2], tp.newLines[0][3], tp.newSpineWidth));
		tp.newLines[1] = perp[0];
		tp.newLines[2] = perp[1];
		
		int[] xy = lT.getXY(tp.newLines[0], p.x, p.y);		
		perp = lT.xyListstoLines(lT.getPerpendicularLine(tp.newLines[0][0],tp.newLines[0][1],xy[0],xy[1], tp.newSpineWidth));
		//nL is new segment that shows where shaft should be
		tp.addTempSpineBase(perp[1][0],perp[1][1],perp[1][2],perp[1][3]);
	}
	
	public void setSelectedSpineType(int i, boolean auto)
	{
		selectedSpine.setSpineType(i, auto);
		tp.repaint();
		selectedSpine = null;
	}
	
	public void findSpine(Dendrite d, Point p)
	{
		if(d == null)
			return;
		SpineInfo[] spines = d.spineData;
		if(spines == null)
			return;
		for(int k = 0; k < spines.length; k++)
		{
			if(spines[k] != null && spines[k].myBounds.contains(p))
				{				
				selectedSpine = spines[k];
				return;
				}
		}
		selectedSpine = null;
	}
	
	public void deleteDendrite(Dendrite d)
	{
		/*
		 * The way we remove dendrites is to transfer the last dendrite to a temp pointer
		 * nullify the last index
		 * look for the selected dendrite in the list
		 * if found put the previous last in that spot
		 * if not found then it was the last and there are no problems
		 * 
		 */
		Dendrite[] myDendrites = tp.getCurrentDendriteGroup();
		int count = 0;
		for(int k = 0; k < myDendrites.length; k++)
		{
			if(myDendrites[k] != null)
				count++;
		}
		
		Dendrite last = myDendrites[count-1];
		myDendrites[count - 1] = null;
		
		for(int k = 0; k < myDendrites.length; k++)
		{
			if(myDendrites[k] != null)
			{
				if(myDendrites[k] == d)
				{
					myDendrites[k] = last;
					selectedDendrite = null;
				}
			}
		}
		
		
	}
	
	public void mouseClickedAddShollAnalysis(MouseEvent e)
	{
		Point p = e.getPoint();
		tp.fL.addSholl(p);
		setMode(0);
	}
	
	public void mouseClickedAddChildDendrite(MouseEvent e)
    {
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		Point p2 = selectedDendrite.getPoint(p);
		if(tp.newDendrite == null)
        {        	
    	tp.newDendrite = new Dendrite(tp.dendriteWidth,tp.dendriteWatch,tp.fL.getCurrentDendriteGroup());        	
    	tp.newDendrite.add(p2.x,p2.y);
    	tp.newDendrite.myParent = selectedDendrite;
    	tp.newDendrite.parentSegment = selectedDendrite.selectedSegment;
    	selectedDendrite.SelectSegment(-1);
    	selectedDendrite = null;
    	this.setMode(1);
    	tp.repaint();
    	
        }
    }
	
	public void mouseMovedAddChildDendrite(MouseEvent e)
    {
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		
    	int k = selectedDendrite.withinSegments(p);
    	selectedDendrite.SelectSegment(k);
    	selectedDendrite.setChildPoint(p);
    	tp.repaint();
    }
	
			
	
	
}
