import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;

public class ShortestPath {
	private static Scanner reader = null;
	private static Node[] junctions;
	private static ArrayList<Connection> edges = new ArrayList<Connection>();
	private static ArrayList<Integer> path = new ArrayList<Integer>();
	private static int distance;

	public static void main(String[] args) throws FileNotFoundException {
		readFile();
		fillGraph();
		fillEdges();
		pathFinder(12, 3);
		displayResults();
		cleanArrays();
		pathFinder(1, 10);
		displayResults();
		cleanArrays();
		pathFinder(2, 13);
		displayResults();
	}
	
	public static void cleanArrays(){
		distance = 0;
		path.clear();
	}

	// Reads the text file...
	public static void readFile() throws FileNotFoundException{
		JFileChooser fileChooser = new JFileChooser();
		if(fileChooser.showOpenDialog(null) == fileChooser.APPROVE_OPTION){
			reader = new Scanner(fileChooser.getSelectedFile());
			if(reader.hasNextInt()){ junctions = new Node[reader.nextInt()]; }
			else throw new FileNotFoundException("Only integers...");
		}
	}
	// Fill each node with its neighbors and distances...
	public static void fillGraph(){
		for(int i = 0; i < junctions.length; i++){
			junctions[i] = new Node();
			junctions[i].setReference(reader.nextInt());
			junctions[i].setName(reader.next());
			int[] adj = new int[reader.nextInt()];
			int[] dist = new int[adj.length];
			for(int j = 0; j < adj.length; j++){
				adj[j] = reader.nextInt();
				dist[j] = reader.nextInt();
			}
			junctions[i].setAdjacencies(adj);
			junctions[i].setDistances(dist);
		}
		reader.close();
	}
	// Creates the adjacency-length relation...
	public static void fillEdges(){
		for(int i = 0; i < junctions.length; i++){
			for(int j = 0; j < junctions[i].getAdjacencies().length; j++){
				edges.add(new Connection(junctions[i], junctions[junctions[i].getAdjacencies()[j]-1], junctions[i].getDistances()[j]));
			}
		}
	}
	// Implementing Bellman-Ford Algorithm...
	public static void pathFinder(int a, int b){
		if(a == b){
			path.add(a);
			distance = 0;
			return;
		}
		//ArrayList<Integer> dist = new ArrayList<Integer>();
		for(Node n : junctions){
			if(n.getReference() == a){ n.setDistFromSource(0); }
			else n.setDistFromSource(Integer.MAX_VALUE);
		}

		for(int i = 0; i < junctions.length-1; i++){
			for(Connection c : edges){
				if(c.getStart().getDistFromSource() == Integer.MAX_VALUE){ continue; }
				if(c.getStart().getDistFromSource() + c.getLenght() < c.getTail().getDistFromSource()){
					c.getTail().setDistFromSource(c.getStart().getDistFromSource() + c.getLenght());
					c.getTail().setPrevNode(c.getStart().getReference());
				}
//				if(dist.get(c.getStart().getReference()-1) + c.getLenght() < dist.get(c.getTail().getReference()-1)){
//					dist.set(c.getTail().getReference()-1, dist.get(c.getStart().getReference()-1) + c.getLenght());
//					junctions[c.getTail().getReference()-1].setPrevNode(c.getStart().getReference()); 
//					//c.getTail().setPrevNode(c.getStart().getReference());
//					//System.out.println(c.getTail().getPrevNode());
//				}
			}
		}
		pathCatcher(b, a);
		//distance = dist.get(path.size()-1);
		//		calculateDistance(dist);
		//		for(int i = 0; i < edges.size(); i++){
		//			if(edges.get(i).getStart().getReference() == b){ pathCatcher(i, "start", a); }
		//			else if(edges.get(i).getTail().getReference() == b){ pathCatcher(i, "tail", a); }
		//		}

	}

	public static boolean areAdjacent(Node a, Node b){
		for(int i = 0; i < a.getAdjacencies().length; i++){ 
			if(a.getAdjacencies()[i] == b.getReference()){ return true; }
		}
		return false;
	}

	public static int distanceFrom(int a, int b){
		Node l = junctions[a-1]; 
		Node m = junctions[b-1];
		for(int i = 0; i < l.getAdjacencies().length; i++){ 
			if(l.getAdjacencies()[i] == m.getReference()){ 
				return l.getDistances()[i]; 
			}
		}
		return Integer.MAX_VALUE;
	}

	public static void calculateDistance(ArrayList<Integer> dist){
		distance = 0;
		for(int i = 1; i < path.size(); i++){
			distance += distanceFrom(path.get(i-1), path.get(i));
		}
	}

	public static void pathCatcher(int endingNode, int startNode){
		Node lastNode = junctions[endingNode-1];
		//System.out.println(lastNode.getReference());
		path.add(lastNode.getReference());
		while(lastNode.getPrevNode() != startNode){
			lastNode = junctions[lastNode.getPrevNode()-1];
			path.add(lastNode.getReference());
		}
		path.add(junctions[startNode-1].getReference());
		//System.out.println(path.toString());
	}

	public static void displayResults(){
		int j;
		for(int i = 0; i < path.size()/2; i++){
			j = path.get(path.size() - i - 1);
			path.set(path.size() - i - 1, path.get(i));
			path.set(i, j);
		}
		System.out.println("The shortest path is the one who passes by the nodes: "+path.toString());//needs arguments...
		System.out.println("It means that it passes through the following Airports: ");//needs arguments...
		for(int i = 0; i < path.size(); i++){
			System.out.println("     "+junctions[path.get(i)-1].getName());
		}
		calculateDistance(path);
		System.out.println("The total distance traveled is: "+ distance + " Kilometers.");
	}

}

class Node {
	private String name;
	private int reference;
	private int prevNodeReference;
	private int[] adjacencies;
	private int[] distances;
	private boolean isRoute = true;
	private boolean beingVisited = false;
	private int distFromSource;

	public Node(){}
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getReference() { return reference; }
	public void setReference(int reference) { this.reference = reference; }
	public int[] getAdjacencies() { return adjacencies; }
	public void setAdjacencies(int[] adjacencies) { this.adjacencies = adjacencies; }
	public int[] getDistances() { return distances; }
	public void setDistances(int[] distances) { this.distances = distances; }
	public boolean isRoute() { return isRoute; }
	public void setRoute(boolean isRoute) { this.isRoute = isRoute; }
	public boolean beingVisited(){ return beingVisited; }
	public void setVisited(boolean beingVisited){ this.beingVisited = beingVisited; } 
	public void setPrevNode(int prevNodeReference){ this.prevNodeReference = prevNodeReference; }
	public int getPrevNode(){ return this.prevNodeReference; }
	public void setDistFromSource(int distFromSource){ this.distFromSource = distFromSource; }
	public int getDistFromSource(){ return this.distFromSource; }
}


class Connection{
	private Node start;
	private Node tail;
	private int lenght;
	public Connection(Node start, Node tail, int lenght){
		this.start = start;
		this.tail = tail;
		this.lenght = lenght;
	}
	public Node getStart(){ return this.start; }
	public Node getTail(){ return this.tail; }
	public int getLenght(){ return this.lenght; }
}