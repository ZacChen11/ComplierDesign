package assignment1;



public class Test {
	public static void main(String[] args){
		SyntacticAnalyzer a = new SyntacticAnalyzer("code_case.txt","production","synatactic_error","symboltables", "semantic_error","lexical_valid",  "lexical_error", "code_data", "code_instruction");
		a.parse();
	}
}
