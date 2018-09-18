package com.imooc.service.word;

import org.apdplat.word.WordSegmenter;
import org.apdplat.word.dictionary.DictionaryFactory;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.util.WordConfTools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/9/5.
 */
public class WordSegmenterImpl implements IWordSegmenter{
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        for(SegmentationAlgorithm segmentationAlgorithm : SegmentationAlgorithm.values()){
            map.put(segmentationAlgorithm.getDes(), seg(text, segmentationAlgorithm));
        }
        return map;
    }

    private static String seg(String text, SegmentationAlgorithm segmentationAlgorithm) {
        StringBuilder result = new StringBuilder();
        for(Word word : WordSegmenter.segWithStopWords(text, segmentationAlgorithm)){
            result.append(word.getText()).append(" ");
        }
        return result.toString();
    }

    public static String getWords(String text){
        WordConfTools.set("dic.path", "classpath:dic.txt,classpath:recommend_dic.txt");
            //WordConfTools.set("stopwords.path", "classpath:stopwords.txt,classpath:recommend__stop_dic.txt");
        DictionaryFactory.reload();//更改词典路径之后，重新加载词典
        StringBuffer result = new StringBuffer();
        //显式指定特定的分词算法
        /***
         * WordSegmenter.seg("APDPlat应用级产品开发平台", SegmentationAlgorithm.BidirectionalMaximumMatching);
         SegmentationAlgorithm的可选类型为：
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
         */
        //List<Word> words = WordSegmenter.seg(text,SegmentationAlgorithm.BidirectionalMaximumMatching);
        //words = WordRefiner.refine(words);
        //筛选过滤字
        List<Word> stopWords = WordSegmenter.segWithStopWords(text,SegmentationAlgorithm.BidirectionalMaximumMatching);
        for(Word word : stopWords){
            result.append(word.getText()).append(",");
        }
        System.out.println(result.toString());
        return result.toString();
    }


}
