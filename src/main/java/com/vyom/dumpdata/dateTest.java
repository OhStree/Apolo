package com.vyom.dumpdata;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dateTest {

	static String PolicyDetailsPeriodofInsuranceFromDate = "1/2/2018";
	static String PolicyDetailsPeriodofInsuranceToDate = "02/03/2019";
	static String ToBeFilledbyOperationsStampDate = "03/04/2020";

	static String dateFormat = "dd/mm/yyyy";
	static String dateFormat1 = "d/m/yyyy";

	public static void main(String[] args) {

		String dateValidation = "failure by date formate or date empty";

		try {

			boolean policyDateFrom = isValidFormat(dateFormat, PolicyDetailsPeriodofInsuranceFromDate.split(" ")[0]);

			boolean policyDateTo = isValidFormat(dateFormat, PolicyDetailsPeriodofInsuranceToDate.split(" ")[0]);

			boolean stampDate = isValidFormat(dateFormat, ToBeFilledbyOperationsStampDate.split(" ")[0]);

			if (policyDateFrom && policyDateTo && stampDate) {

				dateValidation = "success";
				System.out.println(dateValidation);

			}

		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

	}

	public static boolean isValidFormat(String format, String value) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(value);
			if (!value.equals(sdf.format(date))) {
				SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat1);
				date = sdf1.parse(value);
				if (!value.equals(sdf1.format(date))) {
					date = null;
				}

			}
		} catch (Exception exception) {
			exception.getMessage();
		}
		System.out.println(date);
		return date != null;
	}
}
