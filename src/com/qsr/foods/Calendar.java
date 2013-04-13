/*****************************************************************************************
 * @author Bhaskar Reddy
 * 
 * 
 * ***************************************************************************************
 */

package com.qsr.foods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.DroidGap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class Calendar extends Activity {

    public DroidGap mAppView;
    public CordovaWebView mGap;
    public static Context context;
    public MyCalendar m_calendars[];
    public static String m_selectedCalendarId;
    private Uri eventUri;
    
    
    /*************************************************************************
     * @param context
     * @param appView
     ********************************************************************* 
     */
    public Calendar(Context context, CordovaWebView appView) {
        getCalendars(context);
        Calendar.context = context;
        // displayEvents(context);
        mGap = appView;
    }
    
    /*******************************************************************************
     * @author Bhaskar Reddy
     * @desc This inner class behave like bean to hold calendar detils.
     *       ************
     */
    class MyCalendar {
        public String name;
        public String id;

        public MyCalendar( String _id) {
            //name = _name;
            id = _id;
        }
        
        public String toString() {
            return name;
        }
    }

   
    private void getCalendars(Context context) {
        String[] l_projection = new String[] { "_id"};
        Uri l_calendars;
        
        l_calendars = Uri.parse(getCalendarUriBase() + "calendars");

        Cursor l_managedCursor = context.getContentResolver().query(
                l_calendars, l_projection, null, null, null);
        if (l_managedCursor != null && l_managedCursor.moveToFirst()) {
            m_calendars = new MyCalendar[l_managedCursor.getCount()];
            String l_calId;
            int l_cnt = 0;
            int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
            do {
                l_calId = l_managedCursor.getString(l_idCol);
                m_calendars[l_cnt] = new MyCalendar(l_calId);
                ++l_cnt;
            } while (l_managedCursor.moveToNext());
            m_selectedCalendarId = m_calendars[0].id;
        }

    }
    
    /**************************************************************************
     * @written Bhaskar Reddy
     * @Desc This method is to save recurrence events in calendar.
     * @param title
     *            ,location,description,startTime,endTime,dayHour
     * 
     * @return String
     * 
     ************************************************************************** 
     */

    public String addEvent(String title, String eventLocation,
            String description, String startTime, String endTime, String dayHour,String time_zone) {

        String result = null;
        
        System.out.println("start time :" + startTime + "==endTime  :"
                + endTime);
        ContentValues event = new ContentValues();
        
        long event_startTime, event_endTime;
        try {
            event_startTime = getFormatedTime(System.currentTimeMillis()
                    + Long.decode(startTime),time_zone);
            event_endTime = getFormatedTime(System.currentTimeMillis() + Long.decode(endTime),time_zone);
            event.put("calendar_id", m_selectedCalendarId);
            event.put("title", title);
            event.put("description", description);
            event.put("eventLocation", eventLocation);
            event.put("dtstart", event_startTime);
            event.put("dtend", event_endTime);
            event.put("eventTimezone", TimeZone.getDefault().getID());
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }
        try {
            eventUri = Uri.parse(getCalendarUriBase() + "events");
            Uri uri = Calendar.context.getContentResolver().insert(eventUri,
                    event);
            Log.v("++++++test", uri.toString());
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }
        return result;
        
    }
    
    /**************************************************************************
     * @written Bhaskar Reddy
     * @Desc This method is to save recurrence events in calendar.
     * @param
     * 
     * @return String
     * 
     ************************************************************************** 
     */
    public String addRecurrentEvent(String title, String eventLocation,
            String description, String startTime, String rrule,String time_zone) {
        
        String result = null;
        
        System.out.println("Recurring Event");
        ContentValues event = new ContentValues();
        
        long event_startTime;
        try {
            event_startTime = getFormatedTime(System.currentTimeMillis()
                    + Long.decode(startTime),time_zone);
            // event_endTime = System.currentTimeMillis() +
            // Long.decode(endTime);
            
            // durationHrs = (Integer.parseInt(endTime))/60;
            event.put("calendar_id", m_selectedCalendarId);
            event.put("title", title);
            event.put("description", description);
            event.put("eventLocation", eventLocation);
            event.put("dtstart", event_startTime);
            // event.put("dtend", event_startTime);
            event.put("duration", "PT0H60M");
            event.put("rrule", rrule);
            event.put("eventTimezone", TimeZone.getDefault().getID());
            
            // event.put("allDay", 0);
            // event.put("eventStatus", 1);
            // event.put("visibility", 0);
            // event.put("transparency", 0);
            // event.put("hasAlarm", 1);
            
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }
        try {
            eventUri = Uri.parse(getCalendarUriBase() + "events");
            Uri uri = Calendar.context.getContentResolver().insert(eventUri,
                    event);
            Log.v("Inseted successfully", uri.toString());
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }
        return result;
    }
    
    public static Date getDateStr(String p_time_in_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date time = new Date();
        try {
            time = sdf.parse(p_time_in_millis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
    
    /**************************************************************************************
     * 
     *  @written Bhaskar Reddy
     *  @Desc    This method to check device sdk and returns suitable URI.
     * @return   String(Calendar URI Base)
     * 
     * ************************************************************************************
     */
    private String getCalendarUriBase() {
        String calendarUriBase = null;
        
        if (Build.VERSION.SDK_INT >= 8) {
            calendarUriBase = "content://com.android.calendar/";
        } else {
            calendarUriBase = "content://calendar/";
        }
        
        return calendarUriBase;
    }
    
    
    private Long getFormatedTime(long mseconds,String time_zone)
    {
       
    	long actualDate_milliseconds = 0l; 
    	try
    	{
    	SimpleDateFormat dateFormat = new SimpleDateFormat();
	    dateFormat.setTimeZone(TimeZone.getTimeZone(time_zone));
	    actualDate_milliseconds = dateFormat.parse(new SimpleDateFormat().format(new Date(mseconds))).getTime();
    	}catch (Exception e) {
			e.printStackTrace();
		}
    	return actualDate_milliseconds;
    	
    }
    
    
    /********************************************************************************************************
     * @Wrote : V Bhaskar Reddy
     * method : getFormatedDate(String date, String timeZone);
     * Desc   : This method to convert date from actual timezone to device timezone , This method will called from javascript.
     * @param date
     * @param timeZone
     * @return
     * 
     **********************************************************************************************************/
    public void getFormatedDate(String date, String timeZone,String title,String time,String category,String booking_id)
    {
    	String formated_date = null;
    	try
    	{
    		
    		
    		SimpleDateFormat output_date_format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    		SimpleDateFormat dtformat = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
    		java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone(timeZone));
    		cal.setTime(output_date_format.parse(date));
    		
    		java.util.Calendar device_cal = Calendar.convertToLocal(cal);
    	     formated_date = dtformat.format(device_cal.getTime());
    	     final String date_time[] = formated_date.split(" ");
    	     final String converted_date = date_time[0];
    	     final String converted_time = date_time[1]+" "+date_time[2];
    	     
    	     mGap.loadUrl("javascript:getEventDetails('"+title.replace("'", "\\'")+"','"+converted_date+"','"+converted_time+"','"+timeZone+"','"+category+"','"+booking_id+"')");
    	    //return date+"==>"+formated_date+"@@@"+timeZone+"@@@"+title+"@@@"+time+"@@@"+category+"@@@"+booking_id;
    	
    	}catch (Exception e) {
    		e.printStackTrace();
    		formated_date = "Exception while converting date :"+date +" From "+timeZone+"  To "+TimeZone.getDefault().getDisplayName();
    		System.out.println(formated_date);
    		  //mGap.loadUrl("javascript:alert('Exception while converting date :"+date +" From "+timeZone+"  To "+TimeZone.getDefault().getDisplayName()+"')");
		}
    	
    }
    
    /***
     * @Written : V Bhaskar Reddy
     * @Desc  :This method to Convert date and time  from specific timezone to device timezone
     * @param cal (java.util.Calendar)
     * @return java.util.Calendar
     */
    public static java.util.Calendar convertToLocal(java.util.Calendar vendor_cal) {

        Date booked_date = vendor_cal.getTime();
        TimeZone tz = vendor_cal.getTimeZone();
        int offsetToGmt = tz.getOffset(vendor_cal.getTimeInMillis());      
        java.util.Calendar device_cal = java.util.Calendar.getInstance(TimeZone.getDefault());
        device_cal.setTime(booked_date);
        int offsetToLocal = device_cal.getTimeZone().getOffset(device_cal.getTimeInMillis());        
        device_cal.add(java.util.Calendar.MILLISECOND, -offsetToGmt+offsetToLocal);
        return device_cal;
}
    
}