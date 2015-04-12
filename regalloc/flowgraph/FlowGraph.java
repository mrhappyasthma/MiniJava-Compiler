package flowgraph;

public abstract class FlowGraph extends Graph.Graph 
{

	public abstract TempList def(Node node);
	public abstract TempList use(Node node);
	public abstract boolean isMove(Node node);
	public void show(java.io.PrintStream out);
}
