package com.ginz.util.base;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期与字符串相互转换的工具类
 */
public class DateFormatUtil {

	//日期转String	yyyy-MM-dd
	public static String dateToString(Date date) {
		if (date == null) {
			return "未知日期";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}
	
	//日期转String	yyyy-MM-dd HH:mm:ss
	public static String dateToStringM(Date date) {
		if (date == null) {
			return "未知日期";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return dateFormat.format(date);
	}
	
	//日期转String	yyyy-MM-dd HH:mm:ss
	public static String dateToStringS(Date date) {
		if (date == null) {
			return "未知日期";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	//String转日期	yyyy-MM-dd HH:mm:ss
	public static Date toDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//String转日期	yyyy-MM-dd HH:mm:ss
	public static Date toDate2(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//String转日期	yyyy-MM-dd
	public static Date stringToDate(String date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return dateFormat.parse(date);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//获得今年某个月份的最后一天
	@SuppressWarnings("static-access")
	public static String getLastDayOfMonth(int lastMonth) throws Exception {  
		SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
		String stringMonth = "";
		String lastDayString = "";
        Calendar   cDay1   =   Calendar.getInstance();  
        cDay1.setTime(new Date()); 
        int year = cDay1.get(cDay1.YEAR);
        lastMonth = lastMonth+1;
        if(lastMonth <10){
        	stringMonth ="0"+ lastMonth;
        }else{
        	stringMonth =lastMonth+"";
        }
        String dateString = ""+year+"-"+stringMonth+"-"+"05";
        Date date = formate.parse(dateString);
        cDay1.setTime(date);
        int   lastDay   =   cDay1.getMinimum(Calendar.DAY_OF_MONTH);  
        if(lastDay <10){
        	lastDayString ="0"+ lastDay;
        }else{
        	lastDayString =""+ lastDay;
        }
        String rearlyDate = ""+year+"-"+stringMonth+"-"+lastDayString;
        
        return rearlyDate;  
	}  
	
	//减一天
	public static String getYesToday(String today) {
		String todayOne = today+" "+"12:30:30";
		Date yesDate = toDate(todayOne);
		Calendar cal = Calendar.getInstance();
		cal.setTime(yesDate);
		cal.add(Calendar.DATE, -1);
		yesDate = cal.getTime();
		return dateToString(yesDate);
	}
	
	//加一天
	public static String getTemorrowday(String today) {
		String todayOne = today+" "+"12:30:30";
		Date tommorroDate = toDate(todayOne);
		Calendar cal = Calendar.getInstance();
		cal.setTime(tommorroDate);
		cal.add(Calendar.DATE, 1);
		tommorroDate = cal.getTime();
		return dateToString(tommorroDate);
	}
	
	//加n天
	public static String getNOTemorrowday(String today,int number) {
		String todayOne = today+" "+"12:30:30";
		Date tommorroDate = toDate(todayOne);
		Calendar cal = Calendar.getInstance();
		cal.setTime(tommorroDate);
		cal.add(Calendar.DATE, number);
		tommorroDate = cal.getTime();
		return dateToString(tommorroDate);
	}
	
	//根据date取月份
	public static int getMonth(Date time){
		SimpleDateFormat st=new SimpleDateFormat("MM");
		return Integer.parseInt(st.format(time));
	}
	
	//根据date取日期
	public static int getDay(Date time){
		SimpleDateFormat st=new SimpleDateFormat("dd");
		return Integer.parseInt(st.format(time));
	}
	
	//根据日期取星座
	public static String getAstro(int month, int day) {
	    String[] starArr = {"魔羯座","水瓶座", "双鱼座", "白羊座",
	        "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座" };
	    int[] DayArr = {22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22};  // 两个星座分割日
	    int index = month;
	    // 所查询日期在分割日之前，索引-1，否则不变
	    if (day < DayArr[month - 1]) {
	            index = index - 1;
	    }
	    // 返回索引指向的星座string
	    return starArr[index];
	}
	
	//根据用户生日计算年龄
	public static int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();

		if (cal.before(birthday)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				//monthNow==monthBirth 
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				//monthNow>monthBirth 
				age--;
			}
		}
		return age;
	}
	
}
