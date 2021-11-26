package RYQ.grammar;

import RYQ.javafx.Lexical;
import RYQ.javafx.ReadFile;
import RYQ.javafx.Tool;

import java.util.*;

//进行块体的运算，就比如说if后所包含的代码块
public class BlockBody {
    //进行块体的运算，就
    private ArrayList<Node> list_Node;
    private int index;//遍历词法分析结构
    public ArrayList<String> getList_entryInfo() {
        return list_entryInfo;
    }

    public ArrayList<String> getList_mean_error() {
        return list_mean_error;
    }

    public ArrayList<NodeFour> getList_nodeFour() {
        return list_nodeFour;
    }
    public BlockBody(){
    }
    public BlockBody(ArrayList<Node> list_Node,boolean isRight,int index){
        this.index = index;
        this.list_Node = list_Node;
        for(int i=101;i<=103;i++)
            set_declation.add(i);//分别为char int float，不能有const
        set_compare = new HashSet<>();
        for(int i=211;i<216;i++){
            set_compare.add(i);
        }
        for(int i=101;i<=103;i++)
            set_declarationFun.add(i);//分别为char int float，不能有const
        set_declarationFun.add(107);//void    107
        //总的声明类型集合
        set_declarationTotal.add("int");set_declarationTotal.add("float");set_declarationTotal.add("char");set_declarationTotal.add("const");
        set_declarationTotal.add("void");

    }

    public ArrayList<String> getList_error() {
        return list_error;
    }

    //0.一些公共的函数和全局变量-----------------------------------------------------------------------------------------------------------
    ArrayList<String> list_error = new ArrayList<>();
    ArrayList<String> list_entryInfo = new ArrayList<>();
    ArrayList<String> list_mean_error = new ArrayList<>();
    //四元式的列表
    ArrayList<NodeFour> list_nodeFour = new ArrayList<>();
    //布尔表达式的真出口链
    ArrayList<NodeExit> list_true = new ArrayList<>();
    ArrayList<NodeExit> list_false = new ArrayList<>();
    //无条件跳转list,因为在if中可能存在多个if语句嵌套，并且他们都需要跳转到else之后的部分
    ArrayList<NodeExit> list_noConditionTurn = new ArrayList<>();
    //常量表
    HashMap<String,TNode> map_constant = new HashMap<>();

    public HashMap<String, TNode> getMap_constant() {
        return map_constant;
    }

    public HashMap<String, HashMap<String, TNode>> getMap_variable() {
        return map_variable;
    }

    public HashMap<String, FNode> getMap_function() {
        return map_function;
    }

    //变量表由于存在多个同名不同区域变量，因此是一个两层的哈希表，第一层以变量名为键值，第二层以块名为建
    HashMap<String,HashMap<String,TNode>> map_variable = new HashMap<>();//变量表
    //函数表
    HashMap<String, FNode> map_function = new HashMap();//变量表
    //目标代码要使用，利用函数名变量名确定所处栈空间
    HashMap<String ,HashMap<String,String>> map_funcVar = new HashMap<>();
    HashMap<String, String> map_temp;

    private Set<Integer> set_declation = new HashSet();
    private boolean haveleft;//有左半边括号
    private Set<Integer> set_compare;
    private Set<Integer> set_declarationFun = new HashSet<>();
    private Set<String> set_declarationTotal = new HashSet<>();//总的声明词，包括变量，常量，与void

    private int index_block=0;//进入{}时，需以该值为结尾命名块名
    private String nowBlockName = "0/";//当进入一个块时该名名称加两位“index_block/”,当离开一个块时，需要将名称复原，也就是减两位
    private String declaration_type;//这是声明类型，因为声明语句存再函数多次调用，需要用这个变量记住声明类型
    boolean isConst = false;//是否为常量
    //是否正在进行进行表达式语义处理
    boolean isDoingFactor = false;
    //用作算数表达式因子（函数，变量常量）判断是否类型一致
    private String match_type = "";
    //四元式临时地址指向特指newTemp;
    private int index_t = 0;
    //由于历史遗留问题，再次使用一个全局变量存储一个完整表达式运算结束后所产生的结果变量名
    private String str_retArithmetic;
    //记录第一次进入定义其他函数声明,此时需要插入一条sys四元式
    private int index_main = 0;
    //用来记录这是函数中的第几个局部变量
    private int index_func_var=0;
    public boolean isVaule(Node node){//判断是不是常量或者变量
        if(node.value==400 ||node.value==500||node.value==700 ||node.value==800){
            return true;
        }
        else{
            return false;
        }
    }
    //1.Arithmetic算数表达式-----------------------------------------------------------------------------------------------------------


    public boolean isoperater(int i){
        return list_Node.get(i).word.equals("*") || list_Node.get(i).word.equals("+")
                || list_Node.get(i).word.equals("-") || list_Node.get(i).word.equals("/");
    }
    public boolean isoperater(String str){
        return str.equals("*") || str.equals("+")
                || str.equals("-") || str.equals("/")||str.equals("%");
    }



    /**
     * @Author ryq
     * @Description //接下来有三个方法分别对应着检测标识符、数字、函数是为对应类型的
     * @Date  2021/5/18
     * @Param []
     * @return void
     **/
    public void doSubVariable(){
        //需要判断它是否出现在符号表中，出现在符号表之后仍需要判断其是否是在本块或者本块的父块中声明
        Node tempNode = list_Node.get(index);
        if(map_variable.containsKey(tempNode.word)){
            //获取第二层的变量表
            HashMap<String , TNode> mapTemp = map_variable.get(tempNode.word);
            String nestVarName = nestBlockName(mapTemp,nowBlockName);//通过本块名，遍历出父块或者本块的块名
            if(nestVarName.isEmpty()){
                list_mean_error.add("表达式语句变量未定义就使用；"+tempNode);
            }
            else{
                if(!mapTemp.get(nestVarName).type.equals(match_type)){
                    list_mean_error.add("表达式语句变量与需求类型不匹配；"+tempNode);
                }
            }
        }
        else if(map_constant.containsKey(tempNode.word)){
            //常量的块名均为0/,区分是否存在于常量表的方法是直接从名称开始查
            if(!map_constant.get(tempNode.word).type.equals(declaration_type)){
                list_mean_error.add("表达式语句常量与需求类型不匹配；"+tempNode);
            }

        }
        else {
            list_mean_error.add("表达式语句变量未定义就使用；"+tempNode);
        }
    }
    public void dosubNumber(){
        //                    int_type = "400";
        //                    char_type = "500";
        //                    float_type = "800";
        Node tempNode = list_Node.get(index);
        if((tempNode.value==500&&!match_type.equals("char"))||
                (tempNode.value==400&&!match_type.equals("int"))||
                (tempNode.value==800&&!match_type.equals("float"))){
            list_mean_error.add("表达式语句数据类型不匹配"+tempNode);
        }
    }

    public void doSubPramaArith(){
        //每一个函数的参数
        int times=0;
        ArrayList<String> list_type = map_function.get(list_Node.get(index-2).word).list_type;
        for (String s : list_type) {
            //出大问题，如过函数的参数为表达式的时候，表达式需要返回值，但是由于存在递归调用的原因，表达式最后的结果只有当整个表达式计算完成之后才会
            //存入stack_result中，问题就是，怎么单独处理函数内的参数，并且还有一个问题，函数内表达式的四元式怎么刚好处于
            if (isVaule(list_Node.get(index))) {
                //进行函数参数类型比较
                times++;
                match_type = s;
                String str = doAexprArith();
                //获得了该参数生成函数四元式
                list_nodeFour.add(new NodeFour("para",str,"",""));

            }
            if (list_Node.get(index).word.equals(",")) {
                index++;
            }
        }
        if(times!=list_type.size()){
            list_mean_error.add("函数声明参数个数与调用个数不一致,声明个数"+list_type.size()+"，调用个数："+times);
        }
    }

