package com.ginz.util.base;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * 
 * @author Administrator
 *	将带日期类型的Bean转换成jsonObject
 */
public class DateJsonValueProcessor implements JsonValueProcessor {
	//格式
	private String format;
	public DateJsonValueProcessor() {
		//System.out.println("DateJsonValueProcessor");
	}

	public DateJsonValueProcessor(String formatType) {
			this.format = formatType;
		//System.out.println(this.format);
	}

	public Object processArrayValue(Object value, JsonConfig jsonConfig) {

		System.out.println("processArrayValue");

		String[] obj = {};
		if (value instanceof Date[]) {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			Date[] dates = (Date[]) value;
			obj = new String[dates.length];
			for (int i = 0; i < dates.length; i++) {
				obj[i] = sf.format(dates[i]);
			}
		}
		return obj;
	}

	public Object processObjectValue(String key, Object value,
			JsonConfig jsonConfig) {

		//System.out.println("processObjectValue");

		if (value instanceof Date) {
			String str = new SimpleDateFormat(format).format((Date) value);
			return str;
		}
		return value;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
