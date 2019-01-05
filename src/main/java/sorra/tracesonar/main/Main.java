package sorra.tracesonar.main;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import sorra.tracesonar.core.FileWalker;
import sorra.tracesonar.core.Traceback;
import sorra.tracesonar.model.Method;
import sorra.tracesonar.util.*;
import sorra.tracesonar.visitor.*;

public class Main {

  public static void main(String[] args) throws IOException {

    ArgsParser parser = new ArgsParser(args);

    List<String> files = parser.getOptionValues(ArgsParser.Option.FILE);
    List<String> excludes = parser.getOptionValues(ArgsParser.Option.EXCLUDE)
        .stream()
        .map(x -> x.replace('.', '/'))
        .collect(Collectors.toList());

    FileWalker.walkAll(files, excludes);

    boolean potential = parser.getOptionValues(ArgsParser.Option.POTENTIAL).contains("true");
    List<String> queries = parser.getOptionValues(ArgsParser.Option.QUERY);

    StringBuilder allsb = new StringBuilder();
    Set<String> set = new TreeSet<>();

    for (String query : queries) {
      String[] parts = StringUtil.splitFirst(query, "#");
      String qClassName = parts[0];
      String methodName = parts.length >= 2 ? parts[1] : "*";
      qClassName = parts[0].replace('.', '/');
      CharSequence output = new Traceback(false, potential).run(new Method(qClassName, methodName, "*"));

      String[] s = output.toString().split("\n");
      for(int i = 0;i<s.length;i++){
        if(!allsb.toString().contains(s[i])){
          String[] strings = s[i].split(" -> ");
//      System.out.println(strings[1]);
          set.add(strings[0]);//set保存端点信息
          set.add(strings[1]);
          allsb.append(s[i]+"\n");
        }
      }
//      allsb.append(output);
    }
    System.out.println(set.size());
    FileOutput.writeFile("tomcat-1215.edgelist",allsb);//保存边的信息

    String str1 = "digraph \"DirectedGraph\" {\n";
    str1 += ("graph [label = \"" + "test" + "\", labelloc=t, concentrate = true];\n");
//    添加端点信息
//    String[] info = allsb.toString().split("\n");
//    for(int i=0;i<info.length;i++){
//      String[] strings = info[i].split(" -> ");
////      System.out.println(strings[1]);
//      set.add(strings[0]+"\n");
//      set.add(strings[1]+"\n");
//    }

    for(String s : set){
      str1 += s+"\n";
    }
//    FileOutput.writeFile("Class.txt",str1); //单独保存端点信息
    str1 +=  allsb+ "}\n";
    FileOutput.writeFile("tomcat-1215.dot",str1);
//    InputStream tmplInput = Main.class.getClassLoader().getResourceAsStream("traceback.html");
//    if (tmplInput == null) throw new NullPointerException("tmpl file is not found");
//    String tmpl = new String(FileUtil.read(tmplInput, 300), "UTF-8");
//    FileOutput.writeFile("traceback.html", String.format(tmpl, allsb));

//    System.out.println("\nTraceback: traceback.html\n");
  }


}
