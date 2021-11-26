package RYQ.grammar;

import java.util.ArrayList;

public class Node{
    String word;
    int value;//代表着该词的种别码
    int row;
    int column;
    @Override
    public String toString() {
        return "Node{" +
                "word='" + word + '\'' +
                ", value=" + value +
                ", row=" + row +
                ", column=" + column +
                '}';
    }


    public Node(){
        word = "default";
        value = -1;
        row = -1;
        column = -1;
    }
    public Node(int value, String word,int row,int column){
        this.word = word;
        this.value = value;
        this.row = row;
        this.column = column;
    }

}
class TNode{
    String name;
    String type;
    String value;
    String region;

    public TNode(String name, String type, String value, String region) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.region = region;
    }
    public TNode(String name, String type, String region) {
        this.name = name;
        this.type = type;
        this.value = "";//没有值为空；
        this.region = region;
    }
    @Override
    public String toString() {
        return "TNode{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
class FNode{
    String funcType;
    String fname;
    ArrayList<String> list_type;
    int length;

    @Override
    public String toString() {
        return "FNode{" +
                "funcType='" + funcType + '\'' +
                ", fname='" + fname + '\'' +
                ", list_type=" + list_type +
                ", length=" + length +
                '}';
    }

    public FNode(String fname, String funcType, ArrayList<String> list_type) {
        this.fname = fname;
        this.list_type = list_type;
        this.length = list_type.size();
        this.funcType = funcType;
    }
}
class NodeFour{
    String factor1;
    String factor2;
    String factor3;
    String factor4;

    public NodeFour(String factor1, String factor2, String factor3, String factor4) {
        this.factor1 = factor1;
        this.factor2 = factor2;
        this.factor3 = factor3;
        this.factor4 = factor4;
    }

    public NodeFour(){
        factor1 = "";
        factor2 = "";
        factor3 = "";
        factor4 = "";
    }
    @Override
    public String toString() {
        return "("+factor1+", "+factor2+", "+factor3+", "+factor4+")";
    }
}
class NodeExit{
    int index;
    int value_exit;

    public NodeExit(int index, int value_exit) {
        this.index = index;
        this.value_exit = value_exit;
    }

    @Override
    public String toString() {
        return "NodeExit{" +
                "index=" + index +
                ", value_exit=" + value_exit +
                '}';
    }
}
