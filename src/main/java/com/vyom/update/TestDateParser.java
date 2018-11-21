package com.vyom.update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDateParser {

	
	public static void main(String[] args) {
        //System.out.println("isValid - dd/MM/yyyy with 20130925 = " + isValidFormat("dd/MM/yyyy", "20130925"));
        System.out.println("isValid - dd/MMM/yyyy with 25/09/2013 = " + isValidFormat("yyyy/mm/dd", "2018/05/15"));
        //System.out.println("isValid - dd/MM/yyyy with 25/09/2013 12:13:50 = " + isValidFormat("dd/MM/yyyy", "25/09/2013  12:13:50"));
    }

    public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            //ex.printStackTrace();
        }
        return date != null;
    }
	
}
