package com.wondertek.mam.util.others.JAXB;

/**
 * Simple to Introduction
 *
 * @ProjectName: [${project_name}]
 * @Package: [${package_name}.${file_name}]
 * @ClassName: [${type_name}]
 * @Description: [一句话描述该类的功能]
 * @Author: [${user}]
 * @CreateDate: [${date} ${time}]
 * @UpdateUser: [${user}]
 * @UpdateDate: [${date} ${time}]
 * @UpdateRemark: [说明本次修改内容]
 * @Version: [v1.0]
 */

//用于格式化日期在xml中的显示格式，并且由xml unmarshal为java对象时，将字符串解析为Date对象
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateAdapter extends XmlAdapter<String, Date> {

    private String pattern = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat fmt = new SimpleDateFormat(pattern);

    @Override
    public Date unmarshal(String dateStr) throws Exception {
        return fmt.parse(dateStr);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return fmt.format(date);
    }

}