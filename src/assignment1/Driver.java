package assignment1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class Driver {
	public FileOutputStream normalOutput;
	public FileOutputStream errorOutput;
	public FileInputStream input;
	
	Driver(String normalOutputName, String errorOutputName, String inputStreamName){
		
		try {
			normalOutput = new FileOutputStream(normalOutputName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			errorOutput = new FileOutputStream(errorOutputName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			input = new FileInputStream(inputStreamName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void drive(Counter counter){
		
		Token a;
		Scanner b = new Scanner(input);
		
		//scanner will keep reading source file and output token's information until the end of the file
		while(b.endofStream != true){
			a = b.nextToken();
			if(a.getType().equals("Undefined Token") || a.getType().equals("Malformation") || a.getType().equals("Uncompleted Comment")){
				counter.counter = counter.counter + 1;
				try {
					errorOutput.write(a.getToken().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				try {
					normalOutput.write(a.getToken().getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
