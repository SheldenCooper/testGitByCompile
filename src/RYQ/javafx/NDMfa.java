package RYQ.javafx;

import java.lang.reflect.Array;
import java.util.*;

public class NDMfa {
    private ArrayList<ArrayList<Integer>> list_TandNotT  = new ArrayList<>();
    private SubNFA sub_result;
    //正则表达式中的所有变量
    private List<String> list_value;
    ArrayList<String> list_NFA;
    ArrayList<String> list_DFA;
    String str_zhengze;
    public NDMfa(){
        list_NFA =new ArrayList<>();
        list_DFA = new ArrayList<>();
    }
    public boolean isLegal(String regular){
        char[] chars = regular.toCharArray();
        if(!(chars[0]=='('||Character.isLetter(chars[0])||Character.isDigit(chars[0]))){
            return false;
        }
        int flag =0;
        if(chars[0]=='('){
            flag++;
        }

        for(int i=1; i<chars.length-1;i++){
            if(chars[i]=='('){
                flag++;
            }
            else if(chars[i] == ')'){
                flag--;
                if(flag!=0){
                    return false;//括号不成对匹配
                }
            }
            else if(chars[i]=='|'){
                //前一个字符满足要求
                if(chars[i-1]=='*'||chars[i-1]==')'||Character.isLetter(chars[i-1])||Character.isDigit(chars[i-1])){
                    //后一个字符满足
                    if((i<chars.length-1)&&(chars[i+1]=='('||Character.isLetter(chars[i+1])||Character.isDigit(chars[i+1])))
                    {

                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
            else if(chars[i]=='*'){
                //前一个字符满足要求
                if(chars[i-1]==')'||Character.isLetter(chars[i-1])||Character.isDigit(chars[i-1])){

                    if((i<chars.length-1)&&(chars[i+1]=='|'||chars[i+1]=='('||chars[i+1]=='）'||Character.isLetter(chars[i+1])||Character.isDigit(chars[i+1])))
                    {

                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
        }
        //最后一个字符的合法情况
        if(chars[chars.length-1]==')'||Character.isLetter(chars[chars.length-1])||Character.isDigit(chars[chars.length-1])){
            if(chars[chars.length-1]==')' ){
                if(flag!=1){
                    return false;

                }
                else {
                    return true;
                }
            }
            else{
                return true;
            }
        }
        return false;

    }

    //内部类避免同包内重名
    //仅用作记录子NFA的起始和结束点
    public class SubNFA{
        int start;int end;
        public SubNFA(int start, int end){
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }
    public class NodeNFA extends SubNFA{
        private String name;
        public NodeNFA(int start, int end, String name) {
            super(start, end);
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "NodeNFA{" +
                    "start=" + super.getStart() +
                    ", end=" + super.getEnd() +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
    public String addPoit(String regular,Set<String> character){
        if(regular.length()==0){
            return "";
        }
        StringBuffer buffer = new StringBuffer();//做新字符串容器用
        for(int i=0;i<regular.length()-1;i++){
            buffer.append(regular.charAt(i));
            //是操作数
            if(character.contains(String.valueOf(regular.charAt(i)))){
                if(regular.charAt(i+1)=='('){
                    buffer.append(".");
                }
                else if(character.contains(String.valueOf(regular.charAt(i+1)))){
                    buffer.append(".");
                }
            }
            if(regular.charAt(i)=='*'){
                //是操作数
                if(character.contains(String.valueOf(regular.charAt(i+1)))){
                    buffer.append(".");
                }
                else if(regular.charAt(i+1)=='('){
                    buffer.append(".");
                }
            }
            if(regular.charAt(i)==')'){
                if(character.contains(String.valueOf(regular.charAt(i+1)))){
                    buffer.append(".");
                }
                else if(regular.charAt(i)=='('){
                    buffer.append(".");
                }
            }
        }
        buffer.append(regular.charAt(regular.length()-1));
        return buffer.toString();
    }
    //将正则表达式转换为逆波兰式方便NFA化
    public String inverse(String regular, Set<String> character){
        //正则表达式需要一个运算符优先级表
        Map<String,Integer> map_priority = new HashMap<>();
        map_priority.put("#",-1);
        map_priority.put("|",1);
        map_priority.put(".",2);
        map_priority.put("*",3);
        //存放正则表达式的操作数
        Deque<String> stack_value = new LinkedList<>();
        //存放临时运算符
        Deque<String> stack_operation = new LinkedList<>();
        //需要这个#的逻辑是因为栈每次需要与栈顶元素比较，为了保持stack_operation靠近栈顶优先级高，
        //遇到比栈顶小或者等于的元素，则operation栈出栈到value栈，直到满足递增栈，由于“#”的优先级最低，。
        //所以“#”不可能出栈。
        stack_operation.offer("#");
        String str_top;//
        for (int i = 0; i < regular.length(); i++) {
            String str =  String.valueOf(regular.charAt(i));

            if(character.contains(str)){
                stack_value.push(str);
            }
            else{
                if(str.equals("(")){
                    stack_operation.push(str);

                }
                else if(str.equals(")")){
                    //将括号内的操作符全体出栈

                    while(true){
                        str_top = stack_operation.pop();
                        if(str_top.equals("(")){
                            break;
                        }
                        else {
                            stack_value.push(str_top);
                        }

                    }
                }
                else{
                    if(stack_operation.peek().equals("(")){
                        stack_operation.push(str);
                    }
                    else{
                        while(!(stack_operation.peek().equals("("))&&map_priority.get(str)<=map_priority.get(stack_operation.peek())){

                            stack_value.push(stack_operation.pop());
                        }
                        stack_operation.push(str);
                    }
                }

            }
        }
        //符号栈清空
        while(!stack_operation.isEmpty()){
            stack_value.push(stack_operation.poll());
        }
        stack_value.poll();//将没有用了的“#”出栈
        StringBuffer buffer = new StringBuffer();
        //逆波兰式是value栈出栈顺序的逆序
        while(!stack_value.isEmpty()){
            buffer.append(stack_value.pop());
        }
        return buffer.reverse().toString();

    }
    public ArrayList<NodeNFA> getNFA(String regular) {
        //记录每一个操作数（a、b）
        Set<String> character = new HashSet<>();
        list_value = new ArrayList<>();
        for (int i = 0; i < regular.length(); i++) {
            char c = regular.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c)) {
                if(!character.contains(String.valueOf(c))){
                    character.add(String.valueOf(c));
                    list_value.add(String.valueOf(c));
                }

            }
        }
        String regular_opint = addPoit(regular, character);
        System.out.println(regular_opint);
        //调用方法改造正则表达式，在存在‘乘’操作例如a，b出加入点‘.’
        String regular_invers = inverse(regular_opint, character);
        str_zhengze = regular_invers;
        System.out.println(regular_invers);
        //暂存子NFA的栈
        Deque<SubNFA> stack_sub = new LinkedList<>();
        //用于记录NFA中每一个节点
        ArrayList<NodeNFA> list_NFA = new ArrayList<>();
        //NFA节点名用index表示
        int index = 0;
        for (int i = 0; i < regular_invers.length(); i++) {
            String str = String.valueOf(regular_invers.charAt(i));
            if (character.contains(str)) {
                list_NFA.add(new NodeNFA(index, index + 1, str));
                NodeNFA nfa = new NodeNFA(index, index + 1, str);
                stack_sub.push(nfa);
                index += 2;

            } else {
                //分三种情况讨论，分别为“|.*”
                //|
                if (str.equals("|")) {
                    SubNFA sub2 = stack_sub.pop();
                    SubNFA sub1 = stack_sub.pop();
                    list_NFA.add(new NodeNFA(index, sub1.getStart(), "#"));
                    list_NFA.add(new NodeNFA(index, sub2.getStart(), "#"));
                    list_NFA.add(new NodeNFA(sub1.getEnd(), index + 1, "#"));
                    list_NFA.add(new NodeNFA(sub2.getEnd(), index + 1, "#"));
                    //将新的子NFA入栈
                    stack_sub.push(new SubNFA(index, index + 1));
                    index += 2;
                } else if (str.equals("*")) {
                    SubNFA sub = stack_sub.pop();
                    list_NFA.add(new NodeNFA(index, sub.getStart(), "#"));
                    list_NFA.add(new NodeNFA(index, index + 1, "#"));
                    list_NFA.add(new NodeNFA(sub.getEnd(), sub.getStart(), "#"));
                    list_NFA.add(new NodeNFA(sub.getEnd(), index + 1, "#"));
                    stack_sub.push(new SubNFA(index, index + 1));
                    index += 2;

                } else if (str.equals(".")) {
                    SubNFA sub2 = stack_sub.pop();
                    SubNFA sub1 = stack_sub.pop();
                    list_NFA.add(new NodeNFA(sub1.getEnd(), sub2.getStart(), "#"));
                    stack_sub.push(new SubNFA(sub1.getStart(), sub2.getEnd()));
                    //没有新节点，index不变

                }


            }
        }
        //将NFA的起点和终点保存
        sub_result = stack_sub.pop();
        return list_NFA;
    }
    public String[][] getDFA(ArrayList<NodeNFA> list_NFA){
        //首先建立原始DFA表,行用list存，这是因为不知道具体有多少行，每一行中用ArrayList<Integer>[]数组
        ArrayList<ArrayList<Integer>[]> list_prim = new ArrayList<>();
        String[] strs_temp;
        int size=1,index=0;//size表示此时有多少行，index表示，访问到第几行
        //子状态集用list保存，由于需要保存每种状态是否出现过（大set），
        //每种状态采用临时set去重，然后set转list，最后list排序存入大set
        Set<ArrayList<Integer>> set_state = new HashSet<>();
        //声明三个临时变量
        ArrayList<Integer> list_temp;
        Set<Integer> set_temp;
        ArrayList<Integer>[] list_temps;
        //首先初始化首行首列；的通过“衣服c罗”得到的状态列表

        //list_temp = new ArrayList<>();
        //循环所有NFA节点，找到对应状态集
        set_temp = new HashSet<>();
        int start = sub_result.getStart(),end = sub_result.getEnd();
        set_temp.add(start);
        Deque<Integer> stack_temp = new LinkedList<>();
        stack_temp.push(start);

        //找出初始状态集的所有节点值
        while(!stack_temp.isEmpty()){
            int num_visit = stack_temp.pop();
            for(NodeNFA NFA:list_NFA){
                //满足从起点出发并且通过空到达的节点，添加进集合，并且紧接着对新找到的节点同样进行相同的步骤
                if(NFA.getStart()==num_visit && NFA.getName().equals("#")){
                    if(!set_temp.contains(NFA.getEnd())){
                        set_temp.add(NFA.getEnd());
                        stack_temp.push(NFA.getEnd());
                    }
                }
            }
        }
        for(NodeNFA NFA:list_NFA){
            System.out.println(NFA);
        }
        for(int num:set_temp){
            System.out.println(num);
        }
        //转list并排序
        list_temp = new ArrayList<>(set_temp);
        list_temp.sort((x,y)->{
            if(x>y){
                return 1;
            }
            else if(x==y){
                return 0;
            }
            else{
                return -1;
            }
        });
        set_state.add(list_temp);
        //这个为新状态栈，若该栈为空，则退出；
        //为了维持命名时先进县命名，则使用队列
        Queue<ArrayList<Integer>> stack_state = new LinkedList<>();
        stack_state.offer(list_temp);

        int index_column=1;//记录每一行中的列下标，用作数组用
        while(!stack_state.isEmpty()){
            ArrayList<Integer> list_pop = stack_state.poll();
            list_temps = new ArrayList[list_value.size()+1];//如两个变量有三列，第一列为初始祖状态集合
            list_temps[0] = list_pop;
            //对每一个变量进行遍历
            index_column = 1;
            //!!!正确的做法，从子状态集中找到对应元素如a能到达的节点，组成新集合list_new ,在集合中再通过“衣服c罗”能找到的所有节点最后加到list_new中
            //即为从元素a能走到的新元素，
            //证明：因为原集合中包含了所有能从“衣服c罗”走到的节点（例如3-》4,通过衣服c罗走到的话，实际上3对于找到新的节点没有意义
            // ，因为总会从4找到新的节点，若形成闭环的话，同样会从4的新节点的闭环找到），故而刚开始只需要找到元素a所对应的节点，即为新节点
            for(String value:list_value){//对每一种元素进行遍历abc
                set_temp = new HashSet<>();
                //对状态集中每一个遍历全部NFA节点，找到满足条件新节点
                for(int num:list_pop){
                    for(NodeNFA NFA:list_NFA){
                        if(NFA.getStart()==num&&NFA.getName().equals(value)){
                            set_temp.add(NFA.getEnd());
                        }
                    }
                }
                //新节点通过衣服c罗找到的新节点,此时可以通过栈遍历所有的衣服c罗条件
                stack_temp = new LinkedList<>(set_temp);
                while(!stack_temp.isEmpty()){
                    int num_visit = stack_temp.pop();
                    for(NodeNFA NFA:list_NFA){
                        //满足从起点出发并且通过空到达的节点，添加进集合，并且紧接着对新找到的节点同样进行相同的步骤
                        if(NFA.getStart()==num_visit && NFA.getName().equals("#")){
                            if(!set_temp.contains(NFA.getEnd())){
                                set_temp.add(NFA.getEnd());
                                stack_temp.push(NFA.getEnd());
                            }
                        }
                    }
                }
                //转list并排序
                list_temp = new ArrayList<>(set_temp);
                list_temp.sort((x,y)->{
                    if(x>y){
                        return 1;
                    }
                    else if(x==y){
                        return 0;
                    }
                    else{
                        return -1;
                    }
                });
                //若这个list是新状态集
                if(!set_state.contains(list_temp)){
                    stack_state.offer(list_temp);
                    set_state.add(list_temp);
                }
                list_temps[index_column] = list_temp;
                index_column++;
            }
            list_prim.add(list_temps);

        }
        //初始化终结组与非终结组元素表
        list_TandNotT.add(new ArrayList<>());//下标0为终结符
        list_TandNotT.add(new ArrayList<>());//1为非终结符
        //列表转字母表示
        Map<ArrayList<Integer>,Integer> map_change = new HashMap<>();
        index = 0;
        for(ArrayList<Integer>[] list11:list_prim){
            //检测是否新节点中包含NFA中的结束节点，若包含，将其归类为终止节点类
            boolean is_terminate = false;
            for(int num:list11[0]){
                if(num==sub_result.getEnd()){
                    is_terminate = true;
                    break;
                }
            }
            if(is_terminate){//将其加入终结节点list
                list_TandNotT.get(0).add(index);
            }
            else{
                list_TandNotT.get(1).add(index);
            }
            map_change.put(list11[0],index);
            index++;
        }
        int[][] Dstr_DFA = new int[list_prim.size()][list_prim.get(0).length];
        for(int i=0;i<list_prim.size();i++){
            String str_temp="";
            for(int j = 0;j<list_prim.get(0).length;j++){
                str_temp = str_temp+(map_change.get(list_prim.get(i)[j]));
                if(j!=list_prim.get(0).length-1){
                    str_temp+=",";
                }

            }
            list_DFA.add(new String(str_temp));
        }


        return new String[1][1];
    }
    //这个类不严谨，但是因为特殊，如果产生了新的组合，那么他的size一定会改变
    public boolean isSameDlist(ArrayList<ArrayList<Integer>> Dlist1,ArrayList<ArrayList<Integer>> Dlist2){
        if(Dlist1.size()!=Dlist2.size()){
            return false;
        }
        else{
            return true;
        }

    }
//    public static int[][] getMFA(int[][] DFA){
//        ArrayList<ArrayList<Integer>> Dlist = new ArrayList<>(list_TandNotT);
//        while(true){
//            ArrayList<ArrayList<Integer>> Dlist_new = new ArrayList<>();
//            for(ArrayList<Integer> list_temp :Dlist){
//                if()
//            }
//
//        }
//    }
    public void doNDMFA(){
        ArrayList<NodeNFA> list_nfa = getNFA("(a|b)*baa");
        System.out.println("NFA");
        for (NodeNFA nodeNFA:list_nfa){
            list_NFA.add("["+nodeNFA.start+","+nodeNFA.name+","+nodeNFA.end+"]");
        }
        System.out.println("NFA");
        for(String str:list_NFA){
            System.out.println(str);
        }
        getDFA(list_nfa);
        System.out.println("DFA");
        for (String str:list_DFA){
            System.out.println(str);
        }
        System.out.println("逆序后正则表达式"+str_zhengze);


    }
    public static void main(String[] args) {
        NDMfa ndMfa = new NDMfa();
        ndMfa.doNDMFA();

    }
    
}
