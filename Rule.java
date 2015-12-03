package com.csulb.compiler;

public class Rule {
	
	private String description;
	private String lhs;
	private String[] rhs;
	private int ruleNumber;
	private final int KID_COUNT = 4;
	
	public Rule(String d)
	{
		description = d;
	}

	public Rule(String d, int rn)
	{
		description = d;
		ruleNumber = rn;
	}
	
	
	public Rule(String l, String r)
	{
		rhs = new String[KID_COUNT];
//		System.out.println("lhs = " + l + ", rhs = " + r);
		
		lhs = l;
		if(r.contains(" "))
		{
			int pos = 0;
			int index = 0;
			for(int i = 0; i < r.length(); i++)
			{
				if(r.charAt(i) == 32)
				{
					rhs[index] = r.substring(pos, i);
					pos = i + 1;
					++index;
				}
				else if(i == r.length() - 1)
				{
					rhs[index] = r.substring(pos);
					System.out.println("rhs = " + rhs[index]);
					pos = i + 1;
					++index;
				}
			}
		}
		else 
		{
			rhs[0] = r;
		}
	}
	
	public Rule(String l, String r, int rn)
	{
		ruleNumber = rn;
		rhs = new String[KID_COUNT];
//		System.out.println("lhs = " + l + ", rhs = " + r);
		
		lhs = l;
		if(r.contains(" "))
		{
			int pos = 0;
			int index = 0;
			for(int i = 0; i < r.length(); i++)
			{
				if(r.charAt(i) == 32)
				{
					rhs[index] = r.substring(pos, i);
					pos = i + 1;
					++index;
				}
				else if(i == r.length() - 1)
				{
					rhs[index] = r.substring(pos);
					System.out.println("rhs = " + rhs[index]);
					pos = i + 1;
					++index;
				}
			}
		}
		else 
		{
			rhs[0] = r;
		}
	}
	
	public String[] getRHS()
	{
		return rhs;
	}
	
	public String getLHS()
	{
		return lhs;
	}
	
	public String getRule()
	{
		return description;
	}
	
	public int getRuleNumber()
	{
		return ruleNumber;
	}
	
	public void setRuleNumber(int rn)
	{
		ruleNumber = rn;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(lhs + ",");
		for(int i = 0; i < rhs.length; i++)
		{
			sb.append(" " + rhs[i]);
		}
		return sb.toString();
	}
}
