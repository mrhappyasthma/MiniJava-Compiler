package regalloc.graph;

import IR.Quadruple;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import symboltable.Variable;


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
    public Variable getDef(){
        if(instr.getResult()!=null){
            if( (instr.getResult()) instanceof Variable){
                return (Variable) instr.getResult();
            }
        }
        return null;
    }
    
    public List<Variable> getUse(){
        List<Variable> listVar = new ArrayList<Variable>();
        if(instr.getArg1()!=null){
            if((instr.getArg1()) instanceof Variable){
                Variable arg1 = (Variable) instr.getArg1();
                if(!arg1.getType().equals("constant")){
                    listVar.add(arg1);
                }
            }
        }
        if(instr.getArg2()!=null){
            if((instr.getArg2()) instanceof Variable){
                Variable arg2 = (Variable) instr.getArg1();
                if(!arg2.getType().equals("constant")){
                    listVar.add(arg2);
                }
            }
        }
        return listVar;
    }   
    

}

