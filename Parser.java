package com.csulb.compiler;

import java.util.Stack;

public class Parser {
	
	Lexer m_lex;
	Stack<String> m_stack;
	Matrix m_matrix;
	String[] m_endTokens = { "semi", "brace2", "EOF" };
	boolean errors = false;
	
	public Parser(String inputFile) 
	{
		m_lex = new Lexer(inputFile);
		m_stack = new Stack<String>();
		m_matrix = new Matrix();
	}
	
	public void parse() //throws Exception
	{
		m_stack.push("EOF");
		m_stack.push("PGM");
	
		
		Token token = m_lex.nextToken();
		String tokenType = token.getWord();
		String data = token.getData();
		String prediction = null;
		
		while(true)
		{
			prediction = m_stack.pop();
			
			System.out.println("\nprediction = " + prediction + ", token = " + tokenType + ", data = " + data );
			
			if(tokenType.equals(prediction))
			{
					System.out.println("MATCH: [" + tokenType + "]");
					token = m_lex.nextToken();
					tokenType = token.getWord();
					data = token.getData();
					System.out.println("tokenType = " + tokenType);
			}
			else if(m_matrix.isNonTerminal(prediction)) 
			{
				String rule = m_matrix.getRule(prediction, tokenType);
				System.out.println("rule = " + rule);
				if("error".equals(rule))
				{	
					try {
						errorRecovery(prediction, token);
					} catch (Exception e) {
						e.printStackTrace();
						errors = true;
					}
					token = m_lex.backupToken();
					tokenType = token.getWord();
					data = token.getData();
					System.out.println("token = " + token.getWord());
				}
				else {
					pushTheRule(rule);
				}
			}
			else if(prediction.equals("eps"))
			{
				// if epsilon then continue
			}			
			else if(!prediction.equals(tokenType))
			{	
				try {
					errorRecovery(prediction, token);
				} catch (Exception e) {
					e.printStackTrace();
					errors = true;
				}
				token = m_lex.backupToken();
				tokenType = token.getWord();
				data = token.getData();
				System.out.println("token = " + token.getWord());
			}
			
			if(tokenType.equals("EOF"))
			{
				break;
			}			
			System.out.println("..................................");
			System.out.println("...top of stack...");
			for (int i = m_stack.size() - 1; i >= 0; i--) {
			    System.out.println("\t" + m_stack.get(i));
			}
			System.out.println("...bottom of stack...");
		}

		StringBuilder sb = new StringBuilder("\nParse is COMPLETE: ");
		if(tokenType.equals("EOF") && !errors)
		{
			sb.append("you have no errors");
		}
		else 
		{
			sb.append("you have ERRORS");
		}
		System.out.println(sb.toString());
	}
	
	public void errorRecovery(String prediction, Token token) throws Exception
	{
		//output original error message
		try
		{
			throw new Exception("Error: at " + token.getLineNumber() + " at Expected " 
					+ prediction + " but saw " + token.getWord());
		}
		finally
		{
			//pop stack until an end token is seen
			boolean found  = false;
			while(!found)
			{
				for(int i = 0; i < m_endTokens.length; i++)
				{
					System.out.println("m_endTokens[i] = " + m_endTokens[i] +
							", prediction = " + prediction);
					
					if(m_endTokens[i].equals(prediction))
					{
						found = true;
						break;
					}
					
					if(!found && i == m_endTokens.length - 1)
					{
						m_stack.pop();
					}
				}
				//get the next token until it is an end token
				if(!m_stack.isEmpty())
				{
					prediction = m_stack.peek();
				}
				//if recovery cannot be made let user know
				if(!found && m_stack.isEmpty())
				{
					throw new Exception("Error: UNRECOVERABLE, critical damage ");
				}
			}

			while(m_lex.hasNextToken() && !prediction.equals(token.getWord()))
			{
				System.out.println("token.getWord() = " + token.getWord() +
						", prediction = " + prediction);
				token = m_lex.nextToken();
			}
			System.out.println("token.getWord() = " + token.getWord());
			System.out.println("Skipping to the end of the <program-construct> at line " +
					token.getLineNumber());	
		}
	}
	
	public void pushTheRule(String rule)
	{
		if(rule.contains(" "))
		{
			int ws = rule.length();
			for(int i = rule.length() - 1; i >= 0; i--)
			{
				if(rule.charAt(i) == 32)
				{
					m_stack.push(rule.substring(i+1, ws));
					ws = i;
				}
				else if(i == 0)
				{
					m_stack.push(rule.substring(i, ws));
					ws = i;
				}
			}
		}
		else {
			m_stack.push(rule);
		}
	}
	
	public void displayStack()
	{
		while(!m_stack.isEmpty())
		{
			System.out.println(m_stack.peek());
		}
	}
	
	public static void main(String[] args)
	{
		Parser parser = new Parser("printing.txt");
		
		try {
			parser.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
