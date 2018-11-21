package com.vyom.update;

public class Test3 {

	public static void main(String[] args) {
		boolean isDate = false;
		String date1 = "8-7-1988";
		String date2 = "08/04/1987";
		String datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}";

		isDate = date1.matches(datePattern);
		System.out.println("Date :" + date1  + "Ans:" + isDate);

		isDate = date2.matches(datePattern);
		System.out.println("Date :" + date2 + "Ans:" + isDate);
	}

}
