package spreadsheet;

/*
 * TIME STAMP: April 9, 2012 - 3:05 PM
 * LAST EDITED/REVIEWED BY: David Zhang
 * REPORT:
 */

import java.awt.*;
import javax.swing.*;



import java.awt.event.*;
import java.io.File;

import static spreadsheet.MySpreadsheet.*;

public class MySpreadsheet_GUI extends JFrame
{
	//-----------------------------------------creates all the gui components
	public static JButton[][] button;
	private JButton help;
	private JButton undo;
	private JLabel [] x_axis;
	private JLabel [] y_axis;
	private JLabel extraLabel1;
	private JLabel extraLabel2;
	public static JLabel GUIMessages;
	//-----------------------------------------panel for the spreadsheet, the undo button and the help button
	private JPanel p;
	//-----------------------------------------panel for holding the messages that will be displayed to the user
	private JPanel BottomPanel;
	//-----------------------------------------panel to hold the two panels together
	private JPanel entirePanel;
	//-----------------------------------------a list of letters to attach a array index to a letter(for displaying letters on the letter axis)
	public char [] letters= {'A','B','C','D','E','F','G','H','I','J'};

	public MySpreadsheet_GUI(){
		//-----------------------------------------reads from a file that contains the cell values from previous program uses
		File saveData= new File ("saveData.txt");
		if (saveData.exists()) {
			MySpreadsheet.LoadData();
		}

		else {
			//----------------------------------------- if nothing is in the "saveData.txt" then the a new file is made with 0 as every value
			for (int x = 0; x < 10; x ++)
			{
				for (int y = 1; y < 11; y ++)
				{
					values[x][y] = 0;
				}
			}
		}
		//-----------------------------------------introduces the all the GUI components
		button = new JButton [10][10];
		help = new JButton();
		undo = new JButton();
		x_axis = new JLabel[10];
		y_axis = new JLabel[10];
		GUIMessages = new JLabel();
		extraLabel1 = new JLabel();
		extraLabel2 = new JLabel();
		p = new JPanel();
		BottomPanel = new JPanel();
		entirePanel = new JPanel();
		//-----------------------------------------attaches a string to the buttons on the spreadsheet based on what the cell value of its coordinate
		for (int x = 0; x < 10; x ++)
		{
			for (int y = 0; y < 10; y ++)
			{
				if (values[x][y+1] == 0 )
				{
					button [x][y] = new JButton ("");
				}
				else if (values[x][y+1] != 0 )
				{
					button [x][y] = new JButton ("" + Integer.toString(values[x][y+1]));
				}
			}
		}		
		//----------------------------------------- attaches a string to the other buttons and labels
		help = new JButton ("Help");
		undo = new JButton ("Undo");
		GUIMessages = new JLabel("__________");
		//----------------------------------------- attaches a string to the y-axis labels to a letter
		for (int i = 0; i < 10; i ++)
		{
			y_axis [i] = new JLabel ("       " + letters[i]);
		}
		//----------------------------------------- attaches a string to the x-axis 
		for (int i = 0; i < 10; i ++)
		{
			x_axis [i] = new JLabel ("       " + Integer.toString(i+1));
		}
		//----------------------------------------- the layout of the GUI panels 
		p.setLayout (new GridLayout(12, 11));
		entirePanel.setLayout(new BorderLayout(0,1));

		//----------------------------------------- places the x-axis labels on the top row
		p.add(extraLabel1);
		for (int i = 0; i < 10; i ++)
		{
			p.add(x_axis[i]);
		}
		//----------------------------------------- places the y-axis labels and the buttons
		for (int x = 0; x < 10; x ++)
		{
			for (int y = 0; y < 10; y ++)
			{
				if (y == 0)
				{
					p.add(y_axis[x]);
					p.add(button[x][y]);
				}
				p.add(button[x][y]);
			}
		}
		//----------------------------------------- place the buttons help and undo on the bottom
		p.add(extraLabel2);
		p.add(help);
		p.add(undo);
		//----------------------------------------- place the Message label on the bottom
		BottomPanel.add(GUIMessages);
		//----------------------------------------- add the CellButtonFunction to each cell. This function will return a string to GUIMessage when a cell is clicked
		for (int y = 0; y < 10; y ++)
		{
			for (int x = 0; x < 10; x ++)
			{
				//----------------------------------------- creates an instance of the function CellButtonFunctions 
				cellButtonProcedure p = new cellButtonProcedure();
				//cBF [x][y] = new cBF();
				p.x_value = x;
				p.y_value = y;
				button[x][y].addActionListener(p);
			}
		}
		//----------------------------------------- add the undo function to the undo button
		undo.addActionListener(new undoFunction());
		//----------------------------------------- makes an instance of the helpMenu GUI when the help button is clicked		
		help.addActionListener(new helpFunction());
		JFrame frame = new JFrame("MySpreadsheet");//the name that pops up at the top of the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//makes the GUI close when the X is clicked
		entirePanel.add(p, BorderLayout.PAGE_START);				//places the spreadsheet panel on the top of the final panel
		entirePanel.add(BottomPanel, BorderLayout.PAGE_END);		//places the Message panel on the bottom of the final panel
		frame.add(entirePanel);										
		//frame.setSize(900, 800);
		frame.setVisible(true);
		frame.pack();
	}
	/* cellButtonProcedure
	 * This method is used for each button of the cell. When the user clicks a cell, the calue of that cell will be shown on the bottom of the window
	 * Pre: The the coordinate of the button pressed
	 * Post: The value of that coordinate will be displayed in a message at the bottom of the window
	 */  
	public class cellButtonProcedure implements ActionListener
	{
		private int x_value;
		private int y_value;
		public void actionPerformed(ActionEvent e)
		{
			GUIMessages.setText("Value of " + letters[x_value] + Integer.toString(y_value+1) + ": " + values[x_value][y_value+1] );
			//System.out.println("Value of " + letters[x_value] + Integer.toString(y_value+1) + ": " + values[x_value][y_value+1]);
		}
	}
	/* undoFunction 
	 * This method is to attach the action of pressing the undo button to the undo method
	 * Pre: The undo button must be pressed
	 * Post: The undo function will be called up
	 */
	public class undoFunction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			Undo(names);
		}
	}
	/* helpFunction 
	 * the method is to start an instance of the helpmenu class which when the help button is pressed
	 * Pre: The help button must be pressed
	 * Post: A new helpMenu class will be created. This will start a new window of the instructions 
	 */
	public class helpFunction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			new helpMenu();
		}
	}
	/* helpMenu
	 * the method is to start a new instructions window when the help button is pressed
	 * Pre: The helpMenu is called. This can happen if the user enters help in the command bar, or if the help button is pressed
	 * Post: a new window of the instructions window will be called up
	 */
	public static class helpMenu extends JFrame{
		//----------------------------------------- introduces all of the GUI components
		private JFrame helpFrame;
		private JPanel helpMenuPanel;
		private JButton back;		
		private JScrollPane Scroll;
		private JTextArea TextArea;
		
		public helpMenu(){			
			helpMenuPanel = new JPanel();
			back = new JButton();
			back = new JButton("back");
			//----------------------------------------- The instructions that will be displayed
			String str = new String ("MySpreadsheet is a very user-friendly spreadsheet program \n" + 
					"that contains many features.\n"+
					"\n"+
					"MySpreadsheet is only limited to a 10x10 grid, \n"+
					"so if the coordinate you enter is outside of (A-J)(1-10), it will\n"+
					"not be accepted by the program. \n"+
					"\n"+
					"Here is a guide to using MySpreadsheet.\n"+
					"\n"+
					"A couple of notes to take into consideration: \n"+
					"\n"+
					"1. MySpreadsheet accepts commands in any case, so small case or capitals\n"+
					"are all accepted by the program. This rule applies to formulas too.\n"+
					"\n"+
					"2. In this version, formulas are only limited to a maximum of TWO items\n"+
					"on the right side of your equation. Please do not enter anything longer \n"+
					"than that, or the program will terminate. \n"+
					"\n"+
					"3. You are allowed to enter formulas with or without spaces. It will be\n"+ 
					"read properly either way.\n"+
					"\n"+
					"4. Only integers are accepted in this version of MySpreadsheet. Division\n"+
					"with a decimal result will be rounded to the nearest integer.  \n"+
					"\n"+
					"_________________________________________________\n"+
					"\n"+
					"1. Setting the value of a cell to a single value\n"+
					"\n"+
					"There are three options available for you:\n"+ 
					"(A) Directly enter a formula to set the value\n"+
					"	Eg. A5=120 or b10 = 230 \n"+
					"(B) Enter it as text according to the syntax below:\n"+ 
					"		set <coordinate of cell> to <value>\n"+
					"		Eg. Enter your command: Set D3 to 120\n"+
					"		Enter your command: set a9 to 2300\n"+
					"(C) Access the set function through the following procedure:\n"+ 
					"		1. Enter 'menu'\n"+
					"		2. From the options, enter '1'\n"+
					"		3. Follow the instructions on-screen\n"+
					"\n"+
					"2. Copying the value of one cell to another\n"+ 
					"\n"+
					"There are three options available for you:\n"+ 
					"\n"+
					"(A) Copy a value between cells through a formula\n"+ 
					"	Eg. A5=A10 or b2=d9\n"+
					"(B) Enter it as text according to the syntax below:\n"+ 
					"	copy <coordinate of cell> to <coordinate of cell> \n"+
					"	Eg. Enter your command: Copy A5 to D10\n"+
					"		Enter your command: copy a10 to e5\n"+
					"(C) Access the set function through the following procedure:\n"+ 
					"	1. Enter 'menu'\n"+
					"	2. From the options, enter '2'\n"+
					"	3. Follow the instructions on-screen\n"+
					"\n"+
					"3. Entering a formula\n"+ 
					"\n"+
					"Follow the below syntax to enter a valid formula:\n"+ 
					"	<coordinate of a cell>=<a value or a coordinate> <SYMBOL>* <a value or a coordinate>\n"+
					"* - Enter a symbol such as +,-,x,X,*,/\n"+
					"\n"+
					"Eg. A5=1200 or d3 = a1 + b2 or A1 = 20000 + b7 or e3 = a4 x 250 or A10=50x50 \n"+
					"\n"+
					"Accessing this function from 'menu' will only remind you that you can enter your fomula directly\n"+
					"after 'Enter your command'.\n"+
					"\n"+
					"4. Undo Previous Action\n"+ 
					"\n"+
					"(A) Enter 'Undo' in any case after 'Enter your command:' to undo previous action. If no previous action\n"+
					"is available, you will not be allowed to do it.\n"+
					"(B) You can click on the Undo button on the spreadsheet to access this function.\n"+
					"\n"+
					"Again, you can also access the undo function from entering 'menu', followed by '4'\n"+
					"The messages for Undo are displayed in the status bar at the bottom of the spreadsheet.\n"+
					"\n"+
					"5. Clear Saved Data\n"+ 
					"\n"+
					"Enter 'Clear' in any case after 'Enter your command:' and confirm the clearing of saved data.\n"+ 
					"Again, you can also access the undo function from entering 'menu', followed by '5'. You cannot undo this.\n"+
					"NOTe: when you clear the spreadsheet, you are unable to reverse the clear by clicking/entering undo\n"+
					"\n"+
					"6. Quit the Program\n"+ 
					"\n"+
					"Enter 'Quit' in any case after 'Enter your command:' to quit the program. All data is saved automatically as\n"+
					"you use the program. \n"+
					"Again, you can also access the undo function from entering 'menu', followed by '7'\n"+
					"\n"+
					"7. Obtaining the value of a cell\n"+ 
					"\n"+
					"Enter 'Show <coordinate of cell>' in any case after 'Enter your command:' to access the value of a cell.\n"+ 
					"\n"+
					"You can also click on the button of the cell to see the value of it at the bottom of the spreadsheet\n"+
					"\n"+
					"__________________________________________________\n"+
					"\n"+
					"Any questions or comments? Send an email to davzee@hotmail.com or jasonjslam@gmail.com.\n"+
					"\n"+
					"Thanks, we hope you enjoy using our spreadsheet.\n"+
					"\n"+
			"David Zhang & Jason Lam");
			TextArea = new JTextArea(str,55,55);// sets up the text area where the intructions will be
			Scroll = new JScrollPane(TextArea);	//set up the text area so that you can scroll up and down it
			Scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			//----------------------------------------- adds the components to the Panel
			helpMenuPanel.add(Scroll);
			helpMenuPanel.add(back);
			helpFrame = new JFrame("HELP");

			back.addActionListener(new ActionListener() {
				/* back
				 * the method will make the instructions window invisible as if the user exit out of it
				 * Pre: the user clicks back in the instructions window
				 * Post: the instructions window will become invisible
				 */
				public void actionPerformed(ActionEvent e)
				{
					//this is executed when the button is pressed
					helpFrame.setVisible(false);
				}
			});
			helpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//frame.setResizable(false);
			helpFrame.add(helpMenuPanel);
			//frame.setSize(200, 300);
			helpFrame.setVisible(true);
			helpFrame.pack();
		}		
	}	
}

