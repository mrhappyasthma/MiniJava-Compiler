package regalloc;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import regalloc.graph.Node;
import regalloc.graph.NodeInt;
import symboltable.Variable;

public class InterferenceGraph {

    List<Node> flowGraph;
    List<NodeInt> intGraph;
    List<BitSet> liveOut;
    List<Variable> vars;
    
    int numVert;

    public InterferenceGraph(List<Node> fG, List<BitSet> lO, List<Variable> v) {
        flowGraph = fG;
        liveOut = lO;
        vars = v;
        intGraph = new ArrayList<NodeInt>();

    }
    
    public void buildInterferenceGraph(){
        for (int i = 0; i < flowGraph.size(); i++) {
            Node n = flowGraph.get(i);
            if(n.isMove()){
                //later we will need to check if one of the live outs is the arg1
                Variable arg1 = (Variable) n.getInstr().getArg1();
                
                BitSet def = n.calculateDef(vars);
                
                Variable v = vars.get(def.nextSetBit(0));
                
                NodeInt nI = hasVar(v);
                //doesn't exist
                NodeInt node;
                if(nI == null){
                    node = new NodeInt(v);
                    intGraph.add(node);
                }
                //already exists
                else{
                    node = nI;
                }
                
                BitSet lOut = liveOut.get(i);
                for (int j = 0; j < lOut.length(); j++) {
                    //if the bit is set
                    if(lOut.get(i)){
                        //get the var that has the index 
                        v = vars.get(i);
                        //verify if the variable in the live out is not arg1
                        if(!arg1.getName().equals(v.getName())){
                            //verify if this var already is in a NodeInt
                            nI = hasVar(v);
                            NodeInt aux;
                            //if is not in a NodeInt create a new one
                            if(nI == null){
                                aux = new NodeInt(v);
                                intGraph.add(aux);
                            }
                            else{
                                aux = nI;
                            }
                            node.addEdge(aux);
                            aux.addEdge(node);
                        
                        }
                        
                    }
                    
                }
                
            }
            else{
                BitSet def = n.calculateDef(vars);
                Variable v = vars.get(def.nextSetBit(0));
                NodeInt nI = hasVar(v);
                //doesn' exist
                NodeInt node;
                if(nI == null){
                    node = new NodeInt(v);
                    intGraph.add(node);
                }
                //already exists
                else{
                    node = nI;
                }
                
                BitSet lOut = liveOut.get(i);
                for (int j = 0; j < lOut.length(); j++) {
                    //if the bit is set
                    if(lOut.get(i)){
                        //get the var that has the index 
                        v = vars.get(i);
                        //verify if this var already is in a NodeInt
                        nI = hasVar(v);
                        NodeInt aux;
                        //if is not in a NodeInt create a new one
                        if(nI == null){
                            aux = new NodeInt(v);
                            intGraph.add(aux);
                        }
                        else{
                            aux = nI;
                        }
                        node.addEdge(aux);
                        aux.addEdge(node);
                        
                    }
                    
                }
            }
        }
    }
    
    //function that check if the variable already exists in a node
    public NodeInt hasVar(Variable var){
        for (int i = 0; i < intGraph.size(); i++) {
            NodeInt n = intGraph.get(i);
            if(n.getVar().getName().equals(var.getName())){
                return n;
            }
        }
        return null;
    }
    
    
    

}

