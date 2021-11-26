package RYQ.javafx;

import java.io.*;
import java.util.ArrayList;

public class ReadFile {
    public static String readFile(String url)//读文件
    {

        //我需要的是返回字符串类型
        StringBuffer buffer = new StringBuffer();
        //前一个为后一个的父类，Bufferedreader对象提供readLine()方法直接读入一行数据
        try(BufferedReader iner=new BufferedReader(new InputStreamReader(new FileInputStream(url),"UTF-8"));)
        {
            String line;
            while((line=iner.readLine())!=null)
            {
                //去掉所有不可见字符（包括换行符）
                line = line.replaceAll("\\p{C}", "");
//                line = line.replaceAll("(\r\n|\r|\n|\n\r)","<br>");//去掉换行符
                ;
                buffer.append(line.trim()+"\n");
            }
        }
        catch (IOException e)//捕获异常
        {
            e.printStackTrace();
        }
        return buffer.toString();
    }
    public static ArrayList<String> readFileList(String url)//读文件
    {

        //返回每一行
        ArrayList<String> list = new ArrayList();
        //前一个为后一个的父类，Bufferedreader对象提供readLine()方法直接读入一行数据
        try(BufferedReader iner=new BufferedReader(new InputStreamReader(new FileInputStream(url),"UTF-8"));)
        {
            String line;
            while((line=iner.readLine())!=null)
            {
                list.add(line+"\n");
            }
        }
        catch (IOException e)//捕获异常
        {
            e.printStackTrace();
        }
        return list;
    }
    public static void  cleanFile(String url) throws IOException{
        // true表示按追加的方式构造流,false则表明删除原始内容， utf-8表示编码时所使用的编码集
        //建立一个false的bufferedWrite 通过向文件内书写空字符串的方式清空文件内容
        BufferedWriter bw_clear = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url,false),"UTF-8"));
        bw_clear.write("");
        bw_clear.close();
    }
    public static void  bufferWrite(String url,ArrayList<String> list_content) throws IOException{
        // true表示按追加的方式构造流,false则表明删除原始内容， utf-8表示编码时所使用的编码集
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(url,true),"UTF-8"));

        for(String str:list_content){
            bw.write(str+"\n");
        }
        bw.close();
    }

}
