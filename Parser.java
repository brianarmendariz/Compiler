package com.csulb.compiler;

import java.util.Stack;

public class Parser {
	
	Lexer m_lex;
	Stack<String> m_stack;
	Matrix m_matrix;
	
	public Parser(String inputFile) 
	{
		m_lex = new Lexer(inputFile);
		m_stack = new Stack<String>();
		m_matrix = new Matrix();
	}
	
	public void parse() throws Exception
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
			
			
			
			if(m_matrix.isNonTerminal(prediction)) 
			{
				String rule = m_matrix.getRule(prediction, tokenType);
				System.out.println("rule = " + rule);
				if("NO RULE".equals(rule))
				{
					throw new Exception("Error: at " + token.getLineNumber() + " at Expected " 
							+ prediction + " but saw " + tokenType);
				}
				else {
					pushTheRule(rule);
				}
			}
			else if(m_matrix.isTerminal(prediction))
			{
				if(!prediction.equals(tokenType))
				{
					throw new Exception("Error: at " + token.getLineNumber() + " at Expected " 
							+ prediction + " but saw " + tokenType);
				}
				else 
				{
					System.out.println("MATCH: [" + tokenType + "]");
					token = m_lex.nextToken();
					tokenType = token.getWord();
					data = token.getData();
				}
			}
			else if(prediction.equals("equal") && tokenType.equals("equal"))
			{ // if it's an equal sign then eat it
				token = m_lex.nextToken();
				tokenType = token.getWord();
				data = token.getData();
			}
			else if(prediction.equals("eps"))
			{
				// if epsilon then continue
			}
			if(tokenType.equals("EOF"))
			{
				break;
			}
			System.out.println("..................................");
			for(String st : m_stack) 
			{
				System.out.println(st);
			}
		}

		if(tokenType.equals("EOF"))
		{
			System.out.println("\nParse is COMPLETE");
		}
		else 
		{
			System.out.println("\nYou need to DEBUG");
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
			System.out.println(m_stack.pop());
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
