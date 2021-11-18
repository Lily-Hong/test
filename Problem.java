import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

//ע�ͣ����ĵ�û���ṩ���н�����Ž��
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
	 * ���ļ��ж���TSP����ʵ�������ݣ����ݸ�ʽΪ��
	 * type---�����ļ������ͣ���1������µ��ñ�����
	 * city number---�����ģ������������
	 * tour length of best known solution---�������Ž��·������
	 * cityID X Y---������� X���� Y����
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
			System.out.println(ex.getMessage()+", City Number:" + pointNumber);//getMessage()��ȡ�����쳣����ϸ��Ϣ�ַ���
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
						if (Problem.USE_INTEGER_EDGE) {//ʹ��������
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
	

	private void setupNearPointList( ) {  //���ø�������ھӽڵ�����
		nearPointList = new int[pointNumber][];
		for (int i = 0; i < pointNumber; i++) {
			nearPointList[i] = setupNearPointList(i, nearPointNumber);
		}
	}
	
	/**
	 * �ҵ������city�����nearCityNumber�����У�������Ӧ������int[nearCityNumber]��
	 * ���з��������еĵ�0��Ԫ������city����ĳ��У��Դ����ơ�
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
		for (int j = 0; j < nearPointNumber; j++) {  //ѡ�������ڵ�neatPointNumber����
			int index = 0;
			for (int k = 0; k < pointNumber ; k++) {
				if (d[index] > d[k] ) {
					index = k;
				}
			}
			pointList[j] = index; //ѡȡС��ֵ
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
    	    Problem problem = new Problem(fileName); //��ʼ���ಢ���tostring
    	    System.out.println(problem);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    	}
     }
    
    
	private static Problem problem = null;
	//the private data member of class TravelingSalesmanProblem
	private boolean isSymmetric=true;
	private int pointNumber; //����
	private int diameter;
	private static int nearPointNumber = 15; //���н����б���
	
	//�������꣬���û���ṩ����û������
	private double[][] pointPosition; 
	
	//����֮��ľ��룬����洢�ռ䲻�㣬��Ϊnull
	private double[][] pointDistance;

	//���ڳ����б�````
	//nearCityList[i][j]�洢���������i��j���ĳ��У�����
	//nearCityList[1][3] = 10, �������1��(3+1)�����ǳ���10
	private int[][] nearPointList = null;
	
	

	

	public static final boolean USE_INTEGER_EDGE = false;
	public static final int SYMMETRIC_GEO = 3;

	private static String fileName = null;
}