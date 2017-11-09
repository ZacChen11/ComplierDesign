package assignment1;

import java.util.LinkedList;

public class SymbolTable {
	public LinkedList <String []> entry;
	String table_name;
	
	public SymbolTable(String name){
		entry = new LinkedList<String []>();
		this.table_name = name;
	}
	
	public String getTableName(){
		return table_name;
	}
	
	void addrecord(String id, String kind, String type, SemRec dimension, String link, String parent, int size){
		if(dimension == null){
			String[] a = {id, kind, type, "[]", link, parent, Integer.toString(size)};
			entry.add(a);
		}else{
				String[] a = {id, kind, type, dimension.array.toString(), link, parent, Integer.toString(size)};
				entry.add(a);
		}
	}
	
}