    //这个函数的唯作用是，当函数未声明时(有可能是系统函数read和write哦)，仍旧可以通过这些操作进行函数的参数操作，仅仅是不查寒暑表进行参数类型而已。
    public void doPramaArith(){//到底参数是否应有返回值，不应该有！
        //进入这个函数说明此时index指向"("后第一个参数
        while(true){
            if(isVaule(list_Node.get(index))){//如果是变量或者常量(这里常量指的是121，'a',等)
                String str = doAexprArith();
                //获得了该参数生成函数四元式
                list_nodeFour.add(new NodeFour("para",str,"",""));
            }
            if(index>=list_Node.size()){
                return;//说明没有以“）”为结尾返回；
            }
            if(!list_Node.get(index).word.equals(",")){
                break;
            }
            index++;
        }
    }
//1 Arithmetic表达式的非逆波兰式计算中间代码方法-----------------------------------------------------------------------
    public void doArithmetic(){
        list_entryInfo.add("进入算数表达式");
        str_retArithmetic = doAexprArith();
        list_entryInfo.add("退出算数表达式");
}
    public String doAexprArith(){//处理算数表达式

        //表达式做完所产生的返回字符串
        String str_ret = doTermArith();
        //临时字符串
        String str_temp;
        while(index<list_Node.size()&&(list_Node.get(index).word.equals("+")||list_Node.get(index).word.equals("-"))){
            //进入循环代表"+或者-"已经遍历过了；

            String str = list_Node.get(index).word;//记住运算符
            index++;//跳过运算符
            str_temp = doTermArith();
            //此时有了+，或者-这一层的左右两个值，可以开始进行生成新的四元式
            list_nodeFour.add(new NodeFour(str,str_ret,str_temp,"T"+index_t));
            str_ret = "T"+index_t;
            index_t++;

        }

        return str_ret;
        //出循环代表不满足条件，访问下标不用改变
    }
    public String doTermArith(){//处理项
        //表达式做完所产生的返回字符串
        String str_ret = doFactorArith();
        //临时字符串
        String str_temp;
        while(index<list_Node.size()&&(list_Node.get(index).word.equals("*")||list_Node.get(index).word.equals("/")||list_Node.get(index).word.equals("%"))){
            //进入循环代表"*或者/"已经遍历过了；
            //为了维护list访问下标不超限
            String str = list_Node.get(index).word;//记住运算符
            index++;//跳过运算符
            str_temp  = doFactorArith();
            list_nodeFour.add(new NodeFour(str,str_ret,str_temp,"T"+index_t));
            str_ret = "T"+index_t;
            index_t++;
        }
        return str_ret;
    }
    public String doFactorArith(){//处理因子
        String str_ret ="";
        if(index ==list_Node.size()){
            return "空";
        }
        if(list_Node.get(index).word.equals("(")){
            haveleft = true;
            index++;
            str_ret = doAexprArith();//对括号内的项进行操作，当没有加减号时机会返回
            if(index ==list_Node.size()){
                return "空";
            }
            if(!list_Node.get(index).word.equals(")")){
                System.out.println("Arithmetic缺少一次）");
                //isArithmetic = false;
                list_error.add("算数表达式出错,Arithmetic缺少一次）"+list_Node.get(index));

            }
            else{
                haveleft = false;
                index++;
            }

        }
        else if(list_Node.get(index).word.equals("-")){
            index++;
            str_ret = doAexprArith();
        }
        else if(isVaule(list_Node.get(index))){
            str_ret = list_Node.get(index).word;
            //是否正在进行赋值语句，是，就进行判断赋值变量是否与表达式一致。
            //注意函数的处理有两次，ifelse都有
            if(isDoingFactor){//isDoingFactor的意思是正在做赋值语句，需要比较赋值语句变量与表达式类型是否一致。
                if(list_Node.get(index).value==700){//如果是标识符则继续判断是否为函数
                    int index_tag = index;
                    index++;
                    if(list_Node.size()>index && list_Node.get(index).word.equals("(")){
                        index++;//跳过括号进行参数处理
                        list_entryInfo.add("进入函数："+list_Node.get(index_tag).word);
                        //如果函数表没有该函数
                        if(list_Node.get(index-2).word.equals("write")||list_Node.get(index-2).word.equals("read;")){
                            doPramaArith();
                        }
                        else{
                            if(!map_function.containsKey(list_Node.get(index-2).word)){
                                list_mean_error.add("函数没有声明："+list_Node.get(index-2));
                                doPramaArith();
                            }
                            else{
                                //如果返回类型与表达式类型不一样
                                if(!map_function.get(list_Node.get(index-2).word).funcType.equals(match_type)){
                                    list_mean_error.add("函数返回类型与表达式不一致"+list_Node.get(index-2));
                                }
                                doSubPramaArith();
                            }
                        }

                        if(list_Node.size()<=index ||!list_Node.get(index).word.equals(")")){//如果不以右括号结尾
                            list_error.add("函数不以右括号结尾"+list_Node.get(index_tag));

                        }
                        else
                            index++;//跳过末尾括号
                        //此时函数和他的参数已经处理完毕了，需要将该函数所产生的值保存返回（newTemp）

                        String newTemp = "T"+index_t;
                        list_nodeFour.add(new NodeFour("call",str_ret,"",newTemp));
                        index_t++;
                        //改变返回值为调用函数所产生的新的newTemp
                        str_ret = newTemp;
                        list_entryInfo.add("离开函数："+list_Node.get(index_tag).word);
                    }
                    else {
                        //注意此时的index没有指向标识符，而是指向标识符，若此时要对标识符进行操作，那么index--，再复原
                        index--;
                        doSubVariable();
                        index++;
                    }
                }
                else{
                    dosubNumber();
                    index++;//跳过整数、浮点数
                }
            }
            else{
                if(list_Node.get(index).value==700){//如果是标识符则继续判断是否为函数
                    int index_tag = index;
                    index++;
                    if(list_Node.size()>index && list_Node.get(index).word.equals("(")){
                        index++;//跳过括号进行参数处理
                        list_entryInfo.add("进入函数："+list_Node.get(index_tag).word);
                        //如果函数表没有该函数
                        if(list_Node.get(index-2).word.equals("write")||list_Node.get(index-2).word.equals("read;")){
                            doPramaArith();
                        }
                        else{
                            if(!map_function.containsKey(list_Node.get(index-2).word)){
                                list_mean_error.add("函数没有声明："+list_Node.get(index-2));
                                doPramaArith();
                            }
                            else{
                                doSubPramaArith();
                            }
                        }

                        if(list_Node.size()<=index ||!list_Node.get(index).word.equals(")")){//如果不以右括号结尾
                            list_error.add("函数不以右括号结尾"+list_Node.get(index_tag));
                        }
                        else
                            index++;//跳过末尾括号
                        //此时函数和他的参数已经处理完毕了，需要将该函数所产生的值保存返回（newTemp）
                        String newTemp = "T"+index_t;
                        list_nodeFour.add(new NodeFour("call",str_ret,"",newTemp));
                        index_t++;
                        //改变返回值为调用函数所产生的新的newTemp
                        str_ret = newTemp;
                        list_entryInfo.add("离开函数："+list_Node.get(index_tag).word);
                    }
                    else {
                        //注意此时的index没有指向标识符，而是指向标识符，若此时要对标识符进行操作，那么index--，再复原
                    }
                }
                else{
                    index++;//跳过整数、浮点数
                }
            }

        }
        else{
            list_error.add("Arithmetic语法错误:不是变量或者常量："+list_Node.get(index));
            index++;
        }
        return str_ret;
    }

//2.Boolean--------------------------------------------------------------------------------------------------------------------------
    public void backInput(ArrayList<NodeExit> list_exit,int index_back){
        Iterator<NodeExit> iterator = list_exit.iterator();
        while (iterator.hasNext()){
            NodeExit nodeExit = iterator.next();
            if(nodeExit.value_exit==0){
                //回填
                list_nodeFour.get(nodeExit.index).factor4 = String.valueOf(index_back);
                iterator.remove();
            }

        }

    }
    public void doAexprBool(){//处理布尔表达式
        doTermBool();//这里的所有操作均基于产生式所形成的状态转换图
        //布尔表达式由||加上布尔项构成
        while(index<list_Node.size()&&(list_Node.get(index).word.equals("||"))){
            //遇到“||”,将下一四元式index回填至假出口中所有出口为0的所代表的四元式中
            backInput(list_false,list_nodeFour.size());
            index++;
            //为了维护list访问下标不超限
            doTermBool();
        }

    }
    //关于中间代码的注释：
    //布尔表达式中间代码生成分为真出口链和假出口链，每做一次布尔表达式之前都需要将两个链表清空，不能影响之前的
    //布尔表达式结果，布尔表达式结束，例如当if（）中调用布尔表达式时需要及时将布尔表达式所产生的两个链表记录之，
    //这是因为if语句中的多条语句也会调用布尔表达式，同时更新，新的真假出口链。
    public void doBoolean(){
        list_entryInfo.add("进入布尔表达式");
        list_false.clear();
        list_true.clear();
        doAexprBool();
//        if(haveleft) {
//            //一种特殊情况，本来的成对（）能通过程序检测，但是由于list走到底退出了，故而没法检测是否成对
//            System.out.println("末尾缺少）");
//        }
        list_entryInfo.add("退出布尔表达式");
    }
    public void doTermBool(){//处理项
        doFactorBool();
        while(index<list_Node.size()&&(list_Node.get(index).word.equals("&&"))){
            //进入循环代表"*或者/"已经遍历过了；
            //遇到&&回填真出口
            backInput(list_true,list_nodeFour.size());
            index++;
            doFactorBool();

        }

    }
    public boolean havaOperation(int index){
        //判断是否为算数表达式可以用算数表达式类实现，关键判断布尔因子后是算数表达式还是关系表达式
        //方法：通过判断到&&或者||之前是否含有比较运算符
        boolean hava_opration = false;
        for(int i=index;i<list_Node.size();i++){
            //若布尔因子中含有比较符，则为关系表达式
            if(set_compare.contains(list_Node.get(i).value)){
                hava_opration = true;
            }
            if(list_Node.get(i).word.equals("&&")||list_Node.get(i).word.equals("||")||list_Node.get(i).word.equals(";")){//如果是与或
                break;
            }
        }
        return hava_opration;
    }
    public void isCompare(){//是否是关系表达式
        list_entryInfo.add("进入关系表达式");
        //关系表达式左右有两个算数表达式，把两个算数表达式的结束求出来就可以简化为判断两个算数表达式是否正确了
        doArithmetic();
        String str1 = str_retArithmetic;
        String str_opration = list_Node.get(index).word;
        index +=1;//跳过运算符
        //第二个算数表达式
        doArithmetic();
        String str2 = str_retArithmetic;
        int nextFour = list_nodeFour.size();
        list_nodeFour.add(new NodeFour("j"+str_opration,str1,str2,"0"));
        list_nodeFour.add(new NodeFour("j","","","0"));
        //将真假出口写入链表中
        list_true.add(new NodeExit(nextFour,0));
        list_false.add(new NodeExit(nextFour+1,0));
        list_entryInfo.add("退出关系表达式");
    }
    //用在布尔表达式内
    public void isArithmetic2() {
        //可以判断是为正确的算数表达式了
        doArithmetic();
        //每当做完一次算数表达式stackResult中均会保存结果，布尔表达式直接用这个结果就行
        String str1 = str_retArithmetic;
        int nextFour = list_nodeFour.size();
        list_nodeFour.add(new NodeFour("jnz",str1,"","0"));
        list_nodeFour.add(new NodeFour("j","","","0"));
        //将真假出口写入链表中
        list_true.add(new NodeExit(nextFour,0));
        list_false.add(new NodeExit(nextFour+1,0));

    }
    //用在赋值语句中
    public void doFactorBool(){//处理因子
        //因子分为处理算数表达式，！布尔表达式，关系表达式
        if(index == list_Node.size()){
            return;
        }
        else if(list_Node.get(index).word.equals("!")){
            index++;
            doAexprBool();
            return;
        }
        else if(list_Node.get(index).word.equals("(")){
            index++;
            doAexprBool();
            if(index == list_Node.size()){
                return;
            }
            if(!list_Node.get(index).word.equals(")")){
                list_error.add("Boolean缺少一次）"+list_Node.get(index));
            }
            index++;
        }
        else if(havaOperation(index)){//若是之后的布尔项中含有比较符
            isCompare();
        }
        else{
            isArithmetic2();
        }
    }

