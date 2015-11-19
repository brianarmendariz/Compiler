package com.csulb.compiler;

public class Node extends Symbol {
	
	private final int KID_SIZE = 4;
	
	private Node[] kids;
	private int kidCount;
	private int position;

	public Node()
	{
		super("");
		kids = new Node[KID_SIZE];
		position = 0;
	}
	
	public Node(String sym)
	{
		super(sym);
		kids = new Node[KID_SIZE];
		position = 0;
	}
	
	public Node(String sym, int kidAmount)
	{
		super(sym);
		position = 0;
		if(kidAmount > kidCount)
		{
			kids = new Node[KID_SIZE];
		}
		else 
		{
			kids = new Node[kidAmount];
		}
	}
	
	public void addKid(Node kid)
	{
		kids[position] = kid;
		++position;
	}
	
	public boolean hasKids()
	{
		for(int i = 0; i < kids.length; i++)
		{
			if(kids[i] != null)
				return true;
		}
		return false;
	}
	
	public void setKids(Node[] k)
	{
		if(k.length <= KID_SIZE)
		{
			for(int i = 0; i < k.length; i++)
			{
				kids[i] = k[i];
			}	
		}
	}
	
	public Node[] getKids()
	{
		return kids;
	}
	
	public String toString()
	{
/*
		StringBuilder sb = new StringBuilder();
		sb.append("Parent: " + getData() + "\nKids:\n");
		for(int i = 0; i < kids.length; i++)
		{
		 	if(null != kids[i])
		 	{
		 		sb.append("\t" + i + " " + kids[i].getData() + "\n");
		 	}
		}
		return sb.toString();
*/
		return getData();
	}
	

}
