package graph;

public class Node
{
	public Node(Graph g);
	public NodeList succ();
	public NodeList pred();
	public NodeList adj();
	public int outDegree();
	public int inDegree();
	public int degree();
	public boolean goesTo(Node n);
	public boolean comesFrom(Node n);
	public boolean adj(Node n);
	public String toString();

}
