package com.tcd.ir;
import com.tcd.ir.IndexDoc;
import com.tcd.ir.Seperator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
//import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

public class SearchForDocs {
	 
	public static void main(String args[])
	{	
		Seperator sp = new Seperator();
		String createQuerySeparator,createDocSeparator;
		if(args.length>1)
		{
			 createQuerySeparator = args[0];
			createDocSeparator = args[1];
			
		}
		else {
			createQuerySeparator = "/home/professor/cran/cran.qry";
			createDocSeparator = "/home/professor/cran/cran.all.1400";
		}
		sp.createfile(createQuerySeparator);
		sp.createfile(createDocSeparator);
		
		String docsPath = "/home/professor/SeparatedDocs/";
		IndexDoc ob = new IndexDoc();
		ob.fileIndexer(docsPath);
		
		String line;
	//	String line2;
	//	DecimalFormat df2 = new DecimalFormat(".##");
	//	double precisionVal[] = new double[256];
	//	double recallVal[] = new double[256];
	
	HashMap<Integer, ArrayList<Integer>> docs = new HashMap<Integer, ArrayList<Integer>>();

    try {
    
        BufferedReader bufferreader = new BufferedReader(new FileReader("/home/professor/QuerySeparate/CranQuestions"));
        BufferedReader bufferreader2 = new BufferedReader(new FileReader("/home/professor/cran/cranqrel"));
        line = bufferreader.readLine();
       // int j=1;
       // int docNum[] = new int[10000];
        int qnum = 0;
        File file = new File("/home/professor/test.txt");
        FileWriter fr = new FileWriter(file);
        while (line != null) {    
        	    
            String index = "index";
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new ClassicSimilarity());
            int hitsPerPage = 200;
            String field = "contents";
            Analyzer analyzer = new EnglishAnalyzer();
            QueryParser parser = new QueryParser(field, analyzer);
            parser.setAllowLeadingWildcard(true);
            qnum++;
            Query query = parser.parse(line);
            BoostQuery bq = new BoostQuery(query, 1.5f);
           
            /* Code for finding precision and recall manually without using trec_eval
            docs.put(j,doPagingSearch(searcher, bq, hitsPerPage, false,docs,qnum,fr));*/
            doPagingSearch(searcher, bq, hitsPerPage, false,docs,qnum,fr);
       //     j++;
            line = bufferreader.readLine();
            if(line == null) {break;}
            
            
        }
        
       /* 
        * Code for finding precision and recall manually without using trec_eval
        * line2 = bufferreader2.readLine();
        
        int relevantDocCount[] = new int[226];
        
        int[] matchCount = new int[226];
        
        while(line2!=null) {
        		String[] result = line2.split(" ");
        		
        		if(null==result)break;
        		relevantDocCount[Integer.parseInt(result[0])]++;
        		ArrayList<Integer> list = docs.get(Integer.parseInt(result[0]));
        		if(list.contains(Integer.parseInt(result[1]))) {
				matchCount[Integer.parseInt(result[0])]++;
        		}
        		
        		line2= bufferreader2.readLine();
        		if(line2 == null) {break;}
        }
        
         for(int i = 1;i<matchCount.length;i++) {
        	ArrayList<Integer> list = docs.get(i);
        	precisionVal[i] =  Double.parseDouble(Integer.toString(matchCount[i]))/Double.parseDouble(Integer.toString(list.size()));
        	precisionVal[i]=Double.parseDouble(df2.format(precisionVal[i]));
        	
        	System.out.println("\nPrecision value for query number "+i+" is "+precisionVal[i]);
        	 
        recallVal[i] = Double.parseDouble(Integer.toString(matchCount[i]))/Double.parseDouble(Integer.toString(relevantDocCount[i]));;
        recallVal[i]=Double.parseDouble(df2.format(recallVal[i]));
        System.out.println("Recall value for query number "+i+" is "+recallVal[i]);
        
        }
        
        double avgRecall,avgPrecision,recallValSum = 0;
       double precisionValSum=0;
        
       for(int m = 0;m<precisionVal.length;m++) {
    	   precisionValSum+= precisionVal[m];
        	recallValSum+= recallVal[m];
        	
        }
        avgPrecision = precisionValSum/precisionVal.length;
        System.out.println("Final value of precision is "+avgPrecision);
        
        avgRecall = recallValSum/recallVal.length;
        System.out.println("Final value of recall is "+avgRecall);
        */
        
 	 fr.close();
     System.out.println("Processing complete...");
     System.out.println("\nResult file generated at: /home/professor/"+" location"+" with the name test.txt \n");
        

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    } catch (ParseException e) {
		e.printStackTrace();
	}
}
	
	
	
	public static void doPagingSearch(IndexSearcher searcher, BoostQuery query, 
			int hitsPerPage, boolean interactive, HashMap<Integer, ArrayList<Integer>> docs,int qnum, FileWriter fr) throws IOException {
		       
			   TopDocs results = searcher.search(query, 5 * hitsPerPage);
			   ScoreDoc[] hits = results.scoreDocs;
			 
			   
			   for(int x = 0;x<hits.length;x++)
			   {
			   
			   String a = qnum +" 0 "+ searcher.doc(hits[x].doc).get("id") + " " + (x+1) +" "+ hits[x].score+ " 1" +"\n";
			   
			   
		        try {
		           
		            fr.write(a);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			   
			   
			   }
		
			   /*
			    * 
			    * Code for finding precision and recall manually without using trec_eval
			   ArrayList<Integer> docNums = new ArrayList<Integer>();
			   int numTotalHits = Math.toIntExact(results.totalHits);
			   
			   System.out.println(numTotalHits + " total matching documents");

			   int start = 0;
			   int end = Math.min(numTotalHits, hitsPerPage);
		       
		
			    
			    end = Math.min(hits.length, start + hitsPerPage);
			    
			    for (int i = start; i < end; i++) {
			      
			       Document doc = searcher.doc(hits[i].doc);
			       String path = doc.get("path");
			       if (path != null) {
			         if(path.substring(51).equalsIgnoreCase(".DS_Store") ){
			        	 	continue;
			         }
			         docNums.add(Integer.parseInt(path.substring(58)));
			         
			       } else {
			         System.out.println((i+1) + ". " + "No path for this document");
			       }
			                 
			     }

			    }
			   */
			//   return docNums;
			  }
}
