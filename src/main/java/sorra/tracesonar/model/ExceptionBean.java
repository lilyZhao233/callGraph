package sorra.tracesonar.model;

import java.util.List;

public class ExceptionBean {

    private String type;                     //分的类别

    private String thrown;                    //抛出的异常的全名

    private String catched;                   //catch的语句块

    private String block;                     //整体的方法的代码块

    private String method;                    //调用方法的全名

    private String packages;                  //调用方法在调用文件的包名

    private String methodComment;             //调用方法所在方法的注释

    private String exceptionComment;          //异常本身的注释

    private boolean hasForStat;                //异常周围是不是有for循环

    private boolean isOrigin;                  //是不是java本身的异常

    private List<String> parents;              //该异常的父类

    public String getParentException(){
        String str="";
        if(parents!=null) {
            for (String s : parents) {
                 str=str+s+" ";
            }
        }
        return str;
    }
    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }
    public boolean isOrigin() {
        return isOrigin;
    }

    public void setOrigin(boolean origin) {
        isOrigin = origin;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThrown() {
        return thrown;
    }

    public void setThrown(String thrown) {
        this.thrown = thrown;
    }

    public String getCatched() {
        return catched;
    }

    public void setCatched(String catched) {
        this.catched = catched;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public boolean isHasForStat() {
        return hasForStat;
    }

    public void setHasForStat(boolean hasForStat) {
        this.hasForStat = hasForStat;
    }

    public String getMethodComment() {
        return methodComment;
    }

    public void setMethodComment(String methodComment) {
        this.methodComment = methodComment;
    }

    public String getExceptionComment() {
        return exceptionComment;
    }

    public void setExceptionComment(String exceptionComment) {
        this.exceptionComment = exceptionComment;
    }
}
