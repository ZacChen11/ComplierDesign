package assignment1;

import java.io.FileInputStream;
import java.io.IOException;


public class Scanner {
	
	public FileInputStream inputStream;
	public int currentCharacter;
	public int row = 1;
	public int column = 0;
	public boolean backTrack = false;
	public boolean endofStream = false;
	
	public Scanner(FileInputStream input) {
		inputStream = input;
	}
	
	//define createToken method and assign according type, lexeme and location to the token and output its value
	public Token createToken(String type, String lexeme, int row, int column) {		
		Token token = new Token(type, lexeme, row , column);
		return token;	
	}
	
	//scanner scans next source character by caliing nextChar
	public char nextChar(){
		
		if(endofStream){
			return (char)0;
		}
		
		//if scanner calls backChar in previous scanning, it will stay at the same character in the new scanning
		if(backTrack){
			backTrack = false;
			if(currentCharacter == '\n'){
				return ' ';
			}
		}
		else{
			try {
				column = column + 1;
				currentCharacter = inputStream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//when scanner reads a newline character, number of row will add 1 and column will be set to 0
		if(currentCharacter == '\n'){
			row = row + 1;
			column = 0;
			return ' ';
		}
		
		//when scanner reads a tab character, number of column will increase accordingly 
		if(currentCharacter == '\t'){
			column = column + 3;
			return ' ';
		}
		
		//when scanner reaches the end of the file, the value of current character will become -1
		if(currentCharacter == -1){
			return (char)0;
		}
		return (char)currentCharacter;
	}
	
	//when scanner implements backChar, it will set the backTrack flag to true
	public void backChar(){
		backTrack = true;
	}
	
	public Token nextToken() {
		
		char c = nextChar();
		String buffer = new String();
		
		//scanner will skip all the whitespace like "\n","\t","\r"
		while(Character.isWhitespace(c)) {
			c = nextChar();
		}
		
		int rowToken = row;
		int columnToken = column;
		
		if(c == (char)0){
			endofStream = true;
			return createToken("End of Inputstream", "End of Inputstream" , rowToken, columnToken);
		}
		
		if(Character.isLetter(c)){
			buffer = buffer + c;
			c = nextChar();
			while(Character.isLetter(c) || Character.isDigit(c) || c == '_'){
				buffer = buffer + c;
				c = nextChar();
			}
			backChar();
			
			//check if identifier is a keyword
			if(buffer.equals("and")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("not")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("or")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("if")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("then")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("else")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("for")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("class")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("int")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("float")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("get")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("put")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("return")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			if(buffer.equals("program")){
				return createToken("Keywords", buffer, rowToken, columnToken);
			}
			return createToken("Identifier", buffer, rowToken, columnToken);
		}
		
		if(Character.isDigit(c)){
			buffer = buffer + c;
			if(c == '0'){
				c = nextChar();
			}
			else{
				c = nextChar();
				while(Character.isDigit(c)){
					buffer = buffer + c;
					c = nextChar();
				}
			}
			if(c == '.'){
				buffer = buffer + c;
				c = nextChar();
				if(Character.isDigit(c)){
					buffer = buffer + c;
					c = nextChar();
					while(Character.isDigit(c)){
						buffer = buffer +c;
						c = nextChar();
					}
					backChar();
					return createToken("Float", buffer, rowToken, columnToken);
				}
				else {
					backChar();
					return createToken("Malformation", null, rowToken, columnToken);
				}	
			}
			backChar();
			return createToken("Integer", buffer, rowToken, columnToken);
		}
		
		if(c == '='){
			buffer = buffer + c;
			c = nextChar();
			if(c == '='){
				buffer = buffer +c;
				c = nextChar();
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
				
			}
			else{
				backChar();
				return createToken("Assignment", buffer, rowToken, columnToken);
			}
		}
		
		if(c == '<'){
			buffer = buffer + c;
			c = nextChar();
			if(c == '>'){
				buffer = buffer + c;
				c = nextChar();
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
			else if(c == '='){
				buffer = buffer + c;
				c = nextChar();
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
			else{
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
		}
		
		if(c == '>'){
			buffer = buffer + c;
			c = nextChar();
			if(c == '='){
				buffer = buffer + c;
				c = nextChar();
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
			else{
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
		}
		
		if(c == '+' || c == '-'|| c == '*' ){
			buffer = buffer + c;
			return createToken("Operator", buffer, rowToken, columnToken);
		}
		
		if(c == '/'){
			buffer = buffer + c;
			c = nextChar();
			if(c == '*'){
				buffer = buffer + c ;
				c = nextChar();
				while(c != (char)0){
					buffer = buffer + c;
					c = nextChar();
					if(c == '*'){
						buffer = buffer + c;
						c = nextChar();
						if(c == '/'){
							buffer = buffer + c;
							return createToken("Comment", buffer, rowToken, columnToken);
						}
						else{
							backChar();
						}
					}	
				}
				if( c == (char)0){
					backChar();
					return createToken("Uncompleted Comment", null, rowToken, columnToken);
				}
			}
			else if(c == '/'){
				buffer = buffer + c;
				c = nextChar();
				while(c != '\r' && c != '\n' && c != (char)0){
					buffer = buffer + c;
					c = nextChar();
				}
				backChar();
				return createToken("Comment", buffer, rowToken, columnToken);
			}
			else{
				backChar();
				return createToken("Operator", buffer, rowToken, columnToken);
			}
		}
		
		if(c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == '.' || c == ',' || c == ';'){
			buffer = buffer + c;
			c = nextChar();
			backChar();
			return createToken("Delimiter", buffer, rowToken, columnToken);
		}
		buffer = buffer + c;
		return createToken("Undefined Token", buffer, rowToken, columnToken);
		
	}
		 
}
