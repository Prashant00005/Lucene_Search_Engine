package com.tcd.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Seperator {
	


	public void createfile(String fileName) {

		BufferedReader reader;
		PrintWriter writer = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String readByLine = reader.readLine();
			String previousLine = "";
			String DocumentNumber = "";
			boolean docHist = false;
			boolean First = true;
			
			while (readByLine != null) {
				
				if (fileName.equalsIgnoreCase("/home/professor/cran/cran.qry")) 
				{
					if (docHist == false) 
					{
						File f = new File("/home/professor/QuerySeparate/" + "CranQuestions");
						//System.out.println("Prashant"+f.getPath());
						writer = new PrintWriter(new FileWriter(f));
						docHist = true;
					}
					
					if (readByLine.startsWith(".")) 
					{
						//System.out.println("Prashant"+docHist);
						readByLine = readByLine.replaceAll("\\s+", "");
						char c = readByLine.charAt(1);
						
						if (c == 'I')
						{
							if (First != true) 
							{
								writer.println();
							}
							writer.print(readByLine.substring(2));
							writer.print(" ");
							First = false;
						}
						
						else if (c == 'W') 
						{
							previousLine = ".W";
						}
						
					}
					else 
					{
						if (! previousLine.equals(".W")) 
						{
							writer.print(" ");
						}
						writer.print(readByLine);
						previousLine = "";
					}
					
				}
				
				else 
				{
					if (readByLine.startsWith("."))
					{
						readByLine = readByLine.replaceAll("\\s+", "");
						char c = readByLine.charAt(1);
					
						if (c == 'I') 
						{
							if (writer != null) 
							{
								writer.flush();
							}
							DocumentNumber = readByLine.substring(2);
						}
		
						else if (c == 'T') 
						{
							previousLine = ".T";
						}
						
					}
		
					else 
					{
						if (previousLine.equals(".T")) 
						{
							File file = new File("/home/professor/SeparatedDocs/" + DocumentNumber);
							writer = new PrintWriter(new FileWriter(file));
							previousLine = "";
						}
						writer.println(readByLine);
					}
				
				}
				readByLine = reader.readLine();
			
			}
			reader.close();
			writer.flush();

		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

	}

	

}