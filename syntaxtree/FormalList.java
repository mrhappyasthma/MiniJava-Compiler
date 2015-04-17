package syntaxtree;

import java.util.Vector;

public class FormalList {
   private Vector list;

   public FormalList() {
      list = new Vector();
   }

   public void addElement(Formal n) {
      list.add(0, n);
   }

   public Formal elementAt(int i)  { 
      return (Formal)list.elementAt(i); 
   }

   public int size() { 
      return list.size(); 
   }
}
