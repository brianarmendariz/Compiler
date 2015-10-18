package com.csulb.compiler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
	
	// States
	private final String START = "START";
	private final String COMMENT = "COMMENT";
	private final String LU1 = "LU1";
	private final String LUTR = "LUTR";
	private final String DIGIT = "DIGIT";
	private final String DOT = "DOT";
	private final String ID = "ID";
	private final String SIGN = "SIGN";
	private final String WHITESPACE = "WHITESPACE";
	private final String OTHER = "OTHER";
	private final String SL1 = "SL1";
	private final String QUOTE = "QUOTE";
	
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
	
	private String[] keywords = { kwdelse, kwdelseif, kwdfcn, kwdif, 
			kwdmain, kwdprog, kwdreturn, kwdvars, kwdwhile };
	
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
	private final char exclamation = '!';
	
	// Sigils
	private final char at = '@';
	private final char hash = '#';
	private final char usd = '$';
	
	private ArrayList<Character> characterList;
	private ArrayList<Token> tokenList;
	
	private int position;
	private int lastPosition;
	private int tokenPosition;
	
	private String state;
	
	public Lexer(String fileName) 
	{
		characterList = new ArrayList<Character>();
		tokenList = new ArrayList<Token>();
		tokenPosition = 0;
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
	
	public void lexing() 
	{
		position = 0;
		lastPosition = 0;
		char currentChar = 0;
			
		do
		{
			currentChar = nextChar();
			
			if(!getCategory(currentChar).equals("No Category"))	// skip anything not a number
																	// or digit for now
			{
				String cat = getCategory(currentChar);
				
				System.out.println("position " + position + " , currentChar = " + currentChar +
						"\nlastPosition = " + lastPosition + " , " + (int)currentChar +
						"\nstate = " + state + " , category = " + cat + "\n\n");
				
				switch(state)
				{
				case START:
					switch(cat)
					{
					case LU1:
						lastPosition = position;
						state = LU1;
						break;
					case DIGIT:
						lastPosition = position;
						state = DIGIT;
						break;
					case QUOTE:
						lastPosition = position;
						state = QUOTE;
						break;
					case SL1:
						lastPosition = position;
						state = COMMENT;
						break;
					case OTHER: 
						lastPosition = position;
						state = OTHER;
						break;	
					case WHITESPACE:
						lastPosition = position + 1;
						break;
					default:
						break;
					}
					break;
				case LU1:
					if(WHITESPACE == cat || OTHER == cat)
					{
						backup();
						addTokenToList();
					}
					else if(LUTR == cat)
					{
						state = ID;
					}
					
					break;
				case ID:	
					if(LUTR == cat)
					{
						state = ID;
					}
					else if(WHITESPACE == cat || OTHER == cat)
					{
						System.out.println("WHITESPACE pos = " + position +", lp = " + lastPosition + "\n");
						backup();
						addTokenToList();
						System.out.println("pos = " + position +", lp = " + lastPosition + "\n\n\n");
					}
					break;
				case OTHER:
					if('=' == currentChar || '!' == currentChar ||
					   '<' == currentChar || '>' == currentChar ||
					   hasPairedDelimeters())
					{
						System.out.println("hasPairedDelimeters");
						addTokenToList();
					}
					else if(WHITESPACE == cat || OTHER == cat || DIGIT == cat || QUOTE == cat || LU1 == cat)
					{
						System.out.println("in other state! pos = " + position +", lp = " + lastPosition + "\n");
						backup();
						addOtherTokenToList();
						System.out.println("pos = " + position +", lp = " + lastPosition + "\n");
					}
					break;
				case DIGIT:
					if(WHITESPACE == cat || OTHER == cat) 
					{
						backup();
						addDigitTokensToList();
					}
					else if(DIGIT == cat)
					{
						state = DIGIT;
					}
					else if(DOT == cat)
					{
						state = DOT;
					}
					break;
				case DOT:
					if(WHITESPACE == cat || OTHER == cat || QUOTE == cat) 
					{
						backup();
						addDoubleTokensToList();
					}
					else if(DIGIT == cat)
					{
						state = DOT;
					}
					else if(DOT == cat)
					{
						System.out.println("ERROR: NumberFormatException, Too Many .'s in Double");
						backup();
						addDoubleTokensToList();
						state = START;
					}
					break;
				case QUOTE:
					System.out.println("currentChar = " + currentChar);
					extractString(currentChar);
					break;
				case COMMENT:
					System.out.println("\nIN SL1\n");
					System.out.println("pos = " + position +", lp = " + lastPosition + "\n");
					currentChar = nextChar();
					if(slash == currentChar)
					{
						extractComment(currentChar);
					}
					else
					{
						System.out.println("Not a Comment.");
					}
					break;
				default:
					state = "";
					break;
				}
			}
			
			// CAN ADD AN ELSE
			
			
			position++;
		} while(position <= characterList.size());
		{
			System.out.println("end of do while");
			checkForKeywords();
		}
	}
	
	public char nextChar()
	{
		if(position == characterList.size())
		{
			return ' ';
		}
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
		else if(isDot(character)) { return DOT; }
		else if(isQuote(character)) { return QUOTE; }
//		else if(isSign(character)) { return SIGN; }
		else if(isWhiteSpace(character)) { return WHITESPACE; } 
		else if(isSL1(character)) { return SL1; }
		else if(isOther(character)) { return OTHER; } 
		
		return "No Category";
	}
	
	public boolean isCarriageReturn(char character)
	{
		return 13 == character;
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
		return (character >= 48 && character <= 57);
	}
	
	public boolean isDot(char character)
	{
		return 46 == character;
	}
	
	public boolean isQuote(char character)
	{
		return 34 == character;
	}
	
	public boolean isSign(char character)
	{
		return (character == 43 || character == 45);
	}
	
	public boolean isWhiteSpace(char character)
	{
		return (0 <= character && character <= 32);
	}
	
	public boolean isSlash(char character) 
	{
		return character == 47;
	}
	
	public boolean isOther(char character)
	{
		if(brace1 == character) { return true; } 
		else if(brace2 == character) { return true; } 
		else if(parens1 == character) { return true; } 
		else if(parens2 == character) { return true; } 
		else if(angle1 == character) { return true; } 
		else if(angle2 == character) { return true; } 
		else if(bracket1 == character) { return true; } 
		else if(bracket2 == character) { return true; }
		else if(comma == character) { return true; } 
		else if(semi == character) { return true; } 
		else if(equal == character) { return true; }
		else if(aster == character) { return true; }
		else if(slash == character) { return true; }
		else if(caret == character) { return true; }
		else if(plus == character) { return true; }
		else if(minus == character) { return true; }
		else if(exclamation == character) { return true; }
		else if(at == character) { return true; }
		else if(hash == character) { return true; }
		else if(usd == character) { return true; }
		
		return false;
	}
	
	public boolean isSL1(char character)
	{
		if('/' == character) { return true; }
		
		return false;
	}
		
	public boolean hasPairedDelimeters()
	{
		int nextPos = position + 1;
		if(nextPos < characterList.size()){ 
			String nextString = characterList.get(position) + characterList.get(nextPos) + "";
			
			if(opeq.equals(nextString)) { return true; }
			else if(opne.equals(nextString)) { return true; }
			else if(ople.equals(nextString)) { return true; }
			else if(opge.equals(nextString)) { return true; } 
		}	
		return false;
	}
	
	public void addTokenToList()
	{
		String data = "";
	
		for(int i = lastPosition; i <= position; i++)
		{
			data += characterList.get(i);
		}
		tokenList.add(new Token(state, data, -1, -1.0));
		
		state = START;
	}
	
	public void addOtherTokenToList()
	{
		String data = characterList.get(position) + "";
		tokenList.add(new Token(state, data, -1, -1.0));
		state = START;
	}
	
	public void addDigitTokensToList()
	{
		String data = "";
		
		for(int i = lastPosition; i <= position; i++)
		{
			data += characterList.get(i);
		}
		int number = Integer.parseInt(data);
		tokenList.add(new Token(state, null, number, -1.0));
		
		state = START;
	}
	
	public void addDoubleTokensToList()
	{
		String data = "";
		
		for(int i = lastPosition; i <= position; i++)
		{
			data += characterList.get(i);
		}
		double fp = Double.parseDouble(data);
		tokenList.add(new Token("DOUBLE", null, -1, fp));
		
		state = START;
	}
	
	public void extractComment(char currentChar)
	{
		position += 1;
		currentChar = nextChar();
		while(10 != currentChar && position < characterList.size())
		{
			position += 1;
			currentChar = nextChar();
		}
		state = START;		
	}
	
	public void extractString(char currentChar)
	{
		do
		{
			System.out.println("in extractString, pos = " + position);
			position += 1;
			currentChar = nextChar();
		}
		while(34 != currentChar && 10 != currentChar);
		{
		}
		
		if(10 == currentChar)
		{
			System.out.println("\nERROR: Only One Quote In String\n");
		}
		else
		{
			addTokenToList();
		}
		state = START;
	}
	
	public Token nextToken()
	{
		Token token = tokenList.get(tokenPosition);
		tokenPosition++;
		return token; 
	}
	
	public int getSizeOfTokenList()
	{
		return tokenList.size();
	}
	
	public void checkForKeywords()
	{
		for(int i = 0; i < tokenList.size(); i++)
		{
			for(int j = 0; j < keywords.length; j++)
			{
				if(tokenList.get(i).getData() != null && 
						tokenList.get(i).getData().equalsIgnoreCase(keywords[j]))
				{
					//tokenList.get(i).setWord(keywords[j]);
					tokenList.get(i).setWord("KEYWORD");
				}
			}
		}
	}
}
