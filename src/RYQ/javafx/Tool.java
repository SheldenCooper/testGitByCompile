package RYQ.javafx;

import java.io.File;

public class Tool {
    public Tool(){

    }
    public static String ChangeAbsolutePath(String filename){
        File directory = new File("");
        String path_file = directory.getAbsolutePath()+"\\src\\RYQ\\javafx\\"+filename;
        path_file= path_file.replaceAll("\\\\", "\\\\\\\\");
        return path_file;
    }
    public static String ChangeAbsolutePathGrammer(String filename){
        File directory = new File("");
        String path_file = directory.getAbsolutePath()+"\\src\\RYQ\\grammar\\"+filename;
        path_file= path_file.replaceAll("\\\\", "\\\\\\\\");
        return path_file;
    }
    public static String ChangeAbsolutePathRoot(String filename){
        File directory = new File("");
        String path_file = directory.getAbsolutePath()+"\\"+filename;
        path_file= path_file.replaceAll("\\\\", "\\\\\\\\");
        return path_file;
    }
    public static int CountNumWrap(String str){
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)=='\n'){
                count++;
            }
        }
        return count;
    }
}
