package sorra.tracesonar.main;

import sorra.tracesonar.util.FileHandlers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SelfRun {
  public static void main(String[] args) throws IOException {
//    Main.main("-f target/ -q sorra.tracesonar.core.GreatMap sorra.tracesonar.sample.Subclass".split(" "));
    String path = "C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\java";
    List<String> result = FileHandlers.getSources(path);
    StringBuilder stringBuilder = new StringBuilder();
    for(int i=0;i<result.size();i++){
//      System.out.println(result.get(i).replace("C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\java\\","")
//      .replace(".java","").replace("\\","."));
      stringBuilder.append(result.get(i).replace("C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\java\\","")
      .replace(".java","").replace("\\",".")+" ");
    }
    Main.main(("-f Tomcat7.0.jar -q "+ stringBuilder).split(" "));
//    Main.main("-f Tomcat7.0.jar -q org.apache.catalina.connector.InputBuffer".split(" "));
  }
}