    //3.赋值表达式--------------------------------------------------------------------------------------------------------------------

    public void doAexpr() {//处理布尔表达式
        doTerm();
        while (index < list_Node.size() && (list_Node.get(index).word.equals("="))) {
            index++;
            //为了维护list访问下标不超限
            doTerm();
        }

    }
    //判断表达式属于哪一个表达式//只看三种表达式，因为赋值表达式被我们分开为a=b=c=(1+2)，其中的=符号在循环中被遍历
    public int whichException(){//0,1,2分别代表着算数、关系、布尔
        int num_which = 0;
        for(int i=index;i<list_Node.size();i++){
            if(list_Node.get(i).word.equals("=")||list_Node.get(i).word.equals(";")||list_Node.get(i).word.equals(",")||list_Node.get(i).word.equals(")")){//只看到“=”之前是什么表达式,当存在多个语句是，赋值语句向后判断的停止条件还有遇到界符
                break;
            }
            else if(num_which==0 && set_compare.contains(list_Node.get(i).value)){//如果是关系运算符并   且标志还未算数运算符
                num_which = 1;
            }
            else if(num_which!=2 &&(list_Node.get(i).word.equals("||")||list_Node.get(i).word.equals("&&"))){
                num_which = 2;
                break;
            }

        }
        return num_which;
    }

    public void doTerm(){
        //因子分为处理算数表达式，！布尔表达式，关系表达式
        if(index == list_Node.size()){
            return ;
        }
        else {
//            if(index!=0 && list_Node.get(index-1).word.equals("(")){//如果表达式
//
//            }
            int num_which = whichException();
            switch (num_which){
                case 0:
                {
                    doArithmetic();
                }
                break;
                case 1:
                {
                    isCompare();
                }
                break;
                case 2:
                {
                    isBoolean();
                }
                break;

            }
        }
    }
    public void isBoolean(){
        int primLength = list_error.size();
        doBoolean();
        int didLength = list_error.size();
    }

