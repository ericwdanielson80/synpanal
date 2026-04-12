package neuron_analyzer.model;
import neuron_analyzer.view.*;
import neuron_analyzer.controller.*;
import java.io.*;

/**
 * Thin wrapper over DataInputStream/DataOutputStream that centralises all
 * primitive and array read/write operations performed by the save/load
 * routines throughout the application. Every method takes a String
 * "source" label that is used to print a diagnostic message if the
 * underlying IOException occurs, so errors can be traced back to the
 * logical field being written or read. This class is the single
 * serialisation entry point used by every Savable and related helper.
 */
public class IoContainer {

	/** Default constructor; holds no state. */
	public IoContainer()
	{

	}
	
	/**
	 * Writes a single integer to the stream ds, logging source as part
	 * of any error message. The parameter i is the value to write.
	 */
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
	
	/**
	 * Writes an int array by first writing its length and then each
	 * element in order, all via writeInt. The source string is used in
	 * error messages and i is the array being serialised.
	 */
	public void writeIntArray(DataOutputStream ds,String source, int[] i)
	{
		writeInt(ds,source,i.length);
		for(int k = 0; k < i.length; k++)
		{
			writeInt(ds,source,i[k]);
		}		
	}
	
	/**
	 * Writes a single boolean to the stream ds, logging source on any
	 * IOException. The parameter i is the value to write.
	 */
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
	
	/**
	 * Writes a boolean array by first writing its length (0 if the
	 * array is null), then each entry via writeBoolean. The parameter
	 * source labels the field for error messages and i is the array.
	 */
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
	
	/**
	 * Writes a double to the stream ds with source as the error label;
	 * i is the value to serialise.
	 */
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
	
	/**
	 * Reads one integer from di. On IOException the source label is
	 * printed and -1 is returned to signal failure.
	 */
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
	
	/**
	 * Reads an int array previously written via writeIntArray. The
	 * leading length is read first into num, out is sized accordingly,
	 * and each entry is then filled via readInt. Returns null if the
	 * leading length read fails.
	 */
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
	
	/**
	 * Reads one boolean from di, returning false on IOException and
	 * logging source as the error label. The local b is the value read
	 * and ultimately returned.
	 */
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
	
	/**
	 * Reads a boolean array written with writeBooleanArray, using the
	 * leading length k (returns null if k is -1). Each entry is read
	 * via readBoolean into the allocated out array.
	 */
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
	
	/**
	 * Reads a double from di, returning -1 as a sentinel on failure.
	 * The local b holds the value read and returned.
	 */
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
	
	/**
	 * Writes a String as the length-prefixed raw byte representation
	 * returned by s.getBytes(). The local b holds those bytes; the
	 * length is written via writeInt and then the byte payload is
	 * written directly to ds.
	 */
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
	
	/**
	 * Reads a String written by writeString: the leading length is read
	 * via readInt to size the byte array b, di.read then fills it, and
	 * a new String is constructed from those bytes and returned.
	 */
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
