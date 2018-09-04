package com.wondertek.mam.util.dataMigration;

import com.wondertek.mam.util.others.C2SFileTools;
import com.wondertek.mam.util.others.ClassTools;
import com.wondertek.mam.util.ST;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMigrationTools {
    private static Logger log = Logger.getLogger(DataMigrationTools.class);


    public static List<Object> parseXml2Modal(List<File> xmlFiles,Class modalCls,boolean clearFile){
        if( xmlFiles == null || xmlFiles.size() == 0 || modalCls == null) return  null;
        List<Object> resultList = null;

        for(File xmlFile : xmlFiles){
            if(!xmlFile.exists()) continue;

            Map<String, Object> xmlMap = xml2Map(xmlFile.getPath());
            if (xmlMap != null && xmlMap.size() > 0) {
                Object obj = parse2Modal(xmlMap, modalCls);
                if (obj != null) {
                    if(resultList == null)
                        resultList = new ArrayList<Object>();
                    resultList.add(obj);
                }
            }
            if(clearFile){
                C2SFileTools.deleteFileTree(xmlFile);
            }
        }

        return resultList;
    }

    public static List<Object> parseXml2Modal(String txtPath, String xmlRootPath, Class modalCls) {
        if (xmlRootPath == null) {
            xmlRootPath = "";
        }
        List<Object> resultList = new ArrayList<Object>();
        List<String> xmlPathList = parseFile4XmlPaths(txtPath);
        for (String xmlPath : xmlPathList) {
            String xmlFullPath = xmlRootPath.concat(File.separator).concat(xmlPath);
            Map<String, Object> xmlMap = xml2Map(xmlFullPath);
            if (xmlMap != null && xmlMap.size() > 0) {
                Object obj = parse2Modal(xmlMap, modalCls);
                if (obj != null) {
                    resultList.add(obj);
                }
            }
        }
        return resultList;
    }

    public static List<String> parseFile4XmlPaths(String txtPath) {
        if (txtPath == null) return null;

        List<String> xmlPathList = null;
        File txtFile = new File(txtPath);
        if (txtFile.exists() && txtFile.isFile()) {
            Scanner in = null;
            try {
                in = new Scanner(txtFile);
                if (in != null) {
                    xmlPathList = new ArrayList<String>();
                    while (in.hasNextLine()) {
                        String idLine = in.nextLine();
                        if (idLine != null && !"".equals(idLine.trim())) {
                            Pattern pattern = Pattern.compile("\\d+");
                            Matcher matcher = pattern.matcher(idLine);
                            String id = null;
                            while (matcher.find()) {
                                id = matcher.group();
                                if (!ST.isNull(id)) {
                                    break;
                                }
                            }
                            if (!ST.isNull(id)) {
                                String xmlPath = C2SFileTools.getPublishPathById(Long.valueOf(id), new int[]{4, 3, 3});
                                xmlPathList.add(xmlPath.concat(File.separator).concat(id).concat(".xml"));
                            }

                        }
                    }
                }
            } catch (FileNotFoundException e) {
                log.error(e);
            } finally {
                if (in != null) {
                    in.close();
                }
            }

        }
        return xmlPathList;
    }

    public static Map<String, Object> xml2Map(String xmlPath) {
        File xmlFile = new File(xmlPath);
        if (!xmlFile.exists()) {
            return null;
        }
        Map<String, Object> map = null;
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(xmlFile);
        } catch (DocumentException e) {
            log.error("DataMigrationTools.xml2Map() -----> SAXReader read(xmlFile) file failure!", e);
        }
        if (document != null) {
            Element root = document.getRootElement();
            map = new HashMap<String, Object>();
            Map<String, Object> emap = new HashMap<String, Object>();
            xmlElement2Map(root, emap);
            map.put(root.getName(), emap);
        }
        return map;
    }

    public static void xmlElement2Map(Element element, Map<String, Object> emap) {
        List<Element> elements = element.elements();
        if (0 == elements.size()) {
            emap.put(element.getName(), element.getText());
        } else {
            for (Element e : elements) {
                if (e.elements().size() == 0) {
                    emap.put(e.getName(), e.getText());
                    continue;
                }
                Map<String, Object> m = new HashMap<String, Object>();
                xmlElement2Map(e, m);
                Object obj = emap.get(e.getName());
                if (obj != null) {
                    if (obj instanceof Map) {
                        List<Map<String, Object>> tmp = new ArrayList<Map<String, Object>>();
                        tmp.add((Map<String, Object>) obj);
                        tmp.add(m);
                        emap.put(e.getName(), tmp);
                    } else if (obj instanceof List) {
                        ((List<Map<String, Object>>) obj).add(m);
                    }
                } else {
                    emap.put(e.getName(), m);
                }
            }
        }
    }

    public static Map<String, Object> browseMap(Map<String, Object> srcMap, String parentElement) {
        Map<String, Object> resultMap = srcMap;
        if (parentElement != null && !"".equals(parentElement.trim())) {
            String[] pes = parentElement.split("\\.");
            ClassTools.Map cmap = new ClassTools.Map(resultMap);
            for (String pe : pes) {
                //if (!ST.isNull(pe) && resultMap.containsKey(pe)) {
                if (!ST.isNull(pe) && cmap.containsKeyIgnoreCase(pe)) {
                    //Object obj = resultMap.get(pe);
                    Object obj = cmap.getIgnoreCase(pe);
                    if (obj instanceof Map) {
                        resultMap = (Map<String, Object>) obj;
                        cmap = new ClassTools.Map(resultMap);
                    } else {
                        resultMap = null;
                    }
                } else {
                    resultMap = null;                           //modal配置的父节点 与 xml节点名称不一致
                    log.warn("modal中DataMigrationTools注解配置的父节点 与 xml中节点名称不一致");
                }
            }
        }
        return resultMap;
    }

    public static List<Map<String, Object>> browseMap2List(Map<String, Object> srcMap, String parentElement) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = srcMap;
        if (parentElement != null && !"".equals(parentElement.trim())) {
            String[] pes = parentElement.split("\\.");
            for (String pe : pes) {
                if (!ST.isNull(pe) && resultMap.containsKey(pe)) {
                    Object obj = resultMap.get(pe);
                    if (obj instanceof Map) {
                        resultMap = (Map<String, Object>) obj;
                        resultList.clear();
                        resultList.add(resultMap);
                    } else if (obj instanceof List) {
                        resultList = (List<Map<String, Object>>) obj;
                    }
                } else {
                    resultList = null;                           //modal配置的父节点 与 xml节点名称不一致
                    //log.warn("modal中DataMigrationTools注解配置的父节点 与 xml中节点名称不一致");
                }
            }
        }
        return resultList;
    }

    /**
     * 根据 一个pojo对象 生成 其相关的sql插入语句
     * @param modalObj
     * @return
     */
    public static Map<String, Object> buildInsertSql(Object modalObj) {
        Map<String, Object> result = null;
        Class modalCls = modalObj.getClass();
        if (modalCls.isAnnotationPresent(Table.class)) {
            List<Object> params = new ArrayList<Object>();
            String tableName = ((Table) modalCls.getAnnotation(Table.class)).name();
            StringBuffer sql = new StringBuffer("INSERT INTO ");
            StringBuffer paramPerch = new StringBuffer();
            sql.append(tableName).append(" ( ");
            Method[] methods = modalCls.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Id.class) || method.isAnnotationPresent(Column.class)) {
                    Object value = null;
                    try {
                        value = method.invoke(modalObj, null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (value != null && method.isAnnotationPresent(Id.class)) {
                        String col = method.getName().substring(3).toLowerCase();
                        addParam(col, sql, paramPerch, value, params);
                    }
                    if (value != null && method.isAnnotationPresent(Column.class)) {
                        Column colAnn = method.getAnnotation(Column.class);
                        addParam(colAnn.name(), sql, paramPerch, value, params);
                    }
                }
            }
            sql.append(" ) ").append(" VALUES ( ").append(paramPerch.toString()).append(" ) ");
            if (params.size() > 0) {
                result = new HashMap<String, Object>();
                result.put("sql", sql.toString());
                result.put("params", params);
            }
        }
        return result;
    }

    public static String columnFeildNameConvert(Object modalObj, String columnOrFeildName, boolean isColumn2feild) {
        Class modalCls = modalObj.getClass();
        return columnFeildNameConvert(modalCls, columnOrFeildName, isColumn2feild);
    }

    public static String columnFeildNameConvert(Class modalCls, String columnOrFeildName, boolean isColumn2feild) {
        if(modalCls == null || ST.isNull(columnOrFeildName)) return null;
        String columnFeild = null;
        if (modalCls.isAnnotationPresent(Table.class)) {
            Method[] methods = modalCls.getDeclaredMethods();
            for (Method method : methods) {
                if(method.isAnnotationPresent(Id.class) || method.isAnnotationPresent(Column.class)) {
                    String column = null;
                    String feild = method.getName();
                    feild = feild.substring(3, 4).toLowerCase().concat(feild.substring(4));
                    if (method.isAnnotationPresent(Id.class) && !method.isAnnotationPresent(Column.class)) {
                        column = feild;
                    } else if (method.isAnnotationPresent(Column.class)) {
                        column = method.getAnnotation(Column.class).name();
                    }
                    if (isColumn2feild) {
                        if (columnOrFeildName.equals(column)) {
                            columnFeild = feild;
                            break;
                        }
                    } else {
                        if (columnOrFeildName.equals(feild)) {
                            columnFeild = column;
                            break;
                        }
                    }
                }
            }
        }
        return columnFeild;
    }

    private static void addParam(String colName, StringBuffer sql, StringBuffer paramperch, Object value, List<Object> params) {
        if (!ST.isNull(colName)) {
            String tmp = sql.substring(sql.lastIndexOf("(") + 1);
            if (tmp != null && tmp.trim().length() > 0) {
                sql.append(",").append(colName);
            } else if (tmp != null && tmp.trim().length() == 0) {
                sql.append(colName);
            }
            if (paramperch.length() > 0) {
                paramperch.append(",?");
                params.add(value);
            } else {
                paramperch.append("?");
                params.add(value);
            }
        }
    }

    public static Object parse2Modal(Map<String, Object> xmlMap, Class modalCls) {
        if (xmlMap == null || modalCls == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = modalCls.newInstance();
            Method[] modalMethod = modalCls.getMethods();
            if (obj != null) {
                for (Method method : modalMethod) {
                    if (method.isAnnotationPresent(DataMigrationAnnotation.class)) {
                        DataMigrationAnnotation dma = method.getAnnotation(DataMigrationAnnotation.class);
                        String[] xmlElementNames = dma.xmlElementName();
                        String xmlParentElementName = "".equals(dma.xmlParentElementName()) ? modalCls.getSimpleName().toLowerCase() : dma.xmlParentElementName();
                        String migrationType = dma.migrationType();
                        String[] attrTypes = dma.attrType();
                        String attrModalTypeSplitRegex = dma.attrModalTypeSplitRegex();
                        String attrXmlTypeSplitRegex = dma.attrXmlTypeSplitRegex();

                        Class[] type = method.getParameterTypes();
                        if (1 == type.length) {
                            if ("basic".equalsIgnoreCase(migrationType)) {
                                Map<String, Object> xmlCurrentNodeMap = browseMap(xmlMap, xmlParentElementName);
                                for (String xmlElementName : xmlElementNames) {
                                    if (xmlCurrentNodeMap.containsKey(xmlElementName)) {
                                        String value = (String) xmlCurrentNodeMap.get(xmlElementName);
                                        if (!ST.isNull(attrModalTypeSplitRegex) && !ST.isNull(attrTypes) && !ST.isNull(value)) {
                                            String tmpValue = "";
                                            if (!ST.isNull(attrXmlTypeSplitRegex)) {
                                                for (String attrXmlValue : value.split(attrXmlTypeSplitRegex)) {
                                                    if (ST.isNull(attrXmlValue)) {
                                                        continue;
                                                    }
                                                    for (String attrtype : attrTypes) {
                                                        String[] tmp = attrtype.split(attrModalTypeSplitRegex);
                                                        if (tmp != null && 2 == tmp.length && tmp[1].equalsIgnoreCase(attrXmlValue)) {
                                                            if (!ST.isNull(tmp[0])) {
                                                                tmpValue = tmpValue + tmp[0] + attrModalTypeSplitRegex;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    //if (flag) {       //设置默认值 ,以保持长度一致
                                                    //    tmpValue += "-1";
                                                    //}
                                                }
                                                if (!ST.isNull(tmpValue) && tmpValue.indexOf(attrModalTypeSplitRegex) >= 0) {
                                                    tmpValue = tmpValue.substring(0, tmpValue.length() - 1);
                                                }
                                            } else {
                                                for (String attrtype : attrTypes) {
                                                    String[] tmp = attrtype.split(attrModalTypeSplitRegex);
                                                    if (tmp != null && 2 == tmp.length && tmp[1].equalsIgnoreCase(value)) {
                                                        if (!ST.isNull(tmp[0])) {
                                                            tmpValue += tmp[0];
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            value = tmpValue;
                                        }
                                        Object paramValue = new DataMigrationFormater().format(value, type[0]);
                                        method.invoke(obj, paramValue);
                                    }
                                }
                            } else if ("modal".equalsIgnoreCase(migrationType)) {
                                Map<String, Object> xmlCurrentNodeMap = browseMap(xmlMap, xmlParentElementName);
                                Map<String, Object> xmlModalNodeMap = new HashMap<String, Object>();
                                xmlModalNodeMap.put(type[0].getSimpleName().toLowerCase(), xmlCurrentNodeMap);
                                method.invoke(obj, parse2Modal(xmlModalNodeMap, type[0]));
                            } else if ("list".equalsIgnoreCase(migrationType)) {
                                Method getListMethod = modalCls.getDeclaredMethod(method.getName().replaceFirst("s", "g"), null);
                                String methodName = method.getName();
                                String fieldName = methodName.substring(3, 4).toLowerCase().concat(methodName.substring(4));
                                Field field = modalCls.getDeclaredField(fieldName);
                                Type t = field.getGenericType();
                                if (t != null && t instanceof ParameterizedType) {
                                    ParameterizedType p = (ParameterizedType) t;
                                    Class modalCls2 = (Class) p.getActualTypeArguments()[0];
                                    if (modalCls2 != null) {
                                        Object objt = browseMap2List(xmlMap, xmlParentElementName);
                                        if (objt != null && objt instanceof List) {
                                            List<Map<String, Object>> tmps = (List<Map<String, Object>>) objt;
                                            for (Map<String, Object> tmp : tmps) {
                                                Map<String, Object> tmpMap = new HashMap<String, Object>();
                                                tmpMap.put(modalCls2.getSimpleName().toLowerCase(), tmp);
                                                ((List) getListMethod.invoke(obj, null)).add(parse2Modal(tmpMap, modalCls2));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            log.error("DataMigrationTools:parseXml2Modal --> obj = modalCls.newInstance()  Failure!:参数类型不匹配或modalCls没有无参构造又或此类型不支持反射实例对象", e);       //
        } catch (IllegalAccessException e) {
            log.error("DataMigrationTools:parseXml2Modal --> obj = modalCls.newInstance()  Failure!:类的域或方法为私有,不可达到", e);       //
        } catch (InvocationTargetException e) {
            log.error("DataMigrationTools:parseXml2Modal --> obj = modalCls.newInstance()  Failure!:参数类型不匹配或modalCls没有无参构造又或此类型不支持反射实例对象", e);
        } catch (NoSuchMethodException e) {
            log.error("DataMigrationTools:parseXml2Modal --> Method getListMethod...  Failure!:没有此类方法", e);
        } catch (NoSuchFieldException e) {
            log.error("DataMigrationTools:parseXml2Modal --> Field field = modalCls.getDeclaredField(fieldName)  Failure!:modalCls中没有此域", e);
        }
        return obj;
    }

    /**
     * 解读Excle 生成对应 pojo 的List<Map>集合
     * 参数与 parseExcel2Modal 方法类似
     */
    public static List<Map<String, Object>> parseExcel2List(String excelPath, int sheetIndex, int rowBeginIndex, String[] titleArray) {
        if(titleArray == null || titleArray.length <= 0) return null;

        Map<String,Integer> titleMap = new HashMap<String, Integer>();
        for (int i = 0; i < titleArray.length; i++) {
            String columnName = titleArray[i];
            if(ST.isNull(columnName)){
                titleMap.put("noTheField",Integer.MAX_VALUE-1);
            }else {
                titleMap.put(columnName,i);
            }
        }
        if(titleMap.size() > 0) {
            return parseExcel2List(excelPath,sheetIndex,rowBeginIndex,titleMap);
        } else {
            return null;
        }
    }
    public static List<Map<String, Object>> parseExcel2List(String excelPath, int sheetIndex, int rowBeginIndex, Map<String, Integer> titleMap) {
        List<Map<String, Object>> data = null;
        File excelFile = new File(excelPath);
		if (excelFile == null || !excelFile.exists() || titleMap == null || titleMap.size() == 0)
			return data;
		
        Set<Map.Entry<String, Integer>> mapSet = titleMap.entrySet();

        try {
            InputStream is = new FileInputStream(excelFile);
            POIFSFileSystem pffs = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(pffs);

            //检查设定 sheet
            int sheetNum = wb.getNumberOfSheets();
            if (sheetIndex < 0 || sheetIndex > sheetNum - 1)
                return data;   // 指定 sheetOffset 大于Excel文件 实际sheet数量 时 return
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);

            //检查设定 开始 row
            if (rowBeginIndex < 0 || rowBeginIndex > sheet.getLastRowNum() || rowBeginIndex < sheet.getFirstRowNum())
                return data;    // 指定 rowBeginIndex 不在 sheet数据行范围内 ，return

            data = new ArrayList<Map<String, Object>>();
            // 处理每个 HSSFRow
            for (; rowBeginIndex < sheet.getLastRowNum(); rowBeginIndex++) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                HSSFRow row = sheet.getRow(rowBeginIndex);
                for (Map.Entry<String, Integer> mapEntry : mapSet) {
                    String colName = mapEntry.getKey();
                    int colIndex = mapEntry.getValue();
                    if ("noTheField".equalsIgnoreCase(colName) || colIndex > row.getLastCellNum() || colIndex < row.getFirstCellNum())
                        continue;   // 指定cell index 不在文档内容 范围内
                    HSSFCell cell = row.getCell(colIndex);
                    Object cellValue = getHSSFCellValue(cell, false);
                    rowData.put(colName, cellValue);
                }
                data.add(rowData);
            }
        } catch (FileNotFoundException e) {
            log.error("excel file not found ! >>" + excelPath + "<<", e);
        } catch (IOException e) {
            log.error("excel file can not init the instance of HSSFWorkbook ! >>" + excelPath + "<<", e);
        }
        return data;
    }

    /**
     * 解读Excle 生成对应 pojo 的List集合
     * @param excelFile excel 路径 全路径（绝对路径）
     * @param sheetIndex    解析sheet的索引  从0开始
     * @param rowBeginIndex  数据起始行号  从0开始
     * @param rowSize   获取数据的行数,最大限制在5000；   可利用 rowBeginIndex（类似start） 和 rowSize（类似limit） 两个差数 作分页解读
     * @param titleArray    字符串数组型, 根据数组的脚标（0开始）对应 Excel的每列的位置, 数组的字符串型数据对应pojo对象的域名称；
     * @param cls   返回List<?>集合中元素的Class对象
     * @return
     */
    public static <T> List<T> parseExcel2Modal(File excelFile, int sheetIndex, int rowBeginIndex, int rowSize,String[] titleArray, Class<T> cls) {
        if(titleArray == null || titleArray.length <= 0) return null;

        Map<String,Integer> titleMap = new HashMap<String, Integer>();
        for (int i = 0; i < titleArray.length; i++) {
            String columnName = titleArray[i];
            if(ST.isNull(columnName)){
                titleMap.put("noTheField",Integer.MAX_VALUE-1);
            }else {
                titleMap.put(columnName,i);
            }
        }
        if(titleMap.size() > 0) {
            return parseExcel2Modal(excelFile.getPath(),sheetIndex,rowBeginIndex,rowSize,titleMap,cls);
        } else {
            return null;
        }
    }
    public static <T> List<T> parseExcel2Modal(List<File> excelFiles, int sheetIndex, int rowBeginIndex, int rowSize,String[] titleArray, Class<T> cls,boolean clearFile) {
        if(titleArray == null || titleArray.length <= 0) return null;

        Map<String,Integer> titleMap = new HashMap<String, Integer>();
        for (int i = 0; i < titleArray.length; i++) {
            String columnName = titleArray[i];
            if(ST.isNull(columnName)){
                titleMap.put("noTheField",Integer.MAX_VALUE-1);
            }else {
                titleMap.put(columnName,i);
            }
        }
        if(titleMap.size() > 0) {
            List<T> result = null;
            for (File file : excelFiles) {

                List<T> acList = DataMigrationTools.parseExcel2Modal(file.getPath(), sheetIndex, rowBeginIndex, rowSize, titleMap, cls);
                if (acList != null) {
                    if(result == null ){
                        result = new ArrayList<T>();
                    }
                    result.addAll(acList);
                }
                if(file.exists() && clearFile){
                    C2SFileTools.deleteFileTree(file);
                }
            }
            return result;
        } else {
            return null;
        }
    }
    public static <T> List<T> parseExcel2Modal(String excelPath, int sheetIndex, int rowBeginIndex, int rowSize, Map<String, Integer> titleMap, Class<T> cls) {
        if (rowSize <= 0 || rowSize > 5000) rowSize = 5000;      // 限制每次读取最大数据为5000条
        List<T> data = null;
        File excelFile = new File(excelPath);
        if (!excelFile.exists() || titleMap == null || titleMap.size() == 0) return data;
        Set<Map.Entry<String, Integer>> mapSet = titleMap.entrySet();

        try {
            InputStream is = new FileInputStream(excelFile);
            POIFSFileSystem pffs = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(pffs);

            //检查设定 sheet
            int sheetNum = wb.getNumberOfSheets();
            if (sheetIndex < 0 || sheetIndex > sheetNum - 1)
                return data;   // 指定 sheetOffset 大于Excel文件 实际sheet数量 时 return
            HSSFSheet sheet = wb.getSheetAt(sheetIndex);

            //检查设定 开始 row
            if (rowBeginIndex < 0 || rowBeginIndex > sheet.getLastRowNum() || rowBeginIndex < sheet.getFirstRowNum())
                return data;    // 指定 rowBeginIndex 不在 sheet数据行范围内 ，return

            data = new ArrayList<T>();
            // 处理每个 HSSFRow
            for (; rowBeginIndex < sheet.getLastRowNum() && rowBeginIndex < rowSize + rowBeginIndex; rowBeginIndex++) {
                //Map<String, Object> rowData = new HashMap<String, Object>();
                T rowData = cls.newInstance();
                boolean hasDataFlag = false;
                HSSFRow row = sheet.getRow(rowBeginIndex);
                for (Map.Entry<String, Integer> mapEntry : mapSet) {
                    String colName = mapEntry.getKey();
                    int colIndex = mapEntry.getValue();
                    if ("noTheField".equalsIgnoreCase(colName) || colIndex > row.getLastCellNum() || colIndex < row.getFirstCellNum())
                        continue;   // 指定cell index 不在文档内容 范围内
                    String setColModalMethodStr = "set".concat(colName.substring(0, 1).toUpperCase()).concat(colName.substring(1));
                    try {
                        Field field = cls.getDeclaredField(colName);
                        Class<?> fieldType = field.getType();
                        Method setMethod = cls.getDeclaredMethod(setColModalMethodStr, fieldType);
                        HSSFCell cell = row.getCell(colIndex);
                        String cellValue = (String) getHSSFCellValue(cell, true);
                        if(!ST.isNull(cellValue)) {
                            hasDataFlag |= true;
                            Object paramValue = new DataMigrationFormater().format(cellValue, fieldType);
                            setMethod.invoke(rowData, paramValue);
                        }
                        //setMethod.invoke(rowData,cellValue);
                        //rowData.put(colName, cellValue);
                    } catch (NoSuchMethodException e) {
                        log.warn("DataMigrationTools.parseExcel2Modal(): the Modal`s method : >>" + cls.getName() + "." + setColModalMethodStr + "() << can not be found !", e);
                    } catch (NoSuchFieldException e) {
                        log.warn("DataMigrationTools.parseExcel2Modal(): the Modal`s field : >>" + cls.getName() + "." + colName + " << can not be found !", e);
                    } catch (InvocationTargetException e) {
                        log.warn("DataMigrationTools.parseExcel2Modal(): the Modal`s method : >>" + cls.getName() + "." + colName + " << invoke failed !", e);
                    }
                }
                if(hasDataFlag){
                    data.add(rowData);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("DataMigrationTools.parseExcel2Modal(): excel file not found ! >>" + excelPath + "<<", e);
        } catch (IOException e) {
            log.error("DataMigrationTools.parseExcel2Modal(): excel file can not init the instance of HSSFWorkbook ! >>" + excelPath + "<<", e);
        } catch (InstantiationException e) {
            log.error("DataMigrationTools.parseExcel2Modal(): excel file can not init the element of resultList ! >>" + cls.getName() + "<<", e);
        } catch (IllegalAccessException e) {
            log.error("DataMigrationTools.parseExcel2Modal(): excel file can not init the element of resultList ! >>" + cls.getName() + "<<", e);
        }
        return data;
    }

    private static Object getHSSFCellValue(HSSFCell cell, boolean result2String) {
        Object value = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:     //String
                value = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:    // boolean
                value = cell.getBooleanCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date tmpDate = cell.getDateCellValue();
                    if (result2String) {
                        value = new ClassTools.Date(tmpDate);
                    } else {
                        value = tmpDate;     // com.wondertek.mam.util.Date2
                    }
                } else {
                    value = cell.getNumericCellValue();     // double
                }
                break;
            case HSSFCell.CELL_TYPE_FORMULA: // 公式
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                break;
            case HSSFCell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return result2String ? String.valueOf(value) : value;
    }
    public static <T> String list2Str(List<T> list,String splitStr,Class<T> T){
        String s = "";
        if(list != null ){
            list.removeAll(Collections.singleton(null));
            if(list.size() > 0){
                s = list.toString();
                s = s.substring( 1, s.length() - 1).replaceAll("\\s","");
                //if(!ST.isNull(splitStr)){
                if(splitStr != null){
                    s = s.replaceAll(",",splitStr);
                }
            }
        }
        return s;
    }

    //test
    //public static void main(String[] args) {
    //List<Object> stars = parseXml2Modal("D:\\Idea\\MZ\\mobilevideo\\mam\\mam-core\\src\\main\\java\\com\\wondertek\\mam\\util\\6315.xml", Star.class);
    //        List<String> xmlPaths = parseFile4XmlPaths("D:\\Idea\\MZ\\mobilevideo\\mam\\mam-core\\src\\main\\java\\com\\wondertek\\mam\\util\\star.txt");
    //        JSONArray jsonArray = JSONArray.fromObject(xmlPaths);
    //        System.out.println(jsonArray.toString());
    //buildInsertSql(Star.class);
    //}
}
