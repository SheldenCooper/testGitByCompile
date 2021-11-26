//package RYQ.grammar;
//
//import RYQ.javafx.Lexical;
//import RYQ.javafx.ReadFile;
//import RYQ.javafx.Tool;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class TotalCompile {
//    private ArrayList<Node> list_Node;
//    private List<String> totalList ;//用作算数表达式类对象传入的参数
//    private int index;//遍历词法分析结构
//    private Set<String> set_declarationTotal = new HashSet<>();//总的声明词，包括变量，常量，与void
//    private boolean isTotalCompile;//作用，如果是词法错误，将会映射到语法错误上
//    public boolean isTotalCompile(){//布尔表达式也是表达式的一部分
//        return isTotalCompile;
//    }
//    public TotalCompile(){
//    }
//
//    public TotalCompile(ArrayList<Node> list_Node,boolean isWordException,int index){
//        this.isTotalCompile = isWordException;//作用，如果是词法错误，将会映射到语法错误上
//        this.list_Node = list_Node;
//        this.index = index;
//        set_declarationTotal.add("int");set_declarationTotal.add("float");set_declarationTotal.add("char");set_declarationTotal.add("const");
//        set_declarationTotal.add("void");
//    }
//    public void testTotal(){
//        System.out.println("测试程序开始");
//        while(index< list_Node.size()){
//            //做main语句
//            if(list_Node.get(index).word.equals("main")){
//                System.out.println("进入main");
//                index++;
//                if(index+1<list_Node.size()&&list_Node.get(index).word.equals("(")&&list_Node.get(index+1).word.equals(")")){
//                    index +=2;
//                }
//                else{
//                    System.out.println("mian函数后未接()");
//                    isTotalCompile = false;
//                    break;
//                }
//                if(!doFuncBody()){
//                    System.out.println("main方法体内出错");
//                    return;
//                }
//                //做完这个方法，index将指向main（）{}中}
//                System.out.println("退出main");
//                System.out.println("index:"+index);
//                if(index>=list_Node.size()){
//                    break;
//                }
//
//            }
//            //做声明语句
//            else if(set_declation.contains(list_Node.get(index).word)){
//                //做函数相关
//                if(index+2<list_Node.size()&&list_Node.get(index+2).word.equals("(")){
//                    //做函数定义
//                    if(index+4<list_Node.size()&&list_Node.get(index+4).value==700){
//                        Function function = new Function(list_Node,true,index);
//                        function.doDeclartionBody();
//                        index = function.getIndex();
//                        if(!function.isFunction()){
//                            System.out.println("a错误的函数定义");
//                            isTotalCompile = false;
//                            return ;
//                        }
//                    }
//                    //做函数声明
//                    else {
//                        Function function = new Function(list_Node, true, index);
//                        function.doDeclartion();
//                        index = function.getIndex();
//                        if (!function.isFunction()) {
//                            System.out.println("a错误的函数声明");
//                            isTotalCompile = false;
//                            return;
//                        }
//                    }
//
//                }
//                //变量常量声明
//                else{
//                    //跳过const
//                    if(list_Node.get(index).word.equals("const")){
//                        index++;
//                    }
//                    //做赋值语句
//                    Declaration declaration = new Declaration(list_Node,true,index);
//                    declaration.doDeclaration();
//                    index = declaration.getIndex();
//                    if(!declaration.isDeclaration()){
//                        System.out.println("错误的声明语句");
//                        isTotalCompile = false;
//                        return;
//                    }
//                }
//
//
//            }
//            else{
//                isTotalCompile = false;
//
//                System.out.println("不是声明语句或者main函数或者普通函数");
//                return ;
//            }
//            index++;
//
//        }
//        System.out.println("测试程序结束");
//
//    }
////    public boolean doFuncBody(){
////        if(list_Node.get(index).word.equals("{")){
////            System.out.println("进入方法体");
////            index++;
////            //这里是要分情况的，暂时只有一种情况，就是赋值语句
////            while(index < list_Node.size()&&!list_Node.get(index).word.equals("}")){
////                //1：赋值语句
////                if(index+1>=list_Node.size()){
////                    System.out.println("index超过极限");
////                    isTotalCompile = false;
////                    return false;
////                }
////                //赋值语句
////                if(list_Node.get(index).value==700&&list_Node.get(index+1).word.equals("=")){
////
////                    Evaluation evaluation = new Evaluation(list_Node,true,index);//懒得改成多态了
////                    evaluation.doEvaluation();
////                    index = evaluation.getIndex();
////                    System.out.println("结束赋值语句后："+list_Node.get(index));
////
////                    if(!evaluation.isEvaluation()){
////                        System.out.println("a错误的赋值表达式");
////                        isTotalCompile = false;
////                        return false;
////                    }
////
////                }
////                //声明语句
////                else if(set_declation.contains(list_Node.get(index).word)) {
////                    //跳过const
////                    if (list_Node.get(index).word.equals("const")) {
////                        index++;
////                    }
////                    //做赋值语句
////                    Declaration declaration = new Declaration(list_Node,true,index);
////                    declaration.doDeclaration();
////                    index = declaration.getIndex();
////                    if(!declaration.isDeclaration()){
////                        System.out.println("错误的声明语句");
////                        isTotalCompile = false;
////                        return false;
////                    }
////                //bug在这，每条语句执行完了之后他们均指向自己的末尾;or},但是if语句由于考虑了他要链接下面的else，之所以代码中
////                    // 让他执行完一个代码块后，跳过了末尾的}，但是不能这样做，这样做了就不能匹配其他语句的操作了，那么我们需修改if语句内的逻辑
////                }
////                else if (list_Node.get(index).word.equals("if"))
////                {
////                    AIf aIf = new AIf(list_Node,true,index,false);
////                    aIf.doIf(false);
////                    index = aIf.getIndex();
////                    if(!aIf.isIF()){
////                        System.out.println("错误的if语句");
////                        isTotalCompile = false;
////                        return false;
////                    }
////
////                }
////                //5.while
////                else if(list_Node.get(index).word.equals("while")){
////                    Loop loop = new Loop(list_Node,true,index);
////                    loop.doWhile();
////                    index = loop.getIndex();
////                    if(!loop.isLoop()){
////                        System.out.println("错误的while语句");
////                        isTotalCompile = false;
////                        return false;
////                    }
////                }
////                //6.for
////                else if(list_Node.get(index).word.equals("for")){
////                    Loop loop = new Loop(list_Node,true,index);
////                    loop.doFor();
////                    index = loop.getIndex();
////                    if(!loop.isLoop()){
////                        System.out.println("错误的for语句");
////                        isTotalCompile = false;
////                        return false;
////                    }
////                }
////                //这个函数的判断顺序也很重要，必须最后一个判断，因为会影响if（）这种
////                //7.函数单行调用
////                else if(index+1<list_Node.size()&&list_Node.get(index).value==700 && list_Node.get(index+1).word.equals("(")){
////                    Arithmetic arithmetic = new Arithmetic(list_Node,index,true);
////                    arithmetic.doArithmetic();
////                    index = arithmetic.getIndex();
////                    if(!arithmetic.isArithmetic()){
////                        System.out.println("错误的函数语句");
////                        isTotalCompile = false;
////                        return false;
////                    }
////                }
////                //8.return语句
////                else if(list_Node.get(index).word.equals("return")){
////                    System.out.println("进入return语句");
////                    index++;
////                    if(!list_Node.get(index).word.equals(";")){//这说明return的是一个表达式
////                        //直接将表达式当成布尔表达式处理，布尔内部会自行处理
////                        Boolean bool = new Boolean(list_Node,true,index);
////                        bool.doBoolean();
////                        index = bool.getIndex();
////                        if(!bool.isBoolean()){
////                            System.out.println("return 后接的表达式错误");
////                            isTotalCompile = false;
////                            return false;
////                        }
////                    }
////                    //为什么不用else,因为这是必须的操作，不能因为有上if而不进入下if
////                    if(!list_Node.get(index).word.equals(";")){
////                        System.out.println("return 末尾不为;");
////                        isTotalCompile = false;
////                        return false;
////                    }
////                    System.out.println("离开return语句");
////                }
////                else if(list_Node.get(index).word.equals("{")){
////                    BlockBody blockBody = new BlockBody(list_Node,true,index);
////                    blockBody.doBlockBody(false);
////                    index = blockBody.getIndex();
////                    if(!blockBody.isBlockBody()){
////                        System.out.println("单独代码块出错");
////                    }
////
////                }
////                else{
////                    System.out.println("非法开头语句:"+list_Node.get(index).word);
////                    isTotalCompile = false;
////                    return false;
////                }
////                //每一个语句的最后一定是;,需要手动跳过
////                index++;
////            }
////            if(index == list_Node.size()){
////                System.out.println("方法体缺少右大括号}");
////                isTotalCompile = false;
////                return false;
////            }
//////            else{//不缺少大括号，跳过该大括号
//////                index++;
//////            }
////
////        }
////        else {
////            System.out.println("方法体应以{开头");
////        }
////        System.out.println("离开方法体");
////        if(list_Node.size()>index){
////            System.out.print(list_Node.get(index));
////        }
////        return true;
////    }
//
//    public static void main(String[] args) {
//        System.out.println("测试总程序。。。。。。");
//        String path_file = Tool.ChangeAbsolutePath("testTotal");
//        String code = ReadFile.readFile(path_file);
//        Lexical lexical = new Lexical(code);
//        lexical.LexicalAnal();
//        ArrayList<String> list = lexical.getResultList();
//        TotalCompile totalCompile = new TotalCompile(GrammerTool.changeToNode(list),!lexical.isException(),0);
//        totalCompile.testTotal();
//        System.out.println(totalCompile.isTotalCompile);
//        System.out.println(list.size()+":"+totalCompile.index);
//
//    }
//}
