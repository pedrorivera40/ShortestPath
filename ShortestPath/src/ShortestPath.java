/*Department of Electrical and Computer Engineering
 * University of Puerto Rico at Mayaguez
 * ICOM-4075 - Computer Foundations
 * Dr. Kejie Lu
 * 
 * Pedro Luis Rivera
 * Undergraduate Computer Engineering Student
 * November 25, 2016
 * This program consists in an implementation of the Bellman-Ford algorithm, to find the shortest path
 * of a closed graph which is given in a text file. It don't take under consideration negative weights...
 * 
 * The following is an example of how the text file should be written:
 * 
 * 3
 * 1 CA 2 2 100 3 50 // Node 1 is named CA, has 2 adjacencies and the first one is with node 2 and its distance is 100, the second with node 3 and the distance is 50...
 * 2 NY 1 1 100 // Node 2 is named NY and has only one adjacency... it is neighbor of Node 1 and the distance is 100...
 * 3 NJ 1 1 50  // Node 3 is named NJ and has only one adjacency... in is neighbor of Node 1 and the distance is 50...
 * 
 * */
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
		System.out.println("Hi! Thanks for usign the Shortest Path Finder...");
		System.out.println("Please select the MAP file...");
		int source, target;
		readFile();
		fillGraph();
		fillEdges();
		reader = new Scanner(System.in);
		do{
			System.out.println("Please enter the reference number for the Source Node...");
			do{
				while(!reader.hasNextInt()){
					System.out.println("Please enter a positive integer value for the Source");
					reader.next();
				}
				source = reader.nextInt();
				if(source < 1){ System.out.println("ERROR: Source must be greater than 0...");}

			}while(source < 1);
			System.out.println("Please enter the reference number for the Target Node...");
			do{
				while(!reader.hasNextInt()){
					System.out.println("Please enter a positive integer value for the Target");
					reader.next();
				}
				target = reader.nextInt();
				if(target < 1){ System.out.println("ERROR: Target must be greater than 0...");}

			}while(target < 1);
			pathFinder(source, target);
			displayResults();
			cleanArrays();
			System.out.println("Enter any key to continue or enter end to finish the program...");
		}while(!reader.next().equals("end"));
		System.out.println("Thanks for using the Shortest Path Finder...");
	}
	
	/*This method cleans the path array in order to find a new Shortest Path...
	 * It doesn't return any value...
	 * */
	public static void cleanArrays(){
		distance = 0;
		path.clear();
	}

	/*This method finds the location of the text file and use the first line to determine the amount of Nodes...
	 * It doesn't return any value...
	 * */
	public static void readFile() throws FileNotFoundException{
		JFileChooser fileChooser = new JFileChooser();
		if(fileChooser.showOpenDialog(null) == fileChooser.APPROVE_OPTION){
			reader = new Scanner(fileChooser.getSelectedFile());
			if(reader.hasNextInt()){ junctions = new Node[reader.nextInt()]; }
			else throw new FileNotFoundException("Only integers...");
		}
	}
	
	/*This method fills the Node array (junctions)
	 * It doesn't return any value...
	 * */
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
	
	/*This method initializes the edges array...
	 * It doesn't return any value...
	 * */
	public static void fillEdges(){
		for(int i = 0; i < junctions.length; i++){
			for(int j = 0; j < junctions[i].getAdjacencies().length; j++){
				edges.add(new Connection(junctions[i], junctions[junctions[i].getAdjacencies()[j]-1], junctions[i].getDistances()[j]));
			}
		}
	}
	
	/*This method is an implementation of the Bellman-Ford algorithm without considering negative cycles...
	 * @param a is the source index and b is the target index...
	 * It doesn't return any value...
	 * */
	public static void pathFinder(int a, int b){
		if(a == b){
			path.add(a);
			distance = 0;
			return;
		}
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
			}
		}
		pathCatcher(b, a);
	}
	
	/*This method determines wether two nodes are adjacent or not...
	 * @param a and b represents the nodes under consideration...
	 * @return returns true if nodes a and b are adjacent, else returns false...
	 * */
	public static boolean areAdjacent(Node a, Node b){
		for(int i = 0; i < a.getAdjacencies().length; i++){ 
			if(a.getAdjacencies()[i] == b.getReference()){ return true; }
		}
		return false;
	}
	
	/*This method returns the distance between 2 nodes a and b...
	 * @param a and b are the indexes of the nodes (on junctions)
	 * @return returns the distance value from node a to node b.
	 * */
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
	
	/*This method calculates the total distance of the shortest path...
	 * @param It accepts an ArrayList with the path under consideration...
	 * It doesn't return any value...
	 * */
	public static void calculateDistance(ArrayList<Integer> dist){
		distance = 0;
		for(int i = 1; i < path.size(); i++){
			distance += distanceFrom(path.get(i-1), path.get(i));
		}
	}
	
	/*This method goes backwards... From the target to the source and adds to the path every previous node until it reaches the source...
	 * @param it accepts the endingNode and the startNode indexes...
	 * This method doesn't return any value...
	 * */
	public static void pathCatcher(int endingNode, int startNode){
		Node lastNode = junctions[endingNode-1];
		path.add(lastNode.getReference());
		while(lastNode.getPrevNode() != startNode){
			lastNode = junctions[lastNode.getPrevNode()-1];
			path.add(lastNode.getReference());
		}
		path.add(junctions[startNode-1].getReference());
	}
	
	/*This method displays the results of the Shortest Path...
	 * It just print the results, doesn't return any value...
	 * */
	public static void displayResults(){
		int j;
		for(int i = 0; i < path.size()/2; i++){
			j = path.get(path.size() - i - 1);
			path.set(path.size() - i - 1, path.get(i));
			path.set(i, j);
		}
		System.out.println("The shortest path between "+ junctions[path.get(0)-1].getName() +" and "+junctions[path.get(path.size()-1)-1].getName()+" is the one that passes by the nodes: "+path.toString());
		System.out.println("It means that it passes through the following Airports: ");
		for(int i = 0; i < path.size(); i++){
			System.out.println("     "+junctions[path.get(i)-1].getName());
		}
		calculateDistance(path);
		System.out.println("The total distance traveled is: "+ distance + " Kilometers.");
	}
}

/*This class represents a Node (or Vertex)...
 * */
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

/*This class creates the connection or edge between 2 adjacent nodes...
 * In order to create a connection, starting and ending node are needed.
 * Also it needs the lenght (since we are dealing with weighted graphs...)
 * */
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