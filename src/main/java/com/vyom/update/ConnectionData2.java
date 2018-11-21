package com.vyom.update;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConnectionData2 {

	static Connection c = null;
	static String aeReqID = "qqww8";
	static String commissionRate = "7.5";
	static String TPA = "7.5";
	static String intermideation = "2";
	
	

	public static void main(String[] args) throws Exception {
		dumpData();
	}

	static void dumpData() throws Exception {
	

		List<List<String>> selectQueriesList = getSelectQueries(aeReqID);

		for (List<String> selctQueryDetails : selectQueriesList) {

			if (selctQueryDetails.size() == 4) {
				Map<String, String> dbValues = getTableData(selctQueryDetails.get(0));
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(1), selctQueryDetails.get(2),
						selctQueryDetails.get(3));
				//System.out.println("Trying to map =" + selctQueryDetails.get(1));
				//System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);
			}
			if (selctQueryDetails.size() == 2) {
				Map<String, String> dbValues = getTableData(selctQueryDetails.get(0));
				//System.out.println("dbValues "+dbValues);
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(1));
				//System.out.println("Trying to map =" + selctQueryDetails.get(1));
				//System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);
			}
			if (selctQueryDetails.size() == 3) {

				Map<String, String> dbValues = new HashMap<String, String>();
				dbValues.put(selctQueryDetails.get(1), selctQueryDetails.get(2));
				String insertQuery = createQuery(dbValues, selctQueryDetails.get(0));
				//System.out.println("Trying to map =" + selctQueryDetails.get(0));
				//System.out.println("Insert query is= " + insertQuery);
				setData(insertQuery);

			}
		}
	}

	private static void setData(String insertQuery) throws Exception {
		Connection c = getConnection();
		Statement stmt = c.createStatement();
		 System.out.println(insertQuery);
		stmt.executeUpdate(insertQuery);
	}

	private static List<List<String>> getSelectQueries(String aeReqID) {
		List<String> selectQuery = new ArrayList<String>();
		List<List<String>> selectQueriesList = new ArrayList<List<String>>();
	
		// FORM_ORGANIZATION

		String sql1 = "SELECT  ps.pin_code,ps.dist,swd.number_of_employee,ps.business_nature,ps.name, ps.address1 , ps.address2 , ps.address3 , ps.state , ps.city,ps.city "
				+ " FROM  public.\"PROPOSER_DETAILS\" as ps , public.\"SME_WORKINGSHEETCALCULATION_DATA\" as swd"
				+ " WHERE ps.request_id='" + aeReqID + "'"+" and swd.request_id= '" + aeReqID + "'";

		String staticCols1 = "\"primary_organization\",\"payment_location\",\"member_location\",\"request_id\"";
		String staticValues1 = "'TRUE','TRUE','TRUE','"+aeReqID+"'";
		selectQuery.add(sql1);
		selectQuery.add("public.\"FORM_ORGANIZATION\"");
		selectQuery.add(staticCols1);
		selectQuery.add(staticValues1);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_CONTACTS
		String sql2 = "SELECT  cod.first_name ,cod.middle_name ,cod.surname ,pd.address1, pd.address2, pd.address3, pd.state, pd.city, pd.pin_code,cod.email,cod.contact_number,cod.position,cod.position"
				+ " FROM  public.\"CO_ORDINATOR_DETAILS\" as cod, public.\"PROPOSER_DETAILS\" as pd"
				+ " WHERE cod.request_id = pd.request_id and cod.request_id='" + aeReqID + "'"+" and pd.request_id= '" + aeReqID + "'";

		String staticCols2 = "\"title\",\"country\",\"contact_type\",\"same_contact\",\"primary_contact\",\"request_id\"";
		String staticValues2 = "\'Mr\','TRUE','TRUE','TRUE','TRUE','"+aeReqID+"'";

		selectQuery.add(sql2);
		selectQuery.add("public.\"FORM_CONTACTS\"");
		selectQuery.add(staticCols2);
		selectQuery.add(staticValues2);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_POLICY_CREATION
		
	//updation 	
	String sql3 = "SELECT pd.gstin,cd.proposed_policy_scheme_startdate,cd.claim_tobe_send_to_member, cd.claim_tobe_send_to_corporate ,cd.endorsement_starts_date"
				+ " FROM  public.\"COVER_DETAIL\" as cd , public.\"SME_POLICY_DEFAULT_CONFIG\" as spdc ,public.\"PROPOSER_DETAILS\" as pd"
				+ " WHERE cd.request_id= '"+ aeReqID +"'"+" and spdc.request_id= '" + aeReqID + "'"+" and pd.request_id= '" + aeReqID + "'";

		String staticCols3 = "\"policy_type\",\"payment_method\",\"payroll_cut_off_day\",\"payment_terms\",\"credit_terms\",\"write_off_limit\",\"service_tax_gst_applicable\",\"brand\",\"supress_payment_request\",\"paymentr_freequency\",\"endorsement_freequency\",\"tpa_fees\",\"intermideation\",\"term_type\",\"request_id\",\"policy_channel_type\",\"tpa_name\",\"claim_payments_to\"";
		String staticValues3 = "\'Named\',\'Cheque / Cash\',\'360\',\'Payment in Advance\',\'5\',\'0.01\',\'Checked\',\'Apollo_Munich\',\'Checked\',\'Single\',\'Monthly\','"+TPA+"','"+intermideation+"',\'Co-Terminus\','"+aeReqID+"',\'Corporate\',\'Family Health Plan (TPA) Ltd (L.No.13)\',\'Employee\'";

		selectQuery.add(sql3);
		selectQuery.add("public.\"FORM_POLICY_CREATION\"");
		selectQuery.add(staticCols3);
		selectQuery.add(staticValues3);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		
		// FORM_SCHEME_ID_CREATION
		//updation 
		String sql4 = "SELECT amhi.intiative ,pd.name, cd.proposed_policy_scheme_startdate,amhi.intermediary,si.servicing_branch,amhi.intermediary"
				+ " FROM  public.\"PROPOSER_DETAILS\" as pd, public.\"COVER_DETAIL\" as cd, public.\"AMHI_DETAILS\" as amhi , public.\"SME_INPUTS\" as si "
				+ " WHERE  cd.request_id='" + aeReqID + "'"+" and pd.request_id= '" + aeReqID + "'"+" and amhi.request_id= '" + aeReqID + "'"+" and si.request_id= '" + aeReqID + "'";

		String staticCols4 = "\"brand\",\"scheme_cycle\",\"scheme_version_type\",\"renewal_cycle\",\"allow_any_policy_start_date\",\"commission_rate_type\",\"reason_for_scheme_version\",\"default_payment_method\",\"default_payment_frequency\",\"renewal_follow_up_code\",\"request_id\",\"commission_rate\",\"periodunit\",\"protected_underwriting_terms\"";
		String staticValues4 = "\'Apollo Munich\',\'renewable\',\'term\',\'12\',\'False\',\'percentage\',\'New Business\',\'Cheque / Cash\',\'Single\',\'GRP\','"+aeReqID+"','"+commissionRate+"',\'Month\',\'No\'";

		selectQuery.add(sql4);
		selectQuery.add("public.\"FORM_SCHEME_ID_CREATION\"");
		selectQuery.add(staticCols4);
		selectQuery.add(staticValues4);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();

		// FORM_RI_QUOTE

		String sql5 = "SELECT swd.total_sum_insured ,swd.pml"  //dont know ehat happend
				+ " FROM  public.\"SME_WORKINGSHEETCALCULATION_DATA\" as swd"
				+ " WHERE  swd.request_id='" + aeReqID + "'";

		String staticCols5 = "\"ri_reference_code\",\"base_product_indicator\",\"request_id\"";
		String staticValues5 = "\'RC_SME GROUP HEALTH\',\'checked\','"+aeReqID+"'";
		
		selectQuery.add(sql5);
		selectQuery.add("public.\"FORM_RI_QUOTE\"");
		selectQuery.add(staticCols5);
		selectQuery.add(staticValues5);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();
		
		
		//FORM_APPLICABLE_EXCLUSION
		//updation
		String sql6 = "SELECT cd.pre_existing_exclusion_waiver_yes , amhi.total_number_of_dependants "  
				+ " FROM  public.\"COVER_DETAIL\" as cd , public.\"AMHI_DETAILS\" as amhi"
				+ " WHERE  cd.request_id='" + aeReqID + "'";
		
		String staticCols6 = "\"request_id\",\"productname\",\"twob\",\"twoc\"";
		String staticValues6 = "'"+aeReqID+"',\'SME\',\'30D Excl Waiver for all\',\'1st Yr Excl Waiver apply all\'";

		selectQuery.add(sql6);
		selectQuery.add("public.\"FORM_APPLICABLE_EXCLUSION\"");
		selectQuery.add(staticCols6);
		selectQuery.add(staticValues6);
		selectQueriesList.add(selectQuery);
		selectQuery = new ArrayList<String>();
		
		
		
		//FORM_FINANCE
				//new table
				String sql7 = "SELECT amhi.pis_number1,amhi.pis_number2,amhi.pis_number3"  
						+ " FROM  public.\"AMHI_DETAILS\" as amhi"
						+ " WHERE  amhi.request_id='" + aeReqID + "'";
				
				String staticCols7 = "\"request_id\"";
				String staticValues7 = "'"+aeReqID+"'";

				selectQuery.add(sql7);
				selectQuery.add("public.\"FORM_FINANCE\"");
				selectQuery.add(staticCols7);
				selectQuery.add(staticValues7);
				selectQueriesList.add(selectQuery);
				selectQuery = new ArrayList<String>();
	//over	
		
		
		System.out.println(selectQueriesList);
		

		//System.out.println("FORM_RI_QUOTE: "+selectQueriesList);
		return selectQueriesList;
	}

	public static synchronized Connection getConnection() throws Exception {

		if (null == c) {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://10.51.29.21:5432/ProcessDB", "postgres", "admin123");
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
			for (int i = 1; i <=col; i++) {
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
			if(entry.getValue()!=null)
				values = values + "'" + entry.getValue() + "',";
			else
				values = values + "' ',";
		}

		sql = "insert into " + tableName + "( " + cols + staticCols + ") values(" + values + staticValues + ");";
		return sql;
	}

	// if you dont have static data
	private static String createQuery(Map<String, String> dbValues1, String tableName) throws SQLException {
		//System.out.println("dbValues1 "+dbValues1);
		String sql = "insert into " + tableName + " values (";
		String cols = "";
		String values = "";
		for (Map.Entry<String, String> entry : dbValues1.entrySet()) {
			cols = cols + "\"" + entry.getKey() + "\",";
			if(entry.getValue()!=null)
				values = values + "'" + entry.getValue() + "',";
			else
				values = values + "' ',";
		}
		cols = cols.substring(0, cols.length() - 1);
		values = values.substring(0, values.length() - 1);

		sql = "insert into " + tableName + "( " + cols + ") values(" + values + ");";

		return sql;
	}

}