    public int getIndex() {
        return index;
    }
    public void detectEvaluation(){
        int index_temp = index;
        if(list_Node.get(index).value!=700){
            list_error.add("赋值语句前不以标识符开头"+list_Node.get(index));
        }
        else {
            index++;
            if(!list_Node.get(index).word.equals("=")){
                list_error.add("赋值语句标识符后未接“=”"+list_Node.get(index));
            }
        }
        index = index_temp;

    }
    public void doEvaluation(){
        list_entryInfo.add("进入赋值语句");
        detectEvaluation();
        isDoingFactor = true;
        Node tempNode = list_Node.get(index);
        if(map_variable.containsKey(tempNode.word)){
            //获取第二层的变量表
            HashMap<String , TNode> mapTemp = map_variable.get(tempNode.word);
            String nestVarName = nestBlockName(mapTemp,nowBlockName);//通过本块名，遍历出父块或者本块的块名
            if(nestVarName.isEmpty()){
                list_mean_error.add("赋值语句左侧变量未定义就使用；"+tempNode);
                isDoingFactor = false;
            }
            else{
                match_type = mapTemp.get(nestVarName).type;
            }
        }
//      !!临时起意，由于那种多个等号的赋值语句很少出现，我们就不考虑他，不做doAexpr();
        //doAexpr();

        //此时index指向赋值变量
        int index_variable = index;
        //跳过赋值变量和等号；
        index+=2;
        //感觉写的越来越垃圾了，完全就是在应付检查，全是全局变量
        doTerm();
        //这里刚做完表达式的中间代码生成，此时的表达式最终结果就保存在stack.result中
        //执行赋值语句的中间代码生成
        list_nodeFour.add(new NodeFour("=",str_retArithmetic ,"",list_Node.get(index_variable).word));
        if(index==list_Node.size()){//末尾没有；、，
            list_error.add("index超限");
            return;
        }
        if(!list_Node.get(index).word.equals(";")&&!list_Node.get(index).word.equals(",")&&!list_Node.get(index).word.equals(")")){
            list_error.add("末尾不是界符或者右括号"+list_Node.get(index));

        }
        list_entryInfo.add("退出赋值语句");
        isDoingFactor = false;
    }
    //4.declaration----------------------------------------------------------------------------------------------------------------------
    public void doDeclaration(){
        list_entryInfo.add("进入声明语句");
        //若满足3个声明词
        //如果有const，跳过const当做为常量声明
        isConst = false;
        if(list_Node.get(index).word.equals("const")){
            index++;
            isConst = true;
            if(!nowBlockName.equals("0/")){
                list_mean_error.add("常量类型·只能放在所有块的外面"+list_Node.get(index-1));
            }
        }
        if(!set_declation.contains(list_Node.get(index).value)){
            list_error.add("非法声明类型："+list_Node.get(index));
        }
        //记住声明类型
        declaration_type = list_Node.get(index).word;
        index++;
        if(list_Node.get(index).value!=700){
            System.out.println("变量声明不是标识符:"+list_Node.get(index).word);
            list_error.add("变量声明不是标识符:"+list_Node.get(index).word);
        }
        //向表中添加变量或者常量
        addDeclaration();
        //此时index指向了变量或者常量
        if(index_main>0){//处于函数中
            map_temp.put(list_Node.get(index).word,"ss:[bp-"+(index_func_var*2)+"]");
            index_func_var++;
        }
        index++;//跳过了标识符;
        doAfter();
        if(!list_Node.get(index).word.equals(";")&&!list_Node.get(index).word.equals(",")){
            list_error.add("末尾不是界符");
            System.out.println("末尾不是界符");
        }
        isConst = false;
        list_entryInfo.add("结束声明语句");
    }
    public void addDeclaration(){
        String nodeName = list_Node.get(index).word;
        if(isConst){
            //重复定义常量
            if(map_variable.containsKey(nodeName)){
                list_mean_error.add("声明常量重复，在变量表中已存在"+list_Node.get(index));
            }
            else if(map_constant.containsKey(nodeName)){
                list_mean_error.add("重复声明常量："+list_Node.get(index));
            }
            else{
                map_constant.put(nodeName,new TNode(nodeName,declaration_type,nowBlockName));
            }
        }
        else{
            if(map_constant.containsKey(nodeName)){
                list_mean_error.add("声明变量重复，在常量表中已存在"+list_Node.get(index));
            }
            else if(map_function.containsKey(nodeName)){
                list_mean_error.add("声明变量重复，在函数表中已存在"+list_Node.get(index));
            }
            //在常量表中不存在
            else {
                //在变量表中不存在
                if(!map_variable.containsKey(nodeName)){
                    map_variable.put(nodeName,new HashMap<>());
                    map_variable.get(nodeName).put(nowBlockName,new TNode(nodeName,declaration_type,nowBlockName));
                }
                //在变量表中存在，将该变量放入表中
                else{
                    //存在同块的相同变量声明
                    if(map_variable.get(nodeName).containsKey(nowBlockName)){
                        list_mean_error.add("声明变量在同一块中重复声明"+list_Node.get(index));
                    }
                    else{
                        map_variable.get(nodeName).put(nowBlockName,new TNode(nodeName,declaration_type,nowBlockName));
                    }
                }
            }

        }


    }
    /**
     * @Author ryq
     * @Description //这个方法的作用是返回第二层变量表中离自己块最近的声明变量，如果发现自己和自己的父块中均未包含，则返回空字符串
     * @Date  2021/5/18
     * @Param [mapTemp, tempNowBlockName]
     * @return java.lang.String
     **/
    public String nestBlockName(HashMap<String , TNode> mapTemp,String tempNowBlockName){
        String ret_str = "";
        while(!tempNowBlockName.isEmpty()){
            if(mapTemp.containsKey(tempNowBlockName)){
                ret_str += tempNowBlockName;
                break;
            }
            else{
                tempNowBlockName = tempNowBlockName.substring(0,tempNowBlockName.length()-2);
            }
        }
        return ret_str;
    }
    public boolean isAllMatching(int begin,int end){
        for(int i=begin;i<end;i++){
            //只有当它是标识符或者float，int，char时才进行判断
            if(isVaule(list_Node.get(i))){
                Node tempNode = list_Node.get(i);
                //如果是标识符的话
                if(tempNode.value==700){
                    //需要判断它是否出现在符号表中，出现在符号表之后仍需要判断其是否是在本块或者本块的父块中声明
                    if(map_variable.containsKey(tempNode.word)){
                        //获取第二层的变量表
                        HashMap<String , TNode> mapTemp = map_variable.get(tempNode.word);
                        String nestVarName = nestBlockName(mapTemp,nowBlockName);//通过本块名，遍历出父块或者本块的块名
                        if(nestVarName.isEmpty()){
                            list_mean_error.add("声明语句中的赋值语句变量未定义就使用；"+tempNode);
                            return false;
                        }
                        else{
                            if(!mapTemp.get(nestVarName).type.equals(declaration_type)){
                                list_mean_error.add("声明语句中的赋值语句变量与声明类型不匹配；"+tempNode);
                                return false;
                            }
                        }
                    }
                    else if(map_constant.containsKey(tempNode.word)){
                        //常量的块名均为0/,区分是否存在于常量表的方法是直接从名称开始查
                        if(!map_constant.get(tempNode.word).type.equals(declaration_type)){
                            list_mean_error.add("声明语句中的赋值语句常量与声明类型不匹配；"+tempNode);
                            return false;
                        }

                    }
                    else {
                        list_mean_error.add("声明语句中的赋值语句变量未定义就使用；"+tempNode);
                        return false;
                    }
                }
                else{
//                    int_type = "400";
//                    char_type = "500";
//                    float_type = "800";
                    if((tempNode.value==500&&!declaration_type.equals("char"))||
                            (tempNode.value==400&&!declaration_type.equals("int"))||
                            (tempNode.value==800&&!declaration_type.equals("float"))){
                        list_mean_error.add("声明语句中的赋值语句数据类型不匹配"+tempNode);
                    }
                }
            }

        }
        return true;
    }
    public void addvaraAndConstToTable(int index){
        String nodeName = list_Node.get(index).word;
        //如果表中不含有该变量
        if(isConst){
            //重复定义常量
            if(map_variable.containsKey(nodeName)){
                list_mean_error.add("声明常重复，在变量表中已存在"+list_Node.get(index));
            }
            else if(map_constant.containsKey(nodeName)){
                list_mean_error.add("重复声明常量："+list_Node.get(index));
            }
            else{
                map_constant.put(nodeName,new TNode(nodeName,declaration_type,nowBlockName));
            }
        }
        else{
            if(map_constant.containsKey(nodeName)){
                list_mean_error.add("声明变量重复，在常量表中已存在"+list_Node.get(index));
            }
            //在常量表中不存在
            else {
                //在变量表中不存在
                if(!map_variable.containsKey(nodeName)){
                    map_variable.put(nodeName,new HashMap<>());
                    map_variable.get(nodeName).put(nowBlockName,new TNode(nodeName,declaration_type,nowBlockName));
                }
                //在变量表中存在，将该变量放入表中
                else{
                    //存在同块的相同变量声明
                    if(map_variable.get(nodeName).containsKey(nowBlockName)){
                        list_mean_error.add("声明变量在同一块中重复声明"+list_Node.get(index));
                    }
                    else{
                        map_variable.get(nodeName).put(nowBlockName,new TNode(nodeName,declaration_type,nowBlockName));
                    }
                }
            }
        }

    }
    public void doAfter(){//做声明语句之后的事情
        if(list_Node.get(index).word.equals(";")){
            return;
        }
        else if(list_Node.get(index).word.equals("=")){
            index--;
            int index_prim = index+2;//这个下标是指向=后的第一个node
            int index_behind = index;
            while(!list_Node.get(index_behind).word.equals("}")&&
                    !list_Node.get(index_behind).word.equals(";")&&!list_Node.get(index_behind).word.equals(",")&&
                    !list_Node.get(index_behind).word.equals(")")){
                index_behind++;
            }
            if(isAllMatching(index_prim,index_behind)){//如果全匹配
                if(index_main>0){//处于函数中
                    map_temp.put(list_Node.get(index).word,"ss:[bp-"+(index_func_var*2)+"]");
                    index_func_var++;
                }
                //addvaraAndConstToTable(index);
            }
            doEvaluation();
            if(index!=list_Node.size()&&list_Node.get(index).word.equals(",")){
                index++;
                if(index!=list_Node.size()&&list_Node.get(index).value==700){
                    //跳过声明的标识符
                    addDeclaration();//这个方法的唯一要求就是index指向标识符，且不为赋值语句，赋值语句的逻辑见上一个if分支
                    index++;
                    doAfter();
                }
                else{
                    list_error.add("声明词不是标识符："+list_Node.get(index));
                }
            }
        }
        else if(list_Node.get(index).word.equals(",")){//声明中含有多个声明语句语句
            index++;

            if(index!=list_Node.size()&&list_Node.get(index).value==700){
                //index指向标识符
                if(index_main>0){//处于函数中
                    map_temp.put(list_Node.get(index).word,"ss:[bp-"+(index_func_var*2)+"]");
                    index_func_var++;
                }
                addDeclaration();//这个方法的唯一要求就是index指向标识符，且不为赋值语句，赋值语句的逻辑见上一个if分支
                index++;
                doAfter();
            }
            else{
                list_error.add("声明词不是标识符："+list_Node.get(index));
            }
        }
        else {
            list_error.add("声明语句项错误："+list_Node.get(index));
        }
    }
    //5.代码块------------------------------------------------------------------------------------------------------------------

