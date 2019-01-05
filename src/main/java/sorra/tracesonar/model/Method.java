package sorra.tracesonar.model;

import java.util.List;
import java.util.Map;

public class Method {
  public final String owner;
  public final String methodName;
  public final String desc;
  public Map<String,String> info;//异常处理信息
  public Boolean hasParser;//是否解析

  public Map<String,String> getInfo() {
    return info;
  }

  public void setInfo(Map<String, String> info) {
    this.info = info;
  }

  public Boolean getHasParser(){
    return hasParser;
  }

  public void setHasParser(boolean hasParser){
    this.hasParser = hasParser;
  }


  public Method(String owner, String methodName, String desc) {
    this.owner = owner;
    this.methodName = methodName;
    this.desc = desc;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Method caller = (Method) o;

    return methodName.equals(caller.methodName) && owner.equals(caller.owner) && desc.equals(caller.desc);

  }

  @Override
  public int hashCode() {
    int result = owner.hashCode();
    result = 31 * result + methodName.hashCode();
    // result = 31 * result + desc.hashCode();
    return result;
  }

  @Override
  public String toString() {
    String name = methodName;
//    if (methodName.contains("<") || methodName.contains(">")) {
//      name = methodName.replace("<", "&lt;").replace(">", "&gt;");
//    }
//    提取desc中的函数参数信息
    String info = desc.substring(desc.indexOf('('),desc.indexOf(')')+1);
    String[] sp = info.split(";");
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for(int i=0; i<sp.length-1; i++){
      int j = 0;
      while(j<sp[i].length()){
        if(Character.isLowerCase(sp[i].charAt(j))) break;
        else j++;
      }
      sb.append(sp[i].substring(j).replace('/','.'));
      if(i!=sp.length-2) sb.append(", ");
    }
    sb.append(")");
    return owner.replace('/','.')+"#"+name+sb;
//    类信息
//    return owner.replace('/','.');
//    包信息
//    if(owner.lastIndexOf('/')==-1) return ".";
//    return  owner.substring(0,owner.lastIndexOf('/')).replace('/','.');
  }


}
