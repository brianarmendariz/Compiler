package com.csulb.compiler;

import java.util.Stack;

public class Parser {
	
	Lexer m_lex;
	Stack<TreeNode> m_stack;
	Matrix m_matrix;
	String[] m_endTokens = { "semi", "brace2", "EOF" };
	boolean errors = false;
	Node rootNode;
	Rule lastRule = null;
	TreeNode root = null;
	TreeNode lastParent = null;
	boolean lastAddT = false;
	
	public Parser(String inputFile) 
	{
		m_lex = new Lexer(inputFile);
		m_stack = new Stack<TreeNode>();
		m_matrix = new Matrix();
		rootNode = null;
	}
	
	public void parse() //throws Exception
	{
		m_stack.push(new TreeNode("EOF"));
		m_stack.push(new TreeNode("PGM"));
		
		Token token = m_lex.nextToken();
		String tokenType = token.getWord();
		String data = token.getData();
		String prediction = null;
		
		while(true)
		{
			TreeNode currentNode = m_stack.pop();
			prediction = currentNode.getData();
			
			System.out.println("\nprediction = " + prediction + ", token = " + tokenType + ", data = " + data );
			
			addNode(currentNode, token, lastRule);
			
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
		System.out.println(sb.toString() + "\n");
		
		System.out.println(root + ".............");
		TreeNode[] kids = root.children();
		for(int i = 0; i < kids.length; i++)
		{
			System.out.println(i + ": " + kids[i]);
		}
		System.out.println(kids[2] + ".............");
		TreeNode[] kids1 = kids[2].children();
		for(int i = 0; i < kids1.length; i++)
		{
			System.out.println(i + ": " + kids1[i]);
		}
		System.out.println(kids1[0] + ".............");
		TreeNode[] kids2 = kids1[0].children();
		for(int i = 0; i < kids2.length; i++)
		{
			System.out.println(i + ": " + kids2[i]);
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
		TreeNode mom = m_stack.pop();
		//for(int i = )
	}
	
	public void pushTheRule(TreeNode currentNode, Rule rule)
	{
		String[] ruleRHS = rule.getRHS();

		for(int i = ruleRHS.length - 1; i >= 0; i--)
		{
			if(null != ruleRHS[i])
			{
				m_stack.push(new TreeNode(ruleRHS[i]));			
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
	
	public void addNode(TreeNode a_node, Token a_token, Rule a_rule)
	{
		Rule rule = new Rule(a_node.getData(), a_token.getWord());
		System.out.println("a_node = " + a_node.getData() + ", a_token = " + a_token.getWord()
				+ ", rule = " + a_rule);
		
		if(null == root)
		{
			//rootNode = a_node;
			root = a_node;
			lastParent = root;
		}
		else if(a_node.getData().equals(a_token.getWord()))
		{
			System.out.println("here");
//			TreeNode focusNode = lastParent;
			boolean notFound = true;
			for(int i = 0; i < lastParent.children().length; i++)
			{
				if(lastParent.children()[i].getData().equals(a_token.getWord()))
				{
					System.out.println("Token already added");
					notFound = false;
				}
			}
			if(notFound)
			{
				
				System.out.println("notFound lp = " + lastParent + ", " + a_node.getData());
				lastParent.add(a_node);
				lastAddT = true;
			}
		}
		else if(lastAddT && lastRule.getRHS()[0].equals("eps"))
		{
			System.out.println("lp's lp = " + lastParent.getParent());
			lastParent = lastParent.getParent();
		}
		else if(lastRule.getRHS()[0].equals("eps"))
		{
			System.out.println("lp's lp's lp = " + lastParent.getParent().getParent());
			lastParent = lastParent.getParent().getParent();
		}
		else if(m_matrix.isNonTerminal(a_node.getData()))
		{
			System.out.println("node is NON-T");
			lastParent.add(a_node);
			TreeNode node = new TreeNode(a_rule.getRHS()[0]);
/*
			TreeNode node = null;
			for(int i = 0; i < lastParent.children().length; i++)
			{
				if(lastParent.children()[i].getData().equals(a_token.getWord()))
				{
					System.out.println("Token already added");
				}
				else 
				{
					node = lastParent.children()[i];
					break;
				}
			}
*/
			lastParent = a_node;
//			lastRule = new Rule(lastParent.getData(), a_token.getWord());
//			System.out.println(lastParent + ", " + lastRule);
//			addNode(node, a_token, lastRule);
			
			lastAddT = true;
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
