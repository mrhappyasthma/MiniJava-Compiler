package regalloc.graph;

import IR.Quadruple;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;

public class Node {
    Quadruple instr;
    int num;
    List<String> jumpToLabel;
    List<Node> next;
    
    public Node (Quadruple IR, int n){
        instr = IR;
        num=n;
        jumpToLabel = new ArrayList<String>();
	next = new ArrayList<Node>();
    }
    
    //
    public void addJumpTo(String name){
        jumpToLabel.add(name);
    }
    //can be a label or the next instruction
    public List<String> nextLabel(){
        return jumpToLabel;
    }
    
    public void addNext(Node n){
        if(n!=null){
		next.add(n);
	}
    }
    
    public List<Node> nextNode(){
        return next;
    }
    
    public Quadruple getInstr(){
        return instr;
    }
    
    public int getNum(){
        return num;
    }    
   
   
    

}

