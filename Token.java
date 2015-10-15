package com.csulb.compiler;

public class Token {
	
	private String word;
	private String data;
	private int number;
	
	public Token()
	{
		word = null;
		data = null;
		number = 0;
	}
	
	public Token(String w, String d, int n)
	{
		word = w;
		data = d;
		number = n;
	}
	
	public String toString()
	{
		return "\nword = [" + word + "], data = [" + data + "], number = [" + number + "]";
				
	}
}
