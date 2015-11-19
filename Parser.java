package com.csulb.compiler;

import java.util.Stack;

public class Parser {
	
	Lexer m_lex;
	Stack<Node> m_stack;
	Matrix m_matrix;
	String[] m_endTokens = { "semi", "brace2", "EOF" };
	boolean errors = false;
	Node rootNode;
	Rule lastRule = null;
	TreeNode root = null;
	
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
		m_stack.push(new Node("PGM"));
		
		Token token = m_lex.nextToken();
		String tokenType = token.getWord();
		String data = token.getData();
		String prediction = null;
		
		while(true)
		{
			Node currentNode = m_stack.pop();
			prediction = currentNode.getData();
			
			System.out.println("\nprediction = " + prediction + ", token = " + tokenType + ", data = " + data );
			
			addNode(currentNode, token);
			
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
				Rule rulex = new Rule(prediction, rule);
				
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
					System.out.println("token = " + token.getWord());
				}
				else {
					lastRule = rulex;
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
//					System.out.println("m_endTokens[i] = " + m_endTokens[i] +
//							", prediction = " + prediction);
					
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
				System.out.println("token.getWord() = " + token.getWord() +
						", prediction = " + prediction);
				token = m_lex.nextToken();
			}
			System.out.println("token.getWord() = " + token.getWord());
			System.out.println("Skipping to the end of the <program-construct> at line " +
					token.getLineNumber());	
		}
	}
	
	public void predictRule(Rule rx)
	{
		Node mom = m_stack.pop();
		//for(int i = )
	}
	
	public void pushTheRule(Node currentNode, Rule rule)
	{
		String[] ruleRHS = rule.getRHS();

		for(int i = ruleRHS.length - 1; i >= 0; i--)
		{
			if(null != ruleRHS[i])
			{
				m_stack.push(new Node(ruleRHS[i]));			
			}
		}
		
		Node[] kids = new Node[ruleRHS.length];
		for(int i = 0; i < ruleRHS.length; i++)
		{
			if(null != ruleRHS[i])
			{
				kids[i] = new Node(ruleRHS[i]);
			}
		}
		
		//addToParseTree(currentNode, kids);
	}
	
	public void addNode(Node a_node, Token a_token)
	{
		if(null == root)
		{
			//rootNode = a_node;
			root = a_node;
		}
		else
		{
			Node focusNode = rootNode;
			if(!m_matrix.isNonTerminal(a_node.getData()))
			{
				focusNode.addKid(a_node);
			}
			else 
			{
				Node parent;
				while(true)
				{
					parent = focusNode;
				}
			}
		}
	}
	
	public void addToParseTree(Node currentNode, Node[] kids)
	{
		if(null == rootNode)
		{
			rootNode = currentNode;
			System.out.println("root: " + rootNode.hasKids());
			rootNode.setKids(kids);
			System.out.println(kids.length + " " + rootNode.getKids().length);
			for(int i = 0; i < rootNode.getKids().length; i++)
			{
				System.out.println("\t" + rootNode.getKids()[i]);
			}
			System.out.println("root: " + rootNode.hasKids());
		}
		else 
		{
			System.out.println("ADD TO PT");
			int index = 0;
			for(int i = 0; i < rootNode.getKids().length; i++)
			{
				if(rootNode.getKids()[i].getData() == currentNode.getData())
				{
					currentNode = rootNode.getKids()[i];
				}
			}
			if(m_matrix.isNonTerminal(currentNode.getData()))
			{
				currentNode.setKids(kids);
				for(int i = 0; i < currentNode.getKids().length; i++)
				{
					System.out.println("\t" + currentNode.getKids()[i]);
				}
			}
		}
		
		
/*
		String[] ruleRHS = rule.getRHS();
		Node[] nodes = new Node[5];
		for(int i = 0; i < ruleRHS.length; i++)
		{
			if(null != ruleRHS[i])
			{
				System.out.println("ruleRHS = " + ruleRHS[i]);
				nodes[i] = new Node(new Symbol(ruleRHS[i]));
			}
		}
		
		for(int i = ruleRHS.length - 1; i >= 0; i--)
		{
			if(null != ruleRHS[i])
			{
				Node newnew = new Node(new Symbol(ruleRHS[i]));
				m_stack.push(newnew);			
			}
		}
		for(int i = 0; i < nodes.length; i++) 
		{
			if(null != nodes[i])
			{	
				System.out.println(nodes[i].getSymbol());
			}
		}
		Node node = new Node(new Symbol(rule.getLHS()));
		node.setKids(nodes);
		System.out.println(node);
//		ParseTree pt = new ParseTree();
//		pt.addNode(node);
//		System.out.println("In preorderTraverseTree");
//		pt.preorderTraverseTree(node);
//		System.out.println("Out preorderTraverseTree");
		
		if(null == rootNode)
		{
			rootNode = node;
			System.out.println("added");
		}
*/
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
