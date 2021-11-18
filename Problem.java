import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

//注释，该文档没有提供最有解和最优解的
public class Problem {
	//to create a problem 
	private Problem(String filename)  {
		FileReader data;
		Scanner scan;
		Problem.fileName = filename;
		
		try {
			data = new FileReader(filename);
			scan = new Scanner(data);
			//to get the type of the file: 1----symmetric TSP, edge EUC_2D
			//problemType = scan.nextInt();
			readData(scan);
			calcuDistance();
			
			//nearCityNumber = cityNumber;
			scan.close();
			data.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		this.setupNearPointList();
	}

	/**
	 * 从文件中读入TSP问题实例的数据，数据格式为：
	 * type---数据文件的类型，在1的情况下调用本方法
	 * city number---问题规模，即城市数量
	 * tour length of best known solution---问题最优解的路径长度
	 * cityID X Y---城市序号 X坐标 Y坐标
	 * ...
	 * 
	 * @param scan
	 * @throws Exception
	 */
	
	private void readData(Scanner scan) throws Exception {
		//to get the number of city
		pointNumber = scan.nextInt();
		//to get the length of best tour
		diameter = scan.nextInt();
		
		pointPosition = new double[pointNumber][2];
		try {
			pointDistance = new double[pointNumber][pointNumber];
			//dCityDistance = new double[cityNumber][cityNumber];
		} catch ( Throwable ex) {
			pointDistance = null;
			System.out.println(ex.getMessage()+", City Number:" + pointNumber);//getMessage()获取的是异常的详细消息字符串
		}
		for (int i=0; i<pointNumber;i++) {
			//the x position
			
			pointPosition[i][0] = Math.round(scan.nextDouble()*1000000);//scan.nextInt();
			//the y position
			
			pointPosition[i][1] = Math.round(scan.nextDouble()*1000000);//scan.nextInt();
		}
		
	}

	//to calculate the distance between cities
	private void calcuDistance() {
		for (int i=0; i<pointNumber;i++) {
			for (int j=0; j<pointNumber;j++) {
				if (i==j) {
					if (pointDistance != null) {
						pointDistance[i][j]=0;
					}
				} else {
					double distance;
					distance = (pointPosition[i][0]-pointPosition[j][0]);
					distance *= distance;
					distance += (pointPosition[i][1]-pointPosition[j][1])*(pointPosition[i][1]-pointPosition[j][1]);

					if (pointDistance != null) {
						if (Problem.USE_INTEGER_EDGE) {//使用整数边
							pointDistance[i][j] = (int)(Math.round(Math.sqrt(distance))+0.5);
						} else {
							pointDistance[i][j] = (double)Math.sqrt(distance);
						}
					}
				}
			}
		}
	}

	//to ouput the position
	private void outputPosition() {
		for (int i=0; i<pointNumber; i++) {
			System.out.print(i);
			System.out.print(':');
			System.out.print(pointPosition[i][0]);
			System.out.print('-');
			System.out.print(pointPosition[i][1]);
			System.out.println();
		}
	}
	
	//to output the distance between cities
	private void outputDistance() {
		for (int i=0; i<pointNumber; i++) {
			System.out.print(i);
			System.out.print(':');
			for (int j=0; j<pointNumber; j++) {
				System.out.print(getEdge(i,j));
				System.out.print('-');
			}
			System.out.println();
		}
	}
	

	private void setupNearPointList( ) {  //设置各个点的邻居节点数组
		nearPointList = new int[pointNumber][];
		for (int i = 0; i < pointNumber; i++) {
			nearPointList[i] = setupNearPointList(i, nearPointNumber);
		}
	}
	
	/**
	 * 找到离城市city最近的nearCityNumber个城市，返回相应的数组int[nearCityNumber]，
	 * 其中返回数组中的第0个元素是离city最近的城市，以此类推。
	 * @param city
	 * @param nearCityNumber
	 * @return
	 */
	private int[] setupNearPointList( int point, int nearPointNumber) {
		int[] d = new int[pointNumber];
		//calculate the distances between city i and other cities
		for (int j=0; j<pointNumber; j++) {
			if (j != point) {
				d[j] = (int) getEdge(point,j);
			} else {
				d[j] = Integer.MAX_VALUE;
			}
		}
		
		//find the nearest nearCityNumber cities from i
		int[] pointList = new int[nearPointNumber];  
		for (int j = 0; j < nearPointNumber; j++) {  //选出最相邻的neatPointNumber个边
			int index = 0;
			for (int k = 0; k < pointNumber ; k++) {
				if (d[index] > d[k] ) {
					index = k;
				}
			}
			pointList[j] = index; //选取小的值
			d[index] = Integer.MAX_VALUE;
		}
		return pointList;
	}
	
	
	public double evaluate(Solution solution) {
		return evaluate(solution.getTree());
		
	}
	
	public double evaluate(int[] tree) {
		double treeWeight = 0;
		boolean[] visited = new boolean[tree.length];
		for (int i=0; i<=tree.length-1; i++) {
			if (visited[tree[i]]) {
				System.out.println("Wrong Solution");
			}
			treeWeight+= getEdge(i, tree[i]);//cityDistance[i][tour[i]];
			visited[tree[i]] = true;
		}
		for (int i=0; i<pointNumber; i++) {
			if (!visited[i]) {
				System.out.println("Wrong Solution");
			}
		}
		return treeWeight;
	}
	
	
	public static Problem get() {
		if (problem == null) {
			problem = new Problem(fileName);
		}
		return problem;
	}
	
	public static void load(String fileName) {
		Problem.fileName = fileName;
		problem = new Problem(fileName);
	}
	
	public int getDiameter() {return diameter;}
	public int getPointNumber() {return pointNumber;}
	public double[][] getPointPosition() {return pointPosition;}
	public double[][] getPointDistance(){return pointDistance;}


	public double getEdge(int from, int to) {
		if ( pointDistance != null) {
			return pointDistance[from][to];
		} else {
			double distance;
			distance = (pointPosition[from][0]-pointPosition[to][0]);
			distance *= distance;
			distance += (pointPosition[from][1]-pointPosition[to][1])*(pointPosition[from][1]-pointPosition[to][1]);
			if ( Problem.USE_INTEGER_EDGE ) {
				return (int)(Math.round(Math.sqrt(distance))+0.5);
			} else {
				return (Math.sqrt(distance));
			}
		}
	}
	public static int getNearPointNumber() { return nearPointNumber;}
	public static String getFileName() {
		String f = Problem.fileName;
		if (f.lastIndexOf("\\") >= 0) {
    	    return f.substring(f.lastIndexOf("\\")+1);
		} else {
			return f.substring(f.lastIndexOf("/")+1);
		}
    }
	public boolean isSymmetric() {	return isSymmetric;	}

	public int[][] getNearPointList() { 
		if ( nearPointList == null) {
			this.setupNearPointList();
		}
		return nearPointList;	
	}

	
	@Override
	public String toString() {
		String str = Problem.fileName + "\n";
		str =str+ pointNumber + ", " + diameter + "\n";
		for (int i = 0; i < pointNumber; i++) {
			str += i+ ": (" + pointPosition[i][0] + ", " + pointPosition[i][1] + ")\n"; 
		}
		str+="\n";
		str=str+pointDistance[1][5];
		/*
		for (int i = 0; i < pointNumber; i++) {
			for (int j = 0; i < pointNumber; j++) {
				System.out.println(pointDistance[i][j]);
			}
		}*/
			
		return str;
	}																													

    public static void main(String[] args) {
     	String fileName = (new File("")).getAbsolutePath() + "/datas/estein/estein100.txt";
    	try {
    	    Problem problem = new Problem(fileName); //初始化类并输出tostring
    	    System.out.println(problem);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
     }
    
    
	private static Problem problem = null;
	//the private data member of class TravelingSalesmanProblem
	private boolean isSymmetric=true;
	private int pointNumber; //点数
	private int diameter;
	private static int nearPointNumber = 15; //城市近邻列表长度
	
	//城市坐标，如果没有提供，则没有意义
	private double[][] pointPosition; 
	
	//城市之间的距离，如果存储空间不足，则为null
	private double[][] pointDistance;

	//近邻城市列表````
	//nearCityList[i][j]存储的是离城市i第j近的城市，假设
	//nearCityList[1][3] = 10, 则离城市1第(3+1)近的是城市10
	private int[][] nearPointList = null;
	
	

	

	public static final boolean USE_INTEGER_EDGE = false;
	public static final int SYMMETRIC_GEO = 3;

	private static String fileName = null;
}