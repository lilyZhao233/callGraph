package sorra.tracesonar.main;

import sorra.tracesonar.util.FileHandlers;
import sorra.tracesonar.util.FileOutput;
import sorra.tracesonar.util.FileUtil;
import sorra.tracesonar.util.StringUtil;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by MIC on 2018/12/13.
 */
public class convert {
    public static Map<Integer,String> conv(Map<String,Integer> map,String pathname){
        Map<Integer,String> map1 = new HashMap<>();
        try {
            FileReader reader = new FileReader(pathname);
            BufferedReader br = new BufferedReader(reader);
            String line = "";
            int i = 1;
            while((line = br.readLine())!=null){
                map.put(line,i);
                map1.put(i,line);
                i++;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return map1;

    }
    public static void main(String[] args){
        Map<String,Integer> map = new HashMap<>();
        String pathname = "C:\\code\\TraceSonar\\tomcatClass-1212.txt";
        Map<Integer,String> map1=conv(map,pathname);
        StringBuilder sb = new StringBuilder();

        try {
//            String path = "C:\\Users\\MIC\\PycharmProjects\\python\\struc2vec\\emb\\ConvtomcatClass.emb";
            String path ="C:\\Users\\MIC\\PycharmProjects\\python\\node2vec\\graph\\tomcatClass-1212.edgelist";
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line ;
            line = br.readLine();
            while((line = br.readLine())!=null) {
//                String[] info = StringUtil.splitFirst(line," ");
                String[] info = line.split(" ");
                if(map.get(info[0])==null || map.get(info[1])==null){
                    System.out.println(info[0]);
                }
                sb.append(map.get(info[0])+" "+map.get(info[1])+"\n");
//                sb.append(map1.get(Integer.parseInt(info[0])) + " " + info[1] + "\n");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        FileOutput.writeFile("ContomcatClass-struc.edgelist",sb.toString());

    }
}
