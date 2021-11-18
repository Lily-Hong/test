import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Ces {
	protected int[] tree;
	protected int[] pointIndex;
	public static Random rand = new Random();
	protected double[][] tmatric;
	int diameter=15;
	int centerl,centert;
	int usednum=0;
	double cost;
	protected Problem problem=Problem.get();
	protected double[][] pointDistance=problem.getPointDistance();
	protected int pointNum=problem.getPointNumber();
	public Ces() {
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
		
	}
	
	private int[] insert1(int from, int to) {
		int[] tree = this.tree.clone();
		int fromPos = pointIndex[from];
		int toPos = pointIndex[to];

		while (fromPos != toPos ) {
			int prePos = (fromPos - 1 + tree.length)%tree.length;
          	tree[fromPos] = tree[prePos];
        	fromPos = prePos;
        }
		tree[toPos] = from;
		return tree;
    }
	
	private double[][] makeTree(int[] tree) {
		tmatric = new double[pointNum][3];//第一列为连接的点的编号，第二列为所在的层数，第三列为边的长度
		int bound=diameter/2;
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
	public static void main(String[] args) {
		String name = "estein100.txt";
     	String fileName = (new File("")).getAbsolutePath() + "/datas/estein/" + name;
     	Problem.load(fileName);
     	
		Ces ces=new Ces();
		for(int i=0;i<20;i++) {
			System.out.print(ces.tree[i]+" ");
		}
		System.out.print("\n");
		
		for(int j=0;j<1000;j++) {
			System.out.print(j+":");
			int point1 = rand.nextInt(100);
			int point2 = rand.nextInt(100);
			while (point2 == point1) {
				point2 = rand.nextInt(100);
			}
			int[] tree1=ces.insert1(point1, point2);
			//System.out.print("point1 "+point1+"  "+"point2 "+point2+"\t");
			for(int i=0;i<100;i++) {
				System.out.print(tree1[i]+" ");
			}
			System.out.print("\n");
			
			ces.tmatric=ces.makeTree(tree1);
	    	for(int i=0;i<ces.pointNum;i++) {
				System.out.print(i+"\t"+ces.tmatric[i][0]+"  "+ces.tmatric[i][1]+"  "+ces.tmatric[i][2]+"  "+"\n");
			}
	    	System.out.print("\n");
		}
	}

}
