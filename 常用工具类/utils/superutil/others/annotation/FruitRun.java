package com.wondertek.mam.util.superutil.others.annotation;

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
/***********输出结果***************/
public class FruitRun {

    /**
     * @param args
     */
    public static void main(String[] args) {

        FruitInfoUtil.getFruitInfo(Apple.class);
    }
}