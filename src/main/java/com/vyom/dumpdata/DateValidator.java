package com.vyom.dumpdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator {

	public static void main(String[] args) {
		
		System.out.println(getSplitedDate("20/4/2001"));

		System.out.println(isThisDateValid("20/4/2020", "dd/MM/yyyy"));

	}

	public static boolean isThisDateValid(String dateToValidate, String dateFromat) {

		if (dateToValidate == null) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);

		try {

			// if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);
			System.out.println(date);

		} catch (ParseException e) {

			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean getSplitedDate(String dateToValidate) {

		if (dateToValidate == null) {
			return false;
		}
		try {

			String str[] = dateToValidate.split("/");

			if (str[2].length() == 4) {
				return true;
			}

		} catch (Exception exception) {

			exception.getMessage();
			return false;
		}

		return false;
	}

}
