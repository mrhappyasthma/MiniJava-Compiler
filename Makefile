#Mark Klara
#mak241@pitt.edu
#CS1622 - Project 3
#Makefile
all:
	java -jar tools/jflex-1.6.0.jar Lexer.flex
	java -jar tools/java-cup-11a.jar -interface -parser Parser Parser.cup
	javac -cp tools/java-cup-11a.jar *.java sym.java visitor/*.java syntaxtree/*.java symboltable/*.java helper/*.java IR/*.java linker/*.java codegen/*.java regalloc/*.java
clean:
	rm -rf *.class Lexer.java Parser.java sym.java Lexer.java~
	rm -rf visitor/*.class
	rm -rf syntaxtree/*.class
	rm -rf symboltable/*.class
	rm -rf helper/*.class
	rm -rf IR/*.class
	rm -rf linker/*.class
	rm -rf codegen/*.class
	rm -rf regalloc/*.class
cleanOutput:
	rm -rf samples/*.asm
clear:
	clear
run:
	java -cp ./tools/java-cup-11a.jar:. MiniJavaCompiler samples/$(file)
runMars:
	java -jar tools/Mars4_5.jar samples/$(file) 
