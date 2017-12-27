package com.itheima.lucene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class FirstLucence {

	@Test
	public void creatIndex() throws Exception{
//		1、创建一个Directory对象，指定索引库保存的目录。可以是内存也可以是磁盘。一般就是使用磁盘路径。
//		RAMDirectory ramDirectory = new RAMDirectory();//将保存索引的目录创建在内存中
		Directory directory = FSDirectory.open(new File("G:/day101_lucene&solr/day01/index"));
//		2、创建分析器对象Analyzer对象，StandardAnalyzer对象。
		StandardAnalyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new IKAnalyzer();
//		3、IndexWriterConfig对象，两个构造参数，一个是Lucene的版本。分析器对象。
		IndexWriterConfig config= new IndexWriterConfig(Version.LATEST, analyzer);
//		4、创建一个IndexWriter对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
//		5、读取磁盘上的文件
		File dir = new File("G:/day101_lucene&solr/00.参考资料/searchsource");
		File[] files = dir.listFiles();
		for (File file : files) {
//			取文件名称
			String fileName = file.getName();
//			文件的路径
			String filePath = file.getPath();
//			文件的内容
			String fileContent = FileUtils.readFileToString(file);
//			文件的大小
			long fileSize = FileUtils.sizeOf(file);
			
//		6、为每个文件对应的创建一个document对象。
			Document document = new Document();
//		7、对应文件的属性创建Fieldd对象，添加到Document对象中。
			//参数1：域的名称 参数2：域的值 参数3：是否存储，如果存储可以取出，如果不存储不能取出。
			//不影响分词创建索引。
			Field fieldName = new TextField("name", fileName, Store.YES);
			Field fieldPath = new TextField("path", filePath , Store.YES);
			Field fieldContent= new TextField("content", fileContent, Store.YES);
			Field fieldSize= new TextField("size", fileName+"", Store.YES);
			//添加到document中
			document.add(fieldName);
			document.add(fieldPath);
			document.add(fieldContent);
			document.add(fieldSize);
			
//		8、把Document对象写入索引库。
			indexWriter.addDocument(document);
			
		}
//		9、使用IndexWriter的commit方法。
		indexWriter.commit();
//		10、关闭IndexWriter。
		indexWriter.close();
	}
	//查询索引
	@Test
	public void searchIndex() throws Exception{
		// 1）创建一个Directory对象，指定索引库的位置
				Directory directory = FSDirectory.open(new File("G:/day101_lucene&solr/day01/index"));
				// 2）创建一个IndexReader对象
				IndexReader indexReader = DirectoryReader.open(directory);
				// 3）基于IndexReader对象创建一个IndexSearcher对象。
				IndexSearcher indexSearcher = new IndexSearcher(indexReader);
				// 4）创建一个Query对象，TermQuery，根据关键词查询，需要指定要搜索的域及要搜索的关键词。
				Query query = new TermQuery(new Term("name", "全"));
				// 5）执行查询。
				//参数1：查询对象 参数2：返回结果的最大值
				// 6）取查询结果
				TopDocs topDocs = indexSearcher.search(query, 10);
				// 7）取查询结果的总记录数
				System.out.println("查询结果的总记录数：" + topDocs.totalHits);
				// 8）取结果列表
				ScoreDoc[] scoreDocs = topDocs.scoreDocs;
				for (ScoreDoc scoreDoc : scoreDocs) {
					//取文档id
					int docId = scoreDoc.doc;
					//根据id取document对象
					Document document = indexSearcher.doc(docId);
					// 9）打印结果
					System.out.println(document.get("name"));
//					System.out.println(document.get("content"));
					System.out.println(document.get("path"));
					System.out.println(document.get("size"));
				}
				// 10）关闭IndexReader对象
				indexReader.close();
	}
	
//	分析器
	@Test
	public void testTokenStream() throws Exception {
		// 1）创建一个Analyzer对象
		//Analyzer analyzer = new StandardAnalyzer();
		//Analyzer analyzer = new CJKAnalyzer();
		//Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		// 2）使用Analyzer对象的tokenStream方法，需要给一个要分析的文本内容。返回TokenStream对象
		TokenStream tokenStream = analyzer.tokenStream(null, "黑马java,课程设置无缝对接企业用人需求,高薪就业,轻松胜任工作!来黑马java,做被企业疯抢的Java开发人才,拿万元月薪!..");
		// 3）调用TokenStream对象的reset方法。
		tokenStream.reset();
		// 4）给TokenStream对象设置一个引用，代码指针对象。
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		// 5）使用While循环进行遍历。使用方法判断循环是否结束。
		while(tokenStream.incrementToken()) {
			// 6）在循环中可以取引用的内容。就是指针指向 的关键词
			System.out.println(charTermAttribute);
		}
		// 7）关闭TokenStream
		tokenStream.close();
	}
	
}
