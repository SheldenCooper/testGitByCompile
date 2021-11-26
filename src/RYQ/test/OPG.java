package RYQ.test;

import java.io.*;
import java.util.*;

public class OPG {
    public void start() {
        OPG opg= new OPG();
        opg.OPGanalysis("Lex.tys");
    }
    String s="";
	private int  N =100;
	
	//文法
	private Map<String, String> grammar = new HashMap<String, String>();
	
	//终结符号集
	private List<Character> Vt = new ArrayList<Character>();
	
	//非终结符号集
	private List<Character> Vn = new ArrayList<Character>();
	
	//FIRSTVT集
	private Map<Character,Set<Character>> FIRSTVT = new HashMap<Character,Set<Character>>();
	
	//LASTVT集
	private Map<Character,Set<Character>> LASTVT = new HashMap<Character,Set<Character>>();
	
	//算符优先权关系表
	/**
	 * 在OPGtable中，用-1，0,1,2表示优先权关系
	 * 0 表示优先关系等于
	 * 1 表示优先关系小于
	 * 2 表示优先关系大于
	 * -1 表示不存在优先权关系
	 * *
	 */
	private int [][]OPGtable = new int [N][N];
	
	private int   FIRSTVTAllNochange = 1;
	
	private BufferedReader br = null; //输入的二元式文件流
	
	
	public OPG(){
		//设置算符优先文法
		grammar.put("S", "#E#");
		grammar.put("E", "E+T|E-T|T");
		grammar.put("T", "T*F|T/F|F");
		grammar.put("F", "(E)|i");
		
		//设置非终结符号
		Vn.add('S');
		Vn.add('E');
		Vn.add('T');
		Vn.add('F');
	//	Vn.add('P');
		
		//设置终结符号
		Vt.add('#');
		Vt.add('+');
		Vt.add('-');
		Vt.add('*');
		Vt.add('/');
	//	Vt.add('↑');
		Vt.add('(');
		Vt.add(')');
		Vt.add('i');
		
	/*	Vt.add('(');
		Vt.add('i');
		Vt.add('*');
		Vt.add('+');
		Vt.add(')');
		Vt.add('#');
		*/
		
		//初始化算符优先关系矩阵
		for(int l=0;l<Vt.size();l++)
		{
			for(int r =0 ;r<Vt.size();r++)
			{
				OPGtable[l][r]=-1;
			}
		}
		
		System.out.println("文法如下：");
		
		grammar.forEach((k,v)->{
		    System.out.println(k + "→" + v);
		    if(k.equals("E")){
		        System.out.println("Hello E");
		    }
		});
		//为FIRSTVT、LASTVT申请空间
		for(int i = 0;i<Vn.size();++i) {
			FIRSTVT.put(Vn.get(i), new HashSet<Character>());
			LASTVT.put(Vn.get(i), new HashSet<Character>());
		}
		Create_FIRSTVT();
		Create_LASTVT();
		Create_OPGTable();
	}
	//创建非终结符号的FIRSTVT集
	public void Create_FIRSTVT() {
	    s+="Create_FIRSTVT"+"\n";
		//遍历文法。进行非终结符号的FIRSTVT的初始化
		grammar.forEach((k,v)->{
			String []nArryStr = v.split("\\|");
			for(int i=0 ;i<nArryStr.length;i++)
				
			{
				//System.out.println(nArryStr[i]);
				if(Vt.contains(nArryStr[i].charAt(0))) //产生式右部首字母为终结符
				{
					char b= nArryStr[i].charAt(0);
					FIRSTVT.get(k.charAt(0)).add(b);
				}
				if(nArryStr[i].length()>=2)
				if(Vn.contains(nArryStr[i].charAt(0))&&Vt.contains(nArryStr[i].charAt(1))) //产生式右部为非终结符和终结符
				{
					char b=nArryStr[i].charAt(1);
					FIRSTVT.get(k.charAt(0)).add(b);
				}
			}
			
			
		});
		
		do 
		{
			FIRSTVTAllNochange=1;
			grammar.forEach((k,v)->{
				String []nArryStr = v.split("\\|");
				for(int i=0 ;i<nArryStr.length;i++)
					
				{
					char U=k.charAt(0);	//产生式左部非终结符
					char V=nArryStr[i].charAt(0);	//产生式右部第一个符号
					if(Vn.contains(U)&&U!=V&&Vn.contains(V))
					{
						
							FIRSTVT.get(V).forEach(values->{	//遍历产生式右部符号看是否加入左部VT集，不在则加入
								if(!FIRSTVT.get(U).contains(values))
								{
								//	System.out.println(values);
								 FIRSTVT.get(U).add(values);
								 FIRSTVTAllNochange = 0;
								}
							
							});
							
						
					}
					
				}
				
				
			});
			if(FIRSTVTAllNochange==1)
				break;
		}while(true);
		
		//显示FIRSTVT集
		FIRSTVT.forEach((k,v)->{
		    s+=k + ":" + v+"\n";
		   
		});
		writeFile("FirstVT.txt",s);
		s="";
	}
	public void Create_LASTVT() {
	    s+="Create_LASTVT"+"\n";
		
		//遍历文法。进行非终结符号的LASTVT的初始化
		grammar.forEach((k,v)->{
			String []nArryStr = v.split("\\|");
			for(int i=0 ;i<nArryStr.length;i++)
				
			{
				//System.out.println(nArryStr[i]);
				int len= nArryStr[i].length();
				if(Vt.contains(nArryStr[i].charAt(len-1)))	//终结符
				{
					char b= nArryStr[i].charAt(len-1);
					LASTVT.get(k.charAt(0)).add(b);
				}
				if(nArryStr[i].length()>=2)
				if(Vt.contains(nArryStr[i].charAt(len-2))&&Vn.contains(nArryStr[i].charAt(len-1)))	//终结符和非终结符
				{
					char b=nArryStr[i].charAt(len-2);
					LASTVT.get(k.charAt(0)).add(b);
				}
			}
			
			
		});
		
		do 
		{
			FIRSTVTAllNochange=1;
			grammar.forEach((k,v)->{
				String []nArryStr = v.split("\\|");
				for(int i=0 ;i<nArryStr.length;i++)
					
				{
					int len =nArryStr[i].length();
					char U=k.charAt(0);
					char V=nArryStr[i].charAt(len-1);
					if(Vn.contains(U)&&U!=V&&Vn.contains(V))
					{
						
						LASTVT.get(V).forEach(values->{
								if(!LASTVT.get(U).contains(values))
								{
									//System.out.println(values);
									LASTVT.get(U).add(values);
								 FIRSTVTAllNochange = 0;
								}
							
							});
							
						
					}
					
				}
				
				
			});
			if(FIRSTVTAllNochange==1)
				break;
		}while(true);
		
		
		
		LASTVT.forEach((k,v)->{
		    s+=k + ":" + v+"\n";
		   
		});
		writeFile("LastVT.txt",s);
		s="";
	}
	
