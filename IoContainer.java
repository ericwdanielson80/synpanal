package neuron_analyzer;
import java.io.*;

public class IoContainer {

	public IoContainer()
	{
		
	}
	
	public void writeInt(DataOutputStream ds,String source, int i)
	{
		try
		{
			ds.writeInt(i);
		}
		catch (IOException ex)
		{
			System.out.println("Error writing " + source);
		}
	}
	
	public void writeIntArray(DataOutputStream ds,String source, int[] i)
	{
		writeInt(ds,source,i.length);
		for(int k = 0; k < i.length; k++)
		{
			writeInt(ds,source,i[k]);
		}		
	}
	
	public void writeBoolean(DataOutputStream ds,String source, boolean i)
	{
		try{
		ds.writeBoolean(i);
		}
		catch(IOException ex)
		{
			System.out.println("Error writing " + source);
		}
	}
	
	public void writeBooleanArray(DataOutputStream ds,String source, boolean[] i)
	{
		if(i == null)
		{
			writeInt(ds,source,0);
			return;
		}
		writeInt(ds,source,i.length);
		for(int k = 0; k < i.length; k++)
		{
			writeBoolean(ds,source,i[k]);
		}
	}
	
	public void writeDouble(DataOutputStream ds,String source, double i)
	{
		try{
			ds.writeDouble(i);
			}
			catch(IOException ex)
			{
				System.out.println("Error writing " + source);
			}
	}
	
	public int readInt(DataInputStream di,String source)
	{
		try{
			return di.readInt();
			}
		catch(IOException ex)
		{
			System.out.println("Error reading " + source);			
		}		
		return -1;
	}
	
	public int[] readIntArray(DataInputStream di,String source)
	{
		int[] out;
		int num;
		try{
			num = di.readInt();
			}
		catch(IOException ex)
		{
			System.out.println("Error reading " + source);
			return null;
		}	
		out = new int[num];
		for(int k = 0; k < out.length; k++)
		{
			out[k] = readInt(di,source);
		}
		return out;		
	}
	
	public boolean readBoolean(DataInputStream di,String source)
	{
		boolean b = false;
		try{
			b = di.readBoolean();
		}
		catch(IOException ex)
		{
			System.out.println("Error reading " + source);
			return b;
		}
		
		return b;
	}
	
	public boolean[] readBooleanArray(DataInputStream di,String source)
	{
		int k = readInt(di,source);
		if(k == -1)
			return null;
		boolean[] out = new boolean[k];
		for(int j = 0; j < out.length; j++)
		{
			out[j] = readBoolean(di,source);
		}
		return out;
	}
	
	public double readDouble(DataInputStream di,String source)
	{
		double b = -1;
		try{
			b = di.readDouble();
		}
		catch(IOException ex)
		{
			System.out.println("Error reading " + source);
			return b;
		}
		
		return b;
	}
	
	public void writeString(DataOutputStream ds,String source,String s)
	{
		byte[] b = s.getBytes();	
		writeInt(ds,source,b.length);
		try
		{
			ds.write(b);
		}
		catch(IOException ex)
		{
			System.out.println("Error writing " + source);
		}
	}
	
	public String readString(DataInputStream di,String source)
	{
		byte[] b = new byte[readInt(di,source)];		
		try
		{
			di.read(b);
		}
		catch(IOException ex)
		{
			System.out.println("Error writing " + source);
		}
		return new String(b);
	}
	
}
