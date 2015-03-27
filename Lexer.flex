//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
/* Lexer.flex */

/* Part 1 - User code (the text up to the first %% is copied verbatim to the top of the generated lexer class) */
import java.util.*;
import java_cup.runtime.*;
%% 
/* Part 2 - Options and Declarations (consists of code included in the scanner class, lexial states, and macros) */
%class Lexer
%line
%column
%implements sym
%cup

LineTerminator 		 	  = \r|\n|\r\n
InputCharacter 		 	  = [^\r\n]
WhiteSpace     		 	  = {LineTerminator} | [ \t\f]
EndOfLineComment     	  = "//" {InputCharacter}* {LineTerminator}?
TraditionalComment   	  = "/*" [^*] ~"*/" | "/*" "*"+ "/"
IntegerLiteral 		 	  = 0 | [1-9][0-9]*
Identifier 			 	  = [a-zA-Z_][a-zA-Z0-9_]*

%%
/* Part 3 - Lexical Rules (regular expressions and actions that are executed when the scanner matches the RegEx) */

/* Keywords */
<YYINITIAL> "boolean"				{ return new Symbol(BOOLEAN, "boolean"); }
<YYINITIAL> "class"					{ return new Symbol(CLASS, "class"); }
<YYINITIAL> "else"             		{ return new Symbol(ELSE, "else"); }
<YYINITIAL> "extends"				{ return new Symbol(EXTENDS, "extends"); }
<YYINITIAL> "false"					{ return new Symbol(FALSE, "false"); }
<YYINITIAL> "if"					{ return new Symbol(IF, "if"); }
<YYINITIAL> "int"					{ return new Symbol(INT, "int"); }
<YYINITIAL> "length"				{ return new Symbol(LENGTH, "length"); }
<YYINITIAL> "main"					{ return new Symbol(MAIN, "main"); }
<YYINITIAL> "new"					{ return new Symbol(NEW, "new"); }
<YYINITIAL> "public"				{ return new Symbol(PUBLIC, "public"); }
<YYINITIAL> "return"				{ return new Symbol(RETURN, "return"); }
<YYINITIAL> "static" 				{ return new Symbol(STATIC, "static"); }
<YYINITIAL> "String"				{ return new Symbol(STRING, "String"); }
<YYINITIAL> "System.out.println" 	{ return new Symbol(PRINTLN, "System.out.println"); }
<YYINITIAL> "this"					{ return new Symbol(THIS, "this"); }
<YYINITIAL> "true"					{ return new Symbol(TRUE, "true"); }
<YYINITIAL> "void"					{ return new Symbol(VOID, "void"); }
<YYINITIAL> "while"					{ return new Symbol(WHILE, "while"); }

<YYINITIAL> {
	/* arithmetic operators */
	"="							   { return new Symbol(ASSIGNMENT, "="); }
	"+"							   { return new Symbol(PLUS, "+"); }
	"-"							   { return new Symbol(MINUS, "-"); }
	"*"							   { return new Symbol(STAR, "*"); }
	
	/* unary operators */
	"!"							   { return new Symbol(EXCLAMATION, "!"); }
	
	/* comparison operators */
	"<"							   { return new Symbol(LESSTHAN, "<"); }
	
	/* conditional operators */
	"&&"						   { return new Symbol(AND, "&&"); }
	
	/* additional symbols */
	"."						   	   { return new Symbol(PERIOD, "."); }
	","						       { return new Symbol(COMMA, ","); }
	";"						       { return new Symbol(SEMICOLON, ";"); }
	"{"						       { return new Symbol(LEFTCURLY, "{"); }
	"}"						       { return new Symbol(RIGHTCURLY, "}"); }
	"("						       { return new Symbol(LEFTPAREN, "("); }
	")"						       { return new Symbol(RIGHTPAREN, ")"); }
	"["						       { return new Symbol(LEFTBRACKET, "["); }
	"]"						       { return new Symbol(RIGHTBRACKET, "]"); }

	/* literals */
	{IntegerLiteral}			   { return new Symbol(INTEGER, new Integer(Integer.parseInt(yytext()))); }
	
	/* identifiers */
	{Identifier}				   { return new Symbol(IDENTIFIER, yytext()); }
	
	/* comments */
	{TraditionalComment}           { /* ignore */ }
	{EndOfLineComment}			   { /* ignore */ }
	 
	/* whitespace */
	{WhiteSpace}                   { /* ignore */ }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }