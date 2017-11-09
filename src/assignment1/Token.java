package assignment1;

public class Token {
	
	public String type;
	public String lexeme;
	public int row;
	public int column;
	
	//define Token data structure
	public Token(String type, String lexeme, int row, int column){
		this.type = type;
		this.lexeme = lexeme;
		this.row = row;
		this.column = column;
	}
	
	public String getType() {
		return type;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	//return token's information
	public String getToken(){
		return "["+getType()+","+getLexeme()+","+"Position: "+"row:"+getRow()+" "+"column:"+getColumn()+"]"+'\n';
	}
	

}
