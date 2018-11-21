package com.vyom.update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Test {

	public static void main(String args[]) {
		
		System.out.println(isValidFormat("dd/MMM/yyyy", "12/08/2018", Locale.ENGLISH));

	}

	public static boolean isValidFormat(String format, String value, Locale locale) {
		LocalDateTime ldt = null;
		DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

		try {
			ldt = LocalDateTime.parse(value, fomatter);
			String result = ldt.format(fomatter);
			return result.equals(value);
		} catch (DateTimeParseException e) {
			try {
				LocalDate ld = LocalDate.parse(value, fomatter);
				String result = ld.format(fomatter);
				return result.equals(value);
			} catch (DateTimeParseException exp) {
				try {
					LocalTime lt = LocalTime.parse(value, fomatter);
					String result = lt.format(fomatter);
					return result.equals(value);
				} catch (DateTimeParseException e2) {
					
				}
			}
		}

		return false;
	}
}
