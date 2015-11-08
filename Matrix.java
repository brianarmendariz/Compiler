package com.csulb.compiler;

public class Matrix {
	
	private int[][] myMatrix = { 
			{1, 0, 0, 0, 0, 0, //1
			 0, 0, 0, 0, 0, 0,
			 0, 0, 0, 0, 0, 19},
			 
			{0, 2, 0, 0, 0, 0, 
			 0, 0, 0, 0, 0, 0,
			 0, 19, 2, 0, 0, 0},
			
			{0, 3, 0, 0, 0, 0,
			 0, 0, 0, 0, 0, 0,
			 0, 0, 4, 0, 0, 0},
			
			{0, 0, 5, 0, 0, 0,
			 0, 0, 0, 0, 6, 0,
			 0, 0, 6, 6, 6, 0},
			 
			{0, 0, 0, 0, 0, 0, 
		     0, 0, 0, 0, 7, 19,
		     0, 0, 7, 7, 7, 0},
			 
			{0, 0, 0, 0, 0, 0,
			 0, 0, 0, 0, 8, 0,
			 0, 0, 8, 8, 8, 0},
			 
			{0, 0, 0, 9, 10, 0,
		     0, 0, 19, 19, 0, 19,
		     0, 0, 0, 0, 0, 0},

			{0, 0, 0, 0, 0, 0,
			 0, 0, 0, 0, 11, 0,
			 0, 0, 11, 11, 11, 0},
			
			{0, 0, 0, 9, 10, 12,
		     13, 14, 19, 19, 0, 19, 
		     0, 0, 0, 0, 0, 0},

			{0, 0, 0, 0, 0, 0,
		     0, 0, 0, 0, 15, 0, 
		     0, 0, 16, 17, 18, 0}
		};
	
	String[] nonTerminalArray = { "PGM", "STMTS", "STMT", "Y", "ELIST", "E", "Q", "T", "R", "F" } ;
	String [] terminalArray = { "prog", "print", "input", "plus", "minus", "aster", "slash", "caret", 
			"comma", "semi", "parens1", "parens2", "brace1", "brace2", "id", "num", "string", "EOF" };
	
	private Rule rule0 = new Rule("error");
	private Rule rule1 = new Rule("prog brace1 STMTS brace2");
	private Rule rule2 = new Rule("STMT semi STMTS");
	private Rule rule3 = new Rule("print parens1 ELIST parens2");
	private Rule rule4 = new Rule("id equal Y");
	private Rule rule5 = new Rule("input");
	private Rule rule6 = new Rule("E");
	private Rule rule7 = new Rule("E comma ELIST");
	private Rule rule8 = new Rule("T Q");
	private Rule rule9 = new Rule("plus T Q");
	private Rule rule10 = new Rule("minus T Q");
	private Rule rule11 = new Rule("F R");
	private Rule rule12 = new Rule("aster T Q");
	private Rule rule13 = new Rule("slash F R");
	private Rule rule14 = new Rule("caret F R");
	private Rule rule15 = new Rule("parens1 E parens2");
	private Rule rule16 = new Rule("id");
	private Rule rule17 = new Rule("num");
	private Rule rule18 = new Rule("string");
	private Rule rule19 = new Rule("eps");
	
	Rule[] ruleArray = { rule0, rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8,
			rule9, rule10, rule11, rule12, rule13, rule14, rule15, rule16, rule17, 
			rule18, rule19 };
	
	public Matrix() 
	{

	}
	
	public int[][] getMatrix()
	{
		return myMatrix;
	}

	public boolean isNonTerminal(String nonTerminal)
	{
		for(int i = 0; i < nonTerminalArray.length; i++)
		{
			if(nonTerminalArray[i].equals(nonTerminal))
			{
				return true;
			}
		}
		return false;
	}

	public int getIndexOfNonTerminal(String nonTerminal)
	{
		for(int i = 0; i < nonTerminalArray.length; i++)
		{
			if(nonTerminalArray[i].equals(nonTerminal))
			{
				return i;
			}
		}
		return -1;
	}
	
	public int getIndexOfTerminal(String terminal)
	{
		for(int i = 0; i < terminalArray.length; i++)
		{
			if(terminalArray[i].equals(terminal))
			{
				return i;
			}
		}
		return -1;
	}
	
	public String getRule(String nonTerminal, String terminal) 
	{
		int rowNon = getIndexOfNonTerminal(nonTerminal);
		int colTerm = getIndexOfTerminal(terminal);
		int ruleNum = 0;
				
		if(rowNon < 0 || colTerm < 0 || rowNon > 10 || colTerm > 18)
		{
			return ruleArray[ruleNum].getRule();
		}
		else 
		{
			ruleNum = myMatrix[rowNon][colTerm];
		}
		return ruleArray[ruleNum].getRule();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 10; i++) 
		{
			for(int j = 0; j < 18; j++) 
			{
				sb.append(myMatrix[i][j]);
				sb.append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
