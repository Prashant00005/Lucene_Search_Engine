package com.tcd.ir;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class IndexDoc {
  
  IndexDoc() {}

 
  public void fileIndexer(String docsPath) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";
   String indexPath = "index";
  
   boolean create = true;
   

   if (docsPath == null) {
     System.err.println("Usage: " + usage);
     System.exit(1);
   }

   final Path docDir = Paths.get(docsPath);
   System.out.println("Prashant"+docDir);
   if (!Files.isReadable(docDir)) {
     System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
     System.exit(1);
   }
   
   Date start = new Date();
   try {
     System.out.println("Indexing Docs...");

     Directory dir = FSDirectory.open(Paths.get(indexPath));
     Analyzer analyzer = new EnglishAnalyzer();
     IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
     //iwc.setSimilarity(new BM25Similarity());
     iwc.setSimilarity(new ClassicSimilarity());  

     if (create) {
       iwc.setOpenMode(OpenMode.CREATE);
     } else {
       iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
     }

     IndexWriter writer = new IndexWriter(dir, iwc);
     indexDocs(writer, docDir);

      writer.close();

      Date end = new Date();
      System.out.println("Time to index: "+(  end.getTime() - start.getTime() )+ " milliseconds");

    } catch (IOException e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }
static void indexDocs(final IndexWriter writer, Path path) throws IOException {
  if (Files.isDirectory(path)) {
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
         try {
           indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
         } catch (IOException ignore) {
           // don't index files that can't be read.
         }
         return FileVisitResult.CONTINUE;
       }
     });
   } else {
     indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
   }
 }

 static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
   try (InputStream stream = Files.newInputStream(file)) {
     // make a new, empty document
     Document doc = new Document();
     
     Field pathField = new StringField("path", file.toString(), Field.Store.YES);
     doc.add(pathField);
     doc.add(new LongPoint("modified", lastModified));
     
     doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
     doc.add(new StringField("id", file.getFileName().toString(), Field.Store.YES));
      
      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
        // New index, so we just add the document (no old document can be there):
        writer.addDocument(doc);
      } else {
        System.out.println("updating " + file);
        writer.updateDocument(new Term("path", file.toString()), doc);
      }
    }
  }
}
