package neuron_analyzer.view;
import neuron_analyzer.model.*;
import neuron_analyzer.controller.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JMenu;

/**
 * The main application "File" menu. It exposes three actions — Open (to
 * pick image files and start a new analysis session), "Change Log
 * Directory" (to pick where data output is written), and Close (to
 * dispose the host frame) — and routes their selections back to the
 * owning Frame1 instance.
 */
public class FileMenu extends JMenu implements ActionListener{
	JMenuItem Open;
	JMenuItem Close;
	JMenuItem dataDest;

	Frame1 frame;
		/**
		 * Builds the File menu bound to the given Frame1. The constructor
		 * stores f in frame, creates the Open, dataDest ("Change Log
		 * Directory"), and Close menu items, registers this object as
		 * their ActionListener, and finally adds them to the menu in
		 * display order.
		 */
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

		/**
		 * Handles clicks on the three menu items. Open invokes
		 * frame.openFiles() to present the file chooser; if that returns
		 * true (at least one file was picked) the frame's analysis view
		 * is swapped in via setAnalysisFrame on the new file list. The
		 * dataDest item calls frame.setDataFileDirectory() so the user
		 * can pick a log directory (the successful-return branch is
		 * intentionally empty). Close simply disposes the frame,
		 * shutting the window down.
		 */
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