    public boolean doBlockBody(boolean isLoop){
        list_entryInfo.add("进入代码段");
        if(isLoop){
            if(list_Node.get(index).word.equals("{")){
                //更新新块的下标尾部和代码所处下标
                index_block++;
                nowBlockName += index_block+"/";
                index++;
                while(index < list_Node.size()&&!list_Node.get(index).word.equals("}")){
                    if(isEntryedLoop()){
                    }
                    else if(isEntryed()){
                    }
                    else{
                        System.out.println("非法开头语句:"+list_Node.get(index).word);
                        list_error.add("非法开头语句:"+list_Node.get(index));
                    }
                    //每一个语句的最后一定是;,需要手动跳过
                    //退出代码块时复原所处代码块名
                    if(list_Node.get(index).word.equals("}")){
                        nowBlockName = nowBlockName.substring(0,nowBlockName.length()-2);
                    }
                    index++;
                }
                if(index >= list_Node.size()){
                    System.out.println("方法体缺少右大括号}");
                    list_error.add("方法体缺少右大括号}");
                }
                else{

                }


            }
            else {

                if(isEntryedLoop()){
                }
                else if(isEntryed()){
                }
                else{
                    System.out.println("非法开头语句:"+list_Node.get(index).word);
                    list_error.add("非法开头语句:"+list_Node.get(index));
                }

            }
        }
        else {
            if(list_Node.get(index).word.equals("{")){
                index_block++;
                nowBlockName += index_block+"/";
                index++;
                while(index < list_Node.size()&&!list_Node.get(index).word.equals("}")){
                    if(isEntryed()){
                    }
                    else{
                        System.out.println("非法开头语句:"+list_Node.get(index).word);
                        list_error.add("非法开头语句:"+list_Node.get(index));
                    }
                    //退出代码块时复原所处代码块名
                    if(list_Node.get(index).word.equals("}")){
                        nowBlockName = nowBlockName.substring(0,nowBlockName.length()-2);
                    }
                    //每一个语句的最后一定是;,需要手动跳过
                    index++;
                }
                if(index == list_Node.size()){
                    list_error.add("方法体缺少右大括号}");
                }
            }
            else {
                if(isEntryed()){
                }
                else{
                    list_error.add("非法开头语句:"+list_Node.get(index));
                }
            }
        }
        list_entryInfo.add("离开代码段");
        return true;
    }
    //是否进入特定循环语句break;continue;
    public boolean isEntryedLoop(){
        boolean entryed = false;
        if(list_Node.get(index).word.equals("break")){
            entryed = true;
            list_entryInfo.add("进入break语句");
            index++;
            if(!list_Node.get(index).word.equals(";")){//
                System.out.println("break 末尾不为;");
            }
            list_entryInfo.add("离开break语句");
        }
        else if(list_Node.get(index).word.equals("continue")){
            entryed = true;
            list_entryInfo.add("进入continue语句");
            index++;
            if(!list_Node.get(index).word.equals(";")){//这说明return的是一个表达式
                System.out.println("continue 末尾不为;");
            }
            list_entryInfo.add("离开continue语句");
        }
        return entryed;
    }
    //是否进入普通语句
    public boolean isEntryed(){
        boolean entryed =  false;
        //0.return
        if(list_Node.get(index).word.equals("return")){
            entryed = true;
            list_entryInfo.add("进入return语句");
            index++;
            if(!list_Node.get(index).word.equals(";")){//这说明return的是一个表达式
                //直接将表达式当成布尔表达式处理，布尔内部会自行处理
                doArithmetic();
                list_nodeFour.add(new NodeFour("ret",str_retArithmetic,"",""));
            }
            else{
                list_nodeFour.add(new NodeFour("ret","","",""));
            }
            //为什么不用else,因为这是必须的操作，不能因为有上if而不进入下if
            if(!list_Node.get(index).word.equals(";")){
                list_error.add("return 末尾不为;"+list_Node.get(index));
            }
            list_entryInfo.add("离开return语句");
        }
        //
        else if(index+1>=list_Node.size()){
            list_error.add("index超过极限");;
        }
        //1.赋值语句
        else if(list_Node.get(index).value==700&&list_Node.get(index+1).word.equals("=")){
            entryed = true;
            doEvaluation();
        }
        //2.声明语句
        else if(set_declation.contains(list_Node.get(index).value)) {
            entryed = true;
            doDeclaration();

        }
        //3.if语句
        else if (list_Node.get(index).word.equals("if"))
        {
            entryed = true;
            doIf(false);
        }
        //4.while
        else if(list_Node.get(index).word.equals("while")){
            entryed = true;
            doWhile();
        }
        //5.for
        else if(list_Node.get(index).word.equals("for")){
            entryed = true;
            doFor();
        }
        //6.单个函数调用
        //这个函数的判断顺序也很重要，必须最后一个判断，因为会影响if（）这种
        else if(index+1<list_Node.size()&&list_Node.get(index).value==700 && list_Node.get(index+1).word.equals("(")) {
            entryed = true;
            doArithmetic();
        }
        //7.单独代码块出错
        else if(list_Node.get(index).word.equals("{")){
            entryed = true;
            doBlockBody(false);
        }
        return entryed;
    }

    // 6.if------------------------------------------------------------------------------------------------------------------
    public void doIf(boolean isLoop){
        list_entryInfo.add("进入if语句");
        //虽然我们是通过首词为if进来这个函数的，但是为了保持和以前的语法一致性，还是要检测是不是if
        if(!list_Node.get(index).word.equals("if")){
            list_error.add("首字不是if"+list_Node.get(index));
        }
        index++;

        //if后没有左括号直接错误处理
        if(!list_Node.get(index).word.equals("(")){
            System.out.println("缺失if左括号（");
            list_error.add("缺失if左括号（"+list_Node.get(index));
            index--;
        }
        index++;
        isBoolean();

        //做完if语句后的布尔表达式，此时存在两条出口，将这两条出口输出
        //listT中的此刻立即回填，其出口全部指向为即将产生的四元式，有一种很巧妙的情况，就是if{}中为空的时候
        //这样下一个四元式在我们想象中是否就不能指向if中了，而是指向else部分了，但是巧妙的是，if{}中主体结束后如果有else
        //会产生一条无条件跳转语句，相当于我们回填的就是该跳转语句
        //直接回填真出口链表
        backInput(list_true,list_nodeFour.size());
        //假出口只有遇到else或者是遇到if结束的时候才能回填呢，所以要记录下来
        ArrayList<NodeExit> listF = new ArrayList<>(list_false);

        System.out.println("假出口：");
        for(NodeExit nodeExit:listF){
            System.out.println(nodeExit);
        }
        if(!list_Node.get(index).word.equals(")")){
            System.out.println("缺失if右括号）");
            list_error.add("缺失if右括号)"+list_Node.get(index));
            index--;
        }
        index++;
        //if内代码块操作判断
        isBody(isLoop);
        //判断是否为}，如果是，需手动跳过，为什么呢？，因为如果是这里指向}，并且if后还存在else，于if内还未结束，所以需要手动使得index向前一步；
        if(index<list_Node.size()-1&&(list_Node.get(index).word.equals("}")||list_Node.get(index).word.equals(";"))&&list_Node.get(index+1).word.equals("else")){//&&list_Node.get(index+1).word.equals("else")
            if(list_Node.get(index).word.equals("}")){
                nowBlockName = nowBlockName.substring(0,nowBlockName.length()-2);
            }
            index++;
        }
        //因为在BlockBody中的含有{}的是
        //不管是否有else均要回填假出口链表，但是如果有else的话需要在做else中语句之前回填
        if(index<list_Node.size()&&list_Node.get(index).word.equals("else")){
            //神奇的操作发生了，如果存在else，此时需要有一条无条件跳转语句，跳过else部分
            list_noConditionTurn.add(new NodeExit(list_nodeFour.size(),0));
            list_nodeFour.add(new NodeFour("j","","","0"));
            //中间代码中遇到else回填假出口链表
            backInput(listF,list_nodeFour.size());
            index++;
            if(index<list_Node.size()&&list_Node.get(index).word.equals("if")){
                doIf(isLoop);
            }
            else {
                isBody(isLoop);
            }
            //当else结束后，其实就可以回填之前的if结束后的无条件跳转语句了，但是存在一种特殊情况
            //当else结束后，发现跳过；或者}后还是为else，此时就不回填，直到遇到没有连续嵌套的else才进行跳转哦
            if (index+2>=list_Node.size()||index+2<list_Node.size()&&!list_Node.get(index+2).word.equals("else")){
                backInput(list_noConditionTurn,list_nodeFour.size());
            }
        }
        else{
            backInput(listF,list_nodeFour.size());
        }

        list_entryInfo.add("结束if语句");
        //括号内判断是否为布尔表达式
    }
    public void isBody(boolean isLoop){
        doBlockBody(isLoop);
    }
    //7.Loop----------------------------------------------------------------------------------------------------------------------------

