package com.sumslack.freemarker.functions;

import java.util.Date;
import java.util.List;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class DateAdd implements TemplateMethodModel{

	@Override
	public Object exec(List args) throws TemplateModelException {
		String step = "day";
		if(args.size()<2) {
			throw new TemplateModelException("Wrong arguments");
		}
		Object date = args.get(0);
		Date dd = null;
		if(date instanceof String) {
			dd = DateUtil.parse((String)date,"yyyy-MM-dd");
		}else {
			dd = (Date)date;
		}
		if(args.size() == 3) {
			step = Convert.toStr(args.get(2));
		}
		
		if(step.equals("month")) {
			return DateUtil.offsetMonth(dd, Convert.toInt(args.get(1)));
		}else if(step.equals("week")) {
			return DateUtil.offsetWeek(dd, Convert.toInt(args.get(1)));
		}else if(step.equals("hour")) {
			return DateUtil.offsetHour(dd, Convert.toInt(args.get(1)));
		}else {
			return DateUtil.offsetDay(dd, Convert.toInt(args.get(1)));
		}
	}

}
