# Lucene_Search_Engine

The project is divided into three parts: 
<ul>
<li>Modification of query files, separation of cran.all.1400 file into 1400 different documents 
<li>Index documents generated in above step
<li>Search these documents based on given queries in cran.qry file
</ul>

# Implementation

The given query file has lot of irrelevant information and was not aligned properly. Using basic java string operations, I was able to make a new file (/home/professor/QuerySeparate/CranQuestions) that has query number followed by query in each line. Similarly, the cran.all.1400 file had snippets of texts which were present in one document. To index these snippets, I separated them as individual documents and placed them at /home/professor/SeparatedDocs directory.<br />
Since I was new to lucene, initially I did not have much idea about it. So, I went through the demo code that is present in lucene documentation [1]. IndexDoc.java class creates lucene index for cran documents that were generated in first step. The main function instantiates IndexWriter and takes in object of analyzer as argument. It is the main class that is responsible for creating indices. After this, indexDocs () recursive function is called from main which crawls the directory and utilises FileDocument to create Document objects. This object represents content in the file and its creation time and location. Also, document id is stored in a string which is sent to Searcher which is used in result file.<br />
After indexing the documents, the program search for documents based on queries one by one and the result is appended in an output file(/home/professor/test.txt). It uses IndexSearcher, Analyzer and Queryparser. Queryparser takes in “content” of file and analyzer instance as argument to interpret query text in the same way as the documents are interpreted. Search is executed in streaming way, ie., it simply prints document Id and score of each matching document. Before using trec_eval programme to compare the result file and qrel file, I calculated precision and recall[2].<br />
But the scores corresponding to these metrics were strange. Precision was 11.8% and recall was 82%. Since, precision was low, I added query booster with a factor of 1.5 and I got the below mentioned results.

# Choice of analyzers and scoring

On comparing the two analyzers, StandardAnalyzer and EnglishAnalyzer in the above table I can say that the latter performed better for the given documents. StandardAnalyzer, internally uses Java Tokenizer, converting all strings to lowercase and filtering out stop words and characters from the index. Stop words and characters include common language words such as articles (a, an, the, etc.) and other strings that may have less value for searching. On the other hand, EnglishAnalyzer has English stemming enhancements which works well for plain English text.<br />
I have used both BM25 and vector space scoring models one by one and the results are accumulated in the above table. BM25 stands for Best Match 25 and is used for relevance computation. It gives a relevance score, according to probabilistic information retrieval, ought to reflect the probability a user will consider the result relevant. Vector space model, is used for representing text documents as vectors of identifiers, such as, for example, index terms [3].<br />
NOTE: All the results present in the table above, have been evaluated with a cutoff of 3 for relevance.

# Conclusion

After performing the given task, I conclude that for the documents at hand, English Analyzer along with Vector space mode as the scoring model performed the best and gave the highest mean average precision score.

# References
<ul>
<li>lucene.apache.org, (2018) Lucene. Available at: https://lucene.apache.org/core/2_9_4/demo.html(Accessed: 20 February 2018)
<li>en.wikipedia.org, (2018) Wikipedia. Available at: https://en.wikipedia.org/wiki/Precision_and_recall (Accessed: 21 February 2018)
<li>en.wikipedia.org, (2018) Wikipedia. Available at: https://en.wikipedia.org/wiki/Vector_space_model
 (Accessed: 28 February 2018)
