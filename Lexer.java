package com.csulb.compiler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
	
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
		//printList();
	}
	
	public void readProgram(String fileName) throws IOException 
	{
		FileReader fileReader = new FileReader(fileName);
		
		String fileContents = "";
		int i;
		while((i = fileReader.read()) != -1) {
		   char ch = (char)i;
//		   System.out.println(i + ", " + ch);
//		   System.out.println("isChar = " + isChar(i) + "\n");
		 
		   fileContents += ch; 
		   
		   characterList.add(ch);
		}
		
		//System.out.println(fileContents);
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
		
		while(position < characterList.size())
		{
			currentChar = nextChar();
			
			System.out.println("\nposition = " + position + ", currentChar = " + currentChar 
					+ " , " + (int)currentChar);
			
			
			
			if(9 != (int)currentChar &&     // horizontal tab
				10 != (int)currentChar && 	// new line feed
				13 != (int)currentChar && 	// carriage return
				32 != (int)currentChar &&	// space
				!getCategory(currentChar).equals("No Category"))	// skip anything not a number
																	// or digit for now
			{
				String cat = getCategory(currentChar);
				
				switch(state)
				{
				case START:
//					String cat = getCategory(currentChar);
					System.out.println("cat = " + cat);
					
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
					case brace1 + "":
						tokenList.add(new Token(state, brace1 + "", 0));
						state = START;
						break;
					default:
						break;
					}
					break;
				case LU1:
//					String catt = getCategory(currentChar);
					System.out.println("in LU1, catt = " + cat + ", state = " + state);
					
					if(LUTR == cat)
					{
						// save_id_char(currentChar);
						state = ID;
					}
					else 
					{
						backup();
						// do_id();
					}
					
					break;
				case ID:
//					String cattt = getCategory(currentChar);
					System.out.println("in ID, cattt = " + cat + ", state = " + state);
					if(LUTR == cat)
					{
						// save_id_char(currentChar);
						state = ID;
					}
					else 
					{
						backup();
						// do_id();
					}
					
					//position++;
					break;
				case "SL1":
					position += 1;
					currentChar = nextChar();
					if('/' == currentChar)
					{
						System.out.println("made it");
						position += 1;
						currentChar = nextChar();
						while(10 != currentChar)
						{
							position += 1;
							currentChar = nextChar();
						}
						lastPosition = position;
					}
					else 
					{
						backup();
					}
				default:
					state = "";
					System.out.println("state = " + state);
					break;
				}
			}
			else if(32 == currentChar && state.equals(ID))
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
			else if(10 == currentChar)
			{
				lastPosition = position;
				state = START;
			}
			else if(13 == currentChar || 9 == currentChar)
			{
				lastPosition = position;
			}

			System.out.println("break");
			position++;
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
		//int characterInt = (int)character;
		
		if(isChar(character) && !LU1.equals(state) && !ID.equals(state)) 
		{ 
			return LU1; 
		}
		else if(isCharOrUnderScore(character)) { return LUTR; }
		else if(isDigit(character)) { return DIGIT; }
		else if(isSign(character)) { return SIGN; }
		else if(isWhiteSpace(character)) { return WHITESPACE; } 
		else if(isSL1(character)) { return "SL1"; } 
		else if(isBrace1(character)) { return brace1 + ""; } 
		
		
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
	
	public boolean isBrace1(char character)
	{
		return character == brace1;
	}
	
	public boolean isSL1(char character)
	{
		if('/' == character)
		{
			state = "SL1";
			return true;
		}
		return false;
	}
}
