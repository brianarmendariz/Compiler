package com.csulb.compiler;

import java.util.Stack;

public class Parser {
	
	Lexer m_lex;
	Stack<Node> m_stack;
	Matrix m_matrix;
	String[] m_endTokens = { "semi", "brace2", "EOF" };
	boolean errors = false;
	Rule lastRule = null;
	Node rootNode;
	
	public Parser(String inputFile) 
	{
		m_lex = new Lexer(inputFile);
		m_stack = new Stack<Node>();
		m_matrix = new Matrix();
		rootNode = null;
	}
	
	public void parse() //throws Exception
	{
		m_stack.push(new Node("EOF"));
		rootNode = new Node("PGM");
		m_stack.push(rootNode);
		
		Token token = m_lex.nextToken();
		String tokenType = token.getWord();
		String data = token.getData();
		String prediction = null;
		
		while(true)
		{
			Node currentNode = m_stack.pop();
			prediction = currentNode.getData();
			
//			System.out.println("\nprediction = " + prediction + ", token = " + tokenType + ", data = " + data );		
			
			if(tokenType.equals(prediction))
			{
					System.out.println("MATCH: [" + tokenType + "]");
					token = m_lex.nextToken();
					tokenType = token.getWord();
					data = token.getData();
			}
			else if(m_matrix.isNonTerminal(prediction)) 
			{
//				String rule = m_matrix.getRule(prediction, tokenType);
				Rule rule = m_matrix.getRule(prediction, tokenType);
				Rule rulex = new Rule(prediction, rule.getRule(), rule.getRuleNumber());
				
				if("error".equals(rulex.getRHS()[0]))
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
//					System.out.println("token = " + token.getWord());
				}
				else {
					pushTheRule(currentNode, rulex);
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
			    System.out.println("\t" + m_stack.get(i).getData());
			}
			System.out.println("...bottom of stack...\n");
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
		System.out.println(sb.toString() + "\n");
		
		preOrderTraverseTree(rootNode, "");
		
		System.out.println("\n\npost order traverse\n");
		
		postOrderTraverseTree(rootNode, "");
	}
	
	public void postOrderTraverseTree(Node focusNode, String a_indent)
	{
		if(null != focusNode && !focusNode.getData().equals("eps")) {

			for(int i = 0; i < focusNode.getKids().length; i++)
			{
				String indent = "";
				if( focusNode.getKids()[i] != null) {
					for(int j = 0; j < focusNode.getKids()[i].depth(); j++)
					{
						
						indent += "  ";
					}
				}
				preOrderTraverseTree(focusNode.getKids()[i], indent);
			}
			System.out.println(a_indent + focusNode);
		}
	}
	
	public void preOrderTraverseTree(Node focusNode, String a_indent) {

		if(null != focusNode ) {//&& !focusNode.getData().equals("eps")) {

			System.out.println(a_indent + focusNode);

			for(int i = 0; i < focusNode.getKids().length; i++)
			{
				String indent = "";
				if( focusNode.getKids()[i] != null) {
					for(int j = 0; j < focusNode.getKids()[i].depth(); j++)
					{
						
						indent += "  ";
					}
				}
				preOrderTraverseTree(focusNode.getKids()[i], indent);
			}
		}
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
					prediction = m_stack.peek().getData();
				}
				//if recovery cannot be made let user know
				if(!found && m_stack.isEmpty())
				{
					throw new Exception("Error: UNRECOVERABLE, critical damage ");
				}
			}

			while(m_lex.hasNextToken() && !prediction.equals(token.getWord()))
			{
//				System.out.println("token.getWord() = " + token.getWord() +
//						", prediction = " + prediction);
				token = m_lex.nextToken();
			}
//			System.out.println("token.getWord() = " + token.getWord());
			System.out.println("Skipping to the end of the <program-construct> at line " +
					token.getLineNumber());	
		}
	}
	
	public void pushTheRule(Node currentNode, Rule rule)
	{
		String[] ruleRHS = rule.getRHS();

		for(int i = ruleRHS.length - 1; i >= 0; i--)
		{
			if(null != ruleRHS[i])
			{
				Node node = new Node(ruleRHS[i]);
				currentNode.add(node, i);           // builds the ParseTree
				m_stack.push(node);
			}
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
