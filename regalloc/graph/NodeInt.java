
package regalloc.graph;

import java.util.List;
import symboltable.Variable;

public class NodeInt {
    Variable var;
    List<NodeInt> edge;
    
    public NodeInt(Variable v){
        var = v;
    }
    
    public void addEdge(NodeInt e){
        //if the edge doesn't exist yet we add it 
        if(!edge.contains(e)){
            edge.add(e);
        }
    }
    
    public List<NodeInt> getEdge(){
        return edge;
    }
    
    public Variable getVar(){
        return var;
    }
    
}

