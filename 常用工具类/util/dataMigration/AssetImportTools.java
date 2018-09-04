package com.wondertek.mam.util.dataMigration;

import com.wondertek.mam.model.AssetImageImport;
import com.wondertek.mam.model.AssetImport;
import com.wondertek.mam.model.AssetMediaImport;
import com.wondertek.mam.model.CopyrightImport;
import com.wondertek.mam.util.others.C2SFileTools;
import com.wondertek.mam.util.ST;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetImportTools {
    public static List<Object> handleListMapFiles(List<Map<String, Object>> fileListMap, String webroot) {
        List<Object> result = new ArrayList<Object>();
        if(fileListMap == null || fileListMap.size() <= 0) return result;

        List<File> xmlDataFiles = new ArrayList<File>();
        List<File> excelDataFiles = new ArrayList<File>();
        for (Map<String, Object> fileMap : fileListMap) {
            String filePath = null;
            if (fileMap != null && fileMap.containsKey("path") && !ST.isNull((filePath = (String) fileMap.get("path")))) {
//                if (webroot.endsWith(File.separator)) webroot = webroot.substring(0, webroot.length() - 1);
//                if (filePath.startsWith(File.separator)) filePath = filePath.substring(1);
                File temp = new File(C2SFileTools.concatPath(webroot,filePath));
                if (temp != null && temp.exists()) {
                    String fileSuffix = C2SFileTools.getFileSuffix(temp);
                    if ("xml".equalsIgnoreCase(fileSuffix)) {
                        xmlDataFiles.add(temp);
                    } else if ("xls".equalsIgnoreCase(fileSuffix)) {
                        excelDataFiles.add(temp);
                    }
                }
            }
        }

        List<Object> xmlResult = DataMigrationTools.parseXml2Modal(xmlDataFiles, CopyrightImport.class, true);
        List<Object> excelResult = handlExcelFiles2Modal(excelDataFiles);
        if (xmlResult != null && xmlResult.size() > 0) {
            result.addAll(xmlResult);
        }
        if (excelResult != null && excelResult.size() > 0) {
            result.addAll(excelResult);
        }
        return result;
    }
    
    


    public static List<Object> handlExcelFiles2Modal(List<File> excelDataFiles) {
        String[] copyrightTitleArray = new String[]{
                "authorizationWayStr", "name", "publishStr", "cpid", "scarcityStr", "scopeStr", "terminalStr",
                "freeTypeStr", "outputStr", "beginDateStr", "endDateStr", "wayStr", "drmStr", "integrityStr", "influenceStr", "areaStr", "oriPublishStr",
                "blackArea", "chain", "bcLicenseStr", "supportStr", "miguPublishStr", "udid", "discription", "copyrightFilesFromExcel", "importNum"
        };
        String[] assetTitleArray = new String[]{
                null,null,null, "name", "formTypeStr", "shortName", "assetCountryStr", "displayName", "assistName",
                "captionLangStr", "assetContentType", "assetLang", "keywords", "assetPlatform", "onlineTime",
                "isUrgencyStr", "JJName", "assetChildNum","totalNum", "description",  "image", "appandAttr",
                "copyrightLink","mediaFile"
        };
        
        String[] assetMediaTitleArray = new String[]{
                "serialNum", "mediaName"
        };
        String[] assetImageTitleArray = new String[]{
                "serialNum", "imageName"
        };
        
        
        
        
        
        
        
        //
        List<CopyrightImport> excelCopyrightList = DataMigrationTools.parseExcel2Modal(excelDataFiles, 1, 1, -1, copyrightTitleArray, CopyrightImport.class, false);
        List<AssetImport> excelResult = DataMigrationTools.parseExcel2Modal(excelDataFiles, 0, 1, -1, assetTitleArray, AssetImport.class, true);
        DataMigrationTools.parseExcel2Modal(excelDataFiles, 6, 1, -1, assetMediaTitleArray, AssetMediaImport.class, true);
        DataMigrationTools.parseExcel2Modal(excelDataFiles, 3, 1, -1, assetImageTitleArray, AssetImageImport.class, true);
        
        
        
        //Collections.sort(temp);
        List<Object> result = null;
        if(excelCopyrightList != null && excelCopyrightList.size() > 0){
            Map<Integer, List<AssetImport>> excelAssetImportMap = null;
            if (excelResult != null && excelResult.size() > 0) {
                excelAssetImportMap = new HashMap<Integer, List<AssetImport>>();
                for (AssetImport assetImport : excelResult) {
                    Integer cin = getCopyrightImportNum(assetImport.getCopyrightLink());
                    if (!ST.isNull(cin)) {
                        if(excelAssetImportMap.containsKey(cin)){
                            excelAssetImportMap.get(cin).add(assetImport);
                        }else {
                            List<AssetImport> ais = new ArrayList<AssetImport>();
                            ais.add(assetImport);
                            excelAssetImportMap.put(cin,ais);
                        }
                    }
                }
                if(excelAssetImportMap != null && excelAssetImportMap.size() > 0){
                	List<CopyrightImport> list = new ArrayList<CopyrightImport>();
                    for (CopyrightImport ci : excelCopyrightList) {
                        Integer i;
                        if (ci != null && (i = ci.getImportNum()) != null && i >= 0) {
                            List<AssetImport> ais = excelAssetImportMap.get(i);
                            if (ais != null && ais.size() > 0){
                                ci.getAssetImports().addAll(ais);
                                list.add(excelCopyrightList.get(ci.getImportNum()-1));
                            }else {
                            	//list.add(ci);
                                //excelCopyrightList.remove(ci);
                            }
                        }
                    }
                   /* for(int i=0;i<list.size();i++){
                    	//for(int j=0;j<excelCopyrightList.size();j++){
                    		//if(excelCopyrightList.get(j).getImportNum().toString().equals(list.get(i).getImportNum().toString())){
                    			excelCopyrightList.remove(excelCopyrightList.get((list.get(i).getImportNum())));
                    		//}
                    	//}
                    	
                    }*/
                    //excelCopyrightList.re
                    //excelCopyrightList.removeAll(list);
                    result = new ArrayList<Object>();
                    result.addAll(list);
                }
            }
        }
        return result;
    }

    public static Integer getCopyrightImportNum(String copyrightLink) {
        Integer cin = null;
        if (!ST.isNull(copyrightLink)) {
            String[] cpi = copyrightLink.split(":");
            //Pattern pattern = Pattern.compile("\\d+");
            if (cpi[1] != null /*&& pattern.matcher(cpi[1]).matches()*/) {
                Integer i = Integer.valueOf(cpi[1]);
                if (i != null && i >= 0) {
                    cin = i;
                }
            }
        }
        return cin;
    }
}
