package com.imooc.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoxingyun on 2018/8/2.
 */
public class XmlParse {
    private String serialNo;
    private String TimeStamp;
    private Map<String,AssetList> assetList = new HashMap<String,AssetList>();

    public void addAsset(AssetList asset){
        this.assetList.put(asset.getId(),asset);
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public Map<String, AssetList> getAssetList() {
        return assetList;
    }

    public void setAssetList(Map<String, AssetList> assetList) {
        this.assetList = assetList;
    }
}
