package graph;

public class Graph
{
	public Graph();
	public NodeList nodes();
	public Node newNode();
	public void addEdge(Node from, Node to);
	public void rmEdge(Node from, Node to);
	public void show(java.io.PrintStream out);
}
