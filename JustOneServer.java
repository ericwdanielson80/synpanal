package neuron_analyzer;

import java.io.*;
import java.net.*;

public class JustOneServer extends Thread {
   // you may need to customize this for your machine
String[] fN = null;
int fileNum = 0;
boolean isServer = false;
boolean isChecking = true;
boolean isClient = false;
long time;

   public static final int PORT = 1024 ; 

   ServerSocket serverSocket = null;
   Socket clientSocket = null;

   public JustOneServer(String s)
   {
	   time = System.currentTimeMillis();
	   addFile(s);
   }
   
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
   
   public boolean isServer()
   {
	   return isServer;
   }
   
   public boolean isChecking()
   {
	   return isChecking;
   }
   
   public boolean isClient()
   {
	   return isClient;
   }
   
  }

