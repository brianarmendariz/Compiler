package com.csulb.compiler;

public class Token {
	
	private String word;
	private String data;
	
	public Token()
	{
		word = null;
		data = null;
	}
	
	public Token(String w, String d)
	{
		word = w;
		data = d;
	}
	
	public Token(String w, int n)
	{
		word = w;
		data = null;
	}
	
	public Token(String w, double f)
	{
		word = w;
		data = null;
	}
	
	public Token(String w, String d, int n, double f)
	{
		word = w;
		data = d;
	}

	public String getWord()
	{
		return word;
	}
	
	public String getData()
	{
		return data;
	}

	public void setWord(String w)
	{
		word = w;
	}
	
	public void setData(String d)
	{
		data = d;
	}
	
	public String toString()
	{
/*
		if(data != null) return data;
		else if(number != -1) return number + "";
		else if(floatingPoint != -1.0) return floatingPoint + "";
		return "";
*/
		return "\nword = [" + word + "], data = [" + data + "]";
				
	}
}
