package sorra.tracesonar.util;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

public class FileHandlers {
    public static void  getFileList(File dir, List<File> list, FileFilter fileFilter){
        if(dir==null){
            return;
        }
        File[] files=dir.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                getFileList(file,list,fileFilter);
            }else{
                if(fileFilter.accept(file)){
                    list.add(file);
                }
            }
        }

    }
    /**
     * 获取所有资源的路径
     * @param path
     */
    public static List<String> getSources(String path ){
        Vector<String> vector = new Vector<>();
        List<String> paths = new ArrayList<>();
        vector.add(path);
        while(vector.size()>0){
            File[] files = new File(vector.get(0).toString()).listFiles();  //获取该文件夹下所有的文件(夹)名
            vector.remove(0);

            int len=files.length;
            for(int i=0;i<len;i++){
                String tmp=files[i].getAbsolutePath();
                if(files[i].isDirectory())  //如果是目录，则加入队列。
                    vector.add(tmp);
                else {
                    if(files[i].getName().endsWith(".java"))
                    paths.add(tmp);
                }
            }
        }
        return paths;

    }



}
