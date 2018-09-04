package com.wondertek.mam.util.office.excel;

import com.wondertek.mam.util.superutil.thirdparty.excel.BeanToExcel;
import com.wondertek.mam.util.superutil.thirdparty.excel.ExcelDataFormatter;
import com.wondertek.mam.util.superutil.thirdparty.excel.User;

import java.util.ArrayList;
import java.util.List;

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
public class test {
    public static void main(String[] args) throws Exception {

        List<User> userList = new ArrayList<User>();
        for(int i = 0; i < 1000000; i++){
            User user = new User();
            user.setName(i + "name" + i);
            user.setAge(i+"");
            userList.add(user);
        }
        ExcelDataFormatter excelDataFormatter = new ExcelDataFormatter();
       // excelDataFormatter.set();
        BeanToExcel.writeToFile(userList,excelDataFormatter,"C:/data/111rrr.xlsx");

    }
}
