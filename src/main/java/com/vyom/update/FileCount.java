package com.vyom.update;

import java.io.File;

public class FileCount {

	public static void main(String[] args) {
		/*File folder = new File("C:/Path");
		File[] files =  folder.listFiles();
		*/
		String fileName="my TeSt Str";
		
		if(fileName.toUpperCase().contains("TEST"))
			System.out.println("Found");
		else
			System.out.println("Not");
		
	}

}
