package com.csulb.compiler;

public class Matrix {
	
	private String[][] myMatrix = { 
			{"prog brace1 STMTS brace2", "error", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "error", "eps"},
			 
			{"error", "STMT semi STMTS", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "error", "error", 
			 "error", "eps", "STMT semi STMTS", "error", "error", "error"},
			 
			{"error", "print parens1 ELIST parens2", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "error", "error", 
			 "error", "error", "id equal Y", "error", "error", "error"},
			 
			{"error", "error", "input", "error", "error", "error", 
			 "error", "error", "error", "error", "E", "error", 
			 "error", "error", "E", "E", "E", "error"},
			 
			{"error", "error", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "E comma ELIST", "eps", 
			 "error", "error", "E comma ELIST", "E comma ELIST", "E comma ELIST", "error"},
			 
			{"error", "error", "error", "error", "error", "error",
			 "error", "error", "error", "error", "T Q", "error", 
			 "error", "error", "T Q", "T Q", "T Q", "error"},

			{"error", "error", "error", "plus T Q", "minus T Q", "error", 
			 "error", "error", "eps", "eps", "error", "eps", 
			 "error", "error", "error", "error", "error", "error"},		

			{"error", "error", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "F R", "error", 
			 "error", "error", "F R", "F R", "F R", "error"},
			 
			{"error", "error", "error", "plus T Q", "minus T Q", "aster F R", 
			 "slash F R", "caret F R", "eps", "eps", "error", "eps", 
			 "error", "error", "error", "error", "error", "error"},
			 
			{"error", "error", "error", "error", "error", "error", 
			 "error", "error", "error", "error", "parens1 E parens2", "error", 
			 "error", "error", "id", "num", "string", "error"},
		};
	
	String[] nonTerminalArray = { "PGM", "STMTS", "STMT", "Y", "ELIST", "E", "Q", "T", "R", "F" } ;
	String [] terminalArray = { "prog", "print", "input", "plus", "minus", "aster", "slash", "caret", 
			"comma", "semi", "parens1", "parens2", "brace1", "brace2", "id", "num", "string", "EOF" };
	
	public Matrix() 
	{
		
	}
	
	public String[][] getMatrix()
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
		String rule = "";
				
		if(rowNon < 0 || colTerm < 0 || rowNon > 10 || colTerm > 18)
		{
			return "NO RULE";
		}
		else 
		{
			rule = myMatrix[rowNon][colTerm];
		}
		return rule;
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
