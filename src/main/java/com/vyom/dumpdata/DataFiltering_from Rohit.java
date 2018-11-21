package com.vyom.dumpdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionData {

	static Connection c = null;

	public static void main(String[] args) throws Exception {
		dumpData();
	}

	static void dumpData() throws Exception {
		String aeReqID = "101";

		List<List<String>> selectQueriesList = getSelectQueries(aeReqID);

		for (List<String> selctQueryDetails : selectQueriesList) {

			if (selctQueryDetails.size() == 4) {
				Map<String, String> dbValues = getTableData(selctQueryDetails.get(0));
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(1), selctQueryDetails.get(2),
						selctQueryDetails.get(3));
				System.out.println("Trying to map =" + selctQueryDetails.get(1));
				System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);
			}
			if (selctQueryDetails.size() == 2) {
				Map<String, String> dbValues = getTableData(selctQueryDetails.get(0));
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(1));
				System.out.println("Trying to map =" + selctQueryDetails.get(1));
				System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);
			}
			if (selctQueryDetails.size() == 3) {

				Map<String, String> dbValues = new HashMap<String, String>();
				dbValues.put(selctQueryDetails.get(1), selctQueryDetails.get(2));
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(0));
				System.out.println("Trying to map =" + selctQueryDetails.get(0));
				System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);

			}
		}
	}

	private static void setData(String insertQuery) throws Exception {
		Connection c = getConnection();
		Statement stmt = c.createStatement();
		// System.out.println("----------------------" + insertQuery);
		//stmt.executeUpdate(insertQuery);
	}

	private static List<List<String>> getSelectQueries(String aeReqID) {
		List<String> selectQuery = new ArrayList<String>();
		List<List<String>> selectQueriesList = new ArrayList<List<String>>();

		// FORM_ORGANIZATION

		String sql1 = "SELECT  ps.name, ps.address1 , ps.address2 , ps.address3 , ps.state , ps.city, ps.pin_code , dd.Number_of_employee ,ps.business_nature"
				+ " FROM  public.\"PROPOSER_DETAILS\" as ps, public.\"DECLARATION_DETAILS\" as dd"
				+ " WHERE ps.request_id = dd.request_id and dd.request_id='" + aeReqID + "'";

		String staticCols1 = "\"primary_organization\",\"payment_location\",\"member_location\"";
		String staticValues1 = "TRUE,TRUE,TRUE";
		selectQuery.add(sql1);
		selectQuery.add("public.\"FORM_ORGANIZATION\"");
		selectQuery.add(staticCols1);
		selectQuery.add(staticValues1);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_CONTACTS
		String sql2 = "SELECT  cod.first_name, pd.address1, pd.address2, pd.address3, pd.state, pd.city, pd.pin_code,cod.email,cod.contact_number "
				+ " FROM  public.\"CO_ORDINATOR_DETAILS\" as cod, public.\"PROPOSER_DETAILS\" as pd"
				+ " WHERE cod.request_id = pd.request_id and cod.request_id='" + aeReqID + "'";

		String staticCols2 = "\"title\",\"country\",\"contact_type\",\"position\",\"same_contact\",\"primary_contact\"";
		String staticValues2 = "TRUE,TRUE,TRUE,TRUE,TRUE,TRUE";

		selectQuery.add(sql2);
		selectQuery.add("public.\"FORM_CONTACTS\"");
		selectQuery.add(staticCols2);
		selectQuery.add(staticValues2);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_POLICY_CREATION
		String sql3 = "SELECT  cd.proposed_policy_scheme_startdate, rqt.status, amhi.policy_channel_type, cd.claim_tobe_send_to_member, cd.claim_tobe_send_to_corporate"
				+ " FROM  public.\"COVER_DETAIL\" as cd, public.\"REQUEST_TABLE\" as rqt, public.\"AMHI_DETAILS\" as amhi"
				+ " WHERE cd.request_id =rqt.request_id and cd.request_id='" + aeReqID + "'";

		String staticCols3 = "\"policy_type\",\"tpa_name\",\"payment_method\",\"payroll_cut_off_day\",\"payment_terms\",\"credit_terms\",\"write_off_limit\",\"service_tax_gst_applicable\",\"brand\",\"supress_payment_request\",\"paymentr_freequency\",\"endorsement_freequency\",\"policy_status\"";
		String staticValues3 = "\'TRUE\',\'1\',\'FHPL\',\'Cheque/Cash\',\'360\',\'payment_in_advance\',\'5\',\'0.01\',\'Checked\',\'Apollo_Munich\',\'Checked\',\'Single\',\'Monthly\'";

		selectQuery.add(sql3);
		selectQuery.add("public.\"FORM_POLICY_CREATION\"");
		selectQuery.add(staticCols3);
		selectQuery.add(staticValues3);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_SCHEME_ID_CREATION
		String sql4 = "SELECT pd.name, cd.proposed_policy_scheme_startdate, amhi.intermediary, rqt.scheme_id"
				+ " FROM  public.\"PROPOSER_DETAILS\" as pd, public.\"COVER_DETAIL\" as cd ,public.\"AMHI_DETAILS\" as amhi ,public.\"REQUEST_TABLE\" as rqt"
				+ " WHERE  pd.request_id =cd.request_id and cd.request_id='" + aeReqID + "'";

		String staticCols4 = "\"brand\",\"scheme_cycle\",\"servicing_branch\",\"scheme_version_type\",\"renewal_cycle\",\"allow_any_policy_start_date\",\"commission_rate_type\",\"reason_for_scheme_version\",\"default_payment_method\",\"default_payment_frequency\",\"renewal_follow_up_code\"";
		String staticValues4 = "\'Apollo Munich from dropdown\',\'renewable\',\'Bangalore\',\'term\',\'12\',\'False\',\'percentage\',\'New_Business\',\'Cheque/Cash\',\'Single\',\'Fixed_Group_renewal\'";

		selectQuery.add(sql4);
		selectQuery.add("public.\"FORM_SCHEME_ID_CREATION\"");
		selectQuery.add(staticCols4);
		selectQuery.add(staticValues4);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_RI_QUOTE

		String staticCols5 = "ri_reference_code\",\"sum_assured\",\"PML\",\"base_product_indicator";
		String staticValues5 = "RC_SME GROUP HEALTH\',\'member_data,TRUE\',\'100%\',\'checked";

		selectQuery.add("public.\"FORM_RI_QUOTE\"");
		selectQuery.add(staticCols5);
		selectQuery.add(staticValues5);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		System.out.println(selectQueriesList);
		return selectQueriesList;
	}

	public static Connection getConnection() throws Exception {

		if (null == c) {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ProcessDB", "postgres", "postgres");
		}
		return c;
	}

	static Map<String, String> getTableData(String query) throws Exception {
		Connection c = getConnection();
		Statement stmt = c.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		ResultSetMetaData rsmd = rs.getMetaData();

		int col = rsmd.getColumnCount();

		Map<String, String> dbValues = new HashMap<String, String>();

		while (rs.next()) {
			for (int i = 1; i < col; i++) {
				dbValues.put(rsmd.getColumnName(i), rs.getString(i));
			}
		}
		return dbValues;

	}

	// if you have static data
	private static String createQuery(Map<String, String> dbValues1, String tableName, String staticCols,
			String staticValues) throws SQLException {

		String sql = "insert into " + tableName + " values (";
		String cols = "";
		String values = "";
		for (Map.Entry<String, String> entry : dbValues1.entrySet()) {
			cols = cols + "\"" + entry.getKey() + "\",";
			values = values + "'" + entry.getValue() + "',";
		}

		sql = "insert into " + tableName + "( " + cols + staticCols + ") values(" + values + staticValues + ");";
		return sql;
	}

	// if you dont have static data
	private static String createQuery(Map<String, String> dbValues1, String tableName) throws SQLException {

		String sql = "insert into " + tableName + " values (";
		String cols = "";
		String values = "";
		for (Map.Entry<String, String> entry : dbValues1.entrySet()) {
			cols = cols + "\"" + entry.getKey() + "\",";
			values = values + "'" + entry.getValue() + "',";
		}
		cols = cols.substring(0, cols.length() - 1);
		values = values.substring(0, values.length() - 1);

		sql = "insert into " + tableName + "( " + cols + ") values(" + values + ");";

		return sql;
	}

}
