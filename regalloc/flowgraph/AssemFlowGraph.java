package regalloc.flowgraph;

import IR.*;
import regalloc.graph.Node;
import helper.Label;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import symboltable.Variable;
import regalloc.Liveness;

public class AssemFlowGraph{
        List<Quadruple> instr;
        Hashtable<Quadruple, List<Label>> labels;
        
        List<Node> graph;
        //hashtable that keeps the Node of the label
        Hashtable<String,Integer> labelNode;
        List<List<Node>> func;
        //int countInstr;

	public AssemFlowGraph(List<Quadruple> i ,Hashtable<Quadruple, List<Label>> l){
            labels = l;
            instr = i;
            graph = new ArrayList<Node>();
            labelNode = new Hashtable<String,Integer>();
            func = new ArrayList<List<Node>>();
            
            //countInstr=0;
            
        }

        public List<List<Node>> buildCFG(){
            buildNodes();
            boolean isNewFunction = false;
            //String labelNewFunc;
            List<Node> auxList = new ArrayList<Node>();
            
            for (int i = 0; i < graph.size(); i++) {
                
                Node n = graph.get(i);
                
   		             
                if(isNewFunction){
		    isNewFunction = false;
                    //add the actual CFG (actual function) in the List of Lists
                    func.add(auxList);
                    //create a new list to keep the new function
                    auxList = new ArrayList<Node>();
                }
                auxList.add(n);
                if(n.getExitFunction()){
                    if(i!=graph.size()-1){
                        //the next instruction will be a new function
                        isNewFunction = true;
                    }
		   //it's the last node
		    else{
                        func.add(auxList);
                    }
			
                    n.setNextNull();
                    
                }
                else{
                    
                    //if nextLabel() is null it means that is the instruction that follows
                    if(n.nextLabel()==null){
                        //if is not the last node
                        if(i != graph.size()-2){
                            Node aux = graph.get(i+1);
                            n.addNext(aux);
                        }
                    }
                    else{
                        List<String> strAux = n.nextLabel();
                        for (int j = 0; j < strAux.size(); j++) {
                            if( (strAux.get(j)).equals("next")){
                                Node aux = graph.get(i+1);
                                n.addNext(aux);

                            }
                            else{
                                int numLabel = labelNode.get(strAux.get(j));
                                n.addNext(graph.get(numLabel));
                            }

                        }
                    }
                }
                
            }
		
            return func;
        }
        public void buildNodes(){
            Node n = null;
            Node aux = null;
            for (int i = 0; i < instr.size(); i++) {
                Quadruple q = instr.get(i);
                List<Label> labelList = labels.get(q);
                //first node
                n = new Node(q,i);
                if(labelList!=null){
                    for (Label l : labelList) {
                        if(l.printBefore){
                            //save label and its number
                            labelNode.put(l.getName(), i);
                        }
			//the label can be in one line but actually represents the next one
			else{
				labelNode.put(l.getName(),i+1);
			}
                    }
 
                }

		 if(q instanceof UnaryAssignmentIR){
                    if(q.getOp()==null){
                        Variable arg1 = (Variable) q.getArg1();
                        Variable arg2 = (Variable) q.getArg2();
                        if(arg1.getName().equals(arg2.getName())){
                            n.setMove();
                        }
                    }
                }
                if(q instanceof ConditionalJumpIR ){
                    String nameLabel = ((Label)q.getResult()).getName();
                    //iffalse
                    n.addJumpTo(nameLabel);
                    //if true
                    n.addJumpTo("next");
                }
                if(q instanceof UnconditionalJumpIR){
                    String nameLabel = ((Label)q.getResult()).getName();
                    
                    n.addJumpTo(nameLabel);                    
                }
                if(q instanceof ReturnIR){
                    n.setExitFunction();
                }
                if(q instanceof CallIR){
                    String arg1 = (String)q.getArg1();
                    if(!arg1.equals("_system_out_println") && !arg1.equals("_system_exit")){
                        n.addJumpTo(arg1);
                        n.setJumpToFunction(); 
                        n.addJumpTo("next");
                    }
                    if(arg1.equals("_system_exit")){
                        n.setExitFunction();
                    } 
                    
                }
                graph.add(n);
            }
        }
	 public void printGraph(List<Node> graph){
	    System.out.println("New Function");	    	            
            for (int i = 0; i < graph.size(); i++) {
               
		Node n = graph.get(i);
		System.out.print(n.getNum()+ " ");
                System.out.println(n.getInstr().toString());
		List<Node> listN = n.nextNode();
                if(listN.size()!=0){
		    
	            System.out.println("Nexts:");
                    for (int j = 0; j < listN.size(); j++) {
			System.out.print(listN.get(j).getNum()+ " ");
                        System.out.println(listN.get(j).getInstr().toString());
                    
                    }
                }
                
                
            }
            
        }        
        

}


