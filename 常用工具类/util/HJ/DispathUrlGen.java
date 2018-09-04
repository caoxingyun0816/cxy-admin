package com.wondertek.mam.util.HJ;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DispathUrlGen {

    /**
     * @param args
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        URLEncoder.encode("/server/coopAdd.html", "UTF-8");
		URLEncoder.encode(Base64.encode("PrmApplyId=111111&CompanyName=新浪&CompanyNo=88888888&BaseId0001&ProductNameProducId000101&CoopType=1".getBytes("GBK")), "UTF-8");


    }

}

