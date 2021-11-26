package RYQ.grammar;/*
 * @author ryq
 * @Description ://本类用于使用不同的四元式产生对应的目标代码
 * @ClassName : TargetCode
 * @Date 2021/5/23 @10:46
 */

import RYQ.javafx.Lexical;
import RYQ.javafx.ReadFile;
import RYQ.javafx.Tool;

import java.awt.image.ImageProducer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TargetCode {
    private ArrayList<String> list_targetCode;
    private HashMap<String ,String>  map_targetCode ;
    //以下几个变量值从建立中间代码的类中获取
    ArrayList<NodeFour> list_nodeFour;
    HashMap<String,TNode> map_constant;
    //变量表由于存在多个同名不同区域变量，因此是一个两层的哈希表，第一层以变量名为键值，第二层以块名为建
    HashMap<String,HashMap<String,TNode>> map_variable ;//变量表
    //目标代码要使用，利用函数名变量名确定所处栈空间
    HashMap<String ,HashMap<String,String>> map_funcVar = new HashMap<>();
    HashMap<String, String> map_temp;
    //函数表
    HashMap<String, FNode> map_function;//变量表
    private BlockBody blockBody;
    private boolean isInMian =true;
    private String funcName = "空";
    public TargetCode(BlockBody blockBody) {
        this.blockBody = blockBody;
        this.list_targetCode = new ArrayList<>();
        this.map_targetCode = new HashMap<>();
        //在构造函数内填充map
        //避免使用替换函数替换A时，将AX中的A同样替换，特，将对应汇编代码中的所有的A，B，C,D均转换为A1，B1，C1，D1。,同时建t转换为t1
        map_targetCode.put("+","\tmov ax,a1\n\tadd ax,b1\n\tmov t1,ax\n");
        map_targetCode.put("-","\tmov ax,a1\n\tsub ax,b1\n\tmov t1,ax\n");
        map_targetCode.put("*","\tmov ax,a1\n\tmov bx,b1\n\tmul bx\n\tmov t1,ax\n");
        map_targetCode.put("/","\tmov ax,a1\n\tmov dx,0\n\tmov bx,b1\n\tdiv bx\n\tmov t1,ax\n");
        map_targetCode.put("%","\tmov ax,a1\n\tmov dx,0\n\tmov bx,b1\n\tdiv bx\n\tmov t1,dx\n");
        map_targetCode.put("&&","\tmov dx,0\n\tmov ax,a1\n\tcmp ax,0\n\tje _and\n\tmov ax,b1\n\tcmp ax,0\n\tje _and\n\tmov dx,1\n_and:mov t1,dx\n");
        map_targetCode.put("||","\tmov dx,1\n\tmov ax,a1\n\tcmp ax,0\n\tje _or\n\tmov ax,b1\n\tcmp ax,0\n\tje _or\n\tmov dx,0\n_or:mov t1,dx\n");
        map_targetCode.put("j","\tjmp far ptr p1\n");
        map_targetCode.put("j>","\tmov ax,a1\n\tcmp ax,b1\n\tjg p1\n");
        map_targetCode.put("j>=","\tmov ax,a1\n\tcmp ax,b1\n\tjge p1\n");
        map_targetCode.put("j<=","\tmov ax,a1\n\tcmp ax,b1\n\tjle p1\n");
        map_targetCode.put("j<","\tmov ax,a1\n\tcmp ax,b1\n\tjl p1\n");
        map_targetCode.put("j!=","\tmov ax,a1\n\tcmp ax,b1\n\tjne p1\n");
        map_targetCode.put("j==","\tmov ax,a1\n\tcmp ax,b1\n\tje p1\n");
        map_targetCode.put("para","\tmov ax,a1\n\tpush ax\n");
        map_targetCode.put("call","\tcall a1\n");
        map_targetCode.put("call_value","\tcall a1\n\tmov t1,ax\n");
        //这里面有几个特殊的，就是下面这三个，前两个通过ret提取出来，然后判断四元式第二个是否有值来选取二者之一
        map_targetCode.put("ret_value","\tmov ax,a1\n\tmov sp,bp\n\tpop bp\n\tret size\n");
        map_targetCode.put("ret","\tmov sp,bp\n\tpop bp\n\tret size\n");
        //这个无法通过名称取出键值对中的汇编代码，只能通过查函数表中是否含有判断该函数进行判断
        map_targetCode.put("fun","\tpush bp\n\tmov bp,sp\n\tsub sp,size\n");
        //赋值语句
        map_targetCode.put("=","\tmov ax,a1\n\tmov t1,ax\n");
    }
    public void addToTargetCode(ArrayList<String> list_target){
        list_targetCode.addAll(list_target);
    }
    //完成第二条四元式翻译开始之前的所有汇编代码生成。

    public void init(){
        String path_file ;
        list_nodeFour = new ArrayList<>(blockBody.getList_nodeFour());
        map_constant = new HashMap<>(blockBody.getMap_constant());
        //变量表由于存在多个同名不同区域变量，因此是一个两层的哈希表，第一层以变量名为键值，第二层以块名为建
        map_variable = new HashMap<>(blockBody.getMap_variable());//变量表
        //函数表
        map_function = new HashMap<>(blockBody.getMap_function());//变量表
        map_funcVar = new HashMap<>(blockBody.map_funcVar);//函数内参数局部变量大表;
        //将main这个四元式中的所包含的汇编代码写入结果中
        path_file = Tool.ChangeAbsolutePath("main");
        addToTargetCode(ReadFile.readFileList(path_file));
        //接下来需要将变量表和常量表中的变量名转化为汇编代码写入
        ArrayList<String> list_varAndCons = new ArrayList<>();
        //取出map_funcVar中所有的变量
        Set<String> set_var = new HashSet<>();
        map_funcVar.forEach((k,v)->{
            v.forEach((k1,v1)->{
                set_var.add(k1);
            });
        });
        map_constant.forEach((k,v) ->{
            //不在其他函数中的变量
            if(!set_var.contains(k)){
                list_varAndCons.add("\t_"+k+" dw 0\n");
            }
        });
        map_variable.forEach((k,v)->{
            if(!set_var.contains(k)){
                list_varAndCons.add("\t_"+k+" dw 0\n");
            }
        });
        addToTargetCode(list_varAndCons);
        path_file = Tool.ChangeAbsolutePath("mainAppend");
        addToTargetCode(ReadFile.readFileList(path_file));
    }
    public void subChangeToCompileCode(NodeFour nodeFour,int i,String str_compileCode){
        //修改ret四元式
        if(nodeFour.factor1.equals("ret")){
            str_compileCode = str_compileCode.replaceAll("size",""+(Integer.parseInt(map_temp.get("_times"))*4));
        }
        //与是否在main函数内无关
        if(nodeFour.factor1.equals("call")){
            //该四元式没有返回值
            if(nodeFour.factor4.isEmpty()){
                str_compileCode = str_compileCode.replaceAll("a1","_"+nodeFour.factor2);
            }
            else{
                //首先将a1所代表的函数名用正确函数名替换之
                str_compileCode = str_compileCode.replaceAll("a1","_"+nodeFour.factor2);
                //取出factor4,替换函数中的t1
                int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                String str_temp = "es:["+(index*2)+"]";
                str_compileCode = str_compileCode.replaceAll("t1",str_temp);
            }
        }
        else if(map_function.containsKey(nodeFour.factor1)){
            //doNothing
            str_compileCode = str_compileCode.replaceAll("size",""+Integer.parseInt(map_temp.get("_times"))*2);
        }
        //依次判断根据四元式的后三项替换汇编代码
        else{
            //这里说明以下newTemp翻译成的汇编代码，例如T0->es:[0],T1->es:[2],每一个扩展数据段的大小是两个字节
            //而变量常量就需要翻译为 num->ds:[_num]
            //数字常量就可以直接翻译为100->100;
            if(!nodeFour.factor2.isEmpty()){
                //如果四元式中的factor2为常量或者变量
                if (map_variable.containsKey(nodeFour.factor2)||map_constant.containsKey(nodeFour.factor2)){
                    String str_temp = map_temp.get(nodeFour.factor2);
                    str_compileCode = str_compileCode.replaceAll("a1",str_temp);
                }
                else if(nodeFour.factor2.charAt(0) == 'T'){
                    int index  =Integer.parseInt(nodeFour.factor2.substring(1));
                    String str_temp = "es:["+(index*2)+"]";
                    str_compileCode = str_compileCode.replaceAll("a1",str_temp);
                }
                //到这一步的话只能是数字字符
                else{
                    str_compileCode = str_compileCode.replaceAll("a1", nodeFour.factor2);
                }
            }
            //factor3与那啥factor2操作一致
            if(!nodeFour.factor3.isEmpty()){
                //如果四元式中的factor2为常量或者变量
                if (map_variable.containsKey(nodeFour.factor3)||map_constant.containsKey(nodeFour.factor3)){
                    String str_temp = map_temp.get(nodeFour.factor3);
                    str_compileCode = str_compileCode.replaceAll("b1",str_temp);
                }
                else if(nodeFour.factor3.charAt(0) == 'T'){
                    int index  =Integer.parseInt(nodeFour.factor3.substring(1));
                    String str_temp = "es:["+(index*2)+"]";
                    str_compileCode = str_compileCode.replaceAll("b1",str_temp);
                }
                //到这一步的话只能是数字字符
                else{
                    str_compileCode = str_compileCode.replaceAll("b1", nodeFour.factor3);
                }
            }
            //factor4只有两种方式，要么为跳转四元式的序列号，要么为新产生的newTemp
            if(!nodeFour.factor4.isEmpty()){
                //这里有一个另类，那就是a=1;其中a为factor4应该为newTemp或者是变量常量,
                if(nodeFour.factor1.equals("=")){
                    if (map_variable.containsKey(nodeFour.factor4)||map_constant.containsKey(nodeFour.factor4)){
                        String str_temp = map_temp.get(nodeFour.factor4);
                        str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                    }
                    else if(nodeFour.factor4.charAt(0) == 'T'){
                        int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                        String str_temp = "es:["+(index*2)+"]";
                        str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                    }
                }
                else if(nodeFour.factor4.charAt(0) == 'T'){
                    int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                    String str_temp = "es:["+(index*2)+"]";
                    str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                }
                //p1为跳转到的四元式位置
                else{
                    str_compileCode = str_compileCode.replaceAll("p1", "_"+nodeFour.factor4);
                }
            }
        }
        if(map_function.containsKey(nodeFour.factor1)){
            str_compileCode = "_"+nodeFour.factor1+":"+str_compileCode;
        }
        else{
            str_compileCode = "_"+i+":"+str_compileCode;
        }
        //将修改完毕的四元式加到结果list中
        list_targetCode.add(str_compileCode);
    }
    public void changeToCompileCode(){
        init();
        int num_index=0;
        for(int i=1;i<list_nodeFour.size();i++){
            NodeFour nodeFour = list_nodeFour.get(i);
            //对每一条四元式进行翻译
            String str_compileCode = "空";
            if(nodeFour.factor1.equals("ret")){
                if(nodeFour.factor2.isEmpty()){
                    str_compileCode = map_targetCode.get("ret");
                }
                else{
                    str_compileCode = map_targetCode.get("ret_value");
                }
            }
            else if(nodeFour.factor1.equals("call")){
                if(nodeFour.factor4.isEmpty()){
                    str_compileCode = map_targetCode.get("call");
                }
                else{
                    str_compileCode = map_targetCode.get("call_value");
                }
            }
            //如果四元式是函数定义语句
            else if(map_function.containsKey(nodeFour.factor1)){
                str_compileCode = map_targetCode.get("fun");
                funcName = nodeFour.factor1;
                map_temp = map_funcVar.get(funcName);
            }
            else if(map_targetCode.containsKey(nodeFour.factor1)){
                str_compileCode = map_targetCode.get(nodeFour.factor1);
            }
            //此后的处理均是在main函数中的，在其他函数中需要进行，函数单独处理
            //需要一个变量判断是否处于main函数中
            if(!isInMian){
                //不在main函数中，对于后三个factor处理要改变
                subChangeToCompileCode(nodeFour,i,str_compileCode);
                continue;
            }
            if (nodeFour.factor1.equals("sys")){
                isInMian = false;
                str_compileCode = "_quit:\tmov ah,4ch\n" +
                        "\tint 21h\n";
            }
            //至此获得了对应语句中的汇编代码
            //但是还是需要通过四元式中的后三个值将汇编代码中的对应代码替换之；
            //factor2,对应的汇编代码为a1
            //因为call的处理与常规的不太一样(其中的a1并不是通过变量名和newTemp名判断，而是直接填函数名)，所以单独处理
            if(nodeFour.factor1.equals("call")){
                //该四元式没有返回值
                if(nodeFour.factor4.isEmpty()){
                    //怎么说呢，我好像不能生成没有返回值的四元式哈哈
                    str_compileCode = str_compileCode.replaceAll("a1","_"+nodeFour.factor2);
                }
                else{
                    //首先将a1所代表的函数名用正确函数名替换之
                    str_compileCode = str_compileCode.replaceAll("a1","_"+nodeFour.factor2);
                    //取出factor4,替换函数中的t1
                    int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                    String str_temp = "es:["+(index*2)+"]";
                    str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                }
            }
            else if(nodeFour.factor1.equals("fun")){
                //doNothing
            }
            //依次判断根据四元式的后三项替换汇编代码
            else{
                //这里说明以下newTemp翻译成的汇编代码，例如T0->es:[0],T1->es:[2],每一个扩展数据段的大小是两个字节
                //而变量常量就需要翻译为 num->ds:[_num]
                //数字常量就可以直接翻译为100->100;
                if(!nodeFour.factor2.isEmpty()){
                    //如果四元式中的factor2为常量或者变量
                    if (map_variable.containsKey(nodeFour.factor2)||map_constant.containsKey(nodeFour.factor2)){
                        String str_temp = "ds:[_"+nodeFour.factor2+"]";
                        str_compileCode = str_compileCode.replaceAll("a1",str_temp);
                    }
                    else if(nodeFour.factor2.charAt(0) == 'T'){
                        int index  =Integer.parseInt(nodeFour.factor2.substring(1));
                        String str_temp = "es:["+(index*2)+"]";
                        str_compileCode = str_compileCode.replaceAll("a1",str_temp);
                    }
                    //到这一步的话只能是数字字符
                    else{
                        str_compileCode = str_compileCode.replaceAll("a1", nodeFour.factor2);
                    }
                }
                //factor3与那啥factor2操作一致
                if(!nodeFour.factor3.isEmpty()){
                    //如果四元式中的factor2为常量或者变量
                    if (map_variable.containsKey(nodeFour.factor3)||map_constant.containsKey(nodeFour.factor3)){
                        String str_temp = "ds:[_"+nodeFour.factor3+"]";
                        str_compileCode = str_compileCode.replaceAll("b1",str_temp);
                    }
                    else if(nodeFour.factor3.charAt(0) == 'T'){
                        int index  =Integer.parseInt(nodeFour.factor3.substring(1));
                        String str_temp = "es:["+(index*2)+"]";
                        str_compileCode = str_compileCode.replaceAll("b1",str_temp);
                    }
                    //到这一步的话只能是数字字符
                    else{
                        str_compileCode = str_compileCode.replaceAll("b1", nodeFour.factor3);
                    }
                }
                //factor4只有两种方式，要么为跳转四元式的序列号，要么为新产生的newTemp
                if(!nodeFour.factor4.isEmpty()){
                    //这里有一个另类，那就是a=1;其中a为factor4应该为newTemp或者是变量常量,
                    if(nodeFour.factor1.equals("=")){
                        if (map_variable.containsKey(nodeFour.factor4)||map_constant.containsKey(nodeFour.factor4)){
                            String str_temp = "ds:[_"+nodeFour.factor4+"]";
                            str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                        }
                        else if(nodeFour.factor4.charAt(0) == 'T'){
                            int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                            String str_temp = "es:["+(index*2)+"]";
                            str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                        }
                    }
                    else if(nodeFour.factor4.charAt(0) == 'T'){
                        int index  =Integer.parseInt(nodeFour.factor4.substring(1));
                        String str_temp = "es:["+(index*2)+"]";
                        str_compileCode = str_compileCode.replaceAll("t1",str_temp);
                    }
                    //p1为跳转到的四元式位置
                    else{
                        if(Integer.parseInt(nodeFour.factor4)>= list_nodeFour.size()-1){
                            str_compileCode = str_compileCode.replaceAll("p1", "_quit");
                        }
                        else
                        str_compileCode = str_compileCode.replaceAll("p1", "_"+nodeFour.factor4);
                    }
                }
            }
            if(!nodeFour.factor1.equals("sys")){
                str_compileCode = "_"+i+":"+str_compileCode;
            }

            //将修改完毕的四元式加到结果list中
            list_targetCode.add(str_compileCode);
        }

        appendSys();
    }
    public void appendSys(){
        String path_file = Tool.ChangeAbsolutePath("sys");
        addToTargetCode(ReadFile.readFileList(path_file));
    }

    public ArrayList<String> getList_targetCode() {
        return list_targetCode;
    }

    public static void main(String[] args) throws IOException {
        String path_file = Tool.ChangeAbsolutePath("testMeaning");
        String code = ReadFile.readFile(path_file);
        Lexical lexical = new Lexical(code);
        lexical.LexicalAnal();
        ArrayList<String> list = lexical.getResultList();
        BlockBody blockBody = new BlockBody(GrammerTool.changeToNode(list),true,0);
        blockBody.creatFour();
        TargetCode targetCode =  new TargetCode(blockBody);
        targetCode.changeToCompileCode();
        System.out.println("四元式：");
        for (int i = 0; i < targetCode.list_nodeFour.size(); i++) {
            System.out.println(i+"\t"+targetCode.list_nodeFour.get(i));
        }
//        System.out.println("变量表");
//        targetCode.map_variable.forEach((k,v) ->{
//            System.out.println(k);
//        });
        String pathFile = Tool.ChangeAbsolutePathGrammer("result");
        ReadFile.cleanFile(pathFile);
        ReadFile.bufferWrite(pathFile,targetCode.list_targetCode);
//        System.out.println("汇编代码：");
//        for (String str: targetCode.list_targetCode){
//            System.out.print(str);
//        }
    }
}
