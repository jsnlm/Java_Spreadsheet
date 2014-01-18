package spreadsheet;

/*
 * TIME STAMP: April 9, 2012 - 3:05 PM
 * LAST EDITED/REVIEWED BY: David Zhang
 * REPORT:
 */

/*
 * Program Name: MySpreadSheet	Contributors: David Zhang & Jason Lam 	Due Date: April 10, 2012 	Class: ICS3U1-01, Ms. Wun
 * 
 * Program Description: The program simulates a 10x10 spreadsheet, and allows the user to set any cell to a certain value,
 * 						copy and paste values between cells, enter formulas involving the cells, undo their actions and save their
 * 						data automatically or manually. Quick-access command 'menu' allows the user to view all the available options
 * 						in this program. The program features a GUI-based spreadsheet with input taken in directly from the Console. 
 *
 */

import java.io.*;
import java.util.*;

//import static spreadsheet.helpMenu.*;
import static spreadsheet.MySpreadsheet_GUI.*;

public class MySpreadsheet {

	public static int [][] values=new int [10][11]; //Allocate memory for all values
	public static int undoValue=0;	//Keeping track of undo records, with undoValue being the previous value, and undoCoord the previous coordinate 
	public static String undoCoord="",globalCommand="";	//globalCommand stores the user's root command for use in all methods
	public static String [] [] names= new String [10][11];//stores the cell coordinate names 
	public static String [] inputTokens=new String [20];//Allocate memory to store the tokens of user text input
	public static char [] letters= {'A','B','C','D','E','F','G','H','I','J'}; //Used to create the cell coordinate names

	/* ClearData() 
	 * This method clears and resets all values in 'saveData.txt' to 0.
	 * Pre: Text file 'saveData.txt' 
	 * Post: All the saved values are set to 0
	 */

