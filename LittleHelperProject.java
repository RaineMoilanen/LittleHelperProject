/**
 * 
 */
package raine.littlehelperproject;

/**
 * @author Raine J.W. Moilanen
 * The main java class for this application
 *
 */
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class LittleHelperProject {

	/**
	 * @param
	 * args[0] = file to read the inserted/updated variables
	 * args[1] = update or insert
	 * args[2] = table name;
	 * args[3] = separator in the file (for instance , or ; or |)
	 * args[4] = file to write the clauses to (if empty, then just to command line)
	 * args[5] = in case of update, how many columns before where part starts
	 * args[6] = in case of update if there is some generic value to use for all rows (for instance country_code)
	 * args[7] = the  value used for generic parameter for instance fi (6 and 7 then create WHERE country_code = 'fi')
	 * 
	 */
	public static void main(String[] args) {
		String helper="";
		if (args.length == 0) {
			helper="No arguments given! Stopping execution";
			System.out.println(helper);
			return;
		}
		else {
			try {
				//Let's handle the arguments and call either method that creates update clauses or insert clauses
				File file = new File(args[0]);
				if (!file.canRead()) {
					helper = "Can't read the file! Stopping execution";
					System.out.println(helper);
					return;
				}
				String tablename = "TABLENAME";
				if (args.length >= 3) {
					if (args[2] != null && !args[2].equals("")) {
						tablename = args[2];
					}
				}
				String separator = ",";
				if (args.length >= 4) {
					if (args[3] != null && !args[3].equals("")) {
						separator = args[3];
					}
				}
				String filetowrite = "";
				if (args.length >= 5) {
					if (args[4] != null && !args[4].equals("")) {
						filetowrite = args[4];
					}
					if (filetowrite.equals("0")) {
						filetowrite = "";
					}
				}
				int columnsbeforewhere = -1;
				if (args.length >= 6) {
					if (args[5] != null && !args[5].equals("")) {
						try {
							columnsbeforewhere = Integer.parseInt(args[5]);
						}
						catch (NumberFormatException ex) {
							System.out.println("Wrong type of argument! Expected integer! Error: "+ex);
							System.out.println("Error message: "+ex.getMessage());
							System.out.print(ex.getStackTrace());
							return;
						}
					}
				}
				String genericwherecolumn = "";
				if (args.length >= 7) {
					if (args[6] != null && !args[6].equals("")) {
						genericwherecolumn = args[6];
					}
				}
				String genericwhererule = "";
				if (args.length >= 8) {
					if (args[7] != null && !args[7].equals("")) {
						genericwhererule = args[7];
					}
				}
				if (args.length >= 2) {
					if (args[1].equalsIgnoreCase("insert")) {
						createInsertClauses(file, tablename, separator, filetowrite);
					}
					else if (args[1].equalsIgnoreCase("update")) {
						createUpdateClauses(file, tablename, separator, filetowrite, columnsbeforewhere, genericwherecolumn, genericwhererule);
					}
					else {
						System.out.println("Can't recognize action, stopping execution! args[1]: "+args[1]);
					}
				}
				else {
					createUpdateClauses(file, tablename, separator, filetowrite, columnsbeforewhere, genericwherecolumn, genericwhererule);
				}
				
				
						
			}
			catch (Exception ex) {
				helper = "Execution failed: "+ex;
				System.out.println(helper);
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				return;
			}
		}

	}
	
	//function that creates and writes all the update clauses
	private static void createUpdateClauses(File file, String tablename, String separator, String filetowrite, int columnsbeforewhere, String genericwherecolumn, String genericwhererule) {
		try {
			String clause="UPDATE "+tablename+" SET ";
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String apuupdate="";
		    line = br.readLine();
		    String[] types = line.split(separator);
		    if (types == null) {
		    	System.out.println("Empty file!");
		    	br.close();
		    	return;
		    }
		    line = br.readLine();
		    String[] headers = line.split(separator);
		    if (headers == null) {
		    	System.out.println("No headers!");
		    	br.close();
		    	return;
		    }
		    int amountofvalues = 0;
		    if (columnsbeforewhere > 0) {
		    	amountofvalues = columnsbeforewhere;
		    }
		    else {
		    	amountofvalues = headers.length;
		    }
		    int amountofwheresfromthefile = 0;
		    if (columnsbeforewhere > -1) {
		    	if ((headers.length-columnsbeforewhere) > 0) {
		    		amountofwheresfromthefile = (headers.length-columnsbeforewhere);
		    	}
		    }
		    while ((line = br.readLine()) != null) {
		    	String[] muuttujat = line.split(separator);
		    	apuupdate = String.valueOf(clause);
		    	for (int i=0; i<amountofvalues; ++i) {
		    		apuupdate += headers[i]+" = ";
		    		if (types[i].equalsIgnoreCase("string")) {
		    			apuupdate += "'";
		    		}
		    		apuupdate += muuttujat[i];
		    		if (types[i].equalsIgnoreCase("string")) {
		    			apuupdate += "'";
		    		}
		    		if (i < (amountofvalues-1)) {
		    			apuupdate += ", ";
		    		}
		    	}
		    	if (amountofwheresfromthefile > 0) {
		    		apuupdate += " WHERE ";
		    		for (int j = columnsbeforewhere; j < headers.length; ++j) {
		    			apuupdate += headers[j]+" = ";
		    			if (types[j].equalsIgnoreCase("string")) {
			    			apuupdate += "'";
			    		}
			    		apuupdate += muuttujat[j];
			    		if (types[j].equalsIgnoreCase("string")) {
			    			apuupdate += "'";
			    		}
			    		if (j < (amountofvalues-1)) {
			    			apuupdate += " AND ";
			    		}
		    		}
		    	}
		    	else {
		    		if (!genericwherecolumn.equals("")) {
		    			apuupdate += " WHERE ";
		    		}
		    	}
		    	if (!genericwherecolumn.equals("")) {
		    		if (!genericwhererule.equals("")) {
		    			if (amountofwheresfromthefile > 0) {
		    				apuupdate += " AND ";
		    			}
		    			apuupdate += genericwherecolumn+" = "+genericwhererule;
		    		}
	    		}
		    	apuupdate += ";";
		    	if (filetowrite == null || filetowrite.equals("")) {
		    		System.out.println(apuupdate);
		    	}
		    	else {
		    		writeToFile(apuupdate, filetowrite);
		    	}
		    	
		    }
		    br.close();
		}
		catch (Exception ex) {
			System.out.println("Couldn't read file! Exception: "+ex);
			System.out.println("Error message: "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	//function that creates all the insert clauses and writes them
	private static void createInsertClauses(File file, String tablename, String separator, String filetowrite) {
		try {
			String clause = "INSERT INTO "+tablename+" (";
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
		    String apuinsert="";
		    line = br.readLine();
		    String[] types = line.split(separator);
		    if (types == null) {
		    	System.out.println("Empty file!");
		    	br.close();
		    	return;
		    }
		    line = br.readLine();
		    String[] headers = line.split(separator);
		    if (headers == null) {
		    	System.out.println("No headers!");
		    	br.close();
		    	return;
		    }
		    while ((line = br.readLine()) != null) {
		    	String[] muuttujat = line.split(separator);
		    	apuinsert = String.valueOf(clause);
		    	for (int i=0; i < headers.length; ++i) {
		    		apuinsert += headers[i];
		    		if (i < (headers.length-1)) {
		    			apuinsert += ",";
		    		}
		    		else {
		    			apuinsert += ") VALUES (";
		    		}
		    	}
		    	for (int j=0; j < headers.length; ++j) {
		    		if (muuttujat[j] != null) {
		    			if (types[j].equalsIgnoreCase("string")) {
		    				apuinsert += "'"+muuttujat[j]+"'";
		    			}
		    			else {
		    				apuinsert += muuttujat[j];
		    			}
		    		}
		    		else {
		    			apuinsert += "NULL";
		    		}
		    		if (j < (headers.length-1)) {
		    			apuinsert += ",";
		    		}
		    		else {
		    			apuinsert += ");";
		    		}
		    	}	
		    	if (filetowrite == null || filetowrite.equals("")) {
		    		System.out.println(apuinsert);
		    	}
		    	else {
		    		writeToFile(apuinsert, filetowrite);	
		    	}
		    }
		    br.close();
		}
		catch (Exception ex) {
			System.out.println("Couldn't read file! Exception: "+ex);
			System.out.println("Error message: "+ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	//method to write a line to file
	private static void writeToFile(String linetowrite, String filetowrite) {
		try {
			File writefile = new File(filetowrite);
			BufferedWriter bw = new BufferedWriter(new FileWriter(writefile, true));
			bw.append(linetowrite);
			bw.newLine();
			bw.flush();
			bw.close();
		}
		catch (Exception ex) {
			System.out.println("Writing to file failed! Error: "+ex);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

}