package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JMenu;

/**
 * JMenu subclass representing the "Profile" menu in the main frame. It
 * enumerates the user-saved .prf profile files in the Profiles directory
 * and adds a menu item for each, plus a "Save Profile" command. Selecting
 * an entry loads its persisted settings (calibration, dendrite width,
 * categories, thresholds, spine rules) back into the running session via
 * the linked Frame1 and functionListener.
 */
public class ProfileMenu extends JMenu implements ActionListener{
	JMenuItem Save;
	JMenuItem[] profileList;
	Frame1 frame;
	String home;
	functionListener fL;
		/**
		 * Builds the menu titled "Profile", attaches the Save item, and then
		 * loads the list of existing profiles from the Profiles directory under
		 * the supplied homePath to create a menu item per file. The parameter f
		 * is the owning Frame1 used to read defaults and populate settings.
		 */
		public ProfileMenu(Frame1 f,String homePath)
		{
			super("Profile");
			frame = f;
			home = homePath;
			Save = new JMenuItem("Save Profile");
			Save.addActionListener(this);			
			add(Save);
			String[] list = loadProfiles();
			buildMenuItems(list);
			
		}
		
		/** Injects the functionListener used to push calibration, width, and other settings into the analysis pane. */
		public void setFunctionListener(functionListener fl)
		{
			fL = fl;
		}
		
