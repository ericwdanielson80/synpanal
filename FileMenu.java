package neuron_analyzer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;

public class FileMenu extends JMenu implements ActionListener{
	JMenuItem Open;
	JMenuItem Close;
	JMenuItem dataDest;
	
	Frame1 frame;
		public FileMenu(Frame1 f)
		{
			super("File");
			frame = f;
			Open = new JMenuItem("Open");
			Open.addActionListener(this);	
			dataDest = new JMenuItem("Change Log Directory");
			dataDest.addActionListener(this);
			Close = new JMenuItem("Close");
			Close.addActionListener(this);
			add(Open);
			add(dataDest);
			add(Close);
			
			
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == Open)
				{
				if(frame.openFiles())
	    			frame.setAnalysisFrame(frame.fileNames,true);
				}
			if(e.getSource() == dataDest)
			{
				if(frame.setDataFileDirectory())
				{
					
				}
			}
			if(e.getSource() == Close)
			{
				frame.dispose();
			}
					
		}
}
