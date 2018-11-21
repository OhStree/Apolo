import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plan {
	public static void main(String[] args) throws Exception {
		//String test = "SME Group Health (Individual Basis) - A Mum Base Mat PEDEx5L";
		String reqID="req1";
		String COVER_DETAILQuery="SELECT sa_50, sa_100, sa_200, sa_300, sa_500, sa_1000, cover_type_individual, cover_type_floater, maternity_yes, maternity_no, pre_existing_exclusion_waiver_yes,  pre_existing_exclusion_waiver_no, geographycal_location_mumbai, geographycal_location_otm FROM public.\"COVER_DETAIL\" where request_id='"+reqID+"'";
		Map<Integer,List<String>> planMap=getTableData(COVER_DETAILQuery); 
		createProductPlanQuery(planMap,reqID);
		
	}

	public static void createProductPlanQuery(Map<Integer,List<String>> planMap,String reqId)throws Exception{
		Connection con=getConnection();
		Statement stmt = con.createStatement();
		
		for (Map.Entry<Integer,List<String>> entry : planMap.entrySet()) {
		
			List<String> productPlanDetails = entry.getValue();
			
			//List<String> productPlan = new ArrayList<String>(entry.getValue());
			String tableName="public.\"FORM_PRODUCT_PLANS\"";
			String sql = "insert into " + tableName + " (request_id,si, product_plan_name, cover_type) values (";
			String values = "'"+reqId+"',";
			for(String planDetails:productPlanDetails){
				values=values+"'" + planDetails + "',";
			}
			 
			values = values.substring(0, values.length() - 1);
			sql=sql+values+");";
			System.out.println(sql);
			stmt.executeUpdate(sql);
		}
	}
	
	
	
	
	static Map<Integer,List<String>> getTableData(String COVER_DETAILQuery) throws Exception {
		Map<Integer,List<String>> planMap=new HashMap<Integer,List<String>>();
		Integer planCount=0;
		List<String>plan=new ArrayList<String>();
		String cover_type_individual="", cover_type_floater="", maternity_yes="", maternity_no="";
		String pre_existing_exclusion_waiver_yes="",  pre_existing_exclusion_waiver_no="";
		String geographycal_location_mumbai="";
		
		Connection con = getConnection();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(COVER_DETAILQuery);

		List<String>SAList=new ArrayList<String>();
		int startCol=1;
		int endCol=7;
		while (rs.next()) {
			for(int i=startCol;i<endCol;i++){
				SAList.add(rs.getString(i));
			}
			
			 cover_type_individual=rs.getString(7);
			 cover_type_floater=rs.getString(8);
			 maternity_yes=rs.getString(9);
			 maternity_no=rs.getString(10);
			 pre_existing_exclusion_waiver_yes=rs.getString(11); 
			 pre_existing_exclusion_waiver_no=rs.getString(12);
			 geographycal_location_mumbai=rs.getString(13);
			 break;
		}
		System.out.println(SAList);
		System.out.println("cover_type_individual:"+cover_type_individual);
		System.out.println("cover_type_floater:"+cover_type_floater);
		System.out.println("maternity_yes:"+maternity_yes);
		System.out.println("maternity_no:"+maternity_no);
		System.out.println("pre_existing_exclusion_waiver_yes:"+pre_existing_exclusion_waiver_yes);
		System.out.println("pre_existing_exclusion_waiver_no:"+pre_existing_exclusion_waiver_no);
		System.out.println("geographycal_location_mumbai:"+geographycal_location_mumbai);
		
				
		String planName = "SME Group Health";
		String insuranceType = "";
		String planShortCode = "";
		String region = "";
		String PED ="";
		String maternity ="";
				
		if(geographycal_location_mumbai.equalsIgnoreCase("Mumbai")){
			region="MMR";
		}else{
			region="ROI";
		}
		
		if(pre_existing_exclusion_waiver_yes.equalsIgnoreCase("yes")){
			PED = "yes";
		}
		
		if(pre_existing_exclusion_waiver_no.equalsIgnoreCase("yes")){
			PED = "no";
		}
		
		if(maternity_yes.equalsIgnoreCase("yes")){
			maternity = "yes";
		}
		
		if(maternity_no.equalsIgnoreCase("yes")){
			maternity = "no";
		}
				
		if(cover_type_individual.equalsIgnoreCase("individual")){
			insuranceType = "Individual";
			String PREMIUM_CHARTQuery="SELECT \"PLAN_SHORT_CODE\"  FROM public.\"PREMIUM_CHART\" where \"INSURANCE_TYPE\"='"+insuranceType+"' and \"REGION\"='"+region+"' and \"PED\"='"+PED+"' and \"MATERNITY\"='"+maternity+"'";
			rs = stmt.executeQuery(PREMIUM_CHARTQuery);
			while(rs.next()){
				planShortCode=rs.getString(1);
			}
			for(int i=startCol-1;i<endCol-1;i++){
			String siValue=SAList.get(i);
			if(siValue!=null){
				plan.add(siValue);
				String planLocal = createPlan(planName, insuranceType, planShortCode, region, maternity, PED, i);
				plan.add(planLocal);
				plan.add(insuranceType);
				break;
			}
		}
		}
		
		planMap.put(planCount++, plan);
		
		if(cover_type_floater.equalsIgnoreCase("floater")){
			insuranceType = "Floter";
			String PREMIUM_CHARTQuery="SELECT \"PLAN_SHORT_CODE\"  FROM public.\"PREMIUM_CHART\" where \"INSURANCE_TYPE\"='"+insuranceType+"' and \"REGION\"='"+region+"' and \"PED\"='"+PED+"' and \"MATERNITY\"='"+maternity+"'";
			rs = stmt.executeQuery(PREMIUM_CHARTQuery);
			while(rs.next()){
				planShortCode=rs.getString(1);
			}
			for(int i=startCol-1;i<endCol-1;i++){
				plan=new ArrayList<String>();
				String siValue=SAList.get(i);
				if(siValue!=null){
					plan.add(siValue);
					String planLocal = createPlan(planName, insuranceType, planShortCode, region, maternity, PED, i);
					plan.add(planLocal);
					plan.add(insuranceType);
					planMap.put(planCount++, plan);
				}
			}
		}
		System.out.println(planMap);
		return planMap;
	}

	public static String createPlan(String planName, String insuranceType, String planShortCode, String region,
			String maternity, String PED, int sumInsured) throws Exception {
		String maternityLocal = "";
		String PEDLocal = "";

		if (maternity.equalsIgnoreCase("yes")) {
			maternityLocal = "Mat";
		}

		if (PED.equalsIgnoreCase("yes")) {
			PEDLocal = "PEDEx";
		}
		
		if(region.equalsIgnoreCase("MMR")){
			region="Mum";
		}else{
			region="ROI";
		}

		String test = planName + " (" + insuranceType + " Basis) - " + planShortCode + " " + region + " Base "
				+ maternityLocal + " " + PEDLocal + " " + getSumInsured(sumInsured);
		return test;
	}

	public static String getSumInsured(int sumInsured) throws Exception {
		if (sumInsured==0)
			return "50k";
		if (sumInsured==1)
			return "1L";
		if (sumInsured==2)
			return "2L";
		if (sumInsured==3)
			return "3L";
		if (sumInsured==4)
			return "4L";
		if (sumInsured==5)
			return "5L";

		return null;
	}

	public static Connection getConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
		Connection c = DriverManager.getConnection("jdbc:postgresql://10.51.27.11:5432/ProcessDB", "postgres",
				"admin123");

		return c;
	}
}
