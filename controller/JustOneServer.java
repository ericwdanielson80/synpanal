package neuron_analyzer.controller;
import neuron_analyzer.model.*;
import neuron_analyzer.view.*;

import java.io.*;
import java.net.*;

/**
 * Background thread that enforces a single-instance policy for the
 * application by opening a fixed TCP port. If the socket can be bound,
 * this process becomes the server and listens for subsequent launches
 * that send the file path they were asked to open; each received
 * filename is appended to an internal list so the server instance can
 * open those files. If the socket cannot be bound (another instance
 * already owns the port) this process becomes a client: it connects to
 * the existing server, sends its own file path, and then exits. The
 * short-lived loop times out one second after the last incoming
 * connection.
 */
public class JustOneServer extends Thread {
   // you may need to customize this for your machine
String[] fN = null;
int fileNum = 0;
public boolean isServer = false;
public boolean isChecking = true;
public boolean isClient = false;
long time;

   public static final int PORT = 1024 ; 

   public ServerSocket serverSocket = null;
   Socket clientSocket = null;

   /**
    * Records the current time as the server's start timestamp and adds
    * the provided file path s to the internal file list via addFile so
    * it will be processed after the server role is determined.
    */
   public JustOneServer(String s)
   {
	   time = System.currentTimeMillis();
	   addFile(s);
   }
   
   /**
    * Thread body. Attempts to bind a ServerSocket on PORT with backlog
    * 1 and, on success, acts as the canonical server: flipping
    * isServer/isChecking/isClient flags and entering a loop that
    * accepts a client each iteration (terminating after one second of
    * idleness). Each accepted connection reads a single filename line
    * through the BufferedReader input and appends it via addFile,
    * resetting the idle timer. If bind fails, this process becomes a
    * client: opens a Socket to localhost:PORT, writes the first stored
    * filename through a PrintStream, closes both ends, flips its flags
    * to indicate client status, and calls System.exit(1) to terminate.
    */
   public void run() {
	  System.out.println("running");
    try {
      // Create the server socket
    	 System.out.println("open socket");
      serverSocket = new ServerSocket(PORT, 1);  
      while (System.currentTimeMillis() - time < 1000) {
       // Wait for a connection
    	  System.out.println("isServer");
    	  System.out.println("waiting for client");
    	  isServer = true;
          isChecking = false;
          isClient = false; 
       clientSocket = serverSocket.accept();
       System.out.println("client found"); 
       BufferedReader input;
       try {
          input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          addFile(input.readLine());
          input.close();          
          time = System.currentTimeMillis();
       }
       catch (IOException e) {
    	   
          
       }
       
       clientSocket.close();
       }
      }
    catch (IOException ioe) {     
    	System.out.println("cannot socket");
    	try {
  	      Socket clientSocket = new Socket("localhost", JustOneServer.PORT);  	      
  	      PrintStream output;
  	      try {
  	         output = new PrintStream(clientSocket.getOutputStream());
  	         output.println(fN[0]);
  	         output.close();
  	         clientSocket.close();
  	        isServer = false;
 	    	isClient = true;
 	    	isChecking = false;	   
 	    	
 	    	
  	      }
  	      catch (IOException e) {
  	         	    	 	    	
  	      }
  	      System.exit(1);
  	    }
  	    catch (Exception e) {
  	      
  	      }

    	
     }
    }
   
   /**
    * Appends a new filename to the fN array, growing it as necessary.
    * When s is null all instance-role flags are cleared and the method
    * returns, signalling that no file was supplied. If the internal
    * array is not yet allocated, a single-element array is created;
    * otherwise if there is capacity the string is written at fileNum
    * and fileNum incremented, and when full a new array of double
    * length is allocated via System.arraycopy before the append. The
    * parameter s is the file path to add.
    */
   public void addFile(String s)
   {
	   
	   if(s == null)
	   {		   
		   isServer = false;
		   isClient = false;
		   isChecking = false;		   
		   return;
	   }
	
	
   	if(fN == null)
   		{
   		fN = new String[1];
   		fN[0] = s;
   		fileNum = 1;
   		
   		}
   	else
   	{
   		if(fileNum >= fN.length)
   		{
   			
   			String[]tmp = new String[fN.length * 2];
   			System.arraycopy(fN,0,tmp,0,fN.length);
   			fN = tmp;
   			fN[fileNum] = s;
   			fileNum++;
   		}
   		else
   		{   			
   			fN[fileNum] = s;
   			fileNum++;
   		}
   	}   	
   }
   
   /**
    * Returns the stored filenames as a File array of length fileNum,
    * or null when no files have been registered. The local out holds
    * the built array and each fN[k] is wrapped in a new File.
    */
   public File[] getFiles()
   {
	   if(fileNum == 0)
		   return null;
   	File[] out = new File[fileNum];    	
   		for(int k = 0; k < fileNum; k++)
   		{
   			out[k] = new File(fN[k]);
   		}
   	
   	return out;
   }
   
   /** Returns true once this process has taken on the server role. */
   public boolean isServer()
   {
	   return isServer;
   }
   
   /**
    * Returns true while the instance is still probing the port to
    * decide between server and client mode.
    */
   public boolean isChecking()
   {
	   return isChecking;
   }
   
   /** Returns true when this process ended up as a client (another instance owned the port). */
   public boolean isClient()
   {
	   return isClient;
   }
   
  }

