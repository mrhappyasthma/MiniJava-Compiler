#Mark Klara
#mak241@pitt.edu
#CS1622 - Project 3
#Makefile
all:
	java -jar tools/jflex-1.6.0.jar Lexer.flex
	java -jar tools/java-cup-11a.jar -interface -parser Parser Parser.cup
	javac -cp tools/java-cup-11a.jar *.java sym.java visitor/*.java syntaxtree/*.java symboltable/*.java
clean:
	rm -rf *.class Lexer.java Parser.java sym.java Lexer.java~
	rm -rf visitor/*.class
	rm -rf syntaxtree/*.class
	rm -rf symboltable/*.class
clear:
	clear
run:
	java -cp ./tools/java-cup-11a.jar:. MiniJavaCompiler samples/$(file)
