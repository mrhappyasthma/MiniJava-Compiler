# MiniJava-Compiler
This is a simple compiler written in Java to compile the subset of Java called "MiniJava" into MIPS assembly language.  Taken from the website for The MiniJava Project (http://www.cambridge.org/us/features/052182060X/):

>MiniJava is a subset of Java. The meaning of a MiniJava program is given by its meaning as a Java program. Overloading is not allowed in MiniJava. The MiniJava statement System.out.println( ... ); can only print integers. The MiniJava expression e.length only applies to expressions of type int [].

### To Build:

The easiest way to build the project is with the provided Makefile on a linux environment.  Simply running ```make``` should build everything for you.</li>

### To Run:

The easiest way to run is to place your code in the "samples" subfolder and then execute the following command (where "MyMiniJavaFile.java" is the file you wish to compile and resides in the "samples" folder):
```
make run file=MyMiniJavaFile.java
```

To run the compiler, generically, you could manually execute the following command (where "MyMiniJavaFile.java" is the file you wish to compile):
```
java -cp ./tools/java-cup-11a.jar:. MiniJavaCompiler MyMiniJavaFile.java
```

### To Assemble Output:

If you wish to assemble the output, you need to run it through the [Mars Simulator](http://courses.missouristate.edu/kenvollmar/mars/).  If you are on a linux environment, this can be ran easily using the Makefile (where "MyOutput.asm" is the file you wish to assemble/run and resides in the "samples" folder)::
```
make runMars file=MyOutput.asm
```

*Note:  If you are running using an SSH connection, you will need an X-Terminal server.  For windows, I recommend [Xming](http://www.arsc.edu/arsc/knowledge-base/using-xming-x-server-for-/index.xml).

If you wish to run the assembler more generically, you can execute the following command (where "MyOutput.asm" is the file you wish to assemble and run):
```
java -jar tools/Mars4_5.jar MyOutput.asm
```

### About the implementation:

##### Lexical Analysis
The Lexer implementation is written completely using JFlex (see [Lexer.flex](https://github.com/mrhappyasthma/MiniJava-Compiler/blob/master/Lexer.flex)).  You can read more about JFlex here: http://jflex.de/manual.html

##### Syntax Analysis
The Parser implementation is written completely using JavaCup (see [Parser.cup](https://github.com/mrhappyasthma/MiniJava-Compiler/blob/master/Parser.cup)).  You can read more about JavaCup here: http://www2.cs.tum.edu/projects/cup/manual.html

The Grammar for MiniJava as defined by The MiniJava Project can be seen below (http://www.cambridge.org/us/features/052182060X/grammar.html):

![MiniJava Grammar](http://i.imgur.com/XazQEp9.jpg)


##### Symantic Analysis
The front end of the compiler produces an AST as a first type of intermediate representation.  This is traversed to create Three-Address Code.

We use the following set of Three-Address code instructions as our IR [represented by Quadruples]:

![Three-Address Code](http://i.imgur.com/prTDSmZ.png)

##### Code Generation
Lastly, the backend of the compiler generates MIPS Assembly code based on this Three-Address Code.  Our primary target is the MARS MIPS simulator from MSU.  You can read more and download MARS here: http://courses.missouristate.edu/kenvollmar/mars/
