package RYQ.grammar;

import java.util.ArrayList;

public class GrammerTool {
    public static ArrayList<Node> changeToNode(ArrayList<String> list){
        ArrayList<Node> list_temp = new ArrayList();
        for(String str : list){
            int index = str.indexOf("[");
            String strpre =  str.substring(0,index);
            String strbehind = str.substring(index+1,str.length());
            String[] strs1= strpre.split("\\s+");
            String[] strs2 = strbehind.split(":");
            list_temp.add(new Node(Integer.parseInt(strs1[1]),strs1[0],Integer.parseInt(strs2[0]),Integer.parseInt(strs2[1])));
        }
        System.out.println(list_temp.size());
        return list_temp;
    }
}
