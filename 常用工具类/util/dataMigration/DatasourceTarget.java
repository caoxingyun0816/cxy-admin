package com.wondertek.mam.util.dataMigration;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatasourceTarget implements Serializable{
    private String dbDriver;
    private String dbUrl;
    private String dbUser;
    private String dbPasswd;
    private Connection connection;

    public DatasourceTarget(String dbDriver, String dbUrl, String dbUser, String dbPasswd) {
        this.dbDriver = dbDriver;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPasswd = dbPasswd;
    }

    public void init(){
        try {
            buildConnection();
        } catch (Exception e) {
            //log(e.getMessage);
            e.printStackTrace();
        }
    }
    public List<String> getTableList() throws Exception {
        if(this.connection == null  || this.connection.isClosed()) {
            init();
        }
        List<String> tableNameList = null;
        try {
            tableNameList = new ArrayList<String>();
            DatabaseMetaData dmd = this.connection.getMetaData();
            ResultSet rs = dmd.getTables(null,this.dbUser.toUpperCase(),"%",new String[]{"TABLE"});
            while (rs.next()){
                tableNameList.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new Exception("读取数据库的所有表失败！",e);
        }finally {
            this.closeConnection();
        }
        return tableNameList;
    }
    public Map<String,List<Map<String,String>>> getAllTabAndCol () throws Exception {
        if(this.connection == null  || this.connection.isClosed()) {
            init();
        }
        Map<String,List<Map<String,String>>> resMap = new HashMap<String, List<Map<String,String>>>();
        try {
            DatabaseMetaData dmd = this.connection.getMetaData();
            ResultSet rs = dmd.getTables(null,this.dbUser.toUpperCase(),"%",new String[]{"TABLE"});
            while (rs.next()){
                List<Map<String,String>> colMap = getColumnByTableName(rs.getString("TABLE_NAME").toUpperCase());
                if(colMap != null && colMap.size() > 0){
                    resMap.put(rs.getString("TABLE_NAME").toUpperCase(),colMap);
                }
            }
        } catch (SQLException e) {
            throw new Exception("读取数据库的所有表失败！",e);
        }finally {
            this.closeConnection();
        }
        return resMap;
    }
    public List<Map<String,String>> getColumnByTableName (String tableName) throws Exception {
        if(this.connection == null || this.connection.isClosed()) {
            init();
        }
        List<Map<String,String>> colMapList = new ArrayList<Map<String, String>>();
        DatabaseMetaData dmd = this.connection.getMetaData();
        ResultSet colRs = dmd.getColumns(null,"%",tableName.toUpperCase(),"%");
        while (colRs.next()){
            Map<String,String> colMap = new HashMap<String, String>();
            colMap.put("CNAME",colRs.getString("COLUMN_NAME"));
            colMap.put("CTYPE",colRs.getString("TYPE_NAME"));
            colMap.put("CDIGITS",colRs.getString("DECIMAL_DIGITS"));
            colMap.put("CNULLABLE",colRs.getString("NULLABLE"));
            colMapList.add(colMap);
        }
        return colMapList;

    }
    private void buildConnection() throws Exception {
        try {
            Class.forName(this.dbDriver);
            this.connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPasswd);
        } catch (ClassNotFoundException e) {
            throw new Exception("JDBC : 指定的Datasource.dbDriver驱动不存在 : \n" + e.getMessage());
        } catch (SQLException e) {
            throw new Exception("JDBC : DriverManager.getConnection 参数 [dbUrl, dbUser, dbPasswd] 存在问题 \n" + e.getMessage());
        }
    }
    private void closeConnection() throws Exception{
        if(this.connection != null){
            this.connection.close();
        }
    }
    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPasswd() {
        return dbPasswd;
    }

    public void setDbPasswd(String dbPasswd) {
        this.dbPasswd = dbPasswd;
    }

}
