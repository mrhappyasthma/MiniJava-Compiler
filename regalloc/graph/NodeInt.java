
package regalloc.graph;

import java.util.LinkedList;
import java.util.List;
import symboltable.Variable;

public class NodeInt {
    Variable var;
    List<NodeInt> edge;
    
    public NodeInt(Variable v){
        var = v;
        edge = new LinkedList<NodeInt>();
       
    }
    
    public void addEdge(NodeInt e){
        //if the edge doesn't exist yet we add it 
        if(!containsEdge(e)){
            edge.add(e);
        }
    }
    public void rmEdge(NodeInt e){
        edge.remove(e);
    }
    
    public boolean containsEdge(NodeInt e){
        if(!edge.contains(e)){
            return false;
        }
        return true;
    }
    
    public List<NodeInt> getEdge(){
        return edge;
    }
    
    
    public Variable getVar(){
        return var;
    }
    
}

