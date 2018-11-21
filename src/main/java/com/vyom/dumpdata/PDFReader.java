package com.vyom.dumpdata;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckbox;
import org.apache.pdfbox.pdmodel.interactive.form.PDChoiceField;
import org.apache.pdfbox.pdmodel.interactive.form.PDRadioCollection;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextbox;

public class PDFReader {

	public static void main(String[] args) {

		String dbUrl = "jdbc:postgresql://localhost:5432/ProcessDB";
		String schemaName = "public";
		String proposalFormConfigMetaData = "PROPOSAL_FORM_CONFIG_METADATA";
		String dbUserName = "postgres";
		String dbPassword = "postgres";
		String fileName = "C:\\Users\\Admin\\Downloads\\Proposal Form1_0.pdf";
		String requestId = "12345";

		Connection connection = null;
		Map<String, List<String>> mapOfMetaData = null;
		Map<String, String> mapOfPdfValues = null;
		Map<String, Map<String, String>> filteredMap = null;
		String strMsg = "";

		try {

			connection = connectDataBase(connection, dbUrl, dbUserName, dbPassword);
			// Get meta data from database
			mapOfMetaData = getPdfMetaData(connection, schemaName, proposalFormConfigMetaData);
			if (null != mapOfMetaData & mapOfMetaData.size() > 0) {
				// Read data from PDF file
				mapOfPdfValues = readPdfFieldValues(fileName);
				if (null != mapOfPdfValues & mapOfPdfValues.size() > 0) {
					// Get Filtered Data
					filteredMap = getFilteredData(mapOfMetaData, mapOfPdfValues);

					strMsg = insertFormField(connection, filteredMap, requestId, schemaName);

				} else {
					strMsg = "Pdf file does not contains any data";
				}

			} else {
				strMsg = "Table PROPOSAL_FORM_CONFIG_METADATA does not contains any meta data configuration rows.";
			}

		} catch (Exception exception) {
			strMsg = exception.getMessage();
		} finally {
			connection = closeDBConnection(connection);
		}

		System.out.println(strMsg);

	}

	/**
	 * @param mapOfMetaData
	 * @param mapOfPdfValues
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Map<String, String>> getFilteredData(Map<String, List<String>> mapOfMetaData,
			Map<String, String> mapOfPdfValues) throws Exception {

		Map<String, Map<String, String>> filteredMap = null;
		Map<String, String> mapOfRows = null;
		Map<String, String> innerMap = null;

		try {

			filteredMap = new LinkedHashMap<String, Map<String, String>>();

			for (Entry<String, List<String>> entry : mapOfMetaData.entrySet()) {

				if (mapOfPdfValues.containsKey(entry.getKey())) {

					mapOfRows = new LinkedHashMap<String, String>();

					if (filteredMap.containsKey(mapOfMetaData.get(entry.getKey()).get(1))) {

						if (null != mapOfMetaData.get(entry.getKey()) && null != mapOfPdfValues.get(entry.getKey())) {

							innerMap = filteredMap.get(mapOfMetaData.get(entry.getKey()).get(1));
							mapOfRows.putAll(innerMap);
							mapOfRows.put(mapOfMetaData.get(entry.getKey()).get(0).trim().replaceAll("\\s{2,}", " "),
									mapOfPdfValues.get(entry.getKey()).trim().replaceAll("\\s{2,}", " "));

						} else {
							innerMap = filteredMap.get(mapOfMetaData.get(entry.getKey()).get(1));
							mapOfRows.putAll(innerMap);
							mapOfRows.put(mapOfMetaData.get(entry.getKey()).get(0).trim(),
									mapOfPdfValues.get(entry.getKey()));
						}
						filteredMap.put(mapOfMetaData.get(entry.getKey()).get(1), mapOfRows);
					} else {
						if (null != mapOfMetaData.get(entry.getKey()) && null != mapOfPdfValues.get(entry.getKey())) {
							mapOfRows.put(mapOfMetaData.get(entry.getKey()).get(0).trim().replaceAll("\\s{2,}", " "),
									mapOfPdfValues.get(entry.getKey()).trim().replaceAll("\\s{2,}", " "));
						} else {
							mapOfRows.put(mapOfMetaData.get(entry.getKey()).get(0).trim(),
									mapOfPdfValues.get(entry.getKey()));
						}
						filteredMap.put(mapOfMetaData.get(entry.getKey()).get(1), mapOfRows);
					}
				}

			}
		} catch (Exception exception) {
			throw new Exception("Error in getFilteredData() " + exception.getMessage());
		}

		return filteredMap;

	}

	/**
	 * @param filePath
	 * @return
	 */
	
	public static Map<String, String> readPdfFieldValues(String filePath) throws Exception {

		PDDocument pdDocument = null;
		Map<String, String> mapOfPdfValues = null;
		PDDocumentCatalog pdDocumentCatalog = null;
		PDAcroForm pdAcroForm = null;
		List<Object> fields = null;
		File file = null;

		try {

			file = new File(filePath);
			pdDocument = PDDocument.load(file);
			pdDocumentCatalog = pdDocument.getDocumentCatalog();
			pdAcroForm = pdDocumentCatalog.getAcroForm();
			fields = pdAcroForm.getFields();

			mapOfPdfValues = new LinkedHashMap<String, String>();

			for (Object field : fields) {

				if (field instanceof PDTextbox) {
					PDTextbox pdTextbox = (PDTextbox) field;
					mapOfPdfValues.put("PDTextBox " + pdTextbox.getFullyQualifiedName(), pdTextbox.getValue());
				} else if (field instanceof PDChoiceField) {
					PDChoiceField pdChoiceField = (PDChoiceField) field;
					mapOfPdfValues.put("PDChoice " + pdChoiceField.getFullyQualifiedName(), pdChoiceField.getValue());
				} else if (field instanceof PDCheckbox) {
					PDCheckbox pdCheckbox = (PDCheckbox) field;
					mapOfPdfValues.put("PDCheckbox " + pdCheckbox.getFullyQualifiedName(), pdCheckbox.getValue());
				} else if (field instanceof PDRadioCollection) {
					PDRadioCollection pdCheckbox = (PDRadioCollection) field;
					mapOfPdfValues.put("PDCheckbox " + pdCheckbox.getFullyQualifiedName(), pdCheckbox.getValue());
				} else {
					mapOfPdfValues.put(field.toString(), field.getClass().toString());
				}
			}
		} catch (Exception exception) {
			if (null != exception.getMessage()
					&& exception.getMessage().contains("The system cannot find the file specified")) {
				throw new Exception("Diverted in readPdfFieldValues() " + exception.getMessage());
			} else {
				throw new Exception("Error in readPdfFieldValues() " + exception.getMessage());
			}
		} finally {
			if (null != pdDocument) {
				pdDocument.close();
			}
		}

		return mapOfPdfValues;

	}

	/**
	 * @param connection
	 * @param dbUrl
	 * @param dbUserName
	 * @param dbPassword
	 * @return
	 */
	public static Map<String, List<String>> getPdfMetaData(Connection connection, String schemaName, String tableName)
			throws Exception {

		Map<String, List<String>> mapOfMetaData = null;
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		List<String> listRows = null;

		try {
			pStatement = connection.prepareStatement("SELECT form_field_name, field_metadata_id, form_name FROM "
					+ schemaName + ".\"" + tableName + "\"");
			resultSet = pStatement.executeQuery();

			mapOfMetaData = new LinkedHashMap<String, List<String>>();
			while (resultSet.next()) {
				listRows = new ArrayList<String>();
				listRows.add(resultSet.getString("form_field_name"));
				listRows.add(resultSet.getString("form_name"));
				mapOfMetaData.put(resultSet.getString("field_metadata_id"), listRows);
			}
		} catch (Exception exception) {
			throw new Exception("Error in getPdfMetaData() " + exception.getMessage());
		} finally {
			resultSet = closeResultSet(resultSet);
			pStatement = closePreparedStatement(pStatement);
		}
		return mapOfMetaData;
	}

	/**
	 * @param connection
	 * @return Connection
	 */
	public static Connection connectDataBase(Connection connection, String dbUrl, String userName, String password)
			throws Exception {
		try {
			if (null == connection) {
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection(dbUrl, userName, password);// jdbc:postgresql://localhot:5432/vae
			}
		} catch (Exception exception) {
			throw new Exception("Diverted in connectDataBase() " + exception.getMessage());
		}

		return connection;

	}

	/**
	 * @param connection
	 * @return null
	 * @throws SQLException
	 */
	public static Connection closeDBConnection(Connection connection) {
		try {
			if (null != connection) {
				connection.close();
				connection = null;
			}
		} catch (Exception exception) {

		}
		return null;
	}

	/**
	 * @param pStatement
	 * @return null
	 * @throws SQLException
	 */
	public static PreparedStatement closePreparedStatement(PreparedStatement pStatement) throws Exception {
		try {
			if (null != pStatement) {
				pStatement.close();
			}
		} catch (Exception exception) {
			throw new Exception(exception.getMessage());
		}
		return null;
	}

	/**
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet closeResultSet(ResultSet resultSet) throws Exception {
		try {
			if (null != resultSet) {
				resultSet.close();
			}
		} catch (Exception exception) {
			throw new Exception(exception.getMessage());
		}
		return null;
	}

	/**
	 * @param connection
	 * @param filteredMap
	 * @param requestId
	 * @param schemaName
	 * @throws Exception
	 */
	public static String insertFormField(Connection connection, Map<String, Map<String, String>> filteredMap,
			String requestId, String schemaName) throws Exception {

		PreparedStatement preparedStatement = null;
		String sqlInserQuery = "";
		String formName = "";
		String strMsg = "";

		try {

			for (String strKey : filteredMap.keySet()) {
				formName = strKey;
				Map<String, String> mapfColumn = filteredMap.get(strKey);

				int i = 1;

				if ("PROPOSER_DETAILS".equalsIgnoreCase(strKey)) {

					sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
							+ "( request_id, name, address1,"
							+ " city, district, state, pin_code, telephone, fax, emal_id, business_nature, pan) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

					preparedStatement = connection.prepareStatement(sqlInserQuery);

					preparedStatement.setString(i, requestId);
					i++;

					for (String strValues : mapfColumn.keySet()) {

						preparedStatement.setString(i, mapfColumn.get(strValues));
						i++;
					}

					preparedStatement.executeUpdate();

				} else if ("CO_ORDINATOR_DETAILS".equalsIgnoreCase(strKey)) {
					try {
						i = 1;
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ "( request_id, first_name, middle_name, last_name, designation, email, contact_number, phone) "
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						for (String strValues : mapfColumn.keySet()) {
							if (null == mapfColumn.get(strValues) && ("email_id".equalsIgnoreCase(strValues)
									|| "mobile".equalsIgnoreCase(strValues))) {
								throw new Exception("email_id or mobile should not be empty.");
							}

							if ("email_id".equals(strValues)) {
								boolean emailStatus = isValidEmail(mapfColumn.get(strValues));
								if (!emailStatus) {
									throw new Exception("Please provide valid email id.");
								}
							}

							if ("mobile".equals(strValues)) {
								boolean emailStatus = isValidMobile(mapfColumn.get(strValues));
								if (!emailStatus) {
									throw new Exception("Please provide valid mobile.");
								}
							}

							if ("full_name".equalsIgnoreCase(strValues)) {
								String strArray[] = mapfColumn.get(strValues).split(" ");
								for (String eachStr : strArray) {
									preparedStatement.setString(i, eachStr);
									i++;
								}
							} else {
								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}
						preparedStatement.executeUpdate();
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("SME_ExistingPolicyDetail".equalsIgnoreCase(strKey)) {

					try {
						i = 1;
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ "( request_id, start_date, end_date, no_of_lives, proposal_no, insurer_branch,"
								+ " continuous_coverage_years, claim_amount, claim_date, premium,"
								+ " tpa, waiver_30_days_no, waiver_1_year_yes,"
								+ " waiver_1_year_no, waiver_preexisting_yes, waiver_preexisting_no,"
								+ " maternity_coverage_yes, maternity_coverage_no, "
								+ " nine_month_waiver_yes, nine_month_waiver_no, individual, family)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " + " ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						for (String strValues : mapfColumn.keySet()) {

							if ("start_date".equalsIgnoreCase(strValues) || "end_date".equalsIgnoreCase(strValues)
									|| "claim_date".equalsIgnoreCase(strValues)) {

								String strDate = mapfColumn.get(strValues);
								if (null != strDate) {

									try {
										Date utilDate = new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
										java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
										preparedStatement.setString(i, strDate);
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}

								} else {
									preparedStatement.setString(i, null);
								}
								i++;

							} else if ("no_of_lives".equalsIgnoreCase(strValues)) {
								if (null != mapfColumn.get(strValues)) {
									try {
										int noOfLives = Integer.parseInt(mapfColumn.get(strValues));
										preparedStatement.setString(i, mapfColumn.get(strValues));
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									} finally {
										preparedStatement = closePreparedStatement(preparedStatement);
									}
								} else {
									preparedStatement.setString(i, null);
								}
								i++;
							} else if ("claim_amount".equalsIgnoreCase(strValues)
									|| "premeum".equalsIgnoreCase(strValues)) {
								if (null != mapfColumn.get(strValues)) {
									try {
										double noOfLives = Double.parseDouble(mapfColumn.get(strValues));
										preparedStatement.setString(i, mapfColumn.get(strValues));
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}
								} else {
									preparedStatement.setString(i, null);
								}
								i++;
							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}

						preparedStatement.executeUpdate();
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("SME_PREVIOUSPOLICYDETAIL".equalsIgnoreCase(strKey)) {
					try {
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ "( request_id, year, insurer, premium_paid, no_of_members_1, no_of_members_2,"
								+ "claim_settled, claim_outstanding, tpa)" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						int counter = 0;
						for (String strValues : mapfColumn.keySet()) {
							counter++;
							if ("year_one".equalsIgnoreCase(strValues) || "year_two".equalsIgnoreCase(strValues)
									|| "year_three".equalsIgnoreCase(strValues)
									|| "no_of_members_11".equalsIgnoreCase(strValues)
									|| "no_of_members_12".equalsIgnoreCase(strValues)
									|| "no_of_members_21".equalsIgnoreCase(strValues)
									|| "no_of_members_22".equalsIgnoreCase(strValues)
									|| "no_of_members_31".equalsIgnoreCase(strValues)
									|| "no_of_members_32".equalsIgnoreCase(strValues)) {
								if (null != mapfColumn.get(strValues)) {
									try {
										int noOfLives = Integer.parseInt(mapfColumn.get(strValues));
										preparedStatement.setString(i, mapfColumn.get(strValues));
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}
								} else {
									preparedStatement.setString(i, null);
								}
								i++;
							} else if ("claim_settled_11".equalsIgnoreCase(strValues)
									|| "claim_outstanding_12".equalsIgnoreCase(strValues)
									|| "claim_settled_21".equalsIgnoreCase(strValues)
									|| "claim_outstanding_22".equalsIgnoreCase(strValues)
									|| "claim_settled_31".equalsIgnoreCase(strValues)
									|| "claim_outstanding_32".equalsIgnoreCase(strValues)
									|| "premium_paid_one".equalsIgnoreCase(strValues)
									|| "premium_paid_two".equalsIgnoreCase(strValues)
									|| "premium_paid_three".equalsIgnoreCase(strValues)) {
								if (null != mapfColumn.get(strValues)) {
									try {
										double noOfLives = Double.parseDouble(mapfColumn.get(strValues));
										preparedStatement.setString(i, mapfColumn.get(strValues));
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}
								} else {
									preparedStatement.setString(i, null);
								}
								i++;
							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}

							if (counter == 8) {
								preparedStatement.executeUpdate();
								sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
										+ "( request_id, year, insurer, premium_paid, no_of_members_1, no_of_members_2,"
										+ "claim_settled, claim_outstanding, tpa)"
										+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

								preparedStatement.close();
								preparedStatement = connection.prepareStatement(sqlInserQuery);
								i = 1;
								preparedStatement.setString(i, requestId);
								i++;
								counter = 0;
							}

						}
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("COVER_DETAIL".equalsIgnoreCase(strKey)) {
					try {
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ " ( request_id, sa_1000, proposed_policy_scheme_startdate, proposed_end_date, sa_50, sa_100,"
								+ " sa_200, sa_300, sa_500, cover_type_individual, cover_type_floater,"
								+ " basis_of_sum_insured_graded, basis_of_sum_insured_other, sum_insured_reason,"
								+ " maternity_yes, maternity_no, pre_existing_exclusion_waiver_yes,"
								+ " pre_existing_exclusion_waiver_no, geographycal_location_mumbai,"
								+ " geographycal_location_otm, claim_tobe_send_to_corporate, claim_tobe_send_to_member)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?," + " ?, ?, ?, ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);
						preparedStatement.setString(i, requestId);
						i++;

						// isValidCoverDetails(mapfColumn);

						for (String strValues : mapfColumn.keySet()) {

							if ("proposed_policy_scheme_startdate".equalsIgnoreCase(strValues)
									|| "proposed_end_date".equalsIgnoreCase(strValues)) {

								String strDate = mapfColumn.get(strValues);
								if (null != strDate) {
									try {
										Date utilDate = new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
										java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
										preparedStatement.setString(i, strDate);
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}

								} else {
									preparedStatement.setString(i, null);
								}
								i++;

							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}
						preparedStatement.executeUpdate();
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("PAYMENT_DETAILS".equalsIgnoreCase(strKey)) {
					try {
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ " (request_id, instrument_type_cheque, instrument_type_electronic,"
								+ " instrument_type_dd, instrument_no, bank_details, payment_date, amount)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						for (String strValues : mapfColumn.keySet()) {

							if ("payment_date".equalsIgnoreCase(strValues)) {

								String strDate = mapfColumn.get(strValues);
								if (null != strDate) {
									try {
										Date utilDate = new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
										java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
										preparedStatement.setString(i, strDate);
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}

								} else {
									preparedStatement.setString(i, null);
								}
								i++;

							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}

						preparedStatement.executeUpdate();
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("AMHI_DETAILS".equalsIgnoreCase(strKey)) {
					try {
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ " ( request_id, office_code, advisor_code_name, channel_type, branch_receipt_date,"
								+ " business_type_rural, business_type_social, business_type_other)"
								+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						for (String strValues : mapfColumn.keySet()) {

							if ("branch_receipt_date".equalsIgnoreCase(strValues)) {

								String strDate = mapfColumn.get(strValues);
								if (null != strDate) {
									try {
										Date utilDate = new SimpleDateFormat("MM/dd/yyyy").parse(strDate);
										java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
										preparedStatement.setString(i, strDate);
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}

								} else {
									preparedStatement.setString(i, null);
								}
								i++;

							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}

						preparedStatement.executeUpdate();
					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}

				} else if ("DECLARATION_DETAILS".equalsIgnoreCase(strKey)) {
					try {
						sqlInserQuery = "INSERT INTO " + schemaName + ".\"" + strKey + "\""
								+ "( request_id, name, designation, company, number_of_employee,"
								+ "no_of_employees_declare_forcover, total_no_of_dependent, no_of_dependent_declare_forcover)"
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

						preparedStatement = connection.prepareStatement(sqlInserQuery);

						preparedStatement.setString(i, requestId);
						i++;

						for (String strValues : mapfColumn.keySet()) {

							if (null == mapfColumn.get(strValues)) {
								throw new Exception("Missing information, please fill declaration details completely");
							}

							if ("number_of_employee".equalsIgnoreCase(strValues)
									|| "no_of_employees_declare_forcover".equalsIgnoreCase(strValues)
									|| "total_no_of_dependent".equalsIgnoreCase(strValues)
									|| "no_of_dependent_declare_forcover".equalsIgnoreCase(strValues)) {
								if (null != mapfColumn.get(strValues)) {
									try {
										int noOfLives = Integer.parseInt(mapfColumn.get(strValues));
										preparedStatement.setString(i, mapfColumn.get(strValues));
									} catch (Exception exception) {
										throw new Exception(exception.getMessage());
									}
								} else {
									preparedStatement.setString(i, null);
								}
								i++;
							} else {

								preparedStatement.setString(i, mapfColumn.get(strValues));
								i++;
							}
						}

						preparedStatement.executeUpdate();

					} finally {
						preparedStatement = closePreparedStatement(preparedStatement);
					}
					strMsg = "Data inserted successfully for proposer form.";

				} else {

				}

			}

		} catch (Exception exception) {

			throw new Exception("Diverted while inserting data into " + formName + " table " + exception.getMessage());

		} finally {

			preparedStatement = closePreparedStatement(preparedStatement);

		}

		return strMsg;

	}

	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
				+ "A-Z]{2,7}$";

		Pattern pattern = Pattern.compile(emailRegex);
		return pattern.matcher(email).matches();
	}

	public static boolean isValidMobile(String s) {
		Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");
		Matcher m = p.matcher(s);
		return (m.find() && m.group().equals(s));
	}

	public static void isValidCoverDetails(Map<String, String> mapfColumn) throws Exception {

		for (String strValues : mapfColumn.keySet()) {
			if (null == mapfColumn.get(strValues) && ("proposed_policy_scheme_startdate".equalsIgnoreCase(strValues)
					|| "proposed_end_date".equalsIgnoreCase(strValues))) {
				throw new Exception("policy period from and policy period to should not be empty.");
			}

			if (null == mapfColumn.get(strValues) && ("sa_50".equalsIgnoreCase(strValues)
					|| "sa_100".equalsIgnoreCase(strValues) || "sa_200".equalsIgnoreCase(strValues)
					|| "sa_300".equalsIgnoreCase(strValues) || "sa_500".equalsIgnoreCase(strValues))) {

				throw new Exception("In-Patient Sum Insured Type should not be empty.");

			}

			if (null == mapfColumn.get(strValues) && ("cover_type_individual".equalsIgnoreCase(strValues)
					|| ("cover_type_floater".equalsIgnoreCase(strValues)))) {

				throw new Exception("Cover Type should not be empty.");

			}

			if (null == mapfColumn.get(strValues) && ("basis_of_sum_insured_graded".equalsIgnoreCase(strValues)
					|| ("basis_of_sum_insured_other".equalsIgnoreCase(strValues)
							|| ("sum_insured_reason".equalsIgnoreCase(strValues))))) {

				throw new Exception("Basis of Sum Insured should not be empty.");

			}

			if (null == mapfColumn.get(strValues)
					&& ("maternity_yes".equalsIgnoreCase(strValues) || ("maternity_no".equalsIgnoreCase(strValues)))) {

				throw new Exception("Maternity should not be empty.");

			}

			if (null == mapfColumn.get(strValues) && ("pre_existing_exclusion_waiver_yes".equalsIgnoreCase(strValues)
					|| ("pre_existing_exclusion_waiver_no".equalsIgnoreCase(strValues)))) {

				throw new Exception("Pre-existing Exclusion Waiver should not be empty.");

			}

			if (null == mapfColumn.get(strValues) && ("geographycal_location_mumbai".equalsIgnoreCase(strValues)
					|| ("geographycal_location_otm".equalsIgnoreCase(strValues)))) {

				throw new Exception("Geographical Location should not be empty.");

			}

			if (null == mapfColumn.get(strValues) && ("claim_tobe_send_to_corporate".equalsIgnoreCase(strValues)
					|| ("claim_tobe_send_to_member".equalsIgnoreCase(strValues)))) {

				throw new Exception("Claims to be sent to should not be empty.");

			}
		}
	}

}
