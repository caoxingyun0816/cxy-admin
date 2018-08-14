package com.imooc.entity;

/**
 * Created by caoxingyun on 2018/8/2.
 */
public class AssetList {
    private String id;
    private String currentId; // cip 提供的id
    private String type;
    private Integer operation;
    private Integer syncStatus;
    private String syncDesc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Integer getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getSyncDesc() {
        return syncDesc;
    }

    public void setSyncDesc(String syncDesc) {
        this.syncDesc = syncDesc;
    }
}
