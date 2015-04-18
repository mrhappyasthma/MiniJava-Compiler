package regalloc.flowgraph;

import IR.*;
import regalloc.graph.Node;
import helper.Label;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import symboltable.Variable;

public class AssemFlowGraph{
        List<Quadruple> instr;
        Hashtable<Quadruple, List<Label>> labels;
        List<Node> graph;
        //hashtable that keeps the Node of the label
        Hashtable<String,Integer> labelNode;
        //int countInstr;
	public AssemFlowGraph(List<Quadruple> i ,Hashtable<Quadruple, List<Label>> l){
            labels = l;
            instr = i;
            graph = new ArrayList<Node>();
            labelNode = new Hashtable<String,Integer>();
            //countInstr=0;
            
        }
        public Node buildCFG(){
            buildNodes();
            for (int i = 0; i < graph.size(); i++) {
                Node n = graph.get(i);
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
            //return the first node
            return graph.get(0);
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
                if(q instanceof ConditionalJumpIR ){
                    String nameLabel = ((Label)q.getResult()).getName();
                    //iffalse
                    n.addJumpTo(nameLabel);
                    //if true
                    n.addJumpTo("next");
                }
                if(q instanceof UnconditionalJumpIR){
                    String nameLabel = ((Label)q.getResult()).getName();
                    //iffalse
                    n.addJumpTo(nameLabel);                    
                }
                graph.add(n);
            }
	    System.out.println("\nGraph without flow:");
            printGraph();
	    System.out.println();
        }
	 public void printGraph(){
            
            for (int i = 0; i < graph.size(); i++) {
               
		Node n = graph.get(i);
		System.out.print(n.getNum()+ " ");
                System.out.println(n.getInstr().toString());
		if(n.getDef()!=null){		
                	System.out.println("Def "+ n.getDef().getName());
                }
		if(!n.getUse().isEmpty()){
			   List<Variable> listUse = n.getUse();
                 	   System.out.println("Uses:");
			   for (int k = 0; k < listUse.size(); k++) {
				System.out.println(listUse.get(k).getName());
                    
                    }
                }
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

