package com.csulb.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Lexer {
	
	// States
	private final String START = "START";
	private final String COMMENT = "COMMENT";
	private final String LU1 = "LU1";
	private final String LUTR = "LUTR";
	private final String DIGIT = "DIGIT";
	private final String ID = "ID";
	private final String SIGN = "SIGN";
	private final String WHITESPACE = "WHITESPACE";
	private final String SLASH = "SLASH";
	private final String OTHER = "OTHER";
	private final String SL1 = "SL1";
	private final String BRACE1 = "BRACE1";
	private final String PARENS1 = "PARENS1";
	
	// Signs
	private final char signPlus = '+';
	private final char signMinus = '-';
	
	// Multi-char operators
	private final String opeq = "==";
	private final String opne = "!=";
	private final String ople = "<=";
	private final String opge = ">=";
	
	// Keywords
	private final String kwdelse = "else";
	private final String kwdelseif = "elseif";
	private final String kwdfcn = "fcn";
	private final String kwdif = "if";
	private final String kwdmain = "main";
	private final String kwdprog = "prog";
	private final String kwdreturn = "return";
	private final String kwdvars = "vars";
	private final String kwdwhile = "while";
	
	// Paired delimeters
	private final char angle1 = '<';
	private final char angle2 = '>';
	private final char brace1 = '{';
	private final char brace2 = '}';
	private final char bracket1 = '[';
	private final char bracket2 = ']';
	private final char parens1 = '(';
	private final char parens2 = ')';
	
	// Unpaired delimeters
	private final char comma = ',';
	private final char semi = ';';
	
	// Single-char operators 
	private final char equal = '=';
	private final char aster = '*';
	private final char slash = '/';
	private final char caret = '^';
	private final char plus = '+';
	private final char minus = '-';
	
	// Sigils
	private final char at = '@';
	private final char hash = '#';
	private final char usd = '$';
	
	//private char[] chArray;
	private ArrayList<Character> characterList;
	private ArrayList<Token> tokenList;
	
	private int position;
	private int lastPosition;
	
	private String state;
	
	public Lexer(String fileName) 
	{
		characterList = new ArrayList<Character>();
		tokenList = new ArrayList<Token>();
		state = "START";
		try
		{
			readProgram(fileName);
		} catch(Exception e) {
			e.printStackTrace();
		}
		lexing();
	}
	
	public void readProgram(String fileName) throws IOException 
	{
		FileReader fileReader = new FileReader(fileName);
		int i;
		
		while((i = fileReader.read()) != -1) {
		   char ch = (char)i;
		   
		   characterList.add(ch);
		}
	}
	
	public void printList()
	{
		//System.out.println(characterList.size());
		for(int i = 0; i < tokenList.size(); i++)
		{
			System.out.println(tokenList.get(i));
//			System.out.println((int)characterList.get(i));
		}
	}
	
	public void lexing() 
	{
		position = 0;
		lastPosition = 0;
		char currentChar = 0;
		
		try {
			PrintWriter writer = new PrintWriter("the-file-name.txt", "UTF-8");
			
			while(position < characterList.size())
			{
				currentChar = nextChar();
				
				System.out.println("\nposition = " + position + ", currentChar = " + currentChar 
						+ " , " + (int)currentChar);
				writer.println("\nposition = " + position + ", currentChar = " + currentChar 
						+ " , " + (int)currentChar);
				
				
				if(9 != (int)currentChar &&     // horizontal tab
					10 != (int)currentChar && 	// new line feed
					13 != (int)currentChar && 	// carriage return
					!getCategory(currentChar).equals("No Category"))	// skip anything not a number
																		// or digit for now
				{
					String cat = getCategory(currentChar);
					System.out.println("state = " + state);
					
					switch(state)
					{
					case START:
//						String cat = getCategory(currentChar);
						System.out.println("cat = " + cat);
						writer.println("cat = " + cat);
						
						switch(cat)
						{
						case LU1:
							state = LU1;
							break;
						case DIGIT:
							state = DIGIT;
							break;
						case SIGN:
							//position++;
							state = "num1"; 
						// NEED ANOTHER CASE FOR OTHER case OTHER:
						case BRACE1:
							addOtherTokenToList(brace1 + "");
//							tokenList.add(new Token(state, brace1 + "", 0));
//							state = START;
							break;
						default:
							break;
						}
						break;
					case LU1:
//						String catt = getCategory(currentChar);
						System.out.println("in LU1, catt = " + cat + ", state = " + state);
						writer.println("in LU1, catt = " + cat + ", state = " + state);
						
						if(LUTR == cat)
						{
							// save_id_char(currentChar);
							state = ID;
						}
						else if(WHITESPACE == cat) 
						{
							writer.println("made it to LU1!");
							addTokenToList();
						}
						else 
						{
							backup();
							// do_id();
						}
						
						break;
					case ID:
						System.out.println("in ID, cattt = " + cat + ", state = " + state);
						writer.println("in ID, cattt = " + cat + ", state = " + state);
						
						if(LUTR == cat)
						{
							// save_id_char(currentChar);
							state = ID;
						}
						else if(WHITESPACE == cat)
						{
							addTokenToList();
							//state = START;
						}
						else 
						{
							//backup();
							// do_id();
							addTokenToList();
							state = PARENS1;		
						}
						
						break;
					case PARENS1: 
						if(WHITESPACE == cat)
						{
							lastPosition = position + 1;
							state = START;
						}
						else 
						{
							addOtherTokenToList(parens1 + "");
//							tokenList.add(new Token(state, parens1 + "", 0));
//							state = START;
						}
						break;
					case SL1:
						position += 1;
						currentChar = nextChar();
						if('/' == currentChar)
						{
							currentChar = extractComment(currentChar);
						}
						else 
						{
							backup();
						}
						break;
					default:
						state = "";
						System.out.println("default state = " + state);
						writer.println("default state = " + state);
						break;
					}
				}
				else if(10 == currentChar)
				{
					lastPosition = position + 1;
					state = START;
				}
				else if(13 == currentChar || 9 == currentChar)
				{
					lastPosition = position + 1;
					state = START;
					System.out.println("state = "+ state);
					writer.println("state = "+ state);
				}

				System.out.println("state = " + state + "\nbreak");
				writer.println("state = " + state + "\nbreak");
				position++;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public char nextChar()
	{
		return characterList.get(position);
	}
	
	public void backup()
	{
		position--;
	}
	
	public String getCategory(char character)
	{		
		if(isChar(character) && !LU1.equals(state) && !ID.equals(state)) 
		{ 
			return LU1; 
		}
		else if(isCharOrUnderScore(character)) { return LUTR; }
		else if(isDigit(character)) { return DIGIT; }
		else if(isSign(character)) { return SIGN; }
		else if(isWhiteSpace(character)) { return WHITESPACE; } 
		else if(isSL1(character)) { return "SL1"; } 
		else if(isOther(character)) { return getOther(character); } 
		
		
		return "No Category";
	}
	
	/**
	 * Function to check for character a-z or A-Z
	 * @param character
	 * @return
	 */
	public boolean isChar(char character) 
	{
		if((character >= 65 && character <= 90) ||
			(character >= 97 && character <= 122))
			{
				return true;
			}
		return false;
	}

	/**
	 * Function to check for character a-z or A-Z or _
	 * @param character
	 * @return
	 */
	public boolean isCharOrUnderScore(char character) 
	{
		if((character >= 65 && character <= 90) ||
			(character >= 97 && character <= 122) ||
			(character == 95))
			{
				return true;
			}
		return false;
	}
	
	public boolean isDigit(char character)
	{
		return (character >= 0 && character <= 9);
	}
	
	public boolean isSign(char character)
	{
		return (character == 43 || character == 45);
	}
	
	public boolean isWhiteSpace(char character)
	{
		return character == 32;
	}
	
	public boolean isSlash(char character) 
	{
		return character == 47;
	}
	
	public String getOther(char character)
	{
		if('{' == character) { return BRACE1; } 
		if('(' == character) { return PARENS1; } 
		
		return "";
	}
	
	public boolean isOther(char character)
	{
		if('{' == character) { return true; } 
		if('(' == character) { return true; }
		
		return false;
	}
	
	public boolean isSL1(char character)
	{
		if('/' == character)
		{
			state = SL1;
			return true;
		}
		return false;
	}
	
	public void addTokenToList()
	{
		String data = "";
		
		for(int i = lastPosition; i < position; i++)
		{
			data += characterList.get(i);
		}
		tokenList.add(new Token(state, data, 0));
		
		state = START;
		lastPosition = position + 1;
	}
	
	public void addOtherTokenToList(String data)
	{
		tokenList.add(new Token(state, data, 0));
		state = START;
	}
	
	public char extractComment(char currentChar)
	{
		position += 1;
		currentChar = nextChar();
		while(10 != currentChar)
		{
			position += 1;
			currentChar = nextChar();
		}
		lastPosition = position;
		state = START;
		
		return currentChar;
	}
}
