package com.csulb.compiler;

public class Node extends Symbol {
	
	private final int KID_SIZE = 4;
	
	private Node[] kids;
	private int kidCount;
	private int position;
	
	private Node parent;

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
	
	public void add(Node kid)
	{
		kids[position] = kid;
		++position;
		
		kid.parent = this;
	}
	
	public void add(Node kid, int pos)
	{
		kids[pos] = kid;
		
		kid.parent = this;
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

	public int depth ()
	{
	  int depth = recurseDepth( parent, 0 );
	  return depth;
	}

	private int recurseDepth(Node node, int depth)
	{
	  if (node == null)  // reached top of tree
	  {
	    return depth;
	  }
	  else
	  {
	    return recurseDepth(node.parent, depth + 1);
	  }
	}

	public String toString()
	{
		return getData();
	}
}
