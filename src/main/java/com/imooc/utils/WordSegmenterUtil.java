package com.imooc.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//import org.apache.poi.ss.formula.functions.T;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.WordConfTools;

//import com.wondertek.mam.model.BaseAttrEnum;

/***
 * Word 分词器
 * @author caoxingyun
 *
 */
public class WordSegmenterUtil {
	
	/***
	 * 根据默认字典表匹配 去除停词表里的词
	 * @return
	 */
	public static List<Word> getWords(String text){
		List<Word> words = WordSegmenter.seg(text);
		return words;
	}
	
	/***
	 * 根据默认字典表和指定算法匹配 去除停词表里的词
	 *  SegmentationAlgorithm的可选类型为：
         正向最大匹配算法：MaximumMatching
         逆向最大匹配算法：ReverseMaximumMatching
         正向最小匹配算法：MinimumMatching
         逆向最小匹配算法：ReverseMinimumMatching
         双向最大匹配算法：BidirectionalMaximumMatching
         双向最小匹配算法：BidirectionalMinimumMatching
         双向最大最小匹配算法：BidirectionalMaximumMinimumMatching
         全切分算法：FullSegmentation
         最少分词算法：MinimalWordCount
         最大Ngram分值算法：MaxNgramScore
	 * @return
	 */
	public static List<Word> getWordsByAlgorithm(String text,SegmentationAlgorithm alg){
		List<Word> words = WordSegmenter.seg(text,alg);
		return words;
	}
	
	
	/***
	 * 根据默认字典表,停词表匹配
	 * @return
	 */
	public static List<Word> getStopWords(String text){
		List<Word> words = WordSegmenter.segWithStopWords(text);
		return words;
	}
	
	/***
	 * 根据默认字典表,停词表,指定算法匹配
	 * @return
	 */
	public static List<Word> getStopWordsByAlgorithm(String text,SegmentationAlgorithm alg){
		List<Word> words = WordSegmenter.segWithStopWords(text,alg);
		return words;
	}
	
	
	/***
	 * 根据默认字典表，自定义字典表,停词表,指定算法匹配
	 * dicPath "classpath:dic.txt,classpath:word/recommend_dic.txt"
	 * @return
	 */
	public static List<Word> getWordsByDicAdnAlgorithm(String text,String dicPath,SegmentationAlgorithm alg){
		  WordConfTools.set("dic.path", dicPath);
	      DictionaryFactory.reload();//更改词典路径之后，重新加载词典
	      List<Word> words = WordSegmenter.segWithStopWords(text,alg);
	      return words;
	}
	
	/***
	 * 重写推荐标签字典表
	 * @param dicList
	 * @param fileName
	 * @throws IOException
	 */
//	public static void reloadRecommendDic(List<BaseAttrEnum> dicList, String fileName) throws IOException {
//		File file = new File(fileName);
//		if (file.exists()) {
//			file.delete();
//		}
//		BufferedWriter writer = null;
//		try {
//			writer = new BufferedWriter(new FileWriter(file));
//			for (BaseAttrEnum list : dicList) {
//				writer.write(list.getAttrValue() + "\n");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			if(null != writer){
//				writer.close();
//			}
//		}
//	}
	
	/***
	 * 重写描述地区字典表
	 * @param dicList
	 * @param fileName
	 * @throws IOException
	 */
	public static  void reloadOccurredDic(List<Map<String,Object>> dicList, String fileName) throws IOException {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file));
			for (Map<String,Object> map : dicList) {
				writer.write((String)map.get("OCCURRED") + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(null != writer){
				writer.close();
			}
		}
	}
}