    public void doWhile(){//意为做while及其以内的内容，并不是do{}while()
        list_entryInfo.add("进入while语句");
        //跳过while
        index++;
        if(list_Node.get(index).word.equals("(")){
            index++;
        }
        else{
            list_error.add("while语句条件前缺失左括号(");

        }
        //while语句中需要记住while（）中的第一个语句，因为while语句结束后会首先无条件返回到判断语句开始重新判断
        int index_back_loop = list_nodeFour.size();
        doBoolean();
        //直接回填真出口链表
        backInput(list_true,list_nodeFour.size());
        //假出口只有遇到else或者是遇到if结束的时候才能回填呢，所以要记录下来
        ArrayList<NodeExit> listF = new ArrayList<>(list_false);

        //合法while语句
        if(list_Node.get(index).word.equals(")")){
            index++;
        }
        else {
            list_error.add("while条件后缺失右括号）");
        }
        doFuncBody();
        //当while结束,需要无条件跳转到while的判断语句
        list_nodeFour.add(new NodeFour("j","","",String.valueOf(index_back_loop)));
        //回填while假出口链表
        backInput(listF,list_nodeFour.size());
        list_entryInfo.add("结束while语句");
    }
    public void doFor(){
        list_entryInfo.add("进入for语句");
        //跳过for
        index++;
        if(list_Node.get(index).word.equals("(")){
            index++;
        }
        else{
            list_error.add("for语句条件前缺失左括号(");

        }
        //for语句的执行顺序for(E1,E2(判断条件),E3)，先E1,E2判断后无条件转入代码块中执行代码，执行完代码块后无条件转入E3，E3执行完后无条件执行E2
        //那么需要记住的四元式下标有index_E2,indexE3，并且记得代码块体执行完后记得回填for语句的假出口链

        //以前参数是用isParamFor()，但是发现这样不好进行中间代码的生成，故而将方法内容写入
        //for语句其实只要有两个;就能认为是格式正确
        //参数开始---------------------------------------------------------------
        if(!list_Node.get(index).word.equals(";")){
            doEvaluation();
        }
        if(list_Node.get(index).word.equals(";")){
            index++;
        }
        else{
            list_error.add("for的第一个参数后未接;");

        }
        int index_E2 = list_nodeFour.size();
        if(!list_Node.get(index).word.equals(";")){
            doBoolean();
            //无条件跳转到E3的所有操作结束后
            //做完了条件语句后要进行真假出口链均需要回填
            //真出口链在E3的左右操作结束后进行填
            //假出口链在代码块结束后进行回填
        }
        ArrayList<NodeExit> listF = new ArrayList<>(list_false);
        ArrayList<NodeExit> listT = new ArrayList<>(list_true);
        if(list_Node.get(index).word.equals(";")){
            index++;
        }
        else{
            list_error.add("for的第二个参数后未接;");
        }
        int index_E3 = list_nodeFour.size();
        if(!list_Node.get(index).word.equals(")")){
            doEvaluation();
            //该操作结束后需要无条件跳转到条件判断语句E2的开始四元式
            list_nodeFour.add(new NodeFour("j","","",index_E2+""));
            //接下来的四元式就是for语句为真的跳转
            backInput(listT,list_nodeFour.size());
        }
        //参数结束-----------------------------------------------------

        //合法for语句
        if(list_Node.get(index).word.equals(")")){
            index++;
        }
        else {
            list_error.add("for条件后缺失右括号）");
        }
        doFuncBody();
        //所有的语句结束，无条件跳转到E3进行++操作
        list_nodeFour.add(new NodeFour("j","","",index_E3+""));
        //回填for语句假链表出口
        backInput(listF,list_nodeFour.size());
        //-------------------------
        list_entryInfo.add("结束for语句");
    }

