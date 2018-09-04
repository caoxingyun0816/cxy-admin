package com.wondertek.mam.util.others;

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
public class PhoneNumberUtils {

    public static final String CHINAMOBILE_STARTWITH = "1340,1341,1342,1343,1344,1345,1346,1347,1348,135,136,137,138,139,147,150,151,152,157,158,159,178,1705,187,188,182,183,184";

    /**
     * 处理请求参数accountId
     *
     * @param accountType
     * @param accountId
     * @return
     */
    public static String delAccountId(String accountType, String accountId) {
        if ("01".equals(accountType)) {// 手机号
            return delPhoneNumber(accountId);
        } else {
            return accountId;
        }
    }

    /**
     * +86
     * @param phoneNumber
     * @return
     */
    public static String delPhoneNumber(String phoneNumber) {
        String newPhoneNumber = phoneNumber;
        if (newPhoneNumber.startsWith("01") && newPhoneNumber.length() == 13) {
            return "+" + newPhoneNumber;
        }
        if (!newPhoneNumber.startsWith("86")
                && !newPhoneNumber.startsWith("+86"))
            newPhoneNumber = "+86" + newPhoneNumber;
        if (newPhoneNumber.startsWith("86"))
            newPhoneNumber = "+" + newPhoneNumber;
        return newPhoneNumber;

    }

    /**
     * 手机号开头去掉+86
     * @param phoneNumber
     * @return
     */
    public static String subPhoneNumber(String phoneNumber) {
        if (phoneNumber == null)
            return "";
        if (phoneNumber.startsWith("+86"))
            phoneNumber = phoneNumber.substring(3);
        if (phoneNumber.startsWith("86"))
            phoneNumber = phoneNumber.substring(2);
        return phoneNumber;
    }

    /**
     * 判断是否是中国移动号码
     * @param mobile
     * @return
     */
    public static boolean isChinaMobile(String mobile) {
        if (mobile == null) {
            return false;
        }
        mobile = mobile.trim();
        if (mobile.length() == 14 && mobile.startsWith("+86")) {
            mobile = mobile.substring(3);
        }
        if (mobile.length() == 13 && mobile.startsWith("86")) {
            mobile = mobile.substring(2);
        }

        if (mobile.length() != 11) {
            return false;
        }

        String[] startWith = PhoneNumberUtils.CHINAMOBILE_STARTWITH.split(",");
        if (startWith != null) {
            for (String start : startWith) {
                if (mobile.startsWith(start)) {
                    return true;
                }
            }
        }
        return false;
    }

}
