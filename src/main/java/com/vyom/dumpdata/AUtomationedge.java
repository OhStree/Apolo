package com.vyom.dumpdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AUtomationedge {
	public static void main(String[] args) {
		String dateStr = "15/12/2019";
		System.out.println(dateStr);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date date = formatter.parse(dateStr);
			String strDate = formatter.format(date);
			System.out.println("Date is: " + strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}