		/** Dispatches menu actions: clicking Save triggers saveProfile, while clicking one of the profile items loads that profile by its text label. */
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == Save)
				{
					saveProfile();
					return;
				}
			for(int k = 0; k < profileList.length; k++)
			{
				if(e.getSource() == profileList[k])
				{					
					loadProfile(profileList[k].getText());
					return;
				}
			}
					
		}
		
		/** Prompts the user for a new profile filename and, if accepted, writes the current session settings into a .prf file under the Profiles directory. */
		private void saveProfile()
		{
			/*
			 * save profile to a new .prf file in Profiles directory
			 */
			String fileName = getNewFileName();
			if(fileName == null || fL == null)
				return;
			fileName = home + "/Profiles/" + fileName + ".prf";
			makeNewProfile(fileName);
		}
		
		/**
		 * Assembles the profile data (calibration, dendrite width, categories),
		 * writes it to the given fileName via writeFile, and then rebuilds the
		 * menu items so the new profile appears immediately. The locals c, w,
		 * cat, and info collect the formatted lines before writing.
		 */
		private void makeNewProfile(String fileName)
		{					
			String c = "Calibration:" + new Double(fL.getCalibration()).toString();
			String w = "Width:" + new Integer(fL.getDendriteWidth()).toString();
			String cat = "Categories:" + frame.lI.getCategories();
			String[] info = {c,w,cat};
			writeFile(fileName, info);
			reloadMenus();
		}
		
		/** Clears the current menu, re-adds the Save item, and rescans the Profiles directory so newly saved profiles show up in the menu. */
		private void reloadMenus()
		{
			this.removeAll();
			add(Save);
			String[] list = loadProfiles();
			buildMenuItems(list);
			
		}
		
		/** Builds the full path to the profile file "<home>/Profiles/<s>.prf" and invokes readFile to apply its settings. */
		private void loadProfile(String s)
		{
			/*
			 * get info from "s".prf file in Profiles directory
			 * set information in tiff panel and whereever else
			 */
			String file = home + "/Profiles/" + s+".prf";			
			readFile(file);
			
		}
		
		/**
		 * Returns the list of profile names by opening (or creating) the
		 * Profiles directory under the configured home path, enumerating the
		 * .prf files using OnlyExt as a filename filter, and stripping the
		 * ".prf" extension from each entry. Returns null when no files exist.
		 */
		private String[] loadProfiles()
		{
			/*
			 * open directory Profiles
			 * read all *.prf filenames
			 * put all filenames(no extension) into String[] 
			 * 
			 */
			File dir = new File(home + "/Profiles");
			if(!dir.exists())
				{
				try{
					dir.mkdir();
					}
				catch(SecurityException e)
					{
					System.out.println("Cannot create profiles directory");
					}
				}
				
			String[] files = dir.list(new OnlyExt("prf"));		//4-4
			if(files == null)
				return null;
			for(int k = 0; k < files.length; k++)
			{				
				files[k] = files[k].substring(0,files[k].length()-4);				
			}			
			return files;
		}
		
		/** Creates a JMenuItem for each name in list, attaches this class as its ActionListener, and adds it to the menu. Returns immediately when the list is null. */
		private void buildMenuItems(String[] list)
		{			
			if(list == null)
				return;
			profileList = new JMenuItem[list.length];
			for(int k = 0; k < list.length; k++)
			{
				profileList[k] = new JMenuItem(list[k]);
				add(profileList[k]);
				profileList[k].addActionListener(this);
			}
		}
		
		/**
		 * Reads the profile file line by line, handing each line off to
		 * extractInfo for parsing. Silently returns if the file cannot be
		 * opened or found; IO exceptions are stack-traced but not propagated.
		 */
		private void readFile(String file)
		{
			String s = "";
			File f = new File(file);
			if(!f.exists())
			{
				System.out.println("file not found");
				return;
			}
			
			try {
				 
				String currentLine;
	 
				BufferedReader br = new BufferedReader(
					new FileReader(f));
	 
				while ((currentLine = br.readLine()) != null) {
					extractInfo(currentLine);				
				}
	 
			} catch (FileNotFoundException e) {
	 
				e.printStackTrace();
				return;
	 
			} catch (IOException e) {
	 
				e.printStackTrace();
				return;
	 
			}
			
		}
		
		/** Writes each element of info as a separate line to the file at the given path, flushing the writer afterward. */
		private void writeFile(String fileName, String[] info)
		{
			
			File f = new File(fileName);
						
			try {
				 
				BufferedWriter br = new BufferedWriter(
					new FileWriter(f));
	 
				for(int k = 0; k < info.length; k++)
				{
					br.write(info[k]);
					br.newLine();
				}
				br.flush();
	 
			} catch (FileNotFoundException e) {
	 
				e.printStackTrace();
				return;
	 
			} catch (IOException e) {
	 
				e.printStackTrace();
				return;
	 
			}
			
		}
		
		/** Splits a profile line on ":" and forwards the tokens to setInfo for dispatch by key. */
		private void extractInfo(String line)
		{
			String[] info = line.split(":");
			setInfo(info);
		}
		
		/**
		 * Dispatches a parsed profile line to the appropriate setter based on
		 * its first token. Known keys include Calibration, Width, Categories,
		 * per-channel Red/Green/Blue thresholds, Spines, and the four spine
		 * type rule sets (Mushroom, Thin, Stubby, Filopodia).
		 */
		private void setInfo(String[] info)
		{			
			/*
			 * Calibration:0.12726
			 * Width:40
			 * Categories:Ctrl:Transfected
			 * Red:256:256
			 * Green:256:256
			 * Blue:256:256
			 * Spines:HN:HL:NL
			 * Mushroom:1.2:-1:-1
			 * Thin:1:1:1
			 * Stubby:1:1:1
			 * Filopodia:1:1:1
			 * 
			 */
			if(info[0].compareTo("Calibration") == 0)
			{
				setCalibration(info[1]);
				return;
			}
			
			if(info[0].compareTo("Width") == 0)
			{
				setDendriteWidth(info[1]);
				return;
			}
			
			if(info[0].compareTo("Categories") == 0)
			{
				setCategories(info);
				return;
			}
			if(info[0].compareTo("Red") == 0)
			{
				setThreshold(0,info);
				return;
			}
			if(info[0].compareTo("Green") == 0)
			{
				setThreshold(1,info);
				return;
			}
			if(info[0].compareTo("Blue") == 0)
			{
				setThreshold(2,info);
				return;
			}
			if(info[0].compareTo("Spines") == 0)
			{				
				return;
			}
			if(info[0].compareTo("Mushroom") == 0)
			{	
				setSpineRules(0,info);
				return;
			}
			if(info[0].compareTo("Thin") == 0)
			{	
				setSpineRules(1,info);
				return;
			}
			if(info[0].compareTo("Stubby") == 0)
			{	
				setSpineRules(2,info);
				return;
			}
			if(info[0].compareTo("Filopodia") == 0)
			{	
				setSpineRules(3,info);
				return;
			}
		}
		
		/** Parses n as a double and applies it as the calibration via the functionListener when available, or directly on the owning Frame1 otherwise. */
		private void setCalibration(String n)
		{
			if(fL != null)
				fL.setCalibration(new Double(n).doubleValue());
			else
				frame.calibration = new Double(n).doubleValue();
			
		}
		
		/** Parses n as an int and applies it as the dendrite width via the functionListener when available, or directly on the owning Frame1 otherwise. */
		private void setDendriteWidth(String n)
		{
			if(fL != null)
				fL.setDendriteWidth(new Integer(n).intValue());
			else
				frame.dendriteWidth = new Integer(n).intValue();
		}
		
		/** Extracts the category names (all tokens after the leading "Categories" key) and installs them on the log-info tabs. */
		private void setCategories(String[] n)
		{
			String[] s = new String[n.length - 1];
			for(int k = 0; k < s.length; k++)
			{
				s[k] = n[k+1];
			}
			//fL.setCategories(s);
			frame.lI.setTabs(s);
		}
		
		/** Placeholder for loading per-channel threshold settings; currently a no-op. */
		private void setThreshold(int rgb,String[] n)
		{
			
		}
		
		/** Placeholder for loading per-spine-type classification rules; currently a no-op. */
		private void setSpineRules(int spineType,String[] n)
		{
			
		}
		
		/** Opens an OptionsFrame dialog to prompt for a new profile name and returns the user's chosen filename. */
		private String getNewFileName()
		{			
			return new OptionsFrame(fL).newProfile(frame); 
		}
		
		
}
