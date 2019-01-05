package sorra.tracesonar.core;

import java.io.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import sorra.tracesonar.model.Method;
import sorra.tracesonar.util.FileHandlers;
import sorra.tracesonar.util.Filterbyjava;
import sorra.tracesonar.visitor.MethodDeclarationVisitor;


//TODO the tree building is not functional style, thus very hard to understand
public class Traceback {
  private boolean isHtml;
  private boolean includePotentialCalls;

  private Printer printer;
  private StringBuilder output = new StringBuilder();

  public Traceback(boolean isHtml, boolean includePotentialCalls) {
    this.isHtml = isHtml;
    this.includePotentialCalls = includePotentialCalls;
    if (isHtml) {
      printer = node -> {
        if (node.depth == 0) {
          output.append(String.format(
                  "<div class=\"queried\">%s</div>\n", node.self));
        } else {
          String cssClass = "caller";
          if (node.callers.isEmpty()) cssClass += " endpoint";
          if (node.isCallingSuper) cssClass += " potential";

          output.append(String.format(
                  "<div class=\"%s\" style=\"margin-left:%dem\">%s</div>\n", cssClass, node.depth * 5, node.self));
        }

      };
    } else {
      printer = node -> {
        if(node.depth!=0){
//          String[] fileName = node.self.owner.split("/");
//          parse(fileName[fileName.length-1],node.self.methodName,node.self);
          String str ="";
          str += ("\"" + node.parent.self.toString() +"\" -> \"" + node.self.toString() +"\"\n");
          System.out.println(str);
          if(!output.toString().contains(str)){
            output.append(str);
          }
        }

//        char[] indents = new char[node.depth];
//        Arrays.fill(indents, '\t');

      };
    }
  }
  private static void parse(String fileName,String methodName,Method method) {
    String[] classpath = {"C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\lib"};
    String [] sources={"C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\java"};
    File dir=new File("C:\\Users\\MIC\\Documents\\experiment project\\tomcat\\tomcatsrc\\java");
    String str = null;
    List<File> fileList=new ArrayList<File>();
    FileFilter fileFilter=new Filterbyjava(fileName);
    FileHandlers.getFileList(dir,fileList,fileFilter);
    if(fileList.size()!=0){
      File file = fileList.get(0);
      try {
        str = FileUtils.readFileToString(file);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setResolveBindings(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        parser.setBindingsRecovery(true);
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        parser.setProject(null);

        parser.setCompilerOptions(options);

        parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
        parser.setSource(str.toCharArray());
        parser.setUnitName(methodName);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        MethodDeclarationVisitor v = new MethodDeclarationVisitor(method);
        cu.accept(v);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public CharSequence run(Method self) {
    if (isHtml) output.append("<h3>").append(self).append("</h3>\n");
//    else {
////      output.append(self).append("\n");
//    }
    search(self);
    return output;
  }

  private void search(Method self) {
    Stream<TreeNode> nodeStream;
    if (self.owner.equals("*")) {
      nodeStream = ClassMap.INSTANCE.classOutlines.values().stream()
          .flatMap(co -> co.methods.stream())
          .map(this::searchTree);
    } else if (self.methodName.equals("*")) {
      nodeStream = getClassOutline(self).methods.stream()
          .filter(x -> x.owner.equals(self.owner))
          .map(this::searchTree);
    } else if (self.desc.equals("*")) {
      nodeStream = getClassOutline(self).methods.stream()
          .filter(x -> x.methodName.equals(self.methodName) && x.owner.equals(self.owner))
          .map(this::searchTree);
    } else {
      throw new RuntimeException();
    }

    //Separate two stages to help debug
    nodeStream
        .collect(Collectors.toList())//collect the output and convert to list
        .forEach(this::printTree);// output the printTree
//.forEach(System.out::println);
  }

  private static ClassMap.ClassOutline getClassOutline(Method self) {
    ClassMap.ClassOutline classOutline = ClassMap.INSTANCE.classOutlines.get(self.owner);
    if (classOutline == null) {
      throw new RuntimeException("Cannot find class: " + self.owner);
    }
    return  classOutline;
  }

  private TreeNode searchTree(Method method) {
    return searchTree(method, null, false);
  }

  private TreeNode searchTree(Method self, TreeNode parent, boolean asSuper) {
    TreeNode cur = new TreeNode(self, asSuper, parent);
    searchCallers(cur, false);

    //TODO if cur.depth < k (configurable)
    if (includePotentialCalls) {
      for (Method superMethod : ClassMap.INSTANCE.findSuperMethods(self)) {
        TreeNode superCur = new TreeNode(superMethod, asSuper, parent);
        searchCallers(superCur, true);
      }
    }

    return cur;
  }

  private void searchCallers(TreeNode cur, boolean asSuper) {
    if (cur.parent != null) {
      if (cur.parent.findCycle(cur.self)) {
        cur.parent.addCycleEnd(cur.self, asSuper);
        return;
      } else {
        cur.parent.callers.add(cur);
      }
    }

    Set<Method> callers = GreatMap.INSTANCE.getCallerCollector(cur.self).getCallers();
    for (Method caller : callers) {
      if (cur.findCycle(caller)) {
        cur.addCycleEnd(caller, asSuper);
      } else {
        searchTree(caller, cur, asSuper);
      }
    }
  }

  private void printTree(TreeNode node) {
//    if(!node.self.getHasParser()){
//      String[] fileName = node.self.owner.split("/");
//      parse(fileName[fileName.length-1],node.self.methodName,node.self);
//    }

//    if(node.self.getInfo()!=null&&node.self.getInfo().size()>0){
//      System.out.println(node);
      printer.print(node);
      node.callers.forEach(this::printTree);
//    }

  }

  private static class TreeNode {
    Method self;
    boolean isCallingSuper; // self is calling the super method of parent
    TreeNode parent;
    int depth;
    List<TreeNode> callers = new ArrayList<>();

    TreeNode(Method self, boolean isCallingSuper, TreeNode parent) {
      this.self = self;
      this.isCallingSuper = isCallingSuper;
      this.parent = parent;

      if (parent == null) {
        depth = 0;
      } else {
        depth = parent.depth + 1;
      }
    }

    boolean findCycle(Method neo) {
      TreeNode cur = this;
      do {
        if (cur.self.equals(neo) || cur.callers.stream().anyMatch(x -> x.self.equals(neo))) {
          return true;
        }
        cur = cur.parent;
      } while (cur != null);

      return false;
    }

    void addCycleEnd(Method caller, boolean asSuper) {
      TreeNode cycleEnd = new TreeNode(caller, asSuper, this);
      callers.add(cycleEnd);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();

      TreeNode cur = this;
      do {
        sb.append(cur.depth);
        for (int i = 0; i < cur.depth; i++) {
          sb.append("  ");
        }
        sb.append(cur.self).append('\n');
        cur = cur.parent;
      } while (cur != null);

      return sb.toString();
    }
  }

  private interface Printer {
    void print(TreeNode node);
  }
}
