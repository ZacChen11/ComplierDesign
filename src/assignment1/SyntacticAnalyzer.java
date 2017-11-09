package assignment1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class SyntacticAnalyzer {
	public Scanner scanner;
	public Token lookahead;
	public FileInputStream inputfile;
	public FileOutputStream erroroutput;
	public FileOutputStream production;
	public TableHolder holder;
	public FileOutputStream tableoutput;
	public String filename;
	public Driver driver;
	public Counter counter;
	public CodeGenerator code;
	
	
	SyntacticAnalyzer(String program, String outputfile, String synatactic_error, String symboltables, String semantic_error, String lexical_valid, String lexical_error, String code_data, String code_instruction){
		filename = program;
		try {
			inputfile = new FileInputStream(program);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		scanner = new Scanner(inputfile);
		try {
			production = new FileOutputStream(outputfile) ;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			erroroutput = new FileOutputStream(synatactic_error);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder = new TableHolder(symboltables, semantic_error);
		driver = new Driver(lexical_valid, lexical_error, program);
		counter = new Counter();
		code = new CodeGenerator(code_data, code_instruction);
		
	}
	
	public boolean parse(){
		driver.drive(counter);
		lookahead = scanner.nextToken();
		boolean success = false;
		//first pass
		holder.setFirstPass();
		if (prog() && match("End of Inputstream") ){
			
			//update the size of symbol table
			holder.update_size(holder);
			
			
			for(int a = 0; a < holder.tables.size(); a++){
				holder.print(holder.tables.get(a).getTableName());
			}
			success = true;
		}
		else{
			for(int a = 0; a < holder.tables.size(); a++){
				holder.print(holder.tables.get(a).getTableName());
			}
			success = false;
		}
		holder.closeFirstPass();
		
		System.out.println("First pass is implemented correctly");
		
		//second pass
		holder.setSecondPass();
		try {
			inputfile = new FileInputStream (filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scanner =  new Scanner(inputfile);
		lookahead = scanner.nextToken();
		if (prog() && match("End of Inputstream") ){
			success = true;
		}
		else{
			
			success = false;
		}
		System.out.println("Second pass is implemented correctly");
		
		//third pass
		code.setThirdPass(counter);
		try {
			inputfile = new FileInputStream (filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		scanner =  new Scanner(inputfile);
		lookahead = scanner.nextToken();
		if (prog() && match("End of Inputstream") ){
			success = true;
		}
		else{
			
			success = false;
		}
		code.closeThirdPass();
		System.out.println("Third pass is implemented correctly");
		System.out.println("The whole parsing is over");
		return success;
	}
	
	public boolean match(String tokentype){
		
		if (lookahead.getType().equals("Float") || lookahead.getType().equals("Integer") || lookahead.getType().equals("Identifier")){
			if ( lookahead.getType().equals(tokentype) ){
				lookahead = scanner.nextToken();
				return true; }
			else{
				counter.counter = counter.counter + 1;
				try {
					erroroutput.write(("[Syntax error: missing right parenthesis!] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: )] [Current token: "+lookahead.getType()+"]"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lookahead = scanner.nextToken();
				return false;   
			}  
		}
		//specify the content of the token
		else {
			if ( lookahead.getLexeme().equals(tokentype )){
				lookahead = scanner.nextToken();
				return true; }
			else{
				counter.counter = counter.counter + 1;
				try {
					erroroutput.write(("[Syntax error: missing right parenthesis!] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: )] [Current token: "+lookahead.getLexeme()+"]"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lookahead = scanner.nextToken();
				return false;   
			}  	
		}	  
	}
	
	//override
	public boolean match(String tokentype, SemRec r){
		
		if (lookahead.getType().equals("Float") || lookahead.getType().equals("Integer") || lookahead.getType().equals("Identifier")){
			if ( lookahead.getType().equals(tokentype) ){
				if(lookahead.getType().equals("Identifier")){
					r.rec = lookahead.getLexeme();	
				}
				if(lookahead.getType().equals("Integer")){
					r.array.add(Integer.parseInt(lookahead.getLexeme()));
				}
				if(lookahead.getType().equals("Float")){
					r.float_num = Float.parseFloat(lookahead.getLexeme());
				}
				lookahead = scanner.nextToken();
				return true; }
			else{
				counter.counter = counter.counter + 1;
				try {
					erroroutput.write(("[Syntax error: missing right parenthesis!] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: )] [Current token: "+lookahead.getType()+"]"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lookahead = scanner.nextToken();
				return false;   
			}  
		}
		//specify the content of the token
		else {
			if ( lookahead.getLexeme().equals(tokentype )){
				r.rec = lookahead.getLexeme();
				lookahead = scanner.nextToken();
				return true; }
			else{
				counter.counter = counter.counter + 1;
				try {
					erroroutput.write(("[Syntax error: missing right parenthesis!] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: )] [Current token: "+lookahead.getLexeme()+"]"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				lookahead = scanner.nextToken();
				return false;   
			}  	
		}	  
	}
	
	public boolean token_inset(String[] set){
		if (set == null){
			return false;
		}
		if (lookahead.getType().equals("Float") || lookahead.getType().equals("Integer") || lookahead.getType().equals("Identifier")){
			for (int i=0; i < set.length; i++) {
	            if (lookahead.getType().equals(set[i]) ){
	                return true;
	            }   
	        } 
			return false;	
		}
		//specify the content of the token
		else{
			for (int j=0; j < set.length; j++) {
	            if (lookahead.getLexeme().equals(set[j])){
	                return true;
	            }
	        }
			return false;	
		}
	}
	
	public boolean is_Epsilon(String[] set){
		 for (int i=0; i < set.length; i++) {
	            if ( set[i].equals("EPSILON") ) {
	                return true;
	            }  
	     }
		 return false;
	}
	
	public boolean skipErrors(String first[], String follow[]){	
		if (token_inset(first) || (is_Epsilon(first) && token_inset(follow))){
			return true;
		}
		String content = "{";
		for(int m = 0; m<first.length; m++ ){
			content = content + first[m];
			if(m != first.length-1){
				content = content + ", ";
			}
		}
		content = content + "}";
		if (lookahead.getType().equals("Float") || lookahead.getType().equals("Integer") || lookahead.getType().equals("Identifier")){
			counter.counter = counter.counter + 1;
			try {
				erroroutput.write(("[Syntax error£º*] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: "+content+"] [Current token: "+lookahead.getType()+"]"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			counter.counter = counter.counter + 1;
			try {
				erroroutput.write(("[Syntax error: *] [Error location: row:"+lookahead.getRow()+" column:"+lookahead.getColumn()+"] [Expected token: "+content+"] [Current token: "+lookahead.getLexeme()+"]"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while(!(token_inset(first) || token_inset(follow))){
			lookahead = scanner.nextToken();
			if ((is_Epsilon(first) && token_inset(follow)) || (lookahead.getType().equals("End of Inputstream"))){
				return false;
			}
		}
		return true;
	}
	
	//prog -> classDecl_list progBody
	public boolean prog(){
		String first[] = {"class", "program"};
		String first1[] = {"class", "program"};
		if(!skipErrors(first, null)){
			return false;
		}
		//create a global table
		holder.create("Global");
		
		if(token_inset(first1)){
			try {
					production.write(("rule prog: prog -> classDecl_list progBody"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (classDecl_list() && progBody()){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//classDecl -> class id { varDecl_funcDef_list } ;
	public boolean classDecl(){
		String first[] = {"class"};
		String first1[] = {"class"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec classname = new SemRec();
		
		if(token_inset(first1)){
			try {
					production.write(("rule classDecl: classDecl -> class id { varDecl_funcDef_list } ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("class") && id(classname) && holder.duplicateCheck("Global", classname) &&holder.addEntry("Global", classname.rec, "class", null, null, classname.rec, null, 0) && holder.createTable(classname.rec)&& holder.classCheck(classname.rec, lookahead.row, lookahead.column, counter) && match("{") && varDecl_funcDef_list(classname) && match("}") &&match(";") ){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	
	//varDecl_funcDef_list  -> type id varDeclFunctDef_tail | EPSILON
	public boolean varDecl_funcDef_list(SemRec classname){
		String first[] = {"float", "int", "Identifier", "EPSILON"};
		String follow[] = {"}"};
		String first1[] = {"float", "int", "Identifier"};
		if(!skipErrors(first, follow)){
			return false; 
		}
		
		SemRec type = new SemRec();
		SemRec var_name = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule varDecl_funcDef_list: varDecl_funcDef_list  -> type id varDeclFunctDef_tail"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (type(type) && holder.datatypeCheck(type.rec, lookahead.row, lookahead.column,counter) && id(var_name) && varDeclFunctDef_tail(classname, type, var_name)){	
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule varDecl_funcDef_list: varDecl_funcDef_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//varDeclFunctDef_tail -> ( fParams ) funcBody ; varDecl_funcDef_list | arraySize_list ; varDecl_funcDef_list 
	public boolean varDeclFunctDef_tail(SemRec classname, SemRec type, SemRec var_name){
		String first[] = {"[", ";", "("};
		String first1[] = {"("};
		String first2[] = {"[", ";"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		if(token_inset(first1)){
			try {
					production.write(("rule varDeclFunctDef_tail: varDeclFunctDef_tail -> ( fParams ) funcBody ; varDecl_funcDef_list"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (holder.duplicateCheck(classname.rec, var_name) &&  holder.addEntry(classname.rec, var_name.rec, "function", type.rec, null,  classname.rec+":"+var_name.rec, "Global", 0)&& holder.createTable(classname.rec+":"+var_name.rec) &&holder.funCheck(classname.rec, var_name.rec, lookahead.row, lookahead.column, counter) && code.func(var_name, classname.rec) && match("(") && fParams(var_name.rec, classname.rec) && match(")") && funcBody(var_name.rec, classname.rec) && match(";")&& code.funj() && varDecl_funcDef_list(classname)){
				
	                return true;
	            }
	      return false;
		}
		
		SemRec var_dimension = new SemRec(); 
		SemRec size = new SemRec();
		
		if(token_inset(first2)){
			try {
					production.write(("rule varDeclFunctDef_tail: varDeclFunctDef_tail -> arraySize_list ; varDecl_funcDef_list"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (holder.varCheck(classname.rec, var_name.rec, lookahead.row, lookahead.column, counter) && arraySize_list(var_dimension) && holder.duplicateCheck(classname.rec, var_name) && holder.sizeCompute(type, var_dimension, size) && holder.addEntry(classname.rec, var_name.rec, "variable", type.rec, var_dimension, null, "Global", size.size)   && match(";")  && varDecl_funcDef_list(classname)){
				
	                return true;
	            }
	      return false;
		}
		
		return false;
	}
	
	//progBody -> program funcBody ; funcDef_list
	public boolean progBody(){
		String first[] = {"program"};
		String first1[] = {"program"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule progBody: progBody -> program funcBody ; funcDef_list"+'\n').getBytes());
				} catch (IOException e) {
					//        
					e.printStackTrace();
				}
			if (match("program") && code.pro() && holder.addEntry("Global", "program", "function", null, null, "Global:program", null, 0) && holder.createTable("Global:program") && funcBody("program", "Global") && match(";") && code.halt() && funcDef_list("Global")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//funcDef -> funcHead funcBody ;
	public boolean funcDef(String classname){
		String first[] = {"float", "int", "Identifier"};
		String first1[] = {"float", "int", "Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec func_name = new SemRec();
		SemRec func_type = new SemRec();
		
		
		if(token_inset(first1)){
			try {
					production.write(("rule funDef: funcDef -> funcHead funcBody ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (funcHead(func_name, func_type)  && holder.addEntry("Global", func_name.rec, "function", func_type.rec, null, classname+":"+func_name.rec, null, 0) && funcBody(func_name.rec, "Global") && match(";") && code.funj()){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//funcHead -> type id ( fParams )
	public boolean funcHead(SemRec func_name, SemRec func_type){
		String first[] = {"float", "int", "Identifier"};
		String first1[] = {"float", "int", "Identifier"}; 
		if(!skipErrors(first, null)){
			return false;
		}
		
		if(token_inset(first1)){
			try {
					production.write(("rule funHead: funcHead -> type id ( fParams )"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (type(func_type) && id(func_name) && holder.duplicateCheck("Global", func_name) && holder.createTable("Global:"+func_name.rec) && code.func(func_name, "Global") && match("(") && holder.funCheck("Global", func_name.rec, lookahead.row, lookahead.column, counter) && fParams(func_name.rec, "Global") && match(")")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//funcBody -> { funcBody_ }
	public boolean funcBody(String tablename, String classname){
		String first[] = {"{"};
		String first1[] = {"{"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule funBody: funcBody -> { funcBody_ }"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("{") && funcBody_(tablename, classname) && match("}")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//funcBody_ -> type_ id arraySize_list ; funcBody_ | id varDeclStatement_tail | keywordStatment statement_list | EPSILON
	public boolean funcBody_(String tablename, String classname){
		String first[] = {"for", "if", "get", "put", "return", "Identifier", "float", "int", "EPSILON"};
		String follow[] = {"}"};
		String first1[] = {"float", "int"};
		String first2[] = {"Identifier"};
		String first3[] = {"for", "if", "get", "put", "return"};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec type = new SemRec();
		SemRec id = new SemRec();
		SemRec dimension = new SemRec();
		SemRec nest = new SemRec();
		SemRec size = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule funcBody_: funcBody_ -> type_ id arraySize_list ; funcBody_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (type_(type) && holder.datatypeCheck(type.rec, lookahead.row, lookahead.column, counter)&& id(id) && holder.varCheck(classname+":"+tablename, id.rec, lookahead.row, lookahead.column, counter)&& arraySize_list(dimension) && holder.duplicateCheck(classname+":"+tablename, id) && holder.sizeCompute(type, dimension, size) &&holder.addEntry(classname+":"+tablename, id.rec, "variable", type.rec, dimension, null, classname, size.size) && code.varGenerate(id, classname+":"+tablename, holder) && match(";")  &&funcBody_(tablename, classname)){	    
				return true;
	            }
	      return false;
		}
		
		
		if(token_inset(first2)){
			try {
				production.write(("rule funcBody_: funcBody_ -> id varDeclStatement_tail"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (id(id) && holder.nest(nest, id) && varDeclStatement_tail(id, tablename, classname, nest)){	
	                return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
				production.write(("rule funcBody_: funcBody_ -> keywordStatment statement_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (keywordStatment(tablename, classname) && statement_list(tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule funcBody_: funcBody_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//varDeclStatement_tail -> id arraySize_list ; funcBody_ | indiceIdnestList_tail assignOp expr ; statement_list
	public boolean varDeclStatement_tail(SemRec id, String tablename, String classname, SemRec nest){
	String first[] = {".", "[", "=", "Identifier"};
	String first1[] = {"Identifier"};
	String first2[] = {".", "[", "="};
	if(!skipErrors(first, null)){
		return false;
	}
	
	SemRec id_ = new SemRec();
	SemRec dimension_ = new SemRec();
	SemRec index = new SemRec();
	SemRec return_type = new SemRec();
	SemRec right_nest = new SemRec();
	SemRec type = new SemRec();
	SemRec size = new SemRec();
	SemRec right_id = new SemRec();
	
	if(token_inset(first1)){
		try {
				production.write(("rule varDeclStatement_tail: varDeclStatement_tail -> id arraySize_list ; funcBody_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (holder.datatypeCheck(id.rec, lookahead.row, lookahead.column, counter) &&id(id_)&& holder.varCheck(classname+":"+tablename, id_.rec, lookahead.row, lookahead.column, counter) && arraySize_list(dimension_)  && holder.duplicateCheck(classname+":"+tablename, id_) && holder.sizeCompute(id, dimension_, size) && holder.addEntry(classname+":"+tablename, id_.rec, "variable", id.rec, dimension_, null, classname, size.size) && code.varGenerate(id_, classname+":"+tablename, holder) && match(";") &&funcBody_(tablename, classname)){
			
                return true;
            }
      return false;
	}
	if(token_inset(first2)){
		try {
				production.write(("rule varDeclStatement_tail: varDeclStatement_tail -> indiceIdnestList_tail assignOp expr ; statement_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (indiceIdnestList_tail(tablename, classname, nest, index) && holder.nestCheck(nest, tablename, classname, lookahead.row, lookahead.row, counter)&& holder.nestType(type, nest, tablename, classname) && assignOp() && expr( tablename, classname, return_type, right_nest, right_id) && holder.typeCheck(type, return_type, lookahead.row, lookahead.column, counter)&& code.assGenerate(nest, right_nest, right_id, classname+":"+tablename, holder) &&match(";") && statement_list(tablename, classname)){
			return true;
           
			}
      return false;
	}
	return false;
}
	
	//indiceIdnestList_tail -> . id indiceIdnestList_tail | [ arithExpr ] indiceIdnestList_tail | EPSILON
	public boolean indiceIdnestList_tail( String tablename, String classname, SemRec nest, SemRec index){
		String first[] = {"[", ".", "EPSILON"};
		String follow[] = {"="};
		String first1[] = {"."};
		String first2[] = {"["};
		if(!skipErrors(first, follow)){
			return false;
		}
		SemRec name = new SemRec();
		SemRec index_new = new SemRec();
		SemRec id = new SemRec();
		SemRec type = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule indiceIdnestList_tail: indiceIdnestList_tail -> . id indiceIdnestList_tail"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ( match(".") && id(name) && holder.nest(nest, name) && indiceIdnestList_tail( tablename, classname, nest, index_new )){	
				
	                return true;
	            }
	      return false;
		}
		if(!skipErrors(first, follow)){
			return false;
		}
		
		
		
		if(token_inset(first2)){
			try {
				production.write(("rule indiceIdnestList_tail: indiceIdnestList_tail -> [ arithExpr ] indiceIdnestList_tail"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match("[") && arithExpr(id, nest, tablename, classname, type) && match("]") && holder.addIndex(nest, index_new) && indiceIdnestList_tail( tablename, classname, nest, index_new)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule indiceIdnestList_tail: indiceIdnestList_tail -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//keywordStatment -> if ( expr ) then statBlock else statBlock ; | for ( type id assignOp expr ; relExpr ; assignStat ) statBlock ; | get ( variable_ ) ; | put ( expr ) ; | return ( expr ) ;
	public boolean keywordStatment(String tablename, String classname){
		String first[] = {"return", "put", "get", "if", "for"};
		String first1[] = {"if"};
		String first2[] = {"for"};
		String first3[] = {"get"};
		String first4[] = {"put"};
		String first5[] = {"return"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec type = new SemRec();
		SemRec name = new SemRec();
		SemRec nest = new SemRec();
		SemRec right_type = new SemRec();
		SemRec size = new SemRec();
		SemRec dimension = new SemRec();
		SemRec id = new SemRec();
		SemRec fun_type = new SemRec();
		
		if(token_inset(first1)){
			try {
					production.write(("rule keywordStatment: keywordStatment -> if ( expr ) then statBlock else statBlock ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("if") && match("(") && expr(tablename, classname, type, nest, id) && match(")") && match("then") && statBlock(tablename, classname) && match("else") && statBlock(tablename, classname) && match(";")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule keywordStatment: keywordStatment -> for ( type id assignOp expr ; relExpr ; assignStat ) statBlock ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("for") && match("(") && type(type) && holder.datatypeCheck(type.rec, lookahead.row, lookahead.column, counter)&& id(name)&& holder.duplicateCheck(classname+":"+tablename, name) && holder.varCheck(classname+":"+tablename, name.rec, lookahead.row, lookahead.column, counter) && holder.sizeCompute(type, dimension, size) && holder.addEntry(classname+":"+tablename, name.rec, "variable", type.rec, null,  null, classname, size.size)   && assignOp() && expr(tablename, classname, right_type, nest, id) && holder.typeCheck(type, right_type, lookahead.row, lookahead.column, counter)&&match(";") && relExpr(tablename, classname) && match(";") && assignStat(tablename, classname) &&match(")") && statBlock(tablename, classname) && match(";")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
					production.write(("rule keywordStatment: keywordStatment -> get ( variable_ ) ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("get") && match("(") && variable_(nest, type, tablename, classname) &&holder.nestCheck(nest, tablename, classname,  lookahead.row, lookahead.row, counter) && match(")") && match(";")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first4)){
			try {
					production.write(("rule keywordStatment: keywordStatment ->  put ( expr ) ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if ( match("put") && match("(") && expr(tablename, classname, type , nest, id) && match(")") && match(";")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first5)){
			try {
					production.write(("rule keywordStatment: keywordStatment -> return ( expr ) ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if ( match("return") && match("(") && expr(tablename, classname, type, nest, id) && holder.funType(fun_type, tablename, classname, holder) && holder.typeCheck(fun_type, type, lookahead.row, lookahead.column, counter) && match(")") && match(";") && code.return_value(nest, id, tablename, classname, holder)){
				
				
				return true;
	            }
	      return false;
		}
		return false;
	}
	
	//statBlock -> { statement_list } | statement | EPSILON
	public boolean statBlock(String tablename, String classname){
		String first[] = {"for", "if", "get", "put", "return", "Identifier", "{", "EPSILON"};
		String follow[] = {";", "else"};
		String first1[] = {"for", "if", "get", "put", "return", "Identifier"};
		String first2[] = {"{"};
		if(!skipErrors(first, follow)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule statBlock: statBlock -> statement "+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (statement(tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
				production.write(("rule statBlock: statBlock -> { statement_list }"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match("{") && statement_list(tablename, classname) && match("}")){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule statBlock: statBlock -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//statement -> assignmentStatment | keywordStatment
	public boolean statement(String tablename, String classname){
		String first[] = {"Identifier", "for", "if", "get", "put", "return"};
		String first1[] = {"Identifier"};
		String first2[] = {"for", "if", "get", "put", "return"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		
		if(token_inset(first1)){
			try {
					production.write(("rule statement: statement -> assignmentStatment"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (assignmentStatment(tablename, classname)){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule statement: statement -> keywordStatment"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (keywordStatment(tablename, classname)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//assignmentStatment -> variable_ assignOp expr ;
	public boolean assignmentStatment(String tablename, String classname){
		String first[] = {"Identifier"};
		String first1[] = {"Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec left_nest = new SemRec();
		SemRec left_type = new SemRec();
		SemRec right_type = new SemRec();
		SemRec right_nest = new SemRec();
		SemRec id = new SemRec(); 
		
		if(token_inset(first1)){
			try {
					production.write(("rule assignmentStatment: assignmentStatment -> variable_ assignOp expr ;"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (variable_(left_nest, left_type, tablename, classname) && holder.nestCheck(left_nest, tablename, classname,  lookahead.row, lookahead.row, counter) && holder.nestType(left_type, left_nest, tablename, classname)&& assignOp() && expr(tablename, classname, right_type, right_nest, id) && holder.typeCheck(left_type, right_type, lookahead.row, lookahead.column, counter) && code.assGenerate(left_nest, right_nest, id, classname+":"+tablename, holder) && match(";") ){
				return true; 
	            }
	      return false;
		}
		return false;
	}
	
	//assignStat -> variable_ assignOp expr
	public boolean assignStat(String tablename, String classname){
		String first[] = {"Identifier"};
		String first1[] = {"Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec nest = new SemRec();
		SemRec type = new SemRec();
		SemRec right_nest = new SemRec();
		SemRec right_type = new SemRec();
		SemRec id = new SemRec();
		if(token_inset(first1)){
			try {
					production.write(("rule assignStat: assignStat -> variable_ assignOp expr"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (variable_(nest, type, tablename, classname) && holder.nestType(type, nest, tablename, classname) && assignOp() && expr(tablename, classname, right_type, right_nest, id)  && holder.typeCheck(type, right_type, lookahead.row, lookahead.column, counter)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	
	
	//expr -> arithExpr exp_
	public boolean expr(String tablename, String classname, SemRec return_type, SemRec nest, SemRec id){
		String first[] = {"(", "Float", "Integer", "not", "+", "-", "Identifier"};
		String first1[] = {"(", "Float", "Integer", "not", "+", "-", "Identifier"};
	
		if(!skipErrors(first, null)){
			return false;
		}
		

		SemRec right_return_type = new SemRec();
		SemRec right_id = new SemRec();
		
		if(token_inset(first1)){
			try {
					production.write(("rule expr: expr -> arithExpr exp_"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (arithExpr(id ,nest, tablename, classname, return_type) && exp_(tablename, classname, right_return_type, right_id) ){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//exp_ -> relOp arithExpr | EPSILON
	public boolean exp_(String tablename, String classname, SemRec right_return_type, SemRec right_id){
		String first[] = {"<", "<=", "<>", "==", ">", ">=", "EPSILON"};
		String follow[] = {";", ")", ","};
		String first1[] = {"<", "<=", "<>", "==", ">", ">="};
		if(!skipErrors(first, follow)){
			return false;
		}
		SemRec right_nest = new SemRec();
		
		
		
		if(token_inset(first1)){
			try {
				production.write(("rule expr_: expr_ -> relOp arithExpr"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (relOp() && arithExpr(right_id, right_nest, tablename, classname, right_return_type) ){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule expr_: expr_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//relExpr -> arithExpr relOp arithExpr
	public boolean relExpr(String tablename, String classname){
		String first[] = {"(", "Float", "Integer",  "Identifier", "not", "+", "-"};
		String first1[] = {"(", "Float", "Integer",  "Identifier", "not", "+", "-"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec left_type = new SemRec();
		SemRec right_type = new SemRec();
		SemRec left_nest = new SemRec();
		SemRec right_nest = new SemRec();
		SemRec left_id = new SemRec();
		SemRec right_id = new SemRec();
	
		
	
		
		if(token_inset(first1)){
			try {
				production.write(("rule relExpr_: relExpr -> arithExpr relOp arithExpr"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (arithExpr(left_id, left_nest, tablename, classname, left_type) && relOp() && arithExpr(right_id, right_nest, tablename,classname, right_type) && holder.typeCheck(left_type, right_type,lookahead.row, lookahead.column, counter)){
			
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//arithExpr -> term arithExpr_
	public boolean arithExpr(SemRec id, SemRec nest, String tablename, String classname, SemRec return_type){
		String first[] = {"(", "Float", "Integer",  "not", "+", "-", "Identifier"};
		String first1[] = {"(", "Float", "Integer",  "not", "+", "-", "Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec right_nest = new SemRec();
		SemRec right_return_type = new SemRec();
		SemRec right_id = new SemRec();
		if(token_inset(first1)){
			try {
				production.write(("rule arithExpr: arithExpr -> term arithExpr_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			if (term(id, nest, tablename , classname, return_type) && arithExpr_(right_id, right_nest, tablename, classname, right_return_type, return_type)){
	                	
	                	return true;
	            }
	      return false;
		}
		return false;
	}
	
	//arithExpr_ -> addOp term arithExpr_ |EPSILON
	public boolean arithExpr_(SemRec id, SemRec right_nest, String tablename, String classname, SemRec return_type, SemRec type2){
		String first[] = {"+", "-", "or", "EPSILON"};
		String follow[] = { "]", ";", ")", ",", "<", "<=", "<>", "==", ">", ">="};
		String first1[] = {"+", "-", "or"};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec right_id = new SemRec();
		SemRec new_nest = new SemRec();
		SemRec right_return_type = new SemRec();
		if(token_inset(first1)){
			try {
				production.write(("rule arithExpr_: arithExpr_ -> addOp term arithExpr_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (addOp() && term(id, right_nest , tablename, classname, return_type) && holder.typeCheck(return_type, type2, lookahead.row, lookahead.column, counter)&& arithExpr_(right_id, new_nest, tablename, classname, right_return_type, return_type) ){

			
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule arithExpr_: arithExpr_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//sign -> + | -
	public boolean sign(){
		String first[] = {"+", "-"};
		String first1[] = {"+"};
		String first2[] = {"-"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule sign: sign -> +"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
			
			if (match("+")){
	          
			
			return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
				production.write(("rule sign: sign -> -"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}       
			
			
			if (match("-")){
	        
	        return true;
	            }
	      return false;
		}
		return false;
	}
	
	//term -> factor term_
	public boolean term(SemRec name, SemRec nest, String tablename, String classname, SemRec return_type){
		String first[] = {"(", "Float", "Integer", "not", "+", "-", "Identifier"};
		String first1[] = {"(", "Float", "Integer",  "not", "+", "-", "Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec right_id = new SemRec();
		SemRec right_nest = new SemRec();
		SemRec right_return_type = new SemRec();
		if(token_inset(first1)){
			 try {
				production.write(("rule term: term -> factor term_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (factor(name, nest, tablename, classname, return_type) && term_(right_id, right_nest, tablename, classname, right_return_type, return_type) ){
	        
     return true;
	            }
	      return false;
		}
		return false;
	}
	
	//term_ -> multOp factor term_ | EPSILON
	public boolean term_(SemRec id, SemRec right_nest, String tablename, String classname, SemRec return_type, SemRec type2){
		String first[] = {"*", "/", "and", "EPSILON"};
		String follow[] = {"]", ";", ")", ",", "<", "<=", "<>", "==", ">", ">=", "+", "-", "or"};
		String first1[] = {"*", "/", "and"};
		if(!skipErrors(first, follow)){
			return false;
		}
		SemRec right_id = new SemRec();
		SemRec new_nest = new SemRec();
		SemRec right_return_type = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule term_: term_ -> multOp factor term_ "+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			
			if (multOp() && factor(id, right_nest, tablename, classname, return_type) && holder.typeCheck(return_type, type2, lookahead.row, lookahead.column, counter) && term_(right_id, new_nest, tablename, classname, right_return_type, return_type)){
	          
    return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule term_: term_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//factor -> variable_ factor_ | ( arithExpr ) | num | not factor | sign factor
	public boolean factor(SemRec id, SemRec nest, String tablename, String classname, SemRec return_type){
		String first[] = {"+", "-", "not", "Float", "Integer",  "(", "Identifier"};
		String first1[] = {"+", "-"};
		String first2[] = {"not"};
		String first3[] = {"Float", "Integer"};
		String first4[] = {"("};
		String first5[] = {"Identifier"};
		
		if(!skipErrors(first, null)){
			return false;
		}
		
		SemRec type = new SemRec();
		
		
		if(token_inset(first1)){
			try {
				production.write(("rule factor: factor -> sign factor"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

			
			if (sign() && factor(id, nest, tablename, classname, return_type)){	
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
				production.write(("rule factor: factor -> not factor"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			

			
			if (match("not") && factor(id, nest, tablename, classname, return_type)){
	               return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
				production.write(("rule factor: factor -> num"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (num(id, return_type)){
					
	                return true;
	            }
	      return false;
		}
		if(token_inset(first4)){
			try {
					production.write(("rule factor: factor -> ( arithExpr )"+'\n').getBytes());
			} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			if (match("(") && arithExpr(id, nest, tablename, classname, return_type) && match(")")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first5)){
			try {
				production.write(("rule factor: factor -> variable_ factor_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

			
			if ( variable_(nest, type, tablename , classname) && factor_( nest, tablename, classname) && holder.nestCheck(nest, tablename, classname, lookahead.row, lookahead.row, counter) && holder.nestType(return_type, nest, tablename, classname)){	
	                
				
				return true;
	            }
	      return false;
		}
		return false;
	}
	
	//variable_ -> id indice_list idnest_list_
	public boolean variable_( SemRec nest, SemRec type, String tablename, String classname){
		String first[] = {"Identifier"};
		String first1[] = {"Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec name = new SemRec();
		if(token_inset(first1)){
			try {
					production.write(("rule variable_: variable_ -> id indice_list idnest_list_"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (id(name) && holder.nest(nest, name) && indice_list() && idnest_list_(nest , tablename, classname) ){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//idnest_list_ -> idnest_ idnest_list_ | EPSILON
	public boolean idnest_list_(SemRec nest, String tablename, String classname){
		String first[] = {".",  "EPSILON"};
		String follow[] = {")", "=", "]", ";", ",", "<", "<=", "<>", "==", ">", ">=", "+", "-", "or", "*", "/", "and", "("};
		String first1[] = {"."};
		if(!skipErrors(first, follow)){
			return false;
		}
		SemRec name = new SemRec();
		if(token_inset(first1)){
			try {
				production.write(("rule idnest_list_: idnest_list_  -> idnest_ idnest_list_"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (idnest_(name, nest) && holder.nest(nest, name) && idnest_list_(nest , tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule idnest_list_: idnest_list_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//idnest_ -> . id indice_list
	public boolean idnest_(SemRec name, SemRec nest){
		String first[] = {"."};
		String first1[] = {"."};
		if(!skipErrors(first, null)){
			return false;
		}
		
		if(token_inset(first1)){
			try {
				production.write(("rule idnest_: idnest_  -> . id indice_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match(".") && id(name) && indice_list()){	
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//factor_ -> ( aParams ) | EPSILON
	public boolean factor_(SemRec nest, String tablename, String classname){
		String first[] = {"(",  "EPSILON"};
		String follow[] = {"]", ";", ")", ",", "<", "<=", "<>", "==", ">", ">=", "+", "-", "or", "*", "/", "and"};
		String first1[] = {"("};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec typeNest = new SemRec();
		SemRec paraNest = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule factor_: factor_  -> ( aParams )"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match("(") && aParams(typeNest, tablename, classname) && match(")") && holder.paraType(nest, paraNest, tablename, classname) && holder.paraReturnCheck(paraNest, typeNest, lookahead.row, lookahead.column, counter) ){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule factor_: factor_ -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//indice -> [ arithExpr ]
	public boolean indice(SemRec index_type){
		String first[] = {"["};
		String first1[] = {"["};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec index = new SemRec();
		SemRec nest = new SemRec();
		SemRec tablename = new SemRec();
		SemRec classname = new SemRec();
		SemRec i = new SemRec();
		if(token_inset(first1)){
			try {
				production.write(("rule indice: indice  -> [ arithExpr ]"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match("[") && arithExpr(index, nest, tablename.rec, classname.rec, i) && match("]")){	
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//arraySize -> [ Integer ]
	public boolean arraySize(SemRec param_dimension){
		String first[] = {"["};
		String first1[] = {"["};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule arraySize: arraySize  -> [ Integer ]"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (match("[") && match("Integer", param_dimension) && match("]")){	
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//type -> id | type_
	public boolean type(SemRec type){
		String first[] = {"Identifier", "float", "int"};
		String first1[] = {"Identifier"};
		String first2[] = {"float", "int"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule type: type -> id"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (id(type)){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule type: type -> type_"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (type_(type)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//type_ -> float | int
	public boolean type_(SemRec type){
		String first[] = {"float", "int"};
		String first1[] = {"float"};
		String first2[] = {"int"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule type_: type_ -> float"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("float", type)){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule type_: type_ -> int"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("int", type)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//fParams -> type id arraySize_list fParamsTail_list | EPSILON
	public boolean fParams(String functionname, String classname){
		String first[] = {"float", "int", "Identifier",  "EPSILON"};
		String follow[] = { ")"};
		String first1[] = {"float", "int", "Identifier"};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec param_type = new SemRec();
		SemRec param_name = new SemRec();
		SemRec param_dimension = new SemRec();
		SemRec size = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule fParams: fParams  -> type id arraySize_list fParamsTail_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (type(param_type)&& holder.datatypeCheck( param_type.rec, lookahead.row, lookahead.column, counter) && id(param_name) && holder.paraCheck(classname+":"+functionname, param_name.rec, lookahead.row, lookahead.column, counter)&& arraySize_list(param_dimension) && holder.sizeCompute(param_type, param_dimension, size) &&holder.addEntry(classname+":"+functionname, param_name.rec, "parameter", param_type.rec, param_dimension, null, classname, size.size)  && code.varGenerate(param_name, classname+":"+functionname, holder) && fParamsTail_list(functionname, classname)){	
			
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule fParams: fParams -> EPSILON"+'\n').getBytes()); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//aParams -> expr aParamsTail_list | EPSILON
	public boolean aParams(SemRec typeNest, String tablename, String classname){
		String first[] = {"(", "not", "Float", "Integer", "Identifier", "+", "-", "EPSILON"};
		String follow[] = { ")"};
		String first1[] = {"(", "not", "Float", "Integer", "Identifier", "+", "-"};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec type = new SemRec();
		SemRec nest = new SemRec();
		SemRec id = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule aParams: aParams  -> expr aParamsTail_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (expr( tablename, classname, type, nest, id) && holder.paraNest(typeNest, type) && aParamsTail_list(typeNest, tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule aParams: aParams -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//fParamsTail -> , type id arraySize_list
	public boolean fParamsTail(String functionname, String classname){
		String first[] = {","};
		String first1[] = {","};
		if(!skipErrors(first, null)){
			return false;
		}
		SemRec para_type = new SemRec();
		SemRec para_name = new SemRec();
		SemRec para_dimension = new SemRec();
		SemRec size = new SemRec();
		
		if(token_inset(first1)){
			try {
					production.write(("rule fParamsTail: fParamsTail -> , type id arraySize_list"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match(",") && type(para_type)&& holder.datatypeCheck(para_type.rec, lookahead.row, lookahead.column, counter) && id(para_name) && holder.paraCheck(classname+":"+functionname, para_name.rec, lookahead.row, lookahead.column, counter)&& arraySize_list(para_dimension)&& holder.sizeCompute(para_type, para_dimension, size) && holder.addEntry(classname+":"+functionname, para_name.rec, "parameter", para_type.rec, para_dimension, null, classname, size.size) && code.varGenerate(para_name, classname+":"+functionname, holder)  ){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//aParamsTail -> , expr
	public boolean aParamsTail(SemRec typeNest, String tablename, String classname){
		String first[] = {","};
		String first1[] = {","};
		if(!skipErrors(first, null)){
			return false;
		}
	
		SemRec type = new SemRec();
		SemRec nest = new SemRec();
		SemRec id = new SemRec();
		if(token_inset(first1)){
			try {
					production.write(("rule aParamsTail: aParamsTail -> , expr"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match(",") && expr(tablename, classname, type ,nest, id) && holder.paraNest(typeNest, type)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//classDecl_list -> classDecl classDecl_list | EPSILON
	public boolean classDecl_list(){
		String first[] = {"class", "EPSILON"};
		String follow[] = { "program"};
		String first1[] = {"class"};
		if(!skipErrors(first, follow)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule classDecl_list: classDecl_list  -> classDecl classDecl_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (classDecl() && classDecl_list()){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule classDecl_list: classDecl_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//funcDef_list -> funcDef funcDef_list | EPSILON
	public boolean funcDef_list(String classname){
		String first[] = {"float", "int", "Identifier", "EPSILON"};
		String follow[] = {"End of Inputstream"};
		String first1[] = {"float", "int", "Identifier"};
		if(!skipErrors(first, follow)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule funcDef_list: funcDef_list  -> funcDef funcDef_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (funcDef(classname) && funcDef_list(classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule funcDef_list: funcDef_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//statement_list -> statement statement_list | EPSILON
	public boolean statement_list(String tablename, String classname){
		String first[] = {"for", "if", "get", "put", "return", "Identifier", "EPSILON"};
		String follow[] = {"}"};
		String first1[] = {"for", "if", "get", "put", "return", "Identifier"};
		if(!skipErrors(first, follow)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule statement_list: statement_list  -> statement statement_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (statement(tablename,classname) && statement_list(tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule statement_list: statement_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//arraySize_list -> arraySize arraySize_list | EPSILON
	public boolean arraySize_list(SemRec param_dimension){
		String first[] = {"[", "EPSILON"};
		String follow[] = {";", ")", ","};
		String first1[] = {"["};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		if(token_inset(first1)){
			try {
				production.write(("rule arraySize_list: arraySize_list  -> arraySize arraySize_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (arraySize(param_dimension) && arraySize_list(param_dimension)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule arraySize_list: arraySize_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//indice_list -> indice indice_list | EPSILON
	public boolean indice_list(){
		String first[] = {"[", "EPSILON"};
		String follow[] = {")", "=", "]", ";", ",", "<", "<=", "<>", "==", ">", ">=", "+", "-", "or", "*", "/", "and", "(", "."};
		String first1[] = {"["};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		SemRec index_type = new SemRec();
		
		if(token_inset(first1)){
			try {
				production.write(("rule indice_list: indice_list  -> indice indice_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (indice(index_type) && indice_list()){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule indice_list: indice_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//fParamsTail_list -> fParamsTail fParamsTail_list | EPSILON
	public boolean fParamsTail_list(String functionname, String classname){
		String first[] = {",", "EPSILON"};
		String follow[] = {")"};
		String first1[] = {","};
		if(!skipErrors(first, follow)){
			return false;
		}
		if(token_inset(first1)){
			try {
				production.write(("rule fParamsTail_list: fParamsTail_list  -> fParamsTail fParamsTail_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (fParamsTail(functionname, classname) && fParamsTail_list(functionname, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule fParamsTail_list: fParamsTail_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//aParamsTail_list -> aParamsTail aParamsTail_list | EPSILON
	public boolean aParamsTail_list(SemRec typeNest, String tablename, String classname){
		String first[] = {",", "EPSILON"};
		String follow[] = {")"};
		String first1[] = {","};
		if(!skipErrors(first, follow)){
			return false;
		}
		
		
		if(token_inset(first1)){
			try {
				production.write(("rule aParamsTail_list: aParamsTail_list  -> aParamsTail aParamsTail_list"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (aParamsTail(typeNest, tablename, classname) && aParamsTail_list(typeNest, tablename, classname)){	
				
	                return true;
	            }
	      return false;
		}
		else if(token_inset(follow)){
			try {
				production.write(("rule aParamsTail_list: aParamsTail_list -> EPSILON"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	//relOp -> == | <> | < | > | <= | >=
	public boolean relOp(){
		String first[] = {">=", ">", "==", "<>", "<=","<"};
		String first1[] = {">="};
		String first2[] = {">"};
		String first3[] = {"=="};
		String first4[] = {"<>"};
		String first5[] = {"<="};
		String first6[] = {"<"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule relOp: relOp -> >="+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match(">=")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule relOp: relOp -> >"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match(">")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
					production.write(("rule relOp: relOp -> =="+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
			if (match("==")){
				}
	                return true;
	            }
	      return false;
		}
		if(token_inset(first4)){
			try {
					production.write(("rule relOp: relOp -> <>"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
			if (match("<>")){
				    return true;
	            }
	      return false;
		}
		if(token_inset(first5)){
			try {
					production.write(("rule relOp: relOp -> <="+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("<=")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first6)){
			try {
					production.write(("rule relOp: relOp -> <"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("<")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//addOp -> + | - | or
	public boolean addOp(){
		String first[] = {"+", "-", "or"};
		String first1[] = {"+"};
		String first2[] = {"-"};
		String first3[] = {"or"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			 try {
				production.write(("rule addOp: addOp -> +"+'\n').getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if (match("+")){
	         
      return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule addOp: addOp -> -"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("-")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
					production.write(("rule addOp: addOp -> or"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("or")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//multOp -> * | / | and
	public boolean multOp(){
		String first[] = {"*", "/", "and"};
		String first1[] = {"*"};
		String first2[] = {"/"};
		String first3[] = {"and"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule multOp: multOp -> *"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("*")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule multOp: multOp -> /"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("/")){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first3)){
			try {
					production.write(("rule multOp: multOp -> and"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("and")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//num -> Integer | Float
	public boolean num(SemRec id, SemRec return_type){
		String first[] = {"Integer", "Float"};
		String first1[] = {"Integer"};
		String first2[] = {"Float"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule num: num -> Integer"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("Integer", id) && holder.int_type(return_type)){
				
	                return true;
	            }
	      return false;
		}
		if(token_inset(first2)){
			try {
					production.write(("rule num: num -> Float"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("Float", id) && holder.float_type(return_type)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//assignOp -> =
	public boolean assignOp(){
		String first[] = {"="};
		String first1[] = {"="};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule assign: assign -> ="+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("=")){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	
	//id -> Identifier
	public boolean id(SemRec name){
		String first[] = {"Identifier"};
		String first1[] = {"Identifier"};
		if(!skipErrors(first, null)){
			return false;
		}
		if(token_inset(first1)){
			try {
					production.write(("rule id: id -> Identifier"+'\n').getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (match("Identifier", name)){
				
	                return true;
	            }
	      return false;
		}
		return false;
	}
	

	
	
	
}
