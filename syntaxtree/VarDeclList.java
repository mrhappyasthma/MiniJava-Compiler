package syntaxtree;

import java.util.Vector;

public class VarDeclList {
   private Vector list;

   public VarDeclList() {
      list = new Vector();
   }

   public void addElement(VarDecl n) {
      list.add(0, n);
   }

   public VarDecl elementAt(int i)  { 
      return (VarDecl)list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
