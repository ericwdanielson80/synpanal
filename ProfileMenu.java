package neuron_analyzer;

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

public class ProfileMenu extends JMenu implements ActionListener{
	JMenuItem Save;
	JMenuItem[] profileList;
	Frame1 frame;
	String home;
	functionListener fL;
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
		
		public void setFunctionListener(functionListener fl)
		{
			fL = fl;
		}
		
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
		
		private void makeNewProfile(String fileName)
		{					
			String c = "Calibration:" + new Double(fL.getCalibration()).toString();
			String w = "Width:" + new Integer(fL.getDendriteWidth()).toString();
			String cat = "Categories:" + frame.lI.getCategories();
			String[] info = {c,w,cat};
			writeFile(fileName, info);
			reloadMenus();
		}
		
		private void reloadMenus()
		{
			this.removeAll();
			add(Save);
			String[] list = loadProfiles();
			buildMenuItems(list);
			
		}
		
		private void loadProfile(String s)
		{
			/*
			 * get info from "s".prf file in Profiles directory
			 * set information in tiff panel and whereever else
			 */
			String file = home + "/Profiles/" + s+".prf";			
			readFile(file);
			
		}
		
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
		
		private void extractInfo(String line)
		{
			String[] info = line.split(":");
			setInfo(info);
		}
		
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
		
		private void setCalibration(String n)
		{
			if(fL != null)
				fL.setCalibration(new Double(n).doubleValue());
			else
				frame.calibration = new Double(n).doubleValue();
			
		}
		
		private void setDendriteWidth(String n)
		{
			if(fL != null)
				fL.setDendriteWidth(new Integer(n).intValue());
			else
				frame.dendriteWidth = new Integer(n).intValue();
		}
		
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
		
		private void setThreshold(int rgb,String[] n)
		{
			
		}
		
		private void setSpineRules(int spineType,String[] n)
		{
			
		}
		
		private String getNewFileName()
		{			
			return new OptionsFrame(fL).newProfile(frame); 
		}
		
		
}
