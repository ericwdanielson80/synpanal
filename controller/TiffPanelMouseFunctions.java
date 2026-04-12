package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;
import java.awt.event.*;
import java.awt.Point;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.awt.Color;

/**
 * Mouse interaction delegate for the TiffPanel image canvas. This class routes
 * raw AWT mouse events (press, release, drag, move, wheel) into a family of
 * tool-specific handlers keyed by the current editing mode: normal selection,
 * add dendrite, make complex dendrite, finish a complex area, finish a cell,
 * add spine length, add spine width, add spine neck, add spine (cross-marker),
 * remove spine, remove cell, remove dendrite, add spine shaft, add shaft
 * position, add Sholl analysis, and add child dendrite. The same physical
 * click is therefore interpreted differently depending on which tool is active.
 * The class also owns the popup menus shown on right-click for dendrites,
 * spines and empty canvas areas, coordinates with TiffPanel (the view) and
 * with the Dendrite, SpineInfo, Puncta and CellBody data models, and
 * translates screen coordinates to model coordinates using TiffPanel's zoom.
 */
public class TiffPanelMouseFunctions {
public int mode = 0;
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
public Dendrite selectedDendrite;
SpineInfo selectedSpine;

DendritePopupMenu p2;
TiffPanelPopupMenu p3;
SpinePopupMenu p4;

lineTools lT = new lineTools();

	/**
	 * Constructs the mouse-function delegate for a given TiffPanel. Stores a
	 * back-reference to the panel, allocates the shared reusable Point used
	 * when translating screen coordinates to image coordinates, and builds
	 * the three popup menus used on right-click: p2 for a dendrite, p3 for
	 * the panel background and p4 for an individual spine. The parameter t
	 * is the TiffPanel this delegate will serve; the menus are wired to this
	 * delegate and to the panel's shared FrameListener (tp.fL).
	 */
	public TiffPanelMouseFunctions(TiffPanel t)
	{
		tp = t;
		p = new Point(0,0);
		p2 = new DendritePopupMenu(this,tp.fL);	
		p3 = new TiffPanelPopupMenu(tp);	
		p4 = new SpinePopupMenu(this,tp.fL);
	}
	
	/**
	 * Sets the current mouse-tool mode. The parameter t is the new mode
	 * number as enumerated in the comment at the top of the class; subsequent
	 * mouse events will be dispatched to the handler for this mode.
	 */
	public void setMode(int t)
	{
		mode = t;
	}
	
	/**
	 * Sets the current mouse-wheel mode. The parameter t selects which
	 * wheel behavior is active (for example zooming or adjusting spine width).
	 */
	public void setWheelMode(int t)
	{
		mouseWheelMode = t;
	}
	
	/**
	 * Entry point called when the user presses a mouse button on the TiffPanel.
	 * A switch on the current mode dispatches to the appropriate mouseClicked*
	 * helper for that tool (normal selection, add/remove dendrite, add/remove
	 * spine, add shaft, Sholl analysis, child dendrite, etc). The parameter e
	 * carries the raw AWT MouseEvent which is forwarded unchanged to the
	 * chosen helper. Any modes not listed fall through to the default case
	 * and are silently ignored.
	 */
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
	
