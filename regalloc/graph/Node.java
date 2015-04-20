package regalloc.graph;

import IR.Quadruple;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.BitSet;
import symboltable.Variable;


public class Node {
    Quadruple instr;
    int num;
    List<String> jumpToLabel;
    List<Node> next;
    boolean jumpToFunction;
   boolean exitFunction; 

    public Node (Quadruple IR, int n){
        instr = IR;
        num=n;
        jumpToLabel = new ArrayList<String>();
	next = new ArrayList<Node>();
	jumpToFunction =  false;
	exitFunction= false;
    }
    
    //
    public void addJumpTo(String name){
        jumpToLabel.add(name);
    }

    public void setJumpToFunction(){
        jumpToFunction = true;
    }
    
    public boolean getJumpToFunction(){
        return jumpToFunction;
    }
    public void setExitFunction(){
        exitFunction = true;
    }
    
    public boolean getExitFunction(){
        return exitFunction;
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

   public void setNextNull() {
        next.clear();
    }    

    
    public Quadruple getInstr(){
        return instr;
    }
    
    public int getNum(){
        return num;
    }    

    public boolean isMove(){
        if(instr.getArg2()==null){
            Variable res = (Variable)instr.getResult();
            Variable arg1 = (Variable)instr.getArg1();
            if(arg1.getName().equals(res.getName())){
                return true;
            }
        }
        return false;
    }


        public BitSet calculateDef(List<Variable> listVar) {
        BitSet bitDef = new BitSet(listVar.size());
        if (instr.getResult() != null) {
            if ((instr.getResult()) instanceof Variable) {
                for (int i = 0; i < listVar.size(); i++) {
                    if (listVar.get(i).getName().equals(((Variable) instr.getResult()).getName())) {
                        bitDef.set(i);
                    }
                }
            }
        }
        return bitDef;
    }
	

    public BitSet calculateUse(List<Variable> listVar) {
        BitSet bitUse = new BitSet(listVar.size());
        if (instr.getArg1() != null) {
            if ((instr.getArg1()) instanceof Variable) {
                Variable arg1 = (Variable) instr.getArg1();
                if (!arg1.getType().equals("constant")) {
                    for (int i = 0; i < listVar.size(); i++) {
                        if ( arg1.getName().equals(listVar.get(i).getName())) {
                            bitUse.set(i);
                        }
                    }

                }
            }
        }
        if (instr.getArg2() != null) {
            if ((instr.getArg2()) instanceof Variable) {
                Variable arg2 = (Variable) instr.getArg1();
                if (!arg2.getType().equals("constant")) {
                    for (int i = 0; i < listVar.size(); i++) {
                        if ( arg2.getName().equals(listVar.get(i).getName())) {
                            bitUse.set(i);
                        }
                    }
                }
            }
        }
        return bitUse;

    }   
    

}

