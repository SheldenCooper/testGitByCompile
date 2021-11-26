//package RYQ.javafx;
//
//
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//class Lexical
//public class LexicalM {
//    public static void main(String[] args) {
//        String path_file = Tool.ChangeAbsolutePath("Ctest1");
//        String code = ReadFile.readFile(path_file);
//
//        Lexical lexical = new Lexical(code);
//        lexical.LexicalAnal();
//        ArrayList<String> list = lexical.getResultList();
////        Iterator iter =lexical.getMap_operater().entrySet().iterator();
////        while (iter.hasNext()) {
////            Map.Entry entry = (Map.Entry) iter.next();
////            Object key = entry.getKey();
////            Object val = entry.getValue();
////            System.out.println(key + ":" + val);
////        }
//        System.out.println("正确词法");
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println(list.get(i));
//        }
//        System.out.println("错误词法");
//        for(String str:lexical.getErrorList()){
//            System.out.println(str);
//        }
////        System.out.println(code.charAt(lexical.getNumi()-1)+":"+code.charAt(lexical.getNumi()+1));
////        System.out.print((code.charAt(lexical.getNumi())));
//////        System.out.println(code);
//    }
//
//}
