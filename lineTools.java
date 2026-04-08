
package neuron_analyzer;

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
public class lineTools {
    public lineTools() {
    }


    public int[][] getPerpendicularLine(int x1, int y1, int x2, int y2, int width)
    {
        int dX = x2-x1;
        int dY = y2-y1;
        double slope = 0;
        if(dX == 0)
        {
            int[][] out3 = new int[2][4];            
           out3[0][0] = x1 + width / 2;
           out3[1][0] =y1;
           out3[0][1] =x1 - width / 2;
           out3[1][1] =y1;
           out3[0][2] =x2  - width / 2;
           out3[1][2] = y2;
           out3[0][3] = x2 +width / 2;
           out3[1][3] =y2;
           return out3;
        }
        double perpSlope = 0;
        if(dY == 0)
        {
            int[][] out2 = new int[2][4];            
           out2[0][0] = x1;
           out2[1][0] =y1 + width / 2;
           out2[0][1] =x1;
           out2[1][1] =y1 - width / 2;
           out2[0][2] =x2;
           out2[1][2] = y2- width / 2;
           out2[0][3] = x2;
           out2[1][3] =y2 + width / 2;
           return out2;
        }
        perpSlope = (double)dX/(double)dY;
        double width2 = width / 2;

        double pDX = Math.sqrt(((double)width2*(double)width2)/((perpSlope*perpSlope) + (double)1));
        double pDY = perpSlope * pDX;
        if(dY <0&&pDY <0)
            pDY*= -1;
        if(dY>0&&pDY>0)
            pDY*= -1;
        if(dX<0&&pDX>0)
            pDX*=-1;
        if(dX>0&&pDX<0)
            pDX*=-1;



        int out[][] = new int[2][4];
        out[0][0] = (int)((double)x1+pDX);
        out[1][0] = (int)((double)y1+pDY);
        out[0][1] = (int)((double)x1-pDX);
        out[1][1] = (int)((double)y1-pDY);
        out[0][2] = (int)((double)x2-pDX);
        out[1][2] = (int)((double)y2-pDY);
        out[0][3] = (int)((double)x2+pDX);
        out[1][3] = (int)((double)y2+pDY);

        return out;
    }
    
    public int[] findIntersect(int[] lineA, int[] lineB)
    {
    	int dx1 = lineA[0]-lineA[2];
    	int dx2 = lineB[0]-lineB[2];
    	int dy1 = lineA[1]-lineA[3];
    	int dy2 = lineB[1]-lineB[3];
    	double m1 = dy1/dx1;
    	double m2 = dy2/dx2;
    	double b1 = lineA[1] - (m1*lineA[0]);
    	double b2 = lineB[1] - (m2*lineB[0]);
    	
    	double x = (b1-b2) / (m2 - m1);
    	double y = m1*x + b1;
    	
    	return new int[] {(int)x,(int)y};    	
    	
    }
    
    public int[] getXY(int[] lineA,int X, int Y)
    {
    	int dx = lineA[0]-lineA[2];    	
    	int dy = lineA[1]-lineA[3];  
    	
    	double m = 0;
    	if(dx != 0)
    		m = (double)dy/(double)dx;
    	
    	
    	double b = lineA[1] - (m*lineA[0]);  
    	
    	int[] xy = new int[2];
    	
    	if(X < lineA[0] && X < lineA[2])
    	{
    	 X = Math.min(lineA[0], lineA[2]);
    	}
    	else
    	{
    		if(X > lineA[0] && X > lineA[2])
        	{
        	 X = Math.max(lineA[0], lineA[2]);
        	}
    	}
    	
    	if(Y < lineA[1] && Y < lineA[3])
    	{
    	 Y = Math.min(lineA[1], lineA[3]);
    	}
    	else
    	{
    		if(Y > lineA[1] && Y > lineA[3])
        	{
        	 Y = Math.max(lineA[1], lineA[3]);
        	}
    	}
    	
    	
    	if(Math.abs(dx) > Math.abs(dy))
    	{    		
    		xy[0] = X;    		
    		if(m != 0)    			
    			xy[1] = (int)((m*X) + b);
    		else
    			xy[1] = Y;
    		
    	}
    	else
    	{
    		if(m != 0)
    			xy[0] = (int)((Y-b) / m);
    		else
    			xy[0] = X;
    		xy[1] = Y;
    	}
    	    	
    	
    	return xy;
    }
    
    public int[][] xyListstoLines(int[][] xy)
    {
    	int[][] out = new int[2][4];
    	out[0] = new int[] {xy[0][0],xy[1][0],xy[0][1],xy[1][1]};
    	out[1] = new int[] {xy[0][2],xy[1][2],xy[0][3],xy[1][3]};
    	return out;
    }

}