	public static void ClearData() {
		try {
			FileWriter fw= new FileWriter("saveData.txt");
			BufferedWriter bf= new BufferedWriter(fw);
			for (int j=0; j<10; j++) {
				for (int k=1; k<11; k++) {
					bf.write("0"); 	//overwrites everything with a 0
					bf.newLine();
				}
			}
			bf.close();
		}		
		catch (Exception e) {System.out.println("Exception at "+e);}		
		
		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				values[i][j]=0; 	//resets all stored value within the program to 0
				button[i][j-1].setText(Integer.toString(values[i][j]));	
			}
		}
	}

	/* CommandProcess() 
	 * This method is where user commands end up. It analyzes the command, and sends it off to other functions to further process the commands
	 * Pre: User's command from ReadCommand()
	 * Post: The command will be sent to other functions based on the command
	 */
	public static void CommandProcess (String [] a, int [][] values, String [][]names) {

		String [] coord=new String [10]; String response; int number; Scanner input=new Scanner(System.in); //initialize the array for coordinates,
		// and other variables required to read user command

		if (a[0].equalsIgnoreCase("Set")) {	//if the first token of the command is equal to 'set' ignoring case, then the coordinate will be
			coord[0]=a[1].toUpperCase();	//the second token, and the fourth token would be the value that is assigned to the coordinate
			number=Integer.parseInt(a[3]);
			if (CoordinateCheck(coord[0],names)==true) Set(coord[0],number,names,values);	//if coordinate is valid, then the Set() method will be called up
		}
		
		
		else if (a[0].equalsIgnoreCase("Help")) { helpMenu help = new helpMenu(); }	
			
			else if (a[0].equalsIgnoreCase("Copy")) {	//if the first token of the command is equal to 'copy' ignoring case, then the initial coordinate 
			coord[1]=a[1].toUpperCase();			//would be the second token, and the destination token would be the fourth token
			coord[2]=a[3].toUpperCase();
			if (CoordinateCheck(coord[1],names)==true && CoordinateCheck(coord[2],names)==true) Copy(coord[1],coord[2],names,values);
			//if both coordinates are valid then the Copy() method will be called up 
		}
		else if (a[0].equalsIgnoreCase("Show")){//if the first token of the command is equal to 'show' ignoring case, and if the coordinate (or the second token)
			coord[3]=a[1].toUpperCase();		// is valid, then the value of the coordinate would be retrieved by calling up ShowValue() method 
			if (CoordinateCheck(coord[3],names)==true) ShowValue(coord[3],values,names);
		}
		else if (a[0].equalsIgnoreCase("Menu")) {//if the user's command is 'menu', then a list of functions within the program would be displayed
			int userInput; 			
			System.out.println("");
			System.out.println("You have the following options, Enter the number to execute the command ");
			System.out.println("1/ Set the value for a cell (Single Number) 		|| 		2/ Copy the value of a cell to another");
			System.out.println("3/ Set the value for a cell through a formula 		||		4/ Undo your previous action");
			System.out.println("5/ Clear Saved Data 					|| 		6/ Access help & tutorial");
			System.out.println("7/ Quit the spreadsheet program");
			System.out.print(":"); userInput=input.nextInt(); //the user will enter the number of the function he/she wish to use 

			switch (userInput) {			
			case 1: 	System.out.print("Enter the coordinates of your cell: "); coord[0]=input.next();	//gets the coordinate of the cell
						if (CoordinateCheck(coord[0].toUpperCase(),names)==true) {	// checks if the coordinate is valid
							System.out.print("Enter the number you wish to set it to: ");//if coordinate is valid
							userInput=input.nextInt();//obtains the number to assign to the coordinate
							Set(coord[0].toUpperCase(), userInput, names, values); //calls up the Set() method
						}break;			
			case 2: 	System.out.print("Enter the coordinates of initial cell: ");	
						coord[1]=input.next();	//gets the coordinate of the initial cell
						System.out.print("Enter the coordinates of the cell you wish to copy to: ");
						coord[2]=input.next(); //gets the coordinate of the destination cell 
						if (CoordinateCheck(coord[1].toUpperCase(),names)==true && CoordinateCheck(coord[2].toUpperCase(),names)==true) 
							//checks if both coordinates are valid
						Copy (coord[1].toUpperCase(), coord[2].toUpperCase(), names, values); break;	//if valid, calls up Copy() method	
			case 3: 	System.out.print("You could directly enter your formula within the command line. "); break;	//tells the user to directly enter command
																													//within the command line
			case 4: 	Undo(names); break;		//calls up Undo() method	
			case 5: 	System.out.print("Confirm clearing saved data? PLEASE NOTE: Clearing data can not be undone (Y/N): "); 
						response=input.next();//gets user to confirm clearing the data
						if (response.equalsIgnoreCase("Y")) { 
							ClearData();//if affirmative, calls up ClearData() method
						for (int x = 0; x < 10; x ++){
							for (int y = 0; y < 10; y ++){
								button[x][y].setText("");	//sets all buttons to clear to nothing
							}
						}
						System.out.println("Saved data successfully cleared!"); 
						System.out.println("");
						LoadData(); }; break;	//reloads the resetted data again						
			case 6: 
						helpMenu help = new helpMenu();break;									
			case 7: 	System.out.println("Thank you for using MySpreadsheet v1.0.1. All data has been saved."); System.exit(0); break;	//quits the program		
			default: 	System.out.println("Sorry, the number you entered was invalid. Please try again.");	//error message for invalid number
						System.out.println(""); break; 
						}
		}

		else if (globalCommand.equalsIgnoreCase("Undo")) {	//if the command is 'undo' ignoring case, then it will 
			Undo(names);									//call up Undo() method
		}
		
		else if (globalCommand.equalsIgnoreCase("Clear")) {
			System.out.print("Confirm clearing saved data? It could not be undone (Y/N): "); response=input.next();
			if (response.equalsIgnoreCase("Y")) { 
				ClearData(); //if affirmative, calls up ClearData() method
				System.out.println("Saved data successfully cleared!"); 
				System.out.println("");
				LoadData(); 	//reloads resetted data
			}
		}

		else if (globalCommand.equalsIgnoreCase("Quit")) {	//if the command is 'quit' ignoring case, then the program will terminate
			System.out.println("Thank you for using MySpreadsheet. All data has been saved."); 
			System.exit(0);
		}

		else if (RemovesSpacesFromString(globalCommand).substring(2,3).equals("=") || RemovesSpacesFromString(globalCommand).substring(3,4).equals("=")){
			//if an equal sign is read at substring(2,3) or (3,4) of the command with spaces removed, then the program assumes the remaining is a formula
			String[] symbol={"+","-","*","x","X","/"}; 	//used to determine the symbol of calculation within the formula
			boolean checkSymbols=false,checkpoint=false; int assignValue=0, num=0; 
			
			globalCommand=RemovesSpacesFromString(globalCommand);	//removes all spaces within the formula
			
			if (globalCommand.substring(2,3).equals("=")) {num=3;} 
			else if (globalCommand.substring(3,4).equals("=")) {num=4;}
			
			for (int z=0; z<6; z++) {
				for (int i=0; i<globalCommand.length(); i++) {
					if (globalCommand.substring(i,i+1).equals(symbol[z])) {	//if any one of the substrings within the formula is equal to a symbol, 
						checkSymbols=true;		//then there is symbol within the equation, and FormulaAnalysis() will be called up 
						FormulaAnalysis(i,num,symbol[z]);
					}
				}
			}
			
			if (checkSymbols==false){	//if there are no symbols within the equation, it is assumed that the user is assigning a value to the coordinate
										//or copying a value of one cell to another through a formula
				
				if (globalCommand.substring(2,3).equals("=")) {	//Two-character coordinate
					if (Character.isDigit(globalCommand.substring(3,4).charAt(0))) {// if the one substring after the equal sign is a number, it is assumed
																					//that the user is assigning a number to the coordinate 
						assignValue=Integer.parseInt(globalCommand.substring(3,globalCommand.length()));//assign value is the entire substring AFTER the equal sign
						if (CoordinateCheck(globalCommand.substring(0,2).toUpperCase(), names))	//if the coordinate before the equal sign is valid
							{Set(globalCommand.substring(0,2).toUpperCase(),assignValue,names,values); SaveData(values);}	//Set() and SaveData() methods are called up
					}
					else if(Character.isDigit(globalCommand.substring(3,4).charAt(0))==false) {	//if the one substring after the equal sign is a letter, it is assumed
																								// that the user is copying between cells 
						if (CoordinateCheck(globalCommand.substring(0,2).toUpperCase(), names) //checks for validity of coordinates
							&& CoordinateCheck(globalCommand.substring(3,globalCommand.length()).toUpperCase(), names)) {
							checkpoint=true; 
							for (int i=0;i<10;i++) {
								for (int j=1; j<11; j++) {
									if (globalCommand.substring(3,globalCommand.length()).toUpperCase().equals(names[i][j])) {
										assignValue=values[i][j];	//the assign value is the value of the initial cell 
									}
								}
							}
						}
						if (checkpoint==true) Set(globalCommand.substring(0,2).toUpperCase(), assignValue,names,values);	//if the coordinates were valid, the 
															
					}
				}

				else if (globalCommand.substring(3,4).equals("=")) { //Three-character coordinate
					if (Character.isDigit(globalCommand.substring(4,5).charAt(0))) {	//the procedure is same as above for two character-coordinate
						assignValue=Integer.parseInt(globalCommand.substring(4,globalCommand.length()));
						if (CoordinateCheck(globalCommand.substring(0,3).toUpperCase(), names))
							Set(globalCommand.substring(0,3).toUpperCase(),assignValue,names,values); SaveData(values);
					}
					else if(Character.isDigit(globalCommand.substring(4,5).charAt(0))==false) {
						if (CoordinateCheck(globalCommand.substring(0,3).toUpperCase(), names)
							&& CoordinateCheck(globalCommand.substring(4,globalCommand.length()).toUpperCase(), names))	{
							checkpoint=true;
							for (int i=0;i<10;i++) {
								for (int j=1; j<11; j++) {
									if (globalCommand.substring(4,globalCommand.length()).toUpperCase().equals(names[i][j])) 
										assignValue=values[i][j];							
							}
						}
					}
					if (checkpoint==true)Set(globalCommand.substring(0,3).toUpperCase(), assignValue,names,values); SaveData(values);}					
				} 
			}		
		}	

		else {//if the command is not recognized, these messages are displayed
			System.out.println("Sorry, your command could not be recognized.");
			System.out.println("You could view help by entering 'help', or enter 'menu' to view available options.");
			System.out.println("");
		}
	}//CommandProcess()

	/* CoordinateCheck() 
	 * This method checks if coordinates from commands are valid or not. 
	 * Pre: User command containing coordinates
	 * Post: Checks if the coordinates are valid with the boundaries of the spreadsheet. If not, an error message would be displayed.
	 */

	public static boolean CoordinateCheck(String coordinate, String [][] names) {
		boolean checkpoint=false;
		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				if (coordinate.toUpperCase().equals(names[i][j])) {checkpoint=true; break;}	//if the coordinate exists, returns true
			}
		}
		if (checkpoint==false) {	//if the coordinate does not exist, return false along with messages
			System.out.println("Please check your coordinate(s).");
			System.out.println("You could view help by entering 'help', or enter 'menu' to view available options.");
			System.out.println("");
		}
		return checkpoint; 
	} //CoordinateCheck()

	/* Copy() 
	 * This method copies values of cells between each other.
	 * Pre: The initial cell coordinate and value, the destination cell coordinate
	 * Post: The value will be copied from the initial cell to the destination cell
	 */

	public static void Copy (String coord1, String coord2, String [][]names, int[][]values){
		int copyValue=0; 
		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				if (coord1.equals(names[i][j]))  copyValue=values[i][j];//determines the value to copy				
			}
		}

		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				if (coord2.equals(names[i][j])) { 
					undoValue=values[i][j];	//stores the previous value and coordinate 
					undoCoord=names[i][j];
					values[i][j]=copyValue;	//asigns it to the destination corrdinate 
					button[i][j-1].setText(Integer.toString(values[i][j]));	//displays it on the button 
				}
			}
		}

		System.out.println("The value of "+coord1.toUpperCase()+" has been succesfully copied to "+coord2.toUpperCase()+".");
		System.out.println("");
		SaveData(values);
	}//Copy()

	/* FormulaAnalysis() 
	 * This method analyzes formulas and sends them to either NumberCellFormulaCalculation() or CellCellFormulaCalculation().
	 * Pre: A formula entered by the user
	 * Post: It will be processed and sent off to either NumberCellFormulaCalculation() or CellCellFormulaCalculation()  
	 */
	public static void FormulaAnalysis(int i,int num,String symbol) {
		boolean checkpoint=false,checkpoint2=false; 
		if(Character.isDigit(globalCommand.substring(i+1,i+2).charAt(0)) 
		   && !Character.isDigit(globalCommand.substring(num,num+1).charAt(0)))  {	//if the character after the symbol is a digit, then 
																				//NumberCellFormulaCalculation() is called up 
			checkpoint=false; checkpoint2=false; NumberCellFormulaCalculation(i,num,symbol,checkpoint2);											
		}
		else if(Character.isDigit(globalCommand.substring(num,num+1).charAt(0)) 
				&& !Character.isDigit(globalCommand.substring(i+1,i+2).charAt(0))) { 	//if the character after the equal sign is a digit, then 
																					//NumberCellFormulaCalculation() is also called up
			checkpoint=false; checkpoint2=true; NumberCellFormulaCalculation(i,num,symbol,checkpoint2);
		}
		else if (Character.isDigit(globalCommand.substring(num,num+1).charAt(0)) 
				&& Character.isDigit(globalCommand.substring(i+1,i+2).charAt(0))) {
				NumberNumberFormulaCalculation(i,num,symbol);
				checkpoint=false; 
		}
		else checkpoint=true;
		if (checkpoint==true) {CellCellFormulaCalculation(i,num,symbol);}SaveData(values);	//Else CellCellFormulaCalculation() is called up 
	}//FormulaAnalysis
	
	/* NumberNumberFormulaCalculation() 
	 * This method processes formulas containing two values that are not coordinates.
	 * Pre: A formula that contains two values that are not coordinates and are on the right side of equation
	 * Post: The value will be calculated, assigned to the destination coordinate and stored 
	 */
	public static void NumberNumberFormulaCalculation(int i, int num, String symbol) {
		int num1, num2,accum=0; 
		num1=Integer.parseInt(globalCommand.substring(num,i));
		num2=Integer.parseInt(globalCommand.substring(i+1,globalCommand.length()));
		if (symbol.equals("+")) accum=num1+num2;	//Does the appropriate calculation based on symbol
		else if (symbol.equals("-")) accum=num1-num2;
		else if (symbol.equals("*") || symbol.equalsIgnoreCase("x")) accum=num1*num2;
		else if (symbol.equals("/")) accum=num1/num2;
		if (CoordinateCheck(globalCommand.substring(0,num-1),names)) 
			Set(globalCommand.substring(0,num-1).toUpperCase(),accum,names,values);
	}

	/* NumberCellFormulaCalculation() 
	 * This method processes formulas containing a value and a coordinate.
	 * Pre: A formula that contains a value and a coordinate 
	 * Post: The value will be calculated, assigned to the destination coordinate and stored 
	 */
	
	public static void NumberCellFormulaCalculation(int i, int num, String symbol,boolean checkpoint2) {
		
		int accum=0; String coordinate="",value=""; 
		
		if (checkpoint2==true) {
			coordinate=globalCommand.substring(i+1,globalCommand.length()).toUpperCase();
			value=globalCommand.substring(num,i);
		}
		else if (checkpoint2==false){ 
			coordinate=globalCommand.substring(num,i).toUpperCase();
			value=globalCommand.substring(i+1,globalCommand.length());
		}
			
		if (CoordinateCheck(coordinate,names) 		//Checks if both coordinates are valid
			&& CoordinateCheck(globalCommand.substring(0,num-1).toUpperCase(),names)) {	
			
			for (int x=0; x<10; x++) {
				for (int y=1; y<11; y++) {
							
					if (coordinate.equals(names[x][y])) {
						if (symbol.equals("+")) accum=values[x][y]+Integer.parseInt(value);	//Does the appropriate calculation based on symbol
						else if (symbol.equals("-")) accum=values[x][y]-Integer.parseInt(value);
						else if (symbol.equals("*") || symbol.equalsIgnoreCase("x")) accum=values[x][y]*Integer.parseInt(value);
						else if (symbol.equals("/")) accum=values[x][y]/Integer.parseInt(value);
						Set(globalCommand.substring(0,num-1).toUpperCase(),accum,names,values);
						SaveData(values);
					}
				}
			}
		}
	}//NumberCellFormulaCalculation()

	/* CellCellFormulaCalculation() 
	 * This method processes formulas containing two coordinates.
	 * Pre: A formula that contains two coordinates on right side of equation
	 * Post: The value will be calculated, assigned to the destination coordinate and stored 
	 */
	
	public static void CellCellFormulaCalculation(int i, int num, String symbol) {
		int accum=0; 
		
		if (CoordinateCheck(globalCommand.substring(num,i).toUpperCase(), names) 	//Checks if all three coordinates are valid
				&& CoordinateCheck(globalCommand.substring(i+1,globalCommand.length()).toUpperCase(), names)
				&& CoordinateCheck(globalCommand.substring(0,num-1).toUpperCase(), names)) {
			
			for (int x=0; x<10; x++) {
				for (int y=1; y<11; y++) {
					if (globalCommand.substring(num,i).toUpperCase().equals(names[x][y])) {
						accum=values[x][y];	
					}
				}
			}

			for (int x=0; x<10; x++) {
				for (int y=1; y<11; y++) {
					if (globalCommand.substring(i+1,globalCommand.length()).toUpperCase().equals(names[x][y])) {
						if (symbol.equals("+")) accum=accum+values[x][y];		//Does appropriate calculation based on symbol
						else if (symbol.equals("-")) accum=accum-values[x][y];
						else if (symbol.equals("*") || symbol.equalsIgnoreCase("x")) accum=accum*values[x][y];
						else if (symbol.equals("/")) accum=accum/values[x][y];
	
						Set(globalCommand.substring(0,num-1).toUpperCase(),accum,names,values);
					}
				}
			}
		}		
	}//CellCellFormulaCalculation()
	
	/* LoadData() 
	 * This method allows the cell values to be read from a text file 'saveData.txt'.
	 * Pre: Text file 'saveData.txt' 
	 * Post: All the cell values are obtained through a double-for loop and stored to global variable 'values'
	 */

	public static void LoadData() {
		try {
			FileReader fr= new FileReader("saveData.txt");
			Scanner src= new Scanner(fr); 
			for (int j=0; j<10; j++) {
				for (int k=1; k<11; k++) {values[j][k]=src.nextInt();}	//Coordinate values are retrieved from 'saveData.txt' and stored to the array
																		// called values
			}
			src.close();
		}

		catch (Exception e) {System.out.println("Exception at: "+e);}	
	}//LoadData()

	/* LoadSpreadSheet() 
	 * This method loads the GUI-based spreadsheet
	 * Pre: The class of MySpreadsheet_GUI.java
	 * Post: Displays the spreadsheet on screen 
	 */

	public static void LoadSpreadSheet() {
		MySpreadsheet_GUI g1=new MySpreadsheet_GUI();	//Makes a new instance of the GUI
	}//LoadSpreadsheet

	/* ReadCommand() 
	 * This method reads in the user's command and sends it off the CommandProcess to analyze it.
	 * Pre: A user command
	 * Post: Sends the command to CommandProcess
	 */

	public static void ReadCommand(int [][] values, String [][]names){
		String [] inputTokens=new String [20];
		Scanner input=new Scanner(System.in);
		boolean checkpoint=false; 
		int i=0;

		System.out.print("Enter your command: "); globalCommand=input.nextLine();	//gets command

		StringTokenizer st=new StringTokenizer (globalCommand);
		while (st.hasMoreTokens()) {
			inputTokens[i]=st.nextToken();	//splits the command up into tokens, if necessary
			i++; 
		}

		if (i==1 && inputTokens[0].equalsIgnoreCase("Set") || i==1 && inputTokens[0].equalsIgnoreCase("Copy")) {
			checkpoint=false; //prevents users from entering invalid commands such as 'set' and 'copy' that cause fatal exceptions
		}
		else if (i==1 && inputTokens[0].equalsIgnoreCase("Undo") || i==1 && inputTokens[0].equalsIgnoreCase("Clear")
				|| i==1 && inputTokens[0].equalsIgnoreCase("Menu") || i==1 && inputTokens[0].equalsIgnoreCase("Quit")) {
			checkpoint=true;
		}	//these are the only single-word commands that are accepted by the program
		else if (RemovesSpacesFromString(globalCommand).substring(2,3).equals("=") || RemovesSpacesFromString(globalCommand).substring(3,4).equals("=")){
			checkpoint=true; 	//if all the spaces are removed, and substring(2,3) or (3,4) is an equal sign, then the formula is allowed to pass through
		} else checkpoint=true; 

		if (checkpoint==true) {CommandProcess(inputTokens,values,names);}	//if checkpoint is true, the command will continue on into CommandProcess()
		else {
			System.out.println("Sorry, your command could not be recognized.");
			System.out.println("You could view help by entering 'help', or enter 'menu' to view available options.");
			System.out.println("");
		}
	}//ReadCommand()

	/* RemovesSpacesFromString() 
	 * This method removes all spaces within a formula.
	 * Pre: A formula that contains spaces
	 * Post: All spaces will be removed, and the formula would be passed out. 
	 */
	
	public static String RemovesSpacesFromString (String formula)
	{
		String stringWithoutSpaces = "";
		for (int i = 0; i< formula.length(); i++) {
			if (formula.charAt(i)!= ' ') stringWithoutSpaces += Character.toString(formula.charAt(i));
		}
		return stringWithoutSpaces;
	}
	
	/* SaveData()
	 * This method allows the cell values to be stored in a text file 'saveData.txt'.
	 * Pre: All the cell values from global variable 'values' 
	 * Post: Text file 'saveData.txt' with all the cell values stored through a double-for loop 
	 */

	public static void SaveData (int [][] values) { 
		try {
			FileWriter fwriter = new FileWriter ("saveData.txt"); 
			BufferedWriter out = new BufferedWriter (fwriter);
			for (int j=0; j<10; j++) {
				for (int k=1; k<11; k++) {
					out.write(""+values[j][k]);	//writes all saved values into the text file 'saveData.txt'
					out.newLine();
				}
			}
			out.close();
		}

		catch (Exception e) {
			System.out.println("Exception at "+e);
		}		
	}//SaveData()

	/* Set() 
	 * This method sets the specified value of a cell by the user.
	 * Pre: A coordinate and a value to assign it to  
	 * Post: The value will be assigned to the coordinate and saved promptly
	 */

	public static void Set (String coord, int num, String [][] names, int [][]values){

		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				if (coord.equals(names[i][j])) {
					undoValue=values[i][j];	//Saves the previous value and coordinate for undo purpose
					undoCoord=names[i][j];
					values[i][j]=num; 	//the value of the assigned number is stored to the value of the cell
					button[i][j-1].setText(Integer.toString(values[i][j]));	//displayed on the button
				}
			}
		}

		System.out.println("You have successfully set "+coord.toUpperCase()+" to "+num+".");
		System.out.println("");
		SaveData(values);
	}//Set()

	/* ShowValue() 
	 * This method allows a manual display of a specific cell
	 * Pre: If the user wants to see a value of a cell manually
	 * Post: Displays the entire value of a cell
	 */

	public static void ShowValue (String coord, int [][] values, String [][] names) {

		for (int j=0; j<10; j++) {
			for (int k=1; k<11; k++) {
				if (coord.equalsIgnoreCase(names[j][k])) {
					System.out.println("The value of "+coord.toUpperCase()+" is "+values[j][k]+".");
					System.out.println("");//prints out the value of a specific cell
				}
			}
		}
	}//ShowValue()

	/* Undo() 
	 * This method undos the previous action by the user ONCE only. 
	 * Pre: If the user wants to undo previous action 
	 * Post: A cell's value will be restored to its previous value
	 */

	public static void Undo (String [][] names) {
		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				if (undoCoord.equals(names[i][j]) && undoValue!=0) 
				{ values[i][j]=undoValue; button[i][j-1].setText(Integer.toString(values[i][j]));}
				else if (undoCoord.equals(names[i][j]) && undoValue==0) {values[i][j]=undoValue; button[i][j-1].setText((""));}
			}
		}
		if (undoValue==0 && undoCoord.equals("")) GUIMessages.setText("There are no undos available.");	//no undo is available if undoValue=0
		else {
			GUIMessages.setText("Your previous action has been successfully undone. It cannot be redone again.");
			undoValue=0; //after undo is done, the value is set to 0 and the coordinate is set to null
			undoCoord="";
			SaveData(values);//saved automatically
		}
	}//Undo()

	/* main() 
	 * Initiates the entire program. 
	 * Pre: Text file 'saveData.txt', MySpreadsheet_GUI.java 
	 * Post: Initiates the entire program.
	 */

	public static void main (String [] args){
		File saveData= new File ("saveData.txt");

		for (int i=0; i<10; i++) {
			for (int j=1; j<11; j++) {
				names [i][j]=""+letters[i]+j;	//sets the names of all coordinates by attaching a letter between A-J and a number 1-10 to each other
			}
		}

		if (saveData.exists()) {
			LoadData();
		}
		else {
			for (int i=0; i<10; i++) {
				for (int j=1; j<11; j++) {
					values [i][j]=0;
				}
			}
		}

		System.out.println("Welcome to MySpreadsheet");
		System.out.println("Created by: David Zhang & Jason Lam 2012");
		System.out.println("(For optimal experience, Maximize your console window and make sure both the spreadsheet and console are both visible.)");
		System.out.println("");
		LoadSpreadSheet();

		while (true) {
			ReadCommand(values,names);	//runs the readCommand line
			for (int i=0; i<10; i++) {
				for (int j=1; j<11; j++) {
					if (values[i][j] == 0)
					{
						button[i][j-1].setText("");	//sets text to blank if there are no values
					}
				}
			}
			
			
			
		}
	}//main()
}