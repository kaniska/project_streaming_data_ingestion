package com.test.ingestion.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TestDateUtil {
	
	/**
	  Date and Time Pattern  Result  ::
	  
		"yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT  
		"EEE, MMM d, ''yy"  Wed, Jul 4, '01  
		"h:mm a"  12:08 PM  
		"hh 'o''clock' a, zzzz"  12 o'clock PM, Pacific Daylight Time  
		"K:mm a, z"  0:08 PM, PDT  
		"yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM  
		"EEE, d MMM yyyy HH:mm:ss Z"  Wed, 4 Jul 2001 12:08:56 -0700  
		"yyMMddHHmmssZ"  010704120856-0700  
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ"  2001-07-04T12:08:56.235-0700  
		
		 "EEE MMM dd HH:mm:ss zzzz yyyy"  Wed Mar 21 18:18:25 PDT 2012
	  */
	
	/**
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	 public static String formatDate(Date date, String pattern) {
	        if (date == null) throw new IllegalArgumentException("date is null");
	        if (pattern == null) throw new IllegalArgumentException("pattern is null");
	        
	        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
	        //formatter.setTimeZone(GMT);
	        return formatter.format(date);
	    }
	 
	 /**
	  * Salesforce Date Format : yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	  * Quickbook Desktop Date Format : yyyyMMdd HHmmss.SSS
	  * 
	  * @param inputdate
	  * @param appDateFormat TODO
	  * @param tenantAppTimeZone
	  * @return
	  */
	 public static String convertDateToStr(Date inputdate, String appDateFormat, String tenantAppTimeZone) {
		 String strDate = null;
		 try {
			 //SimpleDateFormat TestDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
			 SimpleDateFormat TestDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.US); 
			// Date date = TestDateFormatter.parse("2001-07-04T12:08:56");
			 Date date = TestDateFormatter.parse(inputdate.toString()); // Wed Mar 21 18:18:25 PDT 2012
			 		 
			 SimpleDateFormat sfdcFormatter = new SimpleDateFormat(appDateFormat, Locale.US);  
			 // yyyy-MM-dd'T'HH:mm:ss.SSS'Z' // "yyyyMMdd HHmmss.SSS"
			 // yyyy-MM-dd'T'HH:mm:ss
			 sfdcFormatter.setTimeZone(TimeZone.getTimeZone(tenantAppTimeZone));
			 strDate = sfdcFormatter.format(date);
			 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return strDate;
	 }
	 
	 /**
	  * 
	  * @param inputDateStr
	  * @param appDateFormat
	  * @param tenantAppTimeZone
	  * @return
	  */
	 public static Date convertStrToDate(String inputDateStr, String appDateFormat, String tenantAppTimeZone) {
		 Date date = null;
		 try {
			 SimpleDateFormat sfdcFormatter = new SimpleDateFormat(appDateFormat, Locale.US);  
			 // yyyy-MM-dd'T'HH:mm:ssZZ // yyyy-MM-dd'T'HH:mm:ss.SSS'Z' // "yyyyMMdd HHmmss.SSS" // 
			 sfdcFormatter.setTimeZone(TimeZone.getTimeZone(tenantAppTimeZone));
			 date = sfdcFormatter.parse(inputDateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return date;
	 }
	 
	 
	 /**
	  * 
	  * @param inputDateStr
	  * @param appDateFormat // SFDC - appDateFormat, // QB - 
	  * @param tenantAppTimeZone
	  * @return
	  */
	 public static String convertDefaultDateToSpecificFormat(String initialLoadStartDate, String appDateFormat, String tenantAppTimeZone) {
		 Date date = null;
		 try {
			 SimpleDateFormat sfdcFormatter = new SimpleDateFormat(appDateFormat, Locale.US);  
			 // yyyy-MM-dd'T'HH:mm:ssZZ // yyyy-MM-dd'T'HH:mm:ss.SSS'Z' // "yyyyMMdd HHmmss.SSS" // 
			 sfdcFormatter.setTimeZone(TimeZone.getTimeZone(tenantAppTimeZone));
			 date = sfdcFormatter.parse(initialLoadStartDate);  // "1970-01-01T05:05:05.000Z"
			 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 return convertDateToStr(date, appDateFormat, tenantAppTimeZone);
	 }
	 
	 
	 public static void main(String[] args) {
		 try {		
			 // Salesforce Date Conversions
			 //System.out.println(" Formatted Date >> "+convertDateToStr(new Date(), "yyyy-MM-dd'T'HH:mm:ss.S", "GMT"));
			 
			// System.out.println(" Formatted Date >> "+convertStrToDate("2005-02-17T19:24:58-08:00", "yyyy-MM-dd'T'HH:mm:ss", "PST"));
			 
			 //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // "yyyy-MM-dd HH:mm:ss.S"  // 
			 // 1970-01-01T05:05:05.070Z
			 // QuickBook Date Conversions
			 
			 
			 	 String date1 = "19700101 050505.000" ;
			 
			     String date3 = "20121213 112924.000" ;
			     
			     String date2 = "20121013 112924.000" ;
			     
			     Calendar  cal = Calendar.getInstance();
			     
			     Date currentTime = new Date();
			     
			     Date initialLoad_fromDate = convertStrToDate("1970-01-01T05:05:05.000Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "UTC");
				 cal.setTime(initialLoad_fromDate);
				 cal.add(Calendar.DATE, 2);			 
				 
				 Date initialLoad_toDate = ( cal.getTime().compareTo(currentTime) < 0 ) ? cal.getTime() : currentTime;
			     
				 System.out.println("initialLoad_toDate >>> "+initialLoad_toDate);
				 
			     Date incrementallLoad_fromDate = convertStrToDate("2013-03-18T05:05:05.000Z", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "UTC");
				 cal.setTime(incrementallLoad_fromDate);
				 cal.add(Calendar.DATE, 2);			 
				 Date incrementalLoad_toDate = ( cal.getTime().compareTo(currentTime) < 0 ) ? cal.getTime() : currentTime;
				 
				 System.out.println("incrementalLoad_toDate   >>> "+incrementalLoad_toDate);
				 
			     
			 //cal.setTime(resultingDate1);	     
			     
			 YEAR_COUNT = cal.get(Calendar.YEAR) - 1970;	
				  
			 //findStartDateForReportQuery(date1 ,  date2, "yyyyMMdd HHmmss.SSS", "false") ;			 
			//findStartDateForReportQuery(date2 , date3,  "yyyyMMdd HHmmss.SSS", "false") ;			 
			 
		  } catch (Exception e) {
			e.printStackTrace();
		}
	 }

	 private static int YEAR_COUNT = 0;
	 private static boolean INVOKED_ONCE = false;
	 /**
	  * 
	  * @param Fetch_Data_Since_Last_Run
	 * @param yEAR_COUNT 
	  * @return
	  * @throws ParseException
	  */
	 private static void  findStartDateForReportQuery(String startDateStrInput, String endDateStrInput, String inputDateFormat, String Fetch_Data_Since_Last_Run) throws ParseException{
		 
		 System.out.println("Year Count : "+YEAR_COUNT);
		 
		 //
		 Date resultingDate1 = null;
		 Date resultingDate2 = null;
		 
		 String timeZone = "PST";
		 
		 SimpleDateFormat sfdcFormatter1 = new SimpleDateFormat(inputDateFormat, Locale.US);
		 resultingDate1 = sfdcFormatter1.parse(startDateStrInput);
		 resultingDate2 = sfdcFormatter1.parse(endDateStrInput);
		 
		 if(!startDateStrInput.startsWith("1970") && !Fetch_Data_Since_Last_Run.equals("false")) {
			 Calendar  cal = Calendar.getInstance();
			 cal.setTime(resultingDate1);
			 cal.add(Calendar.YEAR, -1);			 
			 cal.set(Calendar.MONTH, Calendar.JANUARY);
			 cal.set(Calendar.DAY_OF_MONTH, 1);
			 resultingDate1 = cal.getTime();
		 }else{
			 // find the year count 1970 to till date
			 
			 Calendar  cal = Calendar.getInstance();
			 cal.setTime(resultingDate2); // say 2012-12-24
			 
			 if(INVOKED_ONCE) {
				 cal.add(Calendar.YEAR, -1);
			 }
			 
			 cal.set(Calendar.MONTH, Calendar.JANUARY);
			 cal.set(Calendar.DAY_OF_MONTH, 1);
			 resultingDate1 = cal.getTime();
			 
			 if(YEAR_COUNT-- > 0 && INVOKED_ONCE){
				 //
				 cal = Calendar.getInstance();
				 cal.setTime(resultingDate1);
				 cal.set(Calendar.MONTH, Calendar.DECEMBER);			 
				 cal.set(Calendar.DAY_OF_MONTH, 31);
				 resultingDate2 = cal.getTime();
				 
			 }
			 
		 }
		 
		 SimpleDateFormat sfdcFormatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);  
		 sfdcFormatter2.setTimeZone(TimeZone.getTimeZone(timeZone));
		 
		 String  strStartDateOutput = sfdcFormatter2.format(resultingDate1);		
		 String  strEndDateOutput = sfdcFormatter2.format(resultingDate2);
		 
		 System.out.println("\t\t Start Date : "+strStartDateOutput);
		 System.out.println("\t\t End Date : "+strEndDateOutput);
		 
		 INVOKED_ONCE = true;
		//
		 if(startDateStrInput.startsWith("1970") || Fetch_Data_Since_Last_Run.equals("false")) {
			 if(YEAR_COUNT > 0){
				 //
				 findStartDateForReportQuery(strStartDateOutput, strEndDateOutput, "yyyy-MM-dd", Fetch_Data_Since_Last_Run);
			 }
		 }
	 }
	 
	 
}
