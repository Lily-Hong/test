import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SolutionArray  {
	/**
	 * To create a new solution
	 */
	public SolutionArray() {
		//this.fileName=fileName;
		tree=new int[pointNum];
		pointIndex = new int[pointNum];
		List<Integer> list = new LinkedList<>();
		for (int i = 0; i <pointNum; i++) {
			list.add(i);
		}
		Collections.shuffle(list);
		for (int i = 0; i < pointNum; i++) {
			tree[i] = list.get(i);	//一组打乱的点
			pointIndex[tree[i]] = i; //该数组的下标表示点，内容表示排列顺序号
		}
		
		tmatric=makeTree(tree);
		cost = eval(tmatric);
		cost=Math.round(cost);
	}
	
	public SolutionArray(int[] tree1) {  //该数组的下标表示点，内容表示排列顺序号
		this.tree= tree1;
		pointIndex = new int[tree.length];
		for (int i = 0; i < tree.length; i++) {
			pointIndex[tree[i]] = i;
		}
		tmatric=makeTree(tree);
		cost = eval(tmatric);
		cost=Math.round(cost);
		/*
		System.out.print("pcenterl: " +centerl+"   " +"centert: " +centert+"\t");
    	for(int i=0;i<pointNum;i++) {
			System.out.print(" t"+tree[i]+"  ");
			
		}
    	System.out.println("\n ");
    	*/
    
		 
	}
	
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#next(int)
	 */
	//@Override
	public int next(int point) {
		int pos = pointIndex[point];
		pos = (pos+1) % tree.length;
		return tree[pos];
	}
	/* (non-Javadoc)
	 * @see simulatedAnnealing.Solution#previous(int)
	 */
	//@Override
	public int previous(int point) {
		int pos = pointIndex[point];
		pos = (pos-1+tree.length) % tree.length;
		return tree[pos];
	}
	
	//@Override
	public SolutionArray neighbor() {
		//随机选择2点
		int point1 = rand.nextInt(tree.length);
		int point2 = rand.nextInt(tree.length);
		while (point2 == point1) {
			point2 = rand.nextInt(tree.length);
		}
//		int pc = previous(point1);
//		int point2 = Problem.get().getNearPointList()[pc][rand.nextInt(Problem.getNearPointNumber())];
		return neighbor(point1, point2);
	}
	
	//@Override
	public SolutionArray neighbor(int from, int to) {
		/*
		ENeighborType nt = Simulation.neighborType;
		if (Simulation.neighborType == ENeighborType.HYBRID) { //hybrid
			if (rand.nextDouble() < 6.0 / 10) {
				nt = ENeighborType.INVERSE;
			} else if (rand.nextDouble() < 0.75) {
				nt = ENeighborType.INSERT;
			} else {
				nt = ENeighborType.SWAP;
			}
			
		}
		if (nt == ENeighborType.INVERSE) {
		*/
		    tree = insert(from, to);
		    //System.out.print("reverse  ");
		    //Solution s = new SolutionArray(tree);
		   // return s;
		    /*
		} else if (nt == ENeighborType.INSERT) {
			tree = insert(from, to);
			//System.out.print("insert  ");
			
		} else if (nt == ENeighborType.SWAP) {
		    tree = swap(from, to);  
		    //System.out.print("swap  ");
		} */
		return new SolutionArray(tree);
	}

    /**
     * Reverse the cities between city from and city to
     * @param from
     * @param to
     * @return a new tour
     */
	private int[] reverse(int from, int to) {
		int[] tree = this.tree.clone();
		int pos = pointIndex[from];
		int j = pointIndex[to];
		while (  j != pos ) {
			int city = tree[j];
			tree[j] = tree[pos];
			tree[pos] = city;
			j = (j - 1 + tree.length) % tree.length;
			if (j== pos)
				break;
			pos = (pos + 1) % tree.length;
		}
		return tree;
	}
	

	private int[] insert(int from, int to) {
		int[] tree = this.tree.clone();
		int fromPos = pointIndex[from]; //取这个点在tree数组中的位置编号
		int toPos = pointIndex[to];
		/*
		if(toPos<fromPos) {
			int temp=toPos;
			toPos=fromPos;
			fromPos=temp;
		}*/
		//for(int i=fromPos;i<tree.length;i++)
		while (fromPos != toPos ) {
			int prePos = (fromPos - 1 + tree.length)%tree.length; //prePos为from点在tree数组中的前一个位置数值
          	tree[fromPos] = tree[prePos]; //from位置赋值为前一个数值
        	fromPos = prePos;      //指针前移
        }
		tree[toPos] = from;  //把to位置上的点改为from这个点
		return tree;
    }

	private int[] swap(int from, int to) {
		int[] tree = this.tree.clone();
		int fromPos = pointIndex[from];
		int toPos = pointIndex[to];

		tree[fromPos] = to;
        tree[toPos] = from;
        return tree;
  	}
	
	private double[][] makeTree(int[] tree) {
		tmatric = new double[pointNum][3];//第一列为连接的点的编号，第二列为所在的层数，第三列为边的长度
		if(diameter%2==0) {
			centerl=tree[0];
			centert=Integer.MAX_VALUE;
			usednum=1;
			cost=0;	
			tmatric[centerl][0]=centerl;
			tmatric[centerl][1]=0;
			tmatric[centerl][2]=0;
		}
		else {
			centerl=tree[0];
			centert=tree[1];
			usednum=2;
			tmatric[centerl][0]=centert;
			tmatric[centerl][1]=0;
			tmatric[centerl][2]=pointDistance[centerl][centert];
			tmatric[centert][0]=centerl;
			tmatric[centert][1]=0;
			tmatric[centert][2]=0;	
		}
		for(int i=usednum;i<pointNum;i++) {//i代表没有被使用的点次序值
			int aindex=-1;
			double aminedge=Integer.MAX_VALUE;
			int j=0;
			for( ;j<usednum;j++) {//j表示已经使用过的点次序值
				if((pointDistance[tree[i]][tree[j]]<aminedge)&&(tmatric[tree[j]][1]+1<=bound)) {
					aminedge=pointDistance[tree[i]][tree[j]];//找到的最小边的值
					aindex=tree[j];		//找到的最小边的连接编号	
				}
			}
			usednum++;//更新使用点的数目
			tmatric[tree[i]][0]=aindex;//更新连接的点
			tmatric[tree[i]][1]=tmatric[aindex][1]+1;//更新层数
			tmatric[tree[i]][2]=aminedge;//更新连接的边值
			//cost+=aminedge;
		}
		/*System.out.println("centerl: " +centerl+"\t" +"centert: " +centert);
    	for(int i=0;i<pointNum;i++) {
			System.out.print(" "+tree[i]+"  ");
			
		}
    	System.out.print("\n ");
    	*/
		return tmatric;
	}
	private double eval(double tmtric[][]){//表示tree数组中
		double len = 0;
		for (int i = 0; i < pointNum; i++) {
			len+=tmatric[i][2];
		}
		len=Math.round(len);
		return len;
	}
	
	public void save(String fileName) {
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(fileName));
			Problem p = Problem.get();
			int pointNum = p.getPointNumber();
			double[][] pos = p.getPointPosition();
			printWriter.write(pointNum + "\t0\t0\t0\n");
			for (int i = 0; i < pointNum; i++) {  //把解的所有点的坐标值打印出来
				printWriter.write(i + "\t" + pos[i][0] + "\t" +  pos[i][1] + "\n");
			}

			double[][] tmatric = getTmatric();
			printWriter.write("best的center的值："+centerl+"\t"+centert+"\n");
			printWriter.write("best的tmatric的值："+"\n");
			for (int i = 0; i <pointNum; i++) {
				printWriter.write(i+"\t"+tmatric[i][0] + "\t"+tmatric[i][1] +"\t"+tmatric[i][2] +  "\n");
			} 
			//printWriter.write(cost + "\t0\t0\t0\n");
			printWriter.write("cost: " +cost+ "\t0\t0\t0\n");

			printWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public int[] getTree() { return tree; }
	
	public  double[][] getTmatric() {return tmatric;}
	public  int getLastImprove() {return lastImprove;}
	
	
	public static void main(String[] args) {
		
    	String name = "estein100.txt";
     	String fileName = (new File("")).getAbsolutePath() + "/datas/estein/" + name;
     	Problem.load(fileName);
     	SolutionArray gs = new SolutionArray();
     	/*
     	for(int i=0;i<gs.pointNum;i++) {
			System.out.print(gs.tree[i]+" ");
		}
     	
     	double cost1=0;
     	System.out.print("\n");
     	int[] treel=gs.insert(0, 4);
     	for(int i=0;i<gs.pointNum;i++) {
			System.out.print(treel[i]+" ");
		}
     	SolutionArray SA1=new SolutionArray(treel);
     	for(int i=0;i<SA1.pointNum;i++) {
			System.out.print(i+"\t"+SA1.tmatric[i][0]+"\t"+SA1.tmatric[i][1]+"\t"+SA1.tmatric[i][2]+"\n");
			cost1+=SA1.tmatric[i][2];
		}
     	System.out.print("SA1.cost:"+SA1.cost+"\n");
     	System.out.print("SA1.cost1:"+cost1);
     	
     	
     	double cost2=0;
     	System.out.print("\n");
     	int[] treeq=gs.swap(89, 4);
     	for(int i=0;i<gs.pointNum;i++) {
			System.out.print(treeq[i]+" ");
		}
     	SolutionArray SA2=new SolutionArray(treel);
     	for(int i=0;i<SA2.pointNum;i++) {
			System.out.print(i+"\t"+SA2.tmatric[i][0]+"\t"+SA2.tmatric[i][1]+"\t"+SA2.tmatric[i][2]+"\n");
			cost2+=SA2.tmatric[i][2];
		}
     	System.out.print("SA2.cost:"+SA2.cost+"\n");
     	System.out.print("SA2.cost:"+cost2);
     	
     	double cost3=0;
     	System.out.print("\n");
     	int[] treet=gs.reverse(0, 4);
     	for(int i=0;i<gs.pointNum;i++) {
			System.out.print(treet[i]+" ");
		}
     	SolutionArray SA3=new SolutionArray(treel);
     	for(int i=0;i<SA3.pointNum;i++) {
			System.out.print(i+"\t"+SA3.tmatric[i][0]+"\t"+SA3.tmatric[i][1]+"\t"+SA3.tmatric[i][2]+"\n");
			cost3+=SA3.tmatric[i][2];
		}
     	System.out.print("SA3.cost:"+SA3.cost+"\n");
     	System.out.print("SA3.cost:"+cost3);
     	*/
    	int times = 10;
    	int total = 0;
    	SolutionArray ne=gs;
    	/*
    	for(int i=0;i<100;i++) {
    		ne=gs.neighbor();
        	System.out.println("ne.centerl: " +ne.centerl+"\t" +"ne.centert: " +ne.centert); //只有s不正常，tree有问题，但没有问题
        	for(int j=0;j<ne.pointNum;j++) {
    			System.out.print(" "+ne.tree[j]+"  ");
    			
    		}
        	System.out.println("\n ");
    	}
    	*/
     	for (int t = 0; t < times; t++) {
     		//System.out.println("t "+t+" ");
     		SolutionArray s = new SolutionArray();
    		for (int i = 0; i < 10000; i++) {
    			//System.out.println("i "+i+" ");
    			SolutionArray ns = s.neighbor();//此时s的值已经改变
     			if (ns.cost < s.cost) {
     				/*
     				System.out.println("s.centerl: " +s.centerl+"\t" +"s.centert: " +s.centert); //只有s不正常，tree有问题，但没有问题
        	    	for(int j=0;j<s.pointNum;j++) {
        				System.out.print(" "+s.tree[j]+"  ");
        				
        			}
        	    	//System.out.println("\n ");
        	    	 */
    				s = ns;
    				/*
    				s.centerl=ns.centerl;
    				s.centert=ns.centert;
    				System.out.println("!!!!!! ");
    				System.out.println("s.centerl: " +s.centerl+"\t" +"s.centert: " +s.centert);
        	    	for(int j=0;j<s.pointNum;j++) {
        				System.out.print(" "+s.tree[j]+"  ");
        				
        			}
        	    	System.out.println("\n ");
        	    	*/	
    			}
    		}
    		//System.out.println(t  + ", " + s.cost);
    		total += s.cost;
    		if (s.cost < gs.cost) {
    		    ne=gs;
    			gs = s;
    			/*
    			System.out.println("centerl: " +gs.centerl+"\t" +"centert: " +gs.centert);
    	    	for(int i=0;i<gs.pointNum;i++) {
    				System.out.print(" "+gs.tree[i]+"  ");
    				
    			}
    	    	System.out.print("\n ");
    	    	*/
    		}
    	}
     	/*
     	System.out.println("ne.cost:"+  ne.cost );
    	System.out.println("ne centerl: " +ne.centerl+"\t" +"centert: " +ne.centert);
    	//System.out.println("ne centerl: " +ne.tree[0]+"\t" +"centert: " +ne.tree[1]);
    	for(int i=0;i<ne.pointNum;i++) {
			System.out.print(" "+ne.tree[i]+"  ");
			
		}
    
    	System.out.println("\n");
    	*/
     	System.out.print("\n ");
     	System.out.println("最终的gs.cost  "+  gs.cost + ",   " + total/times);
    	System.out.println("centerl: " +gs.centerl+"\t" +"centert: " +gs.centert);
    	for(int i=0;i<gs.pointNum;i++) {
			System.out.print(" "+gs.tree[i]+"  ");
		}
    	
    	System.out.println("\n");
		for(int i=0;i<gs.pointNum;i++) {
			System.out.print(i+"\t"+gs.tmatric[i][0]+"\t"+gs.tmatric[i][1]+"\t"+gs.tmatric[i][2]+"\n");
		}
    	//gs.save((new File("")).getAbsolutePath() + "/results/" + name + ".txt");
        System.out.println(gs);
        /*
     	System.out.println("gscost:"+gs.cost);
        SolutionArray s = gs.neighbor();
        System.out.println("scost:"+s.cost);
     	System.out.print("\n");
     	if (gs.cost < s.cost) {
			s = gs;
		}
     	for(int i=0;i<s.pointNum;i++) {
			System.out.print(s.tree[i]+" ");
		}
    	for(int i=0;i<s.pointNum;i++) {
			System.out.print(i+"\t"+s.tmatric[i][0]+"\t"+s.tmatric[i][1]+"\t"+s.tmatric[i][2]+"\n");
		}
    	System.out.println("scost:"+s.cost);
     	
     	/*
     	double[][] pointdistance=Problem.get().getPointDistance();
    	System.out.println("diameter:"+Problem.get().getDiameter());
    	System.out.println("\"pointDiatance:"+pointdistance[1][2]);
    	System.out.println("bound:"+gs.bound);
    	for(int i=0;i<gs.getTree().length;i++) {
    		System.out.println(gs.getTree()[i]);
    	}
    	System.out.println("cost:"+gs.cost);
    	System.out.println("pointnum:"+gs.pointNum);
    	System.out.println("usednum:"+gs.usednum);
    	System.out.println("tmatric[centerl][2]:"+gs.tmatric[gs.centerl][2]);
    	System.out.println("pointDiatance:"+gs.pointDistance[gs.centerl][2]);
    	System.out.println("centerl:"+gs.centerl);
    	System.out.println("centert:"+gs.centert);
    	for(int i=0;i<100;i++) {
    		System.out.println("i: "+i+" tmatric"+gs.tmatric[i][0]+gs.tmatric[i][1]+gs.tmatric[i][2]); //结果就是都连在29这个点上面，并且都在1层，边都为0
    	}
    	System.out.println("cost:"+gs.cost);
    	System.out.println(Problem.getFileName());
    	for(int i=0;i<100;i++) {
    		System.out.println(gs.getTmatric()[i][0]+gs.getTmatric()[i][1]+gs.getTmatric()[i][2]);
    	}
    	*/
    	//输出所有迭代中的最小值
     	
       
	}
	//private int[] cen;
	protected int centerl;
	protected int centert;
	protected int[] tree;
	protected  double[][] tmatric;
	protected int[] pointIndex;
	protected String fileName;
	protected Problem problem=Problem.get();
	protected int pointNum=problem.getPointNumber();
	protected double[][] pointDistance=problem.getPointDistance();
	protected int diameter=problem.getDiameter();
	protected int bound=problem.getDiameter()/2;
	protected int usednum;
	protected double cost;
	protected int lastImprove=0;
	public static Random rand = new Random();
}
