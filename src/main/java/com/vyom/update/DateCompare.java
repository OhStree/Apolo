package com.vyom.update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCompare {
	public static void main(String[] args) throws ParseException {
		String d1 = "45/50/2019";
		String d2 = "45/51/2019";
		String RIDate = null;

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date1 = formatter.parse(d1);
		Date date2 = formatter.parse(d2);

		int dateCompare = date1.compareTo(date2);

		if (dateCompare == 0) {
			System.out.println("Date is Equal");
			RIDate = d1;
			System.out.println("RIDate = " + RIDate);
		} else if (dateCompare > 0) {
			System.out.println("Date 1 is latest");
			RIDate = d1;
			System.out.println("RIDate = " + RIDate);
		} else {
			System.out.println("Date 2 is latest");
			RIDate = d2;
			System.out.println("RIDate = " + RIDate);
		}
	}
}
