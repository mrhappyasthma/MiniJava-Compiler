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

### About the implementation:

The Lexer implementation is written completely using JFlex (see [Lexer.flex](https://github.com/mrhappyasthma/MiniJava-Compiler/blob/master/Lexer.flex)).  You can read more about JFlex here: http://jflex.de/manual.html

The Parser implementation is written completely using JCup (see [Parser.cup](https://github.com/mrhappyasthma/MiniJava-Compiler/blob/master/Parser.cup)).  You can read more about JavaCup here: http://www2.cs.tum.edu/projects/cup/manual.html

The Grammar for MiniJava as defined by The MiniJava Project can be seen below (http://www.cambridge.org/us/features/052182060X/grammar.html):

![MiniJava Grammar](http://i.imgur.com/XazQEp9.jpg)

The front end of the compiler produces an AST as a first type of intermediate representation.  This is traversed to create Three-Address Code.  Lastly, the backend of the compiler generates MIPS Assembly code based on this Three-Address Code.