    public void doFuncBody(){
        doBlockBody(true);//循环体内肯定使用循环语句
    }
    //8.函数------------------------------------------------------------------------------------------------------------------
    public void addDeclarationFunc(int begin,int end){
        ArrayList<String> list_type = new ArrayList<>();
        boolean rightType =  true;
        int i = begin;
        while(i<end){
            if(set_declation.contains(list_Node.get(i).value)){
                list_type.add(list_Node.get(i).word);
                i++;
            }
            else{
                rightType = false;
                break;
            }
            if(list_Node.get(i).word.equals(",")){
                i++;
            }
        }
        if(rightType){
            //如果包含函数名
            String funcName = list_Node.get(begin-2).word;
            if(map_function.containsKey(funcName)){
                list_mean_error.add("函数表中已存在同名函数"+list_Node.get(index-2));
            }
            else{
                map_function.put(funcName,new FNode(funcName,declaration_type,list_type));
            }
        }
    }
    //函数声明
    public void doDeclarationFunc(){
        //使用这个东西
        //要么是函数声明int func(int);
        //要么是函数定义int func(int a){}
        if(!set_declarationFun.contains(list_Node.get(index).value)){
            list_error.add("非法函数声明类型");
        }
        declaration_type = list_Node.get(index).word;
        index++;//跳过声明类型
        if(list_Node.get(index).value==700 && list_Node.get(index+1).word.equals("(")){
            list_entryInfo.add("进行函数声明："+list_Node.get(index).word);
            index+=2;//跳过左括号
            int index_prim = index;
            doparam();
            int index_behind = index;
            addDeclarationFunc(index_prim,index_behind);
            if(index>=list_Node.size()||!list_Node.get(index).word.equals(")")){
                list_error.add("函数声明参数后不为）:"+list_Node.get(index));
            }
            //此时index指向)
            if(list_Node.get(index).word.equals(")"))
                index++;
            if(!list_Node.get(index).word.equals(";")){
                list_error.add("函数声明语句后缺失;"+list_Node.get(index));
            }

        }
        list_entryInfo.add("离开函数声明");

    }
    /**
     * @Author ryq
     * @Description //本方法作用是检测函数定义参数是否与函数声明时的参数类型一致，若不一致，需记录其错误；
     * @Date  2021/5/18
     * @Param [begin, end]
     * @return void
     **/
    public void detectDeclarationFuncBodyParam(int begin,int end){
        ArrayList<String> list_type = new ArrayList<>();
        for (int i = begin; i < end; ) {
            list_type.add(list_Node.get(i).word);
            String type = list_Node.get(i).word;
            String name = list_Node.get(i+1).word;
            if(map_constant.containsKey(name)){
                list_mean_error.add("声明变量重复，在常量表中已存在"+list_Node.get(index));
            }
            //在常量表中不存在
            else {
                //在变量表中不存在
                if(!map_variable.containsKey(name)){
                    map_variable.put(name,new HashMap<>());
                    map_variable.get(name).put(nowBlockName+(index_block+1)+"/",new TNode(name,type,nowBlockName+(index_block+1)+"/"));
                }
                //在变量表中存在，将该变量放入表中
                else{
                        map_variable.get(name).put(nowBlockName+(index_block+1)+"/",new TNode(name,type,nowBlockName+(index_block+1)+"/"));

                }
            }
            i+=3;
        }
        String funcName = list_Node.get(begin-2).word;//这个下标指向函数名
        FNode fNode = map_function.get(funcName);
        if(fNode.length!=list_type.size()){
            list_mean_error.add("函数声明与函数定义参数个数不一致,声明个数：+"+fNode.length+
                    ",定义个数："+list_type.size()+list_Node.get(index-2));
        }
        else{
            for (int i = 0; i < list_type.size(); i++) {
                if(!fNode.list_type.get(i).equals(list_type.get(i))){
                    list_mean_error.add("函数声明与函数定义参数类型不一致,声明类型：+"+fNode.list_type.get(i)+
                            ",定义类型："+list_type.get(i)+list_Node.get(index-2));
                    break;
                }

            }
        }
    }
    //函数定义
    public void doDeclarationFuncBody(){
        //生成目标代码需要，使用哈希表存储，键为变量名，值为对应栈空间,例如(在参数只有一个的情况下) a->ss:[bp+2]，而函数内的局部变量则使用bp的上空间
        //例如result -> ss:[bp+2]
        //初始化一个临时哈希表用于加入最终的哈希表中
        map_temp = new HashMap<>();
        //记录接下来的第几个局部变量，方便映射到栈空间
        index_func_var =1;
        if(!set_declarationFun.contains(list_Node.get(index).value)){
            list_error.add("非法函数定义类型");
        }
        index++;
        //此时index指向标识符
        list_nodeFour.add(new NodeFour(list_Node.get(index).word,"","",""));
        int size = 0;
        String str_funName =list_Node.get(index).word;

        if(list_Node.get(index).value==700 && list_Node.get(index+1).word.equals("(")){
            list_entryInfo.add("进行函数定义："+list_Node.get(index).word);


            if(!map_function.containsKey(list_Node.get(index).word)){
                list_mean_error.add("函数未先进行声明"+list_Node.get(index));
            }
            else{
                size = map_function.get(list_Node.get(index).word).list_type.size();

            }
            index+=2;//跳过左括号
            int index_prim = index;//这个下标指向（后一位，
            doparamBody(size);
            System.out.println("size:"+size);
            int index_behind = index;
            //检测函数是否已经声明
            if(map_function.containsKey(list_Node.get(index_prim-1).word)){
                //检测参数与声明是否一致
                detectDeclarationFuncBodyParam(index_prim,index_behind);
            }

            if(index>=list_Node.size()||!list_Node.get(index).word.equals(")")){
                list_error.add("函数定义参数后不为):"+list_Node.get(index-2).word);
            }
            //此时index指向)
            index++;
            if(!list_Node.get(index).word.equals("{")){
                list_error.add("函数定义语句后缺失{");
            }
            doBlockBody(false);
        }
        else{
            list_error.add("函数定义不是声标识符加右括号"+list_Node.get(index));
        }
        //做完了需要将map_temp写入mapFuncVar内
        //把局部变量个数写入map_temp
        map_temp.put("_times",(index_func_var-1)+"");
        map_funcVar.put(str_funName,map_temp);
        list_nodeFour.add(new NodeFour("ret","","",""));
        list_entryInfo.add("离开函数定义");
    }
    //函数定义参数
    public void doparamBody(int size){
        size++;
        //获取函数声明表的参数长度
        while(index<list_Node.size()&&!list_Node.get(index).word.equals(")")&&!list_Node.get(index).word.equals("}")){//当缺少右括号时应能通过}跳出循环
            if(set_declation.contains(list_Node.get(index).value) &&!list_Node.get(index).word.equals("void")&&list_Node.get(index+1).value==700){
                //是这样的啊，前面的参数先入栈，那么他的值就为ss:[bp+size*2];
                int num = size*2;
                size--;
                map_temp.put(list_Node.get(index+1).word,"ss:[bp+"+num+"]");
                map_variable.put(list_Node.get(index+1).word,new HashMap<>());
            }
            else {
                list_error.add("非法的函定义(不是声明类型加标识符):"+list_Node.get(index).word);
            }
            index+=2;
            if(list_Node.get(index).word.equals(",")){
                index++;
            }
        }

    }
    //函数声明参数
    public void doparam(){
        while(index<list_Node.size()&&!list_Node.get(index).word.equals(")")&&!list_Node.get(index).word.equals(";")){//当缺少右括号时应能通过;跳出循环
            //如果不是合法的参数类型
            if(!set_declation.contains(list_Node.get(index).value)){
                list_error.add("非法的函数声明参数类型:"+list_Node.get(index));
            }
            index++;
            if(index<list_Node.size()&&list_Node.get(index).word.equals(",")){
                index++;
            }
        }

    }
    //9.总控程序----------------------------------------------------------------------------------------------------------------------
    public void testTotal(){
        while(index< list_Node.size()){
            //做main语句
            if(list_Node.get(index).word.equals("main")){
                list_entryInfo.add("进入main方法");
                index++;
                if(list_Node.get(index).word.equals("(")){
                    index +=1;
                }
                else{
                    list_error.add("main函数缺少左括号（"+list_Node.get(index));
                }
                if (!list_Node.get(index+1).word.equals(")")){
                    index++;
                }
                else{
                    list_error.add("main函数缺少右括号）"+list_Node.get(index));
                }
                if(!list_Node.get(index).word.equals("{")){
                    list_error.add("main函数缺少{"+list_Node.get(index));
                }
                doBlockBody(false);
                //做完这个方法，index将指向main（）{}中}
                list_entryInfo.add("退出main");
                if(index>=list_Node.size()){
                    break;
                }
            }
            //做声明语句
            else if(set_declation.contains(list_Node.get(index).value)){
                //做函数相关
                if(index+2<list_Node.size()&&list_Node.get(index+2).word.equals("(")){
                    //有这种情况，函数内没有参数，那么函数定义与声明的参数内容是一样的
                    if(index+3<list_Node.size()&&list_Node.get(index+3).word.equals(")")){
                        //做函数声明
                        if(index+4<list_Node.size()&&list_Node.get(index+4).word.equals(";")){
                            doDeclarationFunc();
                        }
                        //做函数定义
                        else{
                            index_main++;
                            //当且仅当第一次进入其他函数定义时，同时也是main函数结束时，向四元式链表内插入sys四元式
                            if(index_main==1){
                                list_nodeFour.add(new NodeFour("sys","","",""));
                            }
                            doDeclarationFuncBody();
                        }
                    }
                    //做函数定义
                    else if(index+4<list_Node.size()&&list_Node.get(index+4).value==700){
                        index_main++;
                        //当且仅当第一次进入其他函数定义时，同时也是main函数结束时，向四元式链表内插入sys四元式
                        if(index_main==1){
                            list_nodeFour.add(new NodeFour("sys","","",""));

                        }
                        doDeclarationFuncBody();
                    }
                    //做函数声明
                    else {
                        doDeclarationFunc();
                    }
                }
                //变量常量声明
                else{
                    //跳过const
                    doDeclaration();
                }
            }
            else{
                list_entryInfo.add("不是声明语句或者main函数或者普通函数");
            }
            index++;
        }
    }
    //返回生成的四元式方法
    public ArrayList<NodeFour> creatFour(){
        list_nodeFour.add(new NodeFour("main","","",""));
        testTotal();
        if(index_main==0){
            list_nodeFour.add(new NodeFour("sys","","",""));
        }
        return list_nodeFour;
    }
    public static void main(String[] args) {
        System.out.println("测试total。。。。。。");
        String path_file = Tool.ChangeAbsolutePath("testMeaning");
        String code = ReadFile.readFile(path_file);
        Lexical lexical = new Lexical(code);
        lexical.LexicalAnal();
        ArrayList<String> list = lexical.getResultList();
        BlockBody blockBody = new BlockBody(GrammerTool.changeToNode(list),true,0);
//        blockBody.testTotal();
        blockBody.testTotal();
//        System.out.println("常量表");
//        blockBody.map_constant.forEach((k,v) ->{
//            System.out.println(k+","+v);
//        });
        System.out.println("变量表：");
        blockBody.map_variable.forEach((k,v) ->{
            System.out.println(k+","+v);
        });
//
//        System.out.println("函数表：");
//        blockBody.map_function.forEach((k,v) ->{
//            System.out.println(k+","+v);
//        });
        System.out.println("meaningError信息列表：");
        for(String iterm: blockBody.list_mean_error){
            System.out.println(iterm);
        }

//        System.out.println("error信息列表：");
//        for(String iterm: blockBody.list_error){
//            System.out.println(iterm);
//        }
//        System.out.println("entry信息列表：");
//        for(String iterm: blockBody.list_entryInfo){
//            System.out.println(iterm);
//        }
//        for(String str:list){
//            System.out.println(str);
//        }
//        System.out.println("四元式：");
//        ArrayList<NodeFour> list_nodeFour = blockBody.creatFour();
//        for (int i = 0; i < list_nodeFour.size(); i++) {
//            System.out.println(i+"\t"+list_nodeFour.get(i));
//        }
//        System.out.println("全局变量表");
//        blockBody.map_variable.forEach((k,v)->{
//            System.out.println(k);
//            System.out.println(v);
//        });
//        System.out.println("局部变量表");
//        blockBody.map_funcVar.forEach((k,v)->{
//            System.out.println(k);
//            System.out.println(v);
//        });
//        System.out.println(list.size() +":"+blockBody.index);
    }
}


