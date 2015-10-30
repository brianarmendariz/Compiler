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
	private final String WHITESPACE = "WHITESPACE";
	private final String OTHER = "OTHER";
	private final String SL1 = "SL1";
	private final String QUOTE = "QUOTE";
	private final String EOF = "EOF";
	
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
	private final String kwdinput = "input";
	private final String kwdmain = "main";
	private final String kwdprint = "print";
	private final String kwdprog = "prog";
	private final String kwdreturn = "return";
	private final String kwdvars = "vars";
	private final String kwdwhile = "while";
	
	private String[] keywords = { kwdelse, kwdelseif, kwdfcn, kwdif, 
			kwdinput, kwdmain, kwdprint, kwdprog, kwdreturn, kwdvars, kwdwhile };
	
	// Paired delimeters
	private final char angle1 = '<';
	private final char angle2 = '>';
	private final char brace1 = '{';
	private final char brace2 = '}';
	private final char bracket1 = '[';
	private final char bracket2 = ']';
	private final char parens1 = '(';
	private final char parens2 = ')';
	
	// Unpaired delimiters
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
	
	private boolean pairedDelimiters = false;
	
	/**
	 * Constructor to initialize variables
	 * @param fileName - name of file to do lexical analysis on
	 */
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
	
	/**
	 * Reads in a file and puts contents in to an ArrayList of
	 * characters
	 * @param fileName - name of file to do lexical analysis on
	 * @throws IOException - if file is not found or is corrupt
	 */
	public void readProgram(String fileName) throws IOException 
	{
		FileReader fileReader = new FileReader(fileName);
		int i;
		
		while((i = fileReader.read()) != -1) {
		   char ch = (char)i;
		   
		   characterList.add(ch);
		}
	}
	
	/**
	 * Scans each character in the characterList and determines
	 * if it is a Token based on a finite state machine
	 */
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
					if(WHITESPACE == cat || OTHER == cat || DIGIT == cat || QUOTE == cat)
					{
						backup();
						addTokenToList();
					}
					else if(LUTR == cat || DIGIT == cat)
					{
						state = ID;
					}
					
					break;
				case ID:	
					if(LUTR == cat || DIGIT == cat)
					{
						state = ID;
					}
					else if(WHITESPACE == cat || OTHER == cat || QUOTE == cat)
					{
						backup();
						addTokenToList();
					}
					break;
				case OTHER:
					if('=' == currentChar || '!' == currentChar ||
					   '<' == currentChar || '>' == currentChar)
					{
						pairedDelimiters = true;
						addTokenToList();	
					}
					else if(WHITESPACE == cat || OTHER == cat || DIGIT == cat || QUOTE == cat || LU1 == cat)
					{
						backup();
						addTokenToList();
					}
					break;
				case DIGIT:
					if(WHITESPACE == cat || OTHER == cat || QUOTE == cat || LU1 == cat) 
					{
						backup();
						addTokenToList();
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
						addTokenToList();
					}
					else if(DIGIT == cat)
					{
						state = DOT;
					}
					else if(DOT == cat)
					{
						System.out.println("ERROR: NumberFormatException, Too Many .'s in Double");
						backup();
						addTokenToList();
						state = START;
					}
					break;
				case QUOTE:
					extractString(currentChar);
					break;
				case COMMENT:
					currentChar = nextChar();
					if(slash == currentChar)
					{
						extractComment(currentChar);
					}
					else
					{
						backup();
						addTokenToList();
						state = START;
					}
					break;
				default:
					state = "";
					break;
				}
			}

			position++;
		} while(position <= characterList.size());
		{
			checkForKeywords();
		}
		tokenList.add(new Token(EOF, EOF, -1, -1.0));
	}
	
	/**
	 * Gets the next character in the characterList
	 * @return - the next character
	 */
	public char nextChar()
	{
		if(position == characterList.size())
		{
			return ' ';
		}
		return characterList.get(position);
	}
	
	/**
	 * Sets position back one step
	 */
	public void backup()
	{
		position--;
	}
	
	/**
	 * Gets the category of a specific character
	 * @param character - to be analyzed 
	 * @return - Category of the character
	 */
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
		else if(isWhiteSpace(character)) { return WHITESPACE; } 
		else if(isSL1(character)) { return SL1; }
		else if(isOther(character)) { return OTHER; } 
		
		return "No Category";
	}
	
	public String getOther(char character)
	{
		if(brace1 == character) { return "brace1"; } 
		else if(brace2 == character) { return "brace2"; } 
		else if(parens1 == character) { return "parens1"; } 
		else if(parens2 == character) { return "parens2"; } 
		else if(angle1 == character) { return "angle1"; } 
		else if(angle2 == character) { return "angle2"; } 
		else if(bracket1 == character) { return "bracket1"; } 
		else if(bracket2 == character) { return "bracket2"; }
		else if(comma == character) { return "comma"; } 
		else if(semi == character) { return "semi"; } 
		else if(equal == character) { return "equal"; }
		else if(aster == character) { return "aster"; }
		else if(slash == character) { return "slash"; }
		else if(caret == character) { return "caret"; }
		else if(plus == character) { return "plus"; }
		else if(minus == character) { return "minus"; }
		else if(exclamation == character) { return "exclamation"; }
		else if(at == character) { return "at"; }
		else if(hash == character) { return "hash"; }
		else if(usd == character) { return "usd"; }
		
		return "OTHER";
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
	 * Check for character a-z or A-Z or _
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
	
	/**
	 * Check if character is a digit according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a digit, false otherwise
	 */
	public boolean isDigit(char character)
	{
		return (character >= 48 && character <= 57);
	}
	
	/**
	 * Check if character is a period according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a period, false otherwise
	 */
	public boolean isDot(char character)
	{
		return 46 == character;
	}
	
	/**
	 * Check if character is a quote according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a quote, false otherwise
	 */
	public boolean isQuote(char character)
	{
		return 34 == character;
	}
	
	/**
	 * Check if character is a +/- according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a +/-, false otherwise
	 */
	public boolean isSign(char character)
	{
		return (character == 43 || character == 45);
	}
	
	/**
	 * Check if character is a whitespace, carriage return, tab,
	 * line feed, or any control character according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a whitespace, carriage return, tab,
	 * line feed, or any control character, false otherwise
	 */
	public boolean isWhiteSpace(char character)
	{
		return (0 <= character && character <= 32);
	}
	
	/**
	 * Check if character is a slash according to the 
	 * ASCII table number
	 * @param character - to be analyzed
	 * @return - True if it is a slash, false otherwise
	 */
	public boolean isSlash(char character) 
	{
		return character == 47;
	}
	
	/**
	 * Check if character is considered other
	 * @param character - to be analyzed
	 * @return - True if it is an other char
	 */
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
	
	/**
	 * Checks if character is a slash
	 * @param character - to be analyzed
	 * @return - true if it is a slash
	 */
	public boolean isSL1(char character)
	{
		if('/' == character) { return true; }
		
		return false;
	}
		
	/**
	 * Retrieves paired delimiters String
	 * @return - String value of a pair of delimiters
	 */
	public String getPairedDelimiters()
	{
		int lastPos = position - 1;
		if(position < characterList.size()){ 
			String delimiter = characterList.get(lastPos) + "";
			delimiter += characterList.get(position) + "";
			
			if(opeq.equals(delimiter)) { return "opeq"; }
			else if(opne.equals(delimiter)) { return "opne"; }
			else if(ople.equals(delimiter)) { return "ople"; }
			else if(opge.equals(delimiter)) { return "opge"; } 
		}	
		return "";
	}
	
	/**
	 * Add a Token to the tokenList
	 * Sets state back to start
	 */
	public void addTokenToList()
	{
		String type = state;
		String data = "";
		if(pairedDelimiters) 
		{
			type = getPairedDelimiters();
			pairedDelimiters = false;
		}
		else if(OTHER == state)
		{
			type = getOther(characterList.get(position));
		}
		else if(DOT == state || DIGIT == state)
		{
			type = "num";
		}
		for(int i = lastPosition; i <= position; i++)
		{
			data += characterList.get(i);
		}
		if("string".equals(state))
		{
			data = state;
		}
		tokenList.add(new Token(type.toLowerCase(), data));
		
		state = START;
	}
	
	/**
	 * Extracts a comment, after the slashes it reads
	 * until the next line
	 * @param currentChar - character that is currently
	 * being looked at, then proceeds until a new line
	 */
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
	
	/**
	 * Extract a string which is in double quotes,
	 * if there is not a second double quote it 
	 * reads to the end of the line and throws out
	 * the characters and prints error for user
	 * @param currentChar - to be analyzed
	 */
	public void extractString(char currentChar)
	{
		do
		{
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
			state = "string";
			addTokenToList();
		}
		state = START;
	}
	
	/**
	 * Gets the next token from the tokenList, since
	 * tokenPosition is incremented after the .get
	 * it should never go past the size of the list
	 * @return - the token
	 */
	public Token nextToken()
	{
		Token token = tokenList.get(tokenPosition);
		tokenPosition++;
		return token; 
	}
	
	/**
	 * Size of the tokenList
	 * @return - tokenList size
	 */
	public int getSizeOfTokenList()
	{
		return tokenList.size();
	}
	
	/**
	 * Checks if any of the contents of the tokenList
	 * are keywords, if so then update the Token object
	 */
	public void checkForKeywords()
	{
		for(int i = 0; i < tokenList.size(); i++)
		{
			for(int j = 0; j < keywords.length; j++)
			{
				if(tokenList.get(i).getData() != null && 
						tokenList.get(i).getData().equalsIgnoreCase(keywords[j]))
				{
					tokenList.get(i).setWord("kwd" + keywords[j]);
					//tokenList.get(i).setWord("KEYWORD");
				}
			}
		}
	}
}
