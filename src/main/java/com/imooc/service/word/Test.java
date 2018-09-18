package com.imooc.service.word;

import org.apache.tomcat.jni.FileInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by caoxingyun on 2018/9/5.
 */
public class Test {


    public static Map<String, Set<String>> contrast(String text){
        Map<String, Set<String>> map = new LinkedHashMap<>();
        map.put("word分词器", new WordSegmenterImpl().seg(text));
        return map;
    }
    public static Map<String, Map<String, String>> contrastMore(String text){
        Map<String, Map<String, String>> map = new LinkedHashMap<>();
        map.put("word分词器", new WordSegmenterImpl().segMore(text));
        return map;
    }
    public static void show(Map<String, Set<String>> map){
        map.keySet().forEach(k -> {
            System.out.println(k + " 的分词结果：");
            AtomicInteger i = new AtomicInteger();
            map.get(k).forEach(v -> {
                System.out.println("\t" + i.incrementAndGet() + " 、" + v);
            });
        });
    }
    public static void showMore(Map<String, Map<String, String>> map){
        map.keySet().forEach(k->{
            System.out.println(k + " 的分词结果：");
            AtomicInteger i = new AtomicInteger();
            map.get(k).keySet().forEach(a -> {
                System.out.println("\t" + i.incrementAndGet()+ " 、【"   + a + "】\t" + map.get(k).get(a));
            });
        });
    }
    public static void main(String[] args) throws IOException {
        //show(contrast("研究生命的起源"));
        //showMore(contrastMore("研究生命的起源"));
//        List<String> strList = new ArrayList<String>();
//        strList.add("aa");
//        strList.add("bb");
//        strList.add("曹兴运");
//        File file = new File("a.txt");
//        if(file.exists()){
//            file.delete();
//        }
//        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//        for(String l:strList){
//            writer.write(l+"\n");
//        }
//        writer.close();
//        System.out.println("1");
        new WordSegmenterImpl().getWords("中国曹兴运大牙是一个程序猿CCTV5+dy");
    }
}
