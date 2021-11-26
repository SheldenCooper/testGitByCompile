package RYQ.javafx;

import java.util.ArrayList;
import java.util.HashMap;

public class Lexical {
    private final String int_type = "400";
    private final String char_type = "500";
    private final String str_type = "600";
    private final String tag_type = "700";
    private final String float_type = "800";
    private String code;
    private HashMap<String,Integer> map_operater;
    private HashMap<String,Integer> map_keywords;
    private final char erroChar = -38+'0';
    private HashMap<String,Integer> map_limit;
    private int num_row,num_column;//每个字符在代码中的行列值

    public boolean isException() {
        return exception;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    private int numi;//代码字符串用作访问下标
    private boolean exception;
    private ArrayList<String> resultList;
    private ArrayList<String> errorList;
    public ArrayList<String> getResultList() {
        return resultList;
    }
    public HashMap<String, Integer> getMap_operater() {
        return map_operater;
    }
    public int getNumi() {
        return numi;
    }

    public Lexical(String code){
        this.num_row = 1;
        this.num_column =1;
        this.numi = 0;
        this.code = code;
        this.exception = false;
        this.resultList =new ArrayList();
        this.errorList = new ArrayList();
        this.map_limit =new HashMap();
        this.map_operater =new HashMap();
        this.map_keywords =new HashMap();
       // 将运算符文件写入对应哈希表
//        String path_file = Tool.ChangeAbsolutePath("operater");
//        ArrayList<String> list_operater = ReadFile.readFileList(path_file);
//        String[] strs;
//        for (int i = 0; i < list_operater.size(); i++) {
//            strs = list_operater.get(i).split("\\s+");
//            map_operater.put(strs[0],Integer.valueOf(strs[1]));
//        }
        map_operater.put("(",201);
        map_operater.put(")",202);
        map_operater.put("[",203);
        map_operater.put("]",204);
        map_operater.put("!",205);
        map_operater.put("*",206);
        map_operater.put("/",207);
        map_operater.put("%",208);
        map_operater.put("+",209);
        map_operater.put("-",210);
        map_operater.put("<",211);
        map_operater.put("<=",212);
        map_operater.put(">",213);
        map_operater.put(">=",214);
        map_operater.put("==",215);
        map_operater.put("!=",216);
        map_operater.put("&&",217);
        map_operater.put("||",218);
        map_operater.put("=",219);
        map_operater.put(".",220);
        map_operater.put("#",221);
        map_operater.put("&",222);
        map_operater.put("|",223);

        //将关键字写入对应哈希表
//        path_file = Tool.ChangeAbsolutePath("keyWord");
//        list_operater = ReadFile.readFileList(path_file);
//
//        for (int i = 0; i < list_operater.size(); i++) {
//            strs = list_operater.get(i).split("\\s+");
//            map_keywords.put(strs[0],Integer.valueOf(strs[1]));
//        }
        map_keywords.put("char",101);
        map_keywords.put("int",102);
        map_keywords.put("float",103);
        map_keywords.put("break",104);
        map_keywords.put("const",105);
        map_keywords.put("return",106);
        map_keywords.put("void",107);
        map_keywords.put("continue",108);
        map_keywords.put("do",109);
        map_keywords.put("while",110);
        map_keywords.put("if",111);
        map_keywords.put("else",112);
        map_keywords.put("for",113);

        //将界符写入对应哈希表
//        path_file = Tool.ChangeAbsolutePath("limit");
//        list_operater = ReadFile.readFileList(path_file);
//
//        for (int i = 0; i < list_operater.size(); i++) {
//            strs = list_operater.get(i).split("\\s+");
//            map_limit.put(strs[0],Integer.valueOf(strs[1]));
//        }
        map_limit.put("{",301);
        map_limit.put("}",301);
        map_limit.put(";",301);
        map_limit.put(",",301);
        map_limit.put(":",301);





    }
    public void LexicalAnal(){
        while(numi< code.length()){
            char c = code.charAt(numi);
            if(Character.isLetter(c) || c=='_') {
                analTag(numi);

            }
            else if(Character.isDigit(c)){//数字开头
                analDigit(numi);

            }
            else if(map_operater.containsKey(String.valueOf(c))){//运算符
                analOperater(numi);

            }
            else if(c=='\''){//单引号
                anlaSingle(numi);
                numi++;
                num_column++;

            }
            else if(c=='\"'){//双引号
                anlaDouble(numi);
                numi++;
                num_column++;

            }
            else if(map_limit.containsKey(String.valueOf(c))){
                resultList.add(code.charAt(numi)+"\t\t"+map_limit.get(String.valueOf(c))+"["+num_row+":"+num_column);
                numi++;
                num_column++;

            }
            else if(c == '\n'){
                num_row++;
                num_column = 1;
                numi++;
            }
            else if(Character.isWhitespace(c)){
                //anlaEmpty(numi);
                numi++;
                //num_column++;
                //c = code.charAt(numi);
            }
            else{
                System.out.println("库中未存在字符"+String.valueOf(c));
                exception = true;
            }
            if(exception){
                //跳出出现错误的一长段字符或数字，接着向下进行词法分析
                int num_begin = numi;
                numi++;
                while(numi<code.length()&&(Character.isLetter(code.charAt(numi))||Character.isDigit(code.charAt(numi)))){
                    numi++;

                }
                //将错误信息记录下来
                errorList.add("there are little wrong stuff near by :["+code.substring(num_begin,numi)+"];at row:"+num_row+",column:"+num_column);
                num_column += (numi-num_begin);
                exception = false;
            }
        }

    }
    public boolean isYing (char c){
        if(c=='\''||c=='\"'){
            return true;
        }
        return false;
    }
    //跳过所有空格
//    public void anlaEmpty(int index_begin){
//        char c =code.charAt(index_begin);
//        while(c==' '){
//            numi++;
//            //num_column++;
//            c = code.charAt(numi);
//
//        }
//    }
    public void anlaDouble(int index_begin){
        numi++;
        char c= code.charAt(numi);
        while( numi< code.length()-1&&c!='\"'){
            numi++;
            c = code.charAt(numi);

        }
        if(numi==code.length()){
            exception =true;
            return;
        }

        else {
            resultList.add(code.substring(index_begin,numi+1)+"\t\t"+str_type+"["+num_row+":"+num_column);
        }

    }
    public void anlaSingle(int index_begin){
        char c= code.charAt(index_begin+1);
        while( numi< code.length()-1&&c!='\''){
            numi++;
            c = code.charAt(numi);

        }
        //出循环时numi未指向下一个
        if(numi==code.length()||numi-index_begin!=2){
            exception =true;
            return;
        }
        else {
            resultList.add(code.substring(index_begin,numi+1)+"\t\t"+char_type+"["+num_row+":"+num_column);
        }

    }
    //运算符和注释
    public void analOperater(int index_begin){
        char c = code.charAt(index_begin);
        //这里特殊说明一下为什么不怕numi下标超限，这是因为这些函数的进入条件均为非'\n',而每一行均有一个'\n'，所以之多下标指向'\n'即检测到之后推出子函数
        char c2 = code.charAt(index_begin+1);
        //若是双符号的运算符
        //（，和）不属于双符号运算符范围
        if((c=='('||c2==')')||(c2=='('||c==')')){//如果双符号中存在括号，按单运算符计算
            numi++;
            num_column++;
            c = code.charAt(numi);
        }
        else if(map_operater.containsKey(String.valueOf(c2))){
            String str= code.substring(index_begin,index_begin+2);
            System.out.println(str);
            if(map_operater.containsKey(str)){
                numi += 2;
                num_column += 2;
                c = code.charAt(numi);
            }
            else if(str.equals("//")){
                //程序跳过这一行
                numi+=2;
                while(c!='\n'){

                    c = code.charAt(numi);
                    numi++;

                }
                num_row++;
                return;
            }
            else if(str.equals("/*")){//多行注释
                numi += 2 ;
                while(numi+1<code.length()&&(!(code.substring(numi,numi+2).equals("*/")))){
                    //找到出循环,程序跳过很多

                    c = code.charAt(numi);
                    if(c=='\n'){
                        num_column=0;
                        num_row++;
                    }
                    numi++;
                    num_column++;
                }
                //此时numi指向*，应加二
                numi+=2;
                num_column +=2;
                if(numi==code.length()){
                    exception =true;
                }
                return;
            }
            else {
                numi+=2;
                num_column+=2;
                resultList.add(code.charAt(index_begin)+"\t\t"+map_operater.get(String.valueOf(code.charAt(index_begin)))+"["+num_row+":"+num_column);
                resultList.add(code.charAt(index_begin+1)+"\t\t"+map_operater.get(String.valueOf(code.charAt(index_begin+1)))+"["+num_row+":"+num_column);
                return;

            }

        }
        else{
            numi++;
            num_column++;
            c = code.charAt(numi);
        }
        if(!(isYing(c)||map_limit.containsKey(String.valueOf(c))||c==' '||map_operater.containsKey(String.valueOf(c))||Character.isLetter(c)||Character.isDigit(c)||c=='\n')) {
            exception = true;
        }
        else{
            resultList.add(code.substring(index_begin,numi)+"\t\t"+map_operater.get(code.substring(index_begin,numi))+"["+num_row+":"+num_column);
        }


    }
    //标识符
    public void analTag(int index_begin){
        char c = code.charAt(index_begin);
        while(Character.isLetter(c)||Character.isDigit(c)||c=='_'){
            num_column++;
            numi++;
            c = code.charAt(numi);

        }
        //出循环的条件是此时code.charAt(numi)不能再组成一个标识符,此时若满足条件则出错
        //!!bug点，该处条件判断一定为真
        if(!(c==' '||map_limit.containsKey(String.valueOf(c))||map_operater.containsKey(String.valueOf(c))||c=='\n')){
            exception =true;

        }
        else{
            String str = code.substring(index_begin,numi);
            if(map_keywords.containsKey(str)){
                resultList.add(str+"\t\t"+map_keywords.get(str)+"["+num_row+":"+num_column);
            }
            else{
                resultList.add(code.substring(index_begin,numi)+"\t\t"+tag_type+"["+num_row+":"+num_column);
            }
        }
    }
    //处理整数部分传过来的以小数点开始
    public void analDecimals(int index_begin){

        //跳过小数点后数字部分
        numi++;
        num_column++;
        while(Character.isDigit(code.charAt(numi))){
            numi++;
            num_column++;
        }
        //此时做判断，若这个字符为e/E即进行指数判断，若不是，则判断是否出错然后返回
        char c = code.charAt(numi);
        if(c=='E'||c=='e'){
            numi++;
            num_column++;
            //分情况，若e后阶+/-,则可以接0开头
            //若以数字开头则不能为0开头
            c=code.charAt(numi);
            if(c == '+'||c=='-'){
                while(Character.isDigit(code.charAt(numi))){
                    numi++;
                    c = code.charAt(numi);
                }
            }
            else{
                if(c=='0'){//错误情况直接返回
                    exception = true;
                    return;
                }
                if(Character.isDigit(c)){
                    while(Character.isDigit(code.charAt(numi))){
                        numi++;
                        c = code.charAt(numi);
                    }
                }
            }
        }
        if(!(map_limit.containsKey(String.valueOf(c))||c==' '||c=='\n'||map_operater.containsKey(String.valueOf(c)))) {
            exception = true;
        }
        else{
            resultList.add(code.substring(index_begin,numi)+"\t\t"+float_type+"["+num_row+":"+num_column);
        }

    }
    //整数
    public void analDigit(int index_begin){
        char c = code.charAt(index_begin);
        if(c!='0'){//十进制

            while(Character.isDigit(c)){

                num_column++;
                numi++;
                c = code.charAt(numi);
            }
            if(c=='.'){
                analDecimals(index_begin);
                return;//不与整数相同

            }
        }
        else{
            numi++;
            num_column++;
            c = code.charAt(numi);
            if(c>='0'&&c<='7'){
                while(c>='0'&&c<='7'){
                    num_column++;
                    numi++;
                    c = code.charAt(numi);
                }
            }
            else if(c=='x' ||c=='X'){
                numi++;
                num_column++;
                c = code.charAt(numi);
                while (Character.isDigit(c)|| (c>='a'&&c<='f')||(c>='A' && c<='F')){
                    num_column++;
                    numi++;
                    c = code.charAt(numi);
                }
            }
            else if(c=='.'){
                analDecimals(index_begin);
                return;//不与整数相同

            }
        }

        if(!(map_limit.containsKey(String.valueOf(c))||c==' '||c=='\n'||map_operater.containsKey(String.valueOf(c)))) {
            exception = true;
        }
        else{
            resultList.add(code.substring(index_begin,numi)+"\t\t"+int_type+"["+num_row+":"+num_column);
        }
    }


}
