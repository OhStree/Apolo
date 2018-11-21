package com.vyom.dumpdata;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
public class Test1 {

	public static void main(String[] args) {
		
		System.out.println(isValid("2018/12/21"));
		
	}
	
	public static boolean isValid(String value) {
        try {
        	Date sf =  new SimpleDateFormat("dd/mm/yyyy").parse(value);
            System.out.println(sf);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
	
}