	public void Create_OPGTable() {
	    s+="Create_OPGTable"+"\n";
		
		/**
		 * 在OPGtable中，用-1，0,1,2表示优先权关系
		 * 0 表示优先关系等于
		 * 1 表示优先关系小于
		 * 2 表示优先关系大于
		 * -1 表示不存在优先权关系
		 * *
		 */
		grammar.forEach((k,v)->{
			String []nArryStr = v.split("\\|");
			
			for(int i=0 ;i<nArryStr.length;i++)
				
			{
				
				String ruleRight=nArryStr[i];
				int  len = ruleRight.length();
				if(len>=2)
				{
					for(int i1= 0 ;i1<len-1 ;i1++)
					{
						char X1=ruleRight.charAt(i1);
						char X2=ruleRight.charAt(i1+1);
						
						if(Vt.contains(X1)&&Vt.contains(X2))				
							OPGtable[Vt.indexOf(X1)][Vt.indexOf(X2)]=0;//0 表示优先关系等于
						if(Vt.contains(X1)&&Vn.contains(X2))
						{
							FIRSTVT.get(X2).forEach(values->{
								OPGtable[Vt.indexOf(X1)][Vt.indexOf(values)]=1;//1 表示优先关系小于
							});
						}
						if(Vn.contains(X1)&&Vt.contains(X2))					
						{
							LASTVT.get(X1).forEach(values->{
			
								OPGtable[Vt.indexOf(values)][Vt.indexOf(X2)]=2;//2 表示优先关系大于
							});
						}
						if(len>=3&&i1<len-2)
						{
							char X3=ruleRight.charAt(i1+2);
							if(Vt.contains(X1)&&Vn.contains(X2)&&Vt.contains(X3))
							{
								
								OPGtable[Vt.indexOf(X1)][Vt.indexOf(X3)]=0; //0 表示优先关系等于
							}
						}
							
							
					}
					
				}
				
			}
			
			
		});
		
		//输出OPGtable
        s+="\t";
		for(int r=0;r<Vt.size();r++)
		{
		    s+="\t"+Vt.get(r);
		}
		s+="\n";

		for(int l=0 ;l<Vt.size();l++)
		{
		    s+=Vt.get(l)+"\t";
			for(int r=0;r<Vt.size();r++)
			{
				
				if(OPGtable[l][r]==0)
				    s+="\t=·";
				if(OPGtable[l][r]==1)
				    s+="\t<·";
				if(OPGtable[l][r]==2)
				    s+="\t>·";
				if(OPGtable[l][r]==-1)
				    s+="\t";
			}
			s+="\n";
		}
		writeFile("OPGTable.txt",s);
		s="";
	}
	public void OPGanalysis(String fileName) {
		
		File fp = new File(fileName);
		if(!fp.getName().endsWith(".tys")) {
			System.out.println("文件格式不正确...");
			return ;
		}
		//构造文件扫描
		String str="";//保存算术表达式
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fp.getName())));
			String erYuanShi = "";
			while((erYuanShi=br.readLine())!=null) {
				//截取符号串
				String substr=erYuanShi.substring(erYuanShi.indexOf("[") + 1, erYuanShi.lastIndexOf(","));
				if(substr.equals("1")||substr.equals("2"))
				{		
					str+="i";
				}
				else 
				{
					str+=erYuanShi.substring(erYuanShi.indexOf(",") + 1, erYuanShi.lastIndexOf("]"));
				}

			}
			
			
			
			System.out.println("算术表达式："+str);
			str+="#";//在算术表达式后加入#符号
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(fileName+"文件不存在...");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		char Q;
		int k=1,j;
		char s1[]=new char[50];//分析栈内容
		s1[k]='#';

		Boolean orsuccess=true;
		
		int i=-1;
	    s+="\t分析栈如下："+"\n";
		do {
			i++;
			if(Vt.contains(s1[k])) {
				j=k;
			} else {
				j=k-1;
			}
			
			while(get_PriorityRelationship(s1[j],str.charAt(i))==2) {	//大于规约
				do {
					 Q=s1[j];	//保存栈顶元素
					if(Vt.contains(s1[j-1])) {
						j=j-1;
					} else {	//非终结符
						j=j-2;
					}
				} while(get_PriorityRelationship(s1[j],Q)!=1);
				
				
				for(int i1=0;i1<=k;i1++)
				{
				    s+=s1[i1]+" ";
				}
				s+="\t\t\t\t";
				
				for(int i1=i;i1<str.length();i1++)
				{
				    s+=str.charAt(i1)+" ";
				}
				s+="\n"+"\t\t规约";
				for(int i1=j+1;i1<=k;i1++)
				{
					s+=s1[i1]+" ";
				}
				s+="\n";
				k=j+1;
				if(s1[k]=='('&&s1[k+1]==')')
				{
				    s+="( 与 ) 之间缺少表达式或者变量"+"\n";
					orsuccess=false;
				}
			    s1[k]='F';
			    
			    for(int i1=0;i1<=k;i1++)
				{
				    s+=s1[i1]+" ";
				}
				s+="\t\t\t\t";
				
				for(int i1=i;i1<str.length();i1++)
				{
				    s+=str.charAt(i1)+" ";
				}
				s+="\n";
			}
			int flag1=get_PriorityRelationship(s1[j],str.charAt(i));	//移进
			if(flag1==1||flag1==0) {
				k=k+1;
				s1[k]=str.charAt(i);
			} else {
			    s+="Error";
				orsuccess=false;
			}
			
		} while(str.charAt(i)!='#');
		
		if(orsuccess)
		{
		    s+="算术表达式格式正确"+"\n";
		}
		else
		{
		    s+="算术表达式格式错误"+"\n";
		}
		writeFile("OPGResult.txt",s);
		s="";

	}
	
	public int get_PriorityRelationship(char Vt1,char Vt2)
	{
		if(Vt.contains(Vt1)&&Vt.contains(Vt2))
		{
			int PR=OPGtable[Vt.indexOf(Vt1)][Vt.indexOf(Vt2)];
			if( PR==-1)
			{
					//System.out.println(Vt1+"与"+Vt2+"之间不存在优先关系！");
					if(Vt1=='('&&Vt2=='#')
					{
						System.out.println("( " +"右侧缺少 ) 与之匹配");
					}
					if(Vt1=='#'&&Vt2==')')
					{
						System.out.println(") " +"左侧缺少 ( 与之匹配");
					}
					if(Vt1==')')
					{
						if(Vt2=='(')
						{
							System.out.println(")与( " +"之间缺少运算符！");
						}
						else if(Vt2=='i')
						{
							System.out.println(")与变量 " +"之间缺少运算符！");
						}
					}

					if(Vt1=='i')
					{
						if(Vt2=='(')
						{
							System.out.println("变量与( " +"之间缺少运算符！");
						}
						else if(Vt2=='i')
						{
							System.out.println("变量与变量之间" +"之间缺少运算符！");
						}
					}
					return -1;
			}
			else
			{
				return PR;
			}
		}
		else
		{
			System.out.println(Vt1+"与"+Vt2+"之间不存在优先关系！");
			return -1;
		}

	}
	static void writeFile(String savepath,String str){//写文件
		FileOutputStream fos= null;
		try {
			fos=new FileOutputStream(savepath);
			fos.write(str.getBytes());
			fos.close();
			System.out.println("已保存");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
