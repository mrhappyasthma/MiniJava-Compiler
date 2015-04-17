//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
/* Lexer.flex */

/* Part 1 - User code (the text up to the first %% is copied verbatim to the top of the generated lexer class) */
import java.util.*;
import java_cup.runtime.*;
import helper.*;
%% 
/* Part 2 - Options and Declarations (consists of code included in the scanner class, lexial states, and macros) */
%class Lexer
%line
%column
%implements sym
%cup

LineTerminator 				= \r|\n|\r\n
InputCharacter 				= [^\r\n]
WhiteSpace 					= {LineTerminator} | [ \t\f]
EndOfLineComment 			= "//" {InputCharacter}* {LineTerminator}?
TraditionalComment 			= "/*" [^*] ~"*/" | "/*" "*"+ "/"
IntegerLiteral 				= 0 | -?[1-9][0-9]*
Identifier 					= [a-zA-Z_][a-zA-Z0-9_]*

%%
/* Part 3 - Lexical Rules (regular expressions and actions that are executed when the scanner matches the RegEx) */

/* Keywords */
<YYINITIAL> "boolean"				{ return new Symbol(BOOLEAN, yyline+1, yycolumn+1, new Token("boolean", yyline+1, yycolumn+1)); }
<YYINITIAL> "class"					{ return new Symbol(CLASS, yyline+1, yycolumn+1, new Token("class", yyline+1, yycolumn+1)); }
<YYINITIAL> "else"             		{ return new Symbol(ELSE, yyline+1, yycolumn+1, new Token("else", yyline+1, yycolumn+1)); }
<YYINITIAL> "extends"				{ return new Symbol(EXTENDS, yyline+1, yycolumn+1, new Token("extends", yyline+1, yycolumn+1)); }
<YYINITIAL> "false"					{ return new Symbol(FALSE, yyline+1, yycolumn+1, new Token("false", yyline+1, yycolumn+1)); }
<YYINITIAL> "if"					{ return new Symbol(IF, yyline+1, yycolumn+1, new Token("if", yyline+1, yycolumn+1)); }
<YYINITIAL> "int"					{ return new Symbol(INT, yyline+1, yycolumn+1, new Token("int", yyline+1, yycolumn+1)); }
<YYINITIAL> "length"				{ return new Symbol(LENGTH, yyline+1, yycolumn+1, new Token("length", yyline+1, yycolumn+1)); }
<YYINITIAL> "main"					{ return new Symbol(MAIN, yyline+1, yycolumn+1, new Token("main", yyline+1, yycolumn+1)); }
<YYINITIAL> "new"					{ return new Symbol(NEW, yyline+1, yycolumn+1, new Token("new", yyline+1, yycolumn+1)); }
<YYINITIAL> "public"				{ return new Symbol(PUBLIC, yyline+1, yycolumn+1, new Token("public", yyline+1, yycolumn+1)); }
<YYINITIAL> "return"				{ return new Symbol(RETURN, yyline+1, yycolumn+1, new Token("return", yyline+1, yycolumn+1)); }
<YYINITIAL> "static" 				{ return new Symbol(STATIC, yyline+1, yycolumn+1, new Token("static", yyline+1, yycolumn+1)); }
<YYINITIAL> "String"				{ return new Symbol(STRING, yyline+1, yycolumn+1, new Token("String", yyline+1, yycolumn+1)); }
<YYINITIAL> "System.out.println" 	{ return new Symbol(PRINTLN, yyline+1, yycolumn+1, new Token("System.out.println", yyline+1, yycolumn+1)); }
<YYINITIAL> "this"					{ return new Symbol(THIS, yyline+1, yycolumn+1, new Token("this", yyline+1, yycolumn+1)); }
<YYINITIAL> "true"					{ return new Symbol(TRUE, yyline+1, yycolumn+1, new Token("true", yyline+1, yycolumn+1)); }
<YYINITIAL> "void"					{ return new Symbol(VOID, yyline+1, yycolumn+1, new Token("void", yyline+1, yycolumn+1)); }
<YYINITIAL> "while"					{ return new Symbol(WHILE, yyline+1, yycolumn+1, new Token("while", yyline+1, yycolumn+1)); }

<YYINITIAL> {
	/* arithmetic operators */
	"="							   { return new Symbol(ASSIGNMENT, yyline+1, yycolumn+1, new Token("=", yyline+1, yycolumn+1)); }
	"+"							   { return new Symbol(PLUS, yyline+1, yycolumn+1, new Token("+", yyline+1, yycolumn+1)); }
	"-"							   { return new Symbol(MINUS, yyline+1, yycolumn+1, new Token("-", yyline+1, yycolumn+1)); }
	"*"							   { return new Symbol(STAR, yyline+1, yycolumn+1, new Token("*", yyline+1, yycolumn+1)); }
	
	/* unary operators */
	"!"							   { return new Symbol(EXCLAMATION, yyline+1, yycolumn+1, new Token("!", yyline+1, yycolumn+1)); }
	
	/* comparison operators */
	"<"							   { return new Symbol(LESSTHAN, yyline+1, yycolumn+1, new Token("<", yyline+1, yycolumn+1)); }
	
	/* conditional operators */
	"&&"						   { return new Symbol(AND, yyline+1, yycolumn+1, new Token("&&", yyline+1, yycolumn+1)); }
	
	/* additional symbols */
	"."						   	   { return new Symbol(PERIOD, yyline+1, yycolumn+1, new Token(".", yyline+1, yycolumn+1)); }
	","						       { return new Symbol(COMMA, yyline+1, yycolumn+1, new Token(",", yyline+1, yycolumn+1)); }
	";"						       { return new Symbol(SEMICOLON, yyline+1, yycolumn+1, new Token(";", yyline+1, yycolumn+1)); }
	"{"						       { return new Symbol(LEFTCURLY, yyline+1, yycolumn+1, new Token("{", yyline+1, yycolumn+1)); }
	"}"						       { return new Symbol(RIGHTCURLY, yyline+1, yycolumn+1, new Token("}", yyline+1, yycolumn+1)); }
	"("						       { return new Symbol(LEFTPAREN, yyline+1, yycolumn+1, new Token("(", yyline+1, yycolumn+1)); }
	")"						       { return new Symbol(RIGHTPAREN, yyline+1, yycolumn+1, new Token(")", yyline+1, yycolumn+1)); }
	"["						       { return new Symbol(LEFTBRACKET, yyline+1, yycolumn+1, new Token("[", yyline+1, yycolumn+1)); }
	"]"						       { return new Symbol(RIGHTBRACKET, yyline+1, yycolumn+1, new Token("]", yyline+1, yycolumn+1)); }

	/* literals */
	{IntegerLiteral}			   { return new Symbol(INTEGER, yyline+1, yycolumn+1, new Token(new Integer(Integer.parseInt(yytext())), yyline+1, yycolumn+1)); }
	
	/* identifiers */
	{Identifier}				   { return new Symbol(IDENTIFIER, yyline+1, yycolumn+1, new Token(yytext(), yyline+1, yycolumn+1)); }
	
	/* comments */
	{TraditionalComment}           { /* ignore */ }
	{EndOfLineComment}			   { /* ignore */ }
	
	/* whitespace */
	{WhiteSpace}                   { /* ignore */ }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }