/**
 * 
 */
package com.test.ingestion.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * @author Kaniska_Mandal
 * 
 */
public class XMLFlattener {

	public static void main(String[] args) throws Exception {

		// Read in data from a local file
// Build XML Document

SAXBuilder builder = new SAXBuilder();
String columnLimit = null;

Element startingTableNode = new Element("ProfitAndLossSummary");
Element mutableTableRowNode = new Element("stg_pnl_summary");
            mutableTableRowNode.setAttribute(new Attribute("isParent","true"));

Map columnIdDateMap = new HashMap();

Element txnTypeColumn = null;

Element acnthierarchyColumn = null;

Element acntNameColumn = null;

Document inputDoc = null;
InputStream is = new FileInputStream("doc.xml");
try{
	inputDoc =builder.build(is);
}finally{
	if(is!=null)
		try {
			is.close();
		} catch (IOException e) {
			//in this case do nothing
		}
}
Document outputDoc = new Document();

// mutableTableRowNode name
/*
 * Element mutableTableStartNode = new Element("stg_pnl_summary")
 * .addContent("stg_pnl_summary");
 */

XPath x = XPath.newInstance("//NumColumns");
Element columnNumElem = (Element) x.selectSingleNode(inputDoc);
columnLimit = columnNumElem.getText();



List<Element> childElems = inputDoc.getRootElement().getChildren();

for (Element childElem : childElems) {

	if (childElem.getName().equals("ReportRet")) {
		List<Element> reportRetChildElements = childElem
				.getChildren();
		for (Element reportRetChildElem : reportRetChildElements) {

			// store the transaction_type, account_type,
			// transaction_label
			// and all successive transaction_amnt and dates in one
			// XML element

			// //
			if (reportRetChildElem.getName().equals("ColDesc")) {
				List<Element> colDescChildElements = reportRetChildElem
						.getChildren();

				for (Element colDescChildElem : colDescChildElements) {

					if (colDescChildElem.getName().equals(
							"ColTitle")) {
						// create Date column
						String dateVal = colDescChildElem
								.getAttributeValue("value");
						Element parent = (Element) colDescChildElem
								.getParent();
						String columnId = parent
								.getAttributeValue("colID");
						if (dateVal != null && parent != null
								&& !columnId.equals("1")
								&& !columnId.equals(columnLimit))
							columnIdDateMap.put(columnId, dateVal);
					}

				}
			}

			// //
			if (reportRetChildElem.getName().equals("ReportData")) {
				List<Element> rptDataChildElements = reportRetChildElem
						.getChildren();

				for (Element rptDataChildElem : rptDataChildElements) {
					if (rptDataChildElem.getName()
							.equals("TextRow")) {
						// create Txn_Type column

						String attrVal = rptDataChildElem
								.getAttributeValue("value");
						if (null != attrVal
								&& (attrVal.trim().equals("Income")
										|| attrVal.trim().equals(
												"Expense") || attrVal
										.trim()
										.equals("Cost of Goods Sold")
										|| attrVal.trim().equals("Other Income"))) {
							// create transaction_type column
							txnTypeColumn = new Element("txn_type")
									.addContent(attrVal);

						}

					} else if (rptDataChildElem.getName().equals(
							"DataRow")) {
						
						List<Element> dataRowChildElements = rptDataChildElem
								.getChildren();
						for (Element dataRowChildElem : dataRowChildElements) {
						

								if (dataRowChildElem.getName().equals(
										"RowData")) {
									// create Acnt_Fullname column
									// rowType="account"
									String rowType = dataRowChildElem
											.getAttributeValue("rowType");
									if (null != rowType
											&& rowType.trim().equals(
													"account")) {
										acnthierarchyColumn = new Element(
												"acnt_hierarchy")
												.addContent(dataRowChildElem
														.getAttributeValue("value"));

									}

								} else if (dataRowChildElem.getName()
										.equals("ColData")) {
									String colId = dataRowChildElem
											.getAttributeValue("colID");
									if (null != colId
											&& colId.trim().equals("1")) {
										//
										acntNameColumn = new Element(
												"acnt_name")
												.addContent(dataRowChildElem
														.getAttributeValue("value"));

									} else {

										String colValue = dataRowChildElem
												.getAttributeValue("value");

										if (null != colId
												&& !colId.trim().equals(
														columnLimit)) {

											if (null != colValue
													&& !colValue.trim()
															.equals("0.00")) {
												// GOT A DAILY TRANSACTION
												// RECORD
												Element amountColumn = new Element(
														"txn_amnt")
														.addContent(colValue);

												Element dateColumn = new Element(
														"txn_date").addContent((String) columnIdDateMap
																.get(colId));

												if (txnTypeColumn != null) {
													mutableTableRowNode
															.addContent((Element) txnTypeColumn
																	.clone());
												}
												if (acnthierarchyColumn != null) {
													mutableTableRowNode
															.addContent((Element) acnthierarchyColumn
																	.clone());
												}
												if (acntNameColumn != null) {
													mutableTableRowNode
															.addContent((Element) acntNameColumn
																	.clone());
												}

												mutableTableRowNode
														.addContent(dateColumn);
												mutableTableRowNode
														.addContent(amountColumn);
												startingTableNode
														.addContent(mutableTableRowNode);

												// CREATE A NEW ROW
												mutableTableRowNode = new Element(
														"stg_pnl_summary");                         
                                                                                                        mutableTableRowNode.setAttribute(new Attribute("isParent","true"));

											}

										}
									}
								}
					}// End DataRow for loop

					}// End Datarow if loop

				}// End ReportData for loop
				// ///////////
			} // End Report Data if loop
		}
	}
}
// ///////////////////////////

outputDoc.setRootElement(startingTableNode);

 XMLOutputter outputter = new XMLOutputter();

//logger.info(outputter.outputString(outputDoc));

////////////////////////
 System.out
		.println("<====================================================>");
outputter.output(outputDoc, System.out);

//logger.info(outputter.outputString(outputDoc).getBytes("UTF-8"));


	}

}