	/**
	 * Handles the mouse wheel. Depending on the current mode the wheel either
	 * zooms the image (mode 1) or adjusts the working spine width (mode 2).
	 * The parameter e is the AWT MouseWheelEvent forwarded to the chosen
	 * helper so it can read wheel rotation and cursor position.
	 */
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		switch (mode){	
		case 1: Zoom(e); break;
		case 2: ChangeSpineWidth(e); break;
		default: break;
		}
	}
	
	/**
	 * Called on a completed mouse click. The entire dispatch body is commented
	 * out because click handling has been moved into mousePressed to obtain
	 * lower-latency tool response; this method is kept as a stub so that the
	 * MouseListener contract is still satisfied. The parameter e is the AWT
	 * MouseEvent and is currently unused.
	 */
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
	
	/**
	 * Invoked when the user releases a mouse button. Only a few modes care
	 * about release (normal selection dismisses popups, and finish-cell mode
	 * uses the release to finalize a drag-drawn cell outline); all other
	 * modes ignore the event. The parameter e is the AWT MouseEvent which
	 * is passed through to the mode-specific release handler.
	 */
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
	
	/**
	 * Dispatches plain (non-dragged) mouse movement to the tool-specific
	 * "rubber band" handlers: extending the currently growing dendrite,
	 * updating the temporary spine length/width/neck lines, tracking the
	 * shaft position, or previewing a child dendrite attachment. Modes that
	 * do not need live feedback fall through and do nothing. The parameter e
	 * is the AWT MouseEvent carrying the current cursor position.
	 */
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
	
	/**
	 * Dispatches mouse-drag events. Dragging is only meaningful while
	 * sketching a complex dendrite area (mode 3) or tracing a cell body
	 * outline (mode 4); other modes treat drags as no-ops. The parameter e
	 * is the AWT MouseEvent describing the current drag position.
	 */
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
	
	/**
	 * Release handler for the default (mode 0) selection tool. If a
	 * right-button release occurs while either the dendrite popup (p2) or
	 * the panel popup (p3) is visible, that popup is hidden; hiding the
	 * dendrite popup also clears the currently selectedDendrite so no stale
	 * selection persists after the menu closes. The parameter e is the AWT
	 * MouseEvent describing the release.
	 */
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
	
	/**
	 * Press handler for the default (mode 0) selection tool. The click
	 * coordinate is first converted from screen pixels to image pixels using
	 * tp.zoom and stored in the shared Point p. The handler then walks every
	 * dendrite in the current dendrite group; for the dendrite whose area
	 * contains the point it treats a double right-click as "ignore all
	 * puncta in the current color channel", a double left-click as "reset
	 * all puncta", and otherwise scans that dendrite's puncta list looking
	 * for the puncta whose border contains the click: a left click toggles
	 * it selected, a right click toggles it ignored. If no puncta was hit
	 * and the user right-clicked, it tries to identify a dendrite and a
	 * spine at the click to decide which popup to open (dendrite menu p2,
	 * spine menu p4, or panel menu p3). The parameter e is the AWT
	 * MouseEvent. Local variable k walks the dendrites array, j walks the
	 * puncta inside a dendrite, pC accumulates a running total of puncta
	 * across dendrites, start is a dead variable retained from an older
	 * implementation, and foundPuncta records whether a puncta was hit so
	 * the right-click popup is suppressed in that case.
	 */
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
	
	/**
	 * Press handler for the "add dendrite" tool (mode 1). A single left click
	 * extends the in-progress dendrite with a new point (zoom-corrected from
	 * screen to image coordinates), a single right click removes the last
	 * point (and discards the dendrite if it becomes empty), and a double
	 * click finalizes the dendrite by inserting it into the first null slot
	 * of the current group (growing the array if it is full) and counting
	 * its puncta. If the first left click happens inside an existing
	 * dendrite the tool switches to child-dendrite mode (15) instead. The
	 * parameter e is the AWT MouseEvent. The local myDendrites is the
	 * dendrite array for the current color/group, d is the dendrite hit by
	 * the click (if any), and tmp is the doubled-capacity replacement when
	 * the group array needs to grow.
	 */
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
	
	/**
	 * Press handler for the "make complex dendrite" tool (mode 2). A double
	 * click switches into the finish-area tool (mode 3) so the user can
	 * drag-draw the area outline; a single left click appends a point to
	 * the in-progress ComplexDendrite (creating it if needed), and a single
	 * right click rewinds or deletes that in-progress dendrite. The
	 * parameter e is the AWT MouseEvent used both for coordinates and for
	 * click-count / button detection.
	 */
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
	
	/**
	 * Drag handler for the complex-dendrite area sketch (mode 3). Each drag
	 * sample is converted from screen to image coordinates and appended to
	 * the in-progress ComplexDendrite's area outline, then the panel is
	 * repainted. The parameter e is the AWT MouseEvent for the drag.
	 */
	public void mouseDraggedAddComplexDendrite(MouseEvent e)
	{
		((ComplexDendrite)tp.newDendrite).addAreaPoints((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		tp.repaint();
	}
	
	/**
	 * Press handler for mode 3 ("finish area") on a complex dendrite. A
	 * double click closes the area via ComplexDendrite.finishArea, performs
	 * the final remove of the in-progress marker, and then inserts the
	 * completed dendrite into the first null slot of the current group,
	 * counting its puncta. If the group array is full a new array of double
	 * the length is allocated and the reference swapped in. The overlay
	 * image is regenerated, the tool is reset to mode 0, and the data pane
	 * is asked to repaint. The parameter e is the AWT MouseEvent. Local
	 * myDendrites holds the current group's dendrite array and tmp is the
	 * doubled-capacity replacement when growth is needed.
	 */
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
	
	/**
	 * Live "rubber band" feedback while adding a dendrite: if there is an
	 * in-progress dendrite, its last point is moved to follow the mouse
	 * cursor in image coordinates and the panel is repainted. The parameter
	 * e is the AWT MouseEvent that supplies the current cursor position.
	 */
	public void mouseMovedAddDendrite(MouseEvent e) {
        if(tp.newDendrite != null)
        {
            tp.newDendrite.editLast(((e.getX())*100)/tp.zoom,((e.getY())*100)/tp.zoom);
            tp.repaint();
        }
     }
		
	
	/**
	 * Press handler for the finish-cell tool (mode 4). A double left click
	 * with no cell in progress simply leaves cell mode by resetting to
	 * mode 0. A double left click while a CellBody is being drawn closes
	 * its area and inserts it into the first free slot of tp.myCells,
	 * doubling the array length if no free slot exists, then recalculates
	 * cell intensities, notifies cell listeners and triggers a repaint.
	 * The mouseTimer is set to the event's timestamp so that
	 * mouseDraggedAddCell uses this as its starting reference. The
	 * parameter e is the AWT MouseEvent. Local tmp is the doubled
	 * CellBody array used when growth is required.
	 */
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
	
	/**
	 * Release handler for the finish-cell tool (mode 4). Mirrors the press
	 * handler: releasing the left button with no cell in progress drops the
	 * user back to mode 0, and releasing with an in-progress CellBody
	 * closes its area, stores it in tp.myCells (growing the array if
	 * required), recalculates intensities, notifies listeners and repaints.
	 * The parameter e is the AWT MouseEvent. Local tmp is the doubled
	 * CellBody array used when growth is needed.
	 */
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
	
	/**
	 * Drag handler for tracing a cell body outline (mode 4). Drag samples
	 * are ignored for the first 500 ms after mouseTimer to avoid picking
	 * up the drag that immediately follows the double-click that entered
	 * this mode; after that, each sample is added (in image coordinates)
	 * to a CellBody - creating a new CellBody if one is not already in
	 * progress - and the panel is repainted. The parameter e is the AWT
	 * MouseEvent supplying the drag position.
	 */
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
	
	/**
	 * Press handler for the "add spine length" tool (mode 5). On the first
	 * click the method looks for a dendrite whose area contains the click
	 * position, selects it into selectedDendrite, and allocates a fresh
	 * three-line buffer (blue/cyan/magenta) in tp.newLines, seeding line 0
	 * with both endpoints at the click. On the second click it fixes line
	 * 0's end point and advances the tool into add-spine-width (mode 6).
	 * If the first click is outside any dendrite the tool exits back to
	 * mode 0. The parameter e is the AWT MouseEvent. Local myDendrites is
	 * the current dendrite group and k is its iteration index.
	 */
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
		

	/**
	 * Press handler for the "add spine width" tool (mode 6). The first click
	 * seeds line 1 (width) with both endpoints at the click position; the
	 * second click fixes line 1's end point and advances the tool into
	 * add-spine-neck mode (7). The parameter e is the AWT MouseEvent whose
	 * coordinates are converted from screen to image space through
	 * tp.zoom.
	 */
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
	/**
	 * Press handler for the "add spine neck" tool (mode 7). The first click
	 * seeds line 2 (neck) with both endpoints at the click; the second
	 * click fixes its end point and commits a new SpineInfo to the
	 * selectedDendrite using the three previously drawn lines plus the
	 * frame's calibration, then clears selectedDendrite and tp.newLines,
	 * regenerates the overlay, and returns the tool to add-spine-length
	 * mode (5) so another spine can be added, notifying spine listeners
	 * and repainting. The parameter e is the AWT MouseEvent supplying the
	 * click position in screen coordinates.
	 */
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
	
	/**
	 * Live cursor tracking shared by the spine-length, -width and -neck
	 * tools. Updates the end point of the line indexed by row in
	 * tp.newLines so it follows the current mouse position (in image
	 * coordinates); if tp.newLines or that particular line slot is null
	 * the method returns without doing anything. The parameter e is the
	 * AWT MouseEvent supplying the current cursor position and row
	 * selects which of the three spine lines is being edited.
	 */
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
	
	/**
	 * Press handler for the quick "add spine" tool (mode 8). Converts the
	 * click to image coordinates and finds the dendrite whose area contains
	 * the click; if none is found, the tool is dropped back to mode 0.
	 * When a dendrite is found it builds three tiny cross-shaped lines
	 * centered on the click (a 6-pixel X marker) as stand-ins for length,
	 * width and neck, commits a new SpineInfo to that dendrite using the
	 * frame's calibration, clears the working line state, then advances the
	 * tool to mode 8 again and fires spine listeners. The parameter e is
	 * the AWT MouseEvent. Local myDendrites is the current dendrite group
	 * and k is the loop index used to hit-test dendrites.
	 */
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
	
	/**
	 * Press handler for the "remove cell" tool (mode 10). Uses findCell to
	 * identify which CellBody was clicked; if none (k == -1) the method
	 * returns. Otherwise it nulls that slot and then compacts the
	 * tp.myCells array by scanning from the end and moving the last
	 * non-null cell into the vacated index, recalculating intensities and
	 * notifying cell listeners once. The tool returns to mode 0. The
	 * parameter e is the AWT MouseEvent. Local k is the index of the
	 * removed cell and j is the reverse scan cursor used during
	 * compaction.
	 */
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
	
	/**
	 * Returns the index in tp.myCells of the CellBody whose area contains
	 * the click position and whose groupMember matches the currently
	 * selected dendrite group, or -1 if there is no such cell (or if
	 * tp.myCells is null). The parameter e is the AWT MouseEvent whose
	 * coordinates are zoom-corrected into image space. Local k is the
	 * scan index and j is a leftover loop variable that is declared but
	 * unused.
	 */
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
	
	/**
	 * Press handler for the "remove dendrite" tool (mode 11). Currently a
	 * placeholder with no implementation. The parameter e is the AWT
	 * MouseEvent and is ignored.
	 */
	public void mouseClickedRemoveDendrite(MouseEvent e)
	{		
		
	}
	
	/**
	 * Hit-tests the current dendrite group against the click position and
	 * returns the first Dendrite whose dendriteArea contains the point, or
	 * null if none does (or if the group is null). The parameter e is the
	 * AWT MouseEvent used to derive the image-space point. Local k is the
	 * scan index, j is declared but unused, and myDendrites is the current
	 * dendrite group array.
	 */
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
	
	/**
	 * Press handler for the "remove spine" tool (mode 9). First locates the
	 * dendrite under the click; if none, resets to mode 0. Then scans that
	 * dendrite's spineData array for a spine whose bounds contain the
	 * click point. If none is found the tool resets and returns. When a
	 * spine is found the array is compacted: if it is the only spine the
	 * list is emptied; if it is the last element its slot is nulled;
	 * otherwise the last element is swapped into the removed slot. Spine
	 * listeners are notified and the panel/overlay are repainted. The
	 * parameter e is the AWT MouseEvent. Local d is the dendrite hit,
	 * spines is its spineData array, length is the current spine count,
	 * found is the index of the spine to remove (-1 if none), and k is
	 * the scan cursor.
	 */
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
	
	/**
	 * Press handler for the "add spine with shaft" tool (mode 12). The
	 * comment inside the body describes the full protocol: the first click
	 * anchors the base of the shaft inside a dendrite, subsequent mouse
	 * motion extends a primary line from the base to the cursor, a second
	 * click fixes that line's tip and builds two perpendicular "end cap"
	 * lines (one at the base, one at the tip) whose width is tp.newSpineWidth
	 * (mouse-wheel adjustable), a third perpendicular line is used as the
	 * shaft separator which the subsequent add-shaft-position mode will
	 * slide along the primary line. On the final click the three resulting
	 * lines become the sL, sW and sN of a new SpineInfo and the primary
	 * line is discarded. On entry, if tp.newLines is null, the method
	 * looks up the dendrite containing the click, allocates a 4-line
	 * buffer (all blue), seeds line 0 with both endpoints at the click
	 * and records selectedDendrite; otherwise it closes line 0's tip,
	 * calls lineTools to build the two perpendicular lines, stores them
	 * into tp.newLines[1] and [2], and advances to mode 13. The parameter
	 * e is the AWT MouseEvent. Local myDendrites is the current dendrite
	 * group array, k is its scan index, and perp holds the two
	 * perpendicular line segments returned by lineTools.
	 */
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
	
	/**
	 * Press handler for the "add shaft position" tool (mode 13). Picks a
	 * point on the primary shaft line (tp.newLines[0]) that is closest to
	 * the click, using lineTools.getXY to project the click onto the line
	 * (respecting the edge cases described in the inline comment: when one
	 * of x or y is off the line, when both are off, and when choosing
	 * between x and y based on dx versus dy). A new perpendicular pair is
	 * built at that projected position; the second of those two segments
	 * is installed as the temporary spine base via addTempSpineBase.
	 * A new SpineInfo is then created from tp.newLines[1], [2] and [3]
	 * and added to selectedDendrite; selection and line state are cleared
	 * and the tool advances to mode 12. The parameter e is the AWT
	 * MouseEvent. Local nL is a four-element scratch line built from
	 * tp.newLines[0]'s base and the projected point, xy is the
	 * [x,y] projection from lineTools, and perp is the
	 * perpendicular-line pair produced at that projection.
	 */
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
	
	/**
	 * Live feedback for mode 13: as the mouse moves, projects the cursor
	 * onto the primary shaft line tp.newLines[0] and rebuilds a temporary
	 * perpendicular "base" segment at that projection, installing it via
	 * tp.addTempSpineBase, then repaints. The parameter e is the AWT
	 * MouseEvent supplying the cursor position. Local nL is a copy of
	 * tp.newLines[0], xy is the projected [x,y] on that line returned
	 * by lineTools, and perp is the perpendicular pair whose second
	 * segment becomes the temporary spine base.
	 */
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
	
	/**
	 * Convenience method that repaints the data pane, rebuilds the overlay
	 * image, and repaints the TiffPanel, used after any change that
	 * affects both computed data and the image view.
	 */
	public void repaintAll()
	{
		tp.fL.repaintDataPane();
		tp.createOverlayImage();
		tp.repaint();
	}
	
	/**
	 * Wheel-handler that scales the zoom level by 10% per wheel notch,
	 * centered on the current cursor position, then repaints. The
	 * parameter e is the AWT MouseWheelEvent whose rotation and (x, y)
	 * drive the new zoom and its anchor.
	 */
	public void Zoom(MouseWheelEvent e)
	{
		tp.setZoom(tp.getZoom() + ((tp.getZoom() * e.getWheelRotation()*10)/100),e.getX(),e.getY());
        tp.repaint();
	}
	
	/**
	 * Wheel-handler that replaces tp.newSpineWidth with the wheel rotation
	 * value (note the reassignment operator =+, which assigns the
	 * positive wheel delta rather than adding it). The parameter e is
	 * the AWT MouseWheelEvent supplying the rotation.
	 */
	public void ChangeSpineWidth(MouseWheelEvent e)
	{
		tp.newSpineWidth =+ e.getWheelRotation();
	}
	
	/**
	 * Recomputes the two perpendicular cap lines (tp.newLines[1] and [2])
	 * from the current shaft line tp.newLines[0] using tp.newSpineWidth,
	 * then rebuilds the temporary spine-base segment at the current shared
	 * point p's projection onto the shaft and installs it via
	 * tp.addTempSpineBase. Local perp is the pair of perpendicular
	 * segments returned from lineTools and xy is the projected point on
	 * the shaft used to place the base.
	 */
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
	
	/**
	 * Sets the spine type on the currently selected spine (as a result of
	 * a spine popup menu choice), repaints the panel, and clears the
	 * selection. The parameter i is the spine-type code to assign and
	 * auto indicates whether the type was chosen automatically (true)
	 * or manually by the user (false).
	 */
	public void setSelectedSpineType(int i, boolean auto)
	{
		selectedSpine.setSpineType(i, auto);
		tp.repaint();
		selectedSpine = null;
	}
	
	/**
	 * Sets the selectedSpine field to the first spine on the given
	 * dendrite whose bounds contain the given point, or clears it to null
	 * if no such spine exists or if the dendrite is null or has no
	 * spineData. The parameter d is the dendrite to search and p is the
	 * image-space point to test; local spines is d.spineData and k is
	 * the scan index.
	 */
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
	
	/**
	 * Removes the given dendrite from the current group. As described in
	 * the inline comment, the algorithm first counts how many non-null
	 * dendrites exist, saves a reference to the last one, nulls its slot,
	 * then scans the array for the target dendrite d. If found, the saved
	 * "last" reference is moved into its slot and selectedDendrite is
	 * cleared; if not found (because it was already the last element) no
	 * further action is needed. The parameter d is the dendrite to
	 * delete. Local myDendrites is the current group array, count is the
	 * number of non-null entries, last is the previously-final dendrite,
	 * and k is the scan index.
	 */
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
	
	/**
	 * Press handler for the Sholl analysis tool (mode 14). Captures the
	 * click location, forwards it to the FrameListener to add a Sholl
	 * analysis center at that point, and then returns to the default
	 * selection tool. The parameter e is the AWT MouseEvent whose point
	 * (in panel coordinates) is used as the Sholl center; local p
	 * shadows the class field and holds that point.
	 */
	public void mouseClickedAddShollAnalysis(MouseEvent e)
	{
		Point p = e.getPoint();
		tp.fL.addSholl(p);
		setMode(0);
	}
	
	/**
	 * Press handler for the "add child dendrite" tool (mode 15). Converts
	 * the click to image coordinates, asks the parent selectedDendrite for
	 * the corresponding attachment point on its selected segment via
	 * getPoint, and if no dendrite is in progress builds a new Dendrite
	 * seeded at that attachment point whose myParent and parentSegment
	 * fields record the parent relationship. The parent's segment
	 * selection is then cleared, selectedDendrite is released, and the
	 * tool switches to the standard add-dendrite mode (1) so the child
	 * can be grown in the normal way. The parameter e is the AWT
	 * MouseEvent. Local p2 is the point on the parent dendrite where the
	 * child should attach.
	 */
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
	
	/**
	 * Live feedback for the "add child dendrite" tool (mode 15). As the
	 * mouse moves, converts the cursor position to image coordinates and
	 * asks the parent selectedDendrite which of its segments the cursor
	 * is within (via withinSegments), highlights that segment using
	 * SelectSegment, records the point via setChildPoint so the parent
	 * can draw a visual hint, and repaints. The parameter e is the AWT
	 * MouseEvent supplying the cursor position and k is the segment
	 * index returned from withinSegments.
	 */
	public void mouseMovedAddChildDendrite(MouseEvent e)
    {
		p.setLocation((e.getX()*100)/tp.zoom,(e.getY()*100)/tp.zoom);
		
    	int k = selectedDendrite.withinSegments(p);
    	selectedDendrite.SelectSegment(k);
    	selectedDendrite.setChildPoint(p);
    	tp.repaint();
    }
	
			
	
	
}
