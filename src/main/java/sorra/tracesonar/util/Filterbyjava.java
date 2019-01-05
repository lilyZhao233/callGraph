package sorra.tracesonar.util;


import java.io.File;
import java.io.FileFilter;

public class Filterbyjava implements FileFilter {

    public String suffix;

    public Filterbyjava(String suffix) {
        super();
        this.suffix = suffix;
    }

    @Override
    public boolean accept(File pathname) {
        // TODO Auto-generated method stub
        if (pathname.isFile() && pathname.getName().endsWith(".java") && pathname.getName().toLowerCase().contains(suffix.toLowerCase()))
            return true;
        return false;
    }


}