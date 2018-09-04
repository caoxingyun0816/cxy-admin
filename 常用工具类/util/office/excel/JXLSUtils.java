package com.wondertek.mam.util.office.excel;

import jxl.*;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.read.biff.BiffException;
import jxl.write.*;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

/**
 * @ClassName: JXLSUtils
 * @Description: jxl操作excel的工具类
 */
public class JXLSUtils {

    private static DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
    private static final String DEFAULT_SHEET_NAME = "sheet";
    /**
     * @Title: exportexcle
     * @Description: 下载excel
     * @param response
     * @param filename
     *            文件名 ,如:20110808.xls
     * @param listData
     *            数据源
     * @param sheetName
     *            表头名称
     * @param heads
     *            列对应的数据在Map中的key
     * @param columns
     *            列名称集合,如：{物品名称，数量，单价}
     */
    public static void exportexcel(HttpServletResponse response,
                                   String filename, List<Map<String, Object>> listData,
                                   String sheetName, List<String> columns, List<String> heads) {
        // 调用上面的方法、生成Excel文件
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-Disposition", "attachment;filename="
                    // + new String(filename.getBytes("gb2312"), "ISO8859-1")
                    + filename + ".xls");
            exportToExcel(response, listData, sheetName, columns, heads);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @Title: exportToExcel
     * @Description: 导出excel
     * @param response
     * @param objData
     *            导出内容数组
     * @param sheetName
     *            导出工作表的名称
     * @param heads
     *            列对应的数据在Map中的key
     * @param columns
     *            导出Excel的表头数组
     * @return
     */
    public static int exportToExcel(HttpServletResponse response,
                                    List<Map<String, Object>> objData, String sheetName,
                                    List<String> columns, List<String> heads) {
        int flag = 0;
        // 声明工作簿jxl.write.WritableWorkbook
        WritableWorkbook wwb;
        try {
            // 根据传进来的file对象创建可写入的Excel工作薄
            OutputStream os = response.getOutputStream();
            wwb = Workbook.createWorkbook(os);
            /*
             * 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表
             * 打开Excel的时候会看到左下角默认有3个sheet、"sheet1、sheet2、sheet3"
             * 这样代码中的"0"就是sheet1、其它的一一对应 createSheet(sheetName,
             * 0)一个是工作表的名称，另一个是工作表在工作薄中的位置
             */
            WritableSheet ws = wwb.createSheet(sheetName, 0);
            SheetSettings ss = ws.getSettings();
            ss.setVerticalFreeze(1);// 冻结表头
            WritableFont font1 = new WritableFont(
                    WritableFont.createFont("微软雅黑"), 10, WritableFont.BOLD);
            WritableFont font2 = new WritableFont(
                    WritableFont.createFont("微软雅黑"), 9, WritableFont.NO_BOLD);
            WritableCellFormat wcf = new WritableCellFormat(font1);
            WritableCellFormat wcf2 = new WritableCellFormat(font2);
            WritableCellFormat wcf3 = new WritableCellFormat(font2);// 设置样式，字体
            // 创建单元格样式
            // WritableCellFormat wcf = new WritableCellFormat();
            // 背景颜色
            wcf.setBackground(jxl.format.Colour.YELLOW);
            wcf.setAlignment(Alignment.CENTRE); // 平行居中
            wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
            wcf3.setAlignment(Alignment.CENTRE); // 平行居中
            wcf3.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
            wcf3.setBackground(Colour.LIGHT_ORANGE);
            wcf2.setAlignment(Alignment.CENTRE); // 平行居中
            wcf2.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
            /*
             * 这个是单元格内容居中显示 还有很多很多样式
             */
            wcf.setAlignment(Alignment.CENTRE);
            // 判断一下表头数组是否有数据
            if (columns != null && columns.size() > 0) {
                // 循环写入表头
                for (int i = 0; i < columns.size(); i++) {
                    /*
                     * 添加单元格(Cell)内容addCell() 添加Label对象Label()
                     * 数据的类型有很多种、在这里你需要什么类型就导入什么类型 如：jxl.write.DateTime
                     * 、jxl.write.Number、jxl.write.Label Label(i, 0, columns[i],
                     * wcf) 其中i为列、0为行、columns[i]为数据、wcf为样式
                     * 合起来就是说将columns[i]添加到第一行(行、列下标都是从0开始)第i列、样式为什么"色"内容居中
                     */
                    ws.addCell(new Label(i, 0, columns.get(i), wcf));
                }
                // 判断表中是否有数据
                if (objData != null && objData.size() > 0) {
                    // 循环写入表中数据
                    for (int i = 0; i < objData.size(); i++) {
                        // 转换成map集合{activyName:测试功能,count:2}
                        Map<String, Object> map = (Map<String, Object>) objData
                                .get(i);
                        // 循环输出map中的子集：既列值
                        for (int j = 0; j < heads.size(); j++) {
                            ws.addCell(new Label(j, i + 1, String.valueOf(map
                                    .get(heads.get(j)) == null ? "" : map
                                    .get(heads.get(j)))));
                        }
                    }
                } else {
                    flag = -1;
                }
                // 写入Exel工作表
                wwb.write();
                // 关闭Excel工作薄对象
                wwb.close();
                // 关闭流
                os.flush();
                os.close();
                os = null;
            }
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
        } catch (Exception ex) {
            flag = 0;
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * @Title: exportExcleByTemplate
     * @Description: 根据模板导出Excel文件
     * @param response
     * @param templateFilePath 模板文件路径包括模板名称和扩展名
     * @param beanParams 导出数据
     * @param resultFileName 导出文件名称和扩展名
     * @throws UnsupportedEncodingException
     */
    public static void exportExcelByTemplate(HttpServletResponse response, String templateFilePath, Map<String,Object> beanParams, String resultFileName) throws UnsupportedEncodingException {
        //设置响应
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(resultFileName.getBytes("UTF-8"), "ISO8859-1" ));
        response.setContentType("application/vnd.ms-excel");
        //创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        InputStream in=null;
        OutputStream out=null;
        try {
            in=new BufferedInputStream(new FileInputStream(templateFilePath));
            org.apache.poi.ss.usermodel.Workbook workbook=transformer.transformXLS(in, beanParams);
            out=response.getOutputStream();
            //将内容写入输出流并把缓存的内容全部发出去
            workbook.write(out);
            out.flush();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in!=null){try {in.close();} catch (IOException e) {}}
            if (out!=null){try {out.close();} catch (IOException e) {}}
        }
    }

    /**
     * @Title: createExcel
     * @Description: 根据模板生成Excel文件
     * @param srcFilePath 模板文件路径
     * @param beanParams 模板中存放的数据
     * @param destFilePath 生成的文件路径
     */
    public static void createExcel(String srcFilePath, Map<String,Object> beanParams, String destFilePath){
        //创建XLSTransformer对象
        XLSTransformer transformer = new XLSTransformer();
        try {
            //生成Excel文件
            transformer.transformXLS(srcFilePath, beanParams, destFilePath);
        } catch (ParsePropertyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Title: readExcel
     * @Description: 读取Excel文件的内容
     * @param file
     *            待读取的文件
     * @return
     */
    public static List<List<List<String>>> readExcel(File file)
            throws BiffException, IOException {
        Workbook workBook = Workbook.getWorkbook(file);
        if (workBook == null)
            return null;
        return getDataInWorkbook(workBook);
    }

    private static List<List<List<String>>> getDataInWorkbook(Workbook workBook) {
        // 获得了Workbook对象之后，就可以通过它得到Sheet（工作表）对象了
        Sheet[] sheet = workBook.getSheets();
        List<List<List<String>>> dataList = new ArrayList<List<List<String>>>();
        if (sheet != null && sheet.length > 0) {
            // 对每个工作表进行循环
            for (int i = 0; i < sheet.length; i++) {
                List<List<String>> rowList = new ArrayList<List<String>>();
                // 得到当前工作表的行数
                int rowNum = sheet[i].getRows();
                int colNum = sheet[i].getColumns();
                for (int j = 0; j < rowNum; j++) {
                    // 得到当前行的所有单元格
                    Cell[] cells = sheet[i].getRow(j);
                    if (cells != null && cells.length > 0) {
                        List<String> cellList = new ArrayList<String>();
                        // 对每个单元格进行循环
                        for (int k = 0; k < colNum; k++) {
                            Cell cell = sheet[i].getCell(k, j);
                            String cellValue = "";
                            // 判断单元格的值是否是数字
                            if (cell.getType() == CellType.NUMBER) {
                                NumberCell numberCell = (NumberCell) cell;
                                double value = numberCell.getValue();
                                cellValue = decimalFormat.format(value);
                            } else if (cell.getType() == CellType.NUMBER_FORMULA
                                    || cell.getType() == CellType.STRING_FORMULA
                                    || cell.getType() == CellType.BOOLEAN_FORMULA
                                    || cell.getType() == CellType.DATE_FORMULA
                                    || cell.getType() == CellType.FORMULA_ERROR) {
                                FormulaCell nfc = (FormulaCell) cell;
                                cellValue = nfc.getContents();
                            } else {
                                // 读取当前单元格的值
                                cellValue = cell.getContents();
                                // 特殊字符处理
                                cellValue = excelCharaterDeal(cellValue);
                            }
                            // 去掉空格
                            cellList.add(cellValue.trim());
                        }
                        rowList.add(cellList);
                    }
                }
                dataList.add(rowList);
            }
        }
        // 最后关闭资源，释放内存
        workBook.close();
        return dataList;
    }

    /**
     * @Title: toToken
     * @Description: 除去字符串中指定的分隔符
     * @param s
     *            字符串
     * @param val
     *            指定的分隔符
     * @return
     */
    private static String toToken(String s, String val) {
        if (s == null || s.trim().equals("")) {
            return s;
        }
        if (val == null || val.equals("")) {
            return s;
        }
        StringBuffer stringBuffer = new StringBuffer();
        String[] result = s.split(val);
        for (int x = 0; x < result.length; x++) {
            stringBuffer.append(" ").append(result[x]);
        }
        return stringBuffer.toString();

    }

    /**
     * @Title: excelCharaterDeal
     * @Description: Excel特殊字符处理
     * @param str
     *            字符串
     * @return
     */
    private static String excelCharaterDeal(String str) {
        String[] val = { "-", "_", "/" };// 定义特殊字符
        for (String i : val) {
            str = toToken(str, i);
        }
        return str;
    }

    /**
     * @Title: readExcelTitle
     * @Description: 读取Excel表格表头的内容
     * @param file 待读取的文件
     * @return 表头内容的数组
     * @throws Exception
     */
    public String[] readExcelTitle(File file) throws Exception {
        InputStream is = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(is);
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            title[i] = getCellFormatValue(row.getCell(i));
        }
        return title;
    }

    /**
     * @Title: readExcelByPoi
     * @Description: 通过POI读取Excel文件的内容
     * @param file 待读取的文件
     * @return
     * @throws Exception
     */
    public static List<List<List<String>>> readExcelByPoi(File file) throws Exception {
        List<List<List<String>>> dataList = new ArrayList<List<List<String>>>();
        InputStream is = new FileInputStream(file);
        HSSFWorkbook wb = new HSSFWorkbook(is);
        int sheetNum = wb.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            HSSFSheet sheet = wb.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            // 得到总行数
            int rowNum = sheet.getLastRowNum();
            HSSFRow row = sheet.getRow(0);
            if (row == null) {
                continue;
            }
            // 得到总列数
            int colNum = row.getPhysicalNumberOfCells();
            List<List<String>> rowList = new ArrayList<List<String>>();
            // 循环行Row
            for (int j = 0; j <= rowNum; j++) {
                row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                List<String> cellList = new ArrayList<String>();
                int k = 0;
                while (k < colNum) {
                    String cellValue = getCellFormatValue(row.getCell(k)).trim();
                    cellList.add(cellValue);
                    k++;
                }
                rowList.add(cellList);
            }
            dataList.add(rowList);
        }
        return dataList;
    }

    /**
     * @Title: getCellFormatValue
     * @Description: 获取单元格数据内容为字符串类型的数据
     * @param cell Excel单元格
     * @return 单元格数据内容
     */
    private static String getCellFormatValue(HSSFCell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                // 如果当前Cell的Type为NUMERIC
                case HSSFCell.CELL_TYPE_NUMERIC:
                    // 取得当前Cell的数值
                    cellvalue = decimalFormat.format(cell.getNumericCellValue());
                    break;
                case HSSFCell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式
                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();
                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = sdf.format(date);
                    }
                    else {
                        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(cell);
                        switch (cellValue.getCellType()) {
                            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                                cellvalue = String.valueOf(cellValue.getBooleanValue());
                                break;
                            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                                cellvalue = decimalFormat.format(cellValue.getNumberValue());
                                break;
                            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                                cellvalue = String.valueOf(cellValue.getStringValue());
                                break;
                            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK:
                                break;
                            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
                                break;
                        }
                    }
                    break;
                }
                // 如果当前Cell的Type为STRIN
                case HSSFCell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                // 默认的Cell值
                default:
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 导出无动态表头的Excel文件
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destOutputStream
     * @param templateInputStream
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(OutputStream destOutputStream,
                                               InputStream templateInputStream,
                                               List data, String dataKey,
                                               int maxRowPerSheet) throws Exception {
        generateExcelByTemplate(destOutputStream,
                templateInputStream,
                null, null,
                data, dataKey,
                maxRowPerSheet);
    }

    /**
     * 通过Excel模版生成Excel文件
     * <p>
     * 创建Excel模版，变量类似JSP tag风格。
     * 例如：
     * <ul>
     * <li>无动态表头
     * <pre>
     * 序号   名称  规格  创建时间    价格
     * &lt;jx:forEach items="${vms}" var="vm"&gt;
     * ${vm.id} ${vm.name} ${vm.scale} ${vm.created} ${vm.price}
     * &lt;/jx:forEach&gt;
     * </pre>
     * </li>
     * <li>有动态表头
     * <pre>
     * 项目/数量/时间    &lt;jx:forEach items="${dates}" var="date"&gt;    ${date} &lt;/jx:forEach&gt;
     * &lt;jx:forEach items="${itemsx}" var="item"&gt;
     * ${item.name}    &lt;jx:forEach items="${item.counts}" var="count"&gt; ${count}    &lt;/jx:forEach&gt;
     * &lt;/jx:forEach&gt;
     * </pre>
     * </li>
     * </ul>
     * 调用该方法则生成对应的Excel文件。
     * </p>
     * <p>
     * 注意：dataKey不能是items, items是保留字，如果用items则会提示：Collection is null并抛出NullPointerException
     * </p>
     * @param destOutputStream Excel输出流
     * @param templateInputStream Excel模版输入流
     * @param header 动态表头
     * @param headerKey 表头的变量
     * @param data 数据项
     * @param dataKey 数据项变量
     * @param maxRowPerSheet 每个sheet最多行数
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(OutputStream destOutputStream,
                                               InputStream templateInputStream,
                                               List header, String headerKey,
                                               List data, String dataKey,
                                               int maxRowPerSheet) throws Exception {
        List<List> splitData = null;
        @SuppressWarnings("unchecked")
        Map<String, List> beanMap = new HashMap();
        List<String> sheetNames = new ArrayList<String>();
        if (data.size() > maxRowPerSheet) {
            splitData = splitList(data, maxRowPerSheet);
            sheetNames = new ArrayList<String>(splitData.size());
            for (int i = 0; i < splitData.size(); ++i) {
                sheetNames.add(DEFAULT_SHEET_NAME  + i);
            }
        } else {
            splitData = new ArrayList<List>();
            sheetNames.add(DEFAULT_SHEET_NAME + 0);
            splitData.add(data);
        }
        if (null != header) {
            beanMap.put(headerKey, header);
        }
        XLSTransformer transformer = new XLSTransformer();
//         Workbook workbook = transformer.transformMultipleSheetsList(
//                templateInputStream, splitData, sheetNames, dataKey, beanMap, 0);//强制类型转换为Workbook，可能报错
//        workbook.write(destOutputStream);
    }


    /**
     * 导出无动态表头的Excel文件，目标文件和模版文件均为文件路径
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destFilePath
     * @param templateFilePath
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(String destFilePath,
                                               String templateFilePath,
                                               List data, String dataKey, int maxRowPerSheet) throws Exception {
        generateExcelByTemplate(destFilePath, templateFilePath, null, null, data, dataKey, maxRowPerSheet);
    }

    /**
     * 导出有动态表头的Excel文件，目标文件和模版文件均为文件路径
     * <p>
     * 参考重载的有动态表头注释
     * </p>
     * @param destFilePath
     * @param templateFilePath
     * @param header
     * @param headerKey
     * @param data
     * @param dataKey
     * @param maxRowPerSheet
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static void generateExcelByTemplate(String destFilePath,
                                               String templateFilePath,
                                               List header, String headerKey,
                                               List data, String dataKey, int maxRowPerSheet) throws Exception {
        generateExcelByTemplate(new FileOutputStream(destFilePath),
                new FileInputStream(templateFilePath),
                header, headerKey,
                data, dataKey, maxRowPerSheet);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<List> splitList(List data, int maxRowPerSheet) {
        List<List> splitData = new ArrayList<List>();
        List sdata = null;
        for (int i = 0; i < data.size(); ++i) {
            if (0 == i % maxRowPerSheet) {
                if (null != sdata) {
                    splitData.add(sdata);
                }
                sdata = new ArrayList(maxRowPerSheet);
            }
            sdata.add(data.get(i));
        }
        if (0 != maxRowPerSheet % data.size()) {
            splitData.add(sdata);
        }

        return splitData;
    }

    /**
     * @deprecated  导出方法，传入参数为模板名称，该参数从界面传入,支持SQL语句参数查询
     * @author      lls
     */
//    public void exportJXLS(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
//    {
//
//        Map submap = this.filterRequestParameterMap(request);
//        //获取传入的模板名称
//        String xlsTemplateName=(String)submap.get("TemplateName");
//
//        //获取到导出模板的绝对全路径
//        String xlsTemplateFileNameURI=this.getClass().getClassLoader().getResource("excelTemplate/"+xlsTemplateName+".xls").toString();
//        String  xlsTemplateFileName=xlsTemplateFileNameURI.substring(6,xlsTemplateFileNameURI.length());
//
//        try {
//            //获取连接对象
//            Connection conn=null;
//            conn=jdbcUtil.getConnection();
//            //将报表实现类注入到submap中
//            ReportManager rm = new ReportManagerImpl( conn, submap );
//            submap.put("rm", rm);
//            //获取模板的输入流
//            InputStream is =new BufferedInputStream(new FileInputStream(xlsTemplateFileName));
//            //通过模板输入流以及报表实现类生成一个excel工作簿
//            XLSTransformer transformer = new XLSTransformer();
//            HSSFWorkbook workbook=(HSSFWorkbook)transformer.transformXLS(is, submap);
//            //弹出框保存工作簿
//            saveExcelFile(workbook,xlsTemplateName,response);
//
//            jdbcUtil.close(conn);
//        }catch (Exception e) {
//
//            e.printStackTrace();
//        }
//    }
    /**
     * @deprecated                   弹出框保存工作簿
     * @param hssWorkbook            工作簿
     * @param xlsTemplateName        下载的工作簿名称
     * @param response
     */
    private void saveExcelFile(HSSFWorkbook hssWorkbook ,String xlsTemplateName,HttpServletResponse response)
    {

        //设置导出弹出框，以及下载文件名称
        response.setHeader("Content-Disposition","attachment;filename="+xlsTemplateName+".xls");
        OutputStream os;
        try {
            os = response.getOutputStream();
            hssWorkbook.write(os);
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * 如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     * 如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     * fieldMap.put("college.collegeName","学院名称")
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param out       导出流
     * @throws ExcelException
     */
    public static <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            String sheetName,
            int sheetSize,
            OutputStream out
    ) throws ExcelException{


        if(list.size()==0 || list==null){
            throw new ExcelException("数据源中没有任何数据");
        }

        if(sheetSize>65535 || sheetSize<1){
            sheetSize=65535;
        }

        //创建工作簿并发送到OutputStream指定的地方
        WritableWorkbook wwb;
        try {
            wwb = Workbook.createWorkbook(out);

            //因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
            //所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
            //1.计算一共有多少个工作表
            double sheetNum=Math.ceil(list.size()/new Integer(sheetSize).doubleValue());

            //2.创建相应的工作表，并向其中填充数据
            for(int i=0; i<sheetNum; i++){
                //如果只有一个工作表的情况
                if(1==sheetNum){
                    WritableSheet sheet=wwb.createSheet(sheetName, i);
                    fillSheet(sheet, list, fieldMap, 0, list.size()-1);

                    //有多个工作表的情况
                }else{
                    WritableSheet sheet=wwb.createSheet(sheetName+(i+1), i);

                    //获取开始索引和结束索引
                    int firstIndex=i*sheetSize;
                    int lastIndex=(i+1)*sheetSize-1>list.size()-1 ? list.size()-1 : (i+1)*sheetSize-1;
                    //填充工作表
                    fillSheet(sheet, list, fieldMap, firstIndex, lastIndex);
                }
            }

            wwb.write();
            wwb.close();

        }catch (Exception e) {
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelException("导出Excel失败");
            }
        }

    }


    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，工作表大小为2003支持的最大值）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param out       导出流
     * @throws ExcelException
     */
    public static  <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            String sheetName,
            OutputStream out
    ) throws ExcelException{

        listToExcel(list, fieldMap, sheetName, 65535, out);

    }


    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（导出到浏览器，可以自定义工作表的大小）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param sheetSize    每个工作表中记录的最大个数
     * @param response  使用response可以导出到浏览器
     * @throws ExcelException
     */
    public static  <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            String sheetName,
            int sheetSize,
            HttpServletResponse response
    ) throws ExcelException{

        //设置默认文件名为当前时间：年月日时分秒
        String fileName=new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();

        //设置response头信息
        response.reset();
        response.setContentType("application/vnd.ms-excel");        //改成输出excel文件
        response.setHeader("Content-disposition","attachment; filename="+fileName+".xls" );

        //创建工作簿并发送到浏览器
        try {

            OutputStream out=response.getOutputStream();
            listToExcel(list, fieldMap, sheetName, sheetSize,out );

        } catch (Exception e) {
            e.printStackTrace();

            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelException("导出Excel失败");
            }
        }
    }


    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（导出到浏览器，工作表的大小是2003支持的最大值）
     * @param list      数据源
     * @param fieldMap      类的英文属性和Excel中的中文列名的对应关系
     * @param response  使用response可以导出到浏览器
     * @throws ExcelException
     */
    public static <T>  void   listToExcel (
            List<T> list ,
            LinkedHashMap<String,String> fieldMap,
            String sheetName,
            HttpServletResponse response
    ) throws ExcelException{

        listToExcel(list, fieldMap, sheetName, 65535, response);
    }

    /**
     * @MethodName          : excelToList
     * @Description             : 将Excel转化为List
     * @param in                    ：承载着Excel的输入流
     * @param sheetName        ：要导入的工作表序号
     * @param entityClass       ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap          ：Excel中的中文列头和类的英文属性的对应关系Map
     * @param uniqueFields  ：指定业务主键组合（即复合主键），这些列的组合不能重复
     * @return                      ：List
     * @throws ExcelException
     */
    public static <T>  List<T>  excelToList(
            InputStream in,
            String sheetName,
            Class<T> entityClass,
            LinkedHashMap<String, String> fieldMap,
            String[] uniqueFields
    ) throws ExcelException{

        //定义要返回的list
        List<T> resultList=new ArrayList<T>();

        try {

            //根据Excel数据源创建WorkBook
            Workbook wb=Workbook.getWorkbook(in);
            //获取工作表
            Sheet sheet=wb.getSheet(sheetName);

            //获取工作表的有效行数
            int realRows=0;
            for(int i=0;i<sheet.getRows();i++){

                int nullCols=0;
                for(int j=0;j<sheet.getColumns();j++){
                    Cell currentCell=sheet.getCell(j,i);
                    if(currentCell==null || "".equals(currentCell.getContents().toString())){
                        nullCols++;
                    }
                }

                if(nullCols==sheet.getColumns()){
                    break;
                }else{
                    realRows++;
                }
            }


            //如果Excel中没有数据则提示错误
            if(realRows<=1){
                throw new ExcelException("Excel文件中没有任何数据");
            }


            Cell[] firstRow=sheet.getRow(0);

            String[] excelFieldNames=new String[firstRow.length];

            //获取Excel中的列名
            for(int i=0;i<firstRow.length;i++){
                excelFieldNames[i]=firstRow[i].getContents().toString().trim();
            }

            //判断需要的字段在Excel中是否都存在
            boolean isExist=true;
            List<String> excelFieldList=Arrays.asList(excelFieldNames);
            for(String cnName : fieldMap.keySet()){
                if(!excelFieldList.contains(cnName)){
                    isExist=false;
                    break;
                }
            }

            //如果有列名不存在，则抛出异常，提示错误
            if(!isExist){
                throw new ExcelException("Excel中缺少必要的字段，或字段名称有误");
            }


            //将列名和列号放入Map中,这样通过列名就可以拿到列号
            LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
            for(int i=0;i<excelFieldNames.length;i++){
                colMap.put(excelFieldNames[i], firstRow[i].getColumn());
            }



            //判断是否有重复行
            //1.获取uniqueFields指定的列
            Cell[][] uniqueCells=new Cell[uniqueFields.length][];
            for(int i=0;i<uniqueFields.length;i++){
                int col=colMap.get(uniqueFields[i]);
                uniqueCells[i]=sheet.getColumn(col);
            }

            //2.从指定列中寻找重复行
            for(int i=1;i<realRows;i++){
                int nullCols=0;
                for(int j=0;j<uniqueFields.length;j++){
                    String currentContent=uniqueCells[j][i].getContents();
                    Cell sameCell=sheet.findCell(currentContent,
                            uniqueCells[j][i].getColumn(),
                            uniqueCells[j][i].getRow()+1,
                            uniqueCells[j][i].getColumn(),
                            uniqueCells[j][realRows-1].getRow(),
                            true);
                    if(sameCell!=null){
                        nullCols++;
                    }
                }

                if(nullCols==uniqueFields.length){
                    throw new ExcelException("Excel中有重复行，请检查");
                }
            }

            //将sheet转换为list
            for(int i=1;i<realRows;i++){
                //新建要转换的对象
                T entity=entityClass.newInstance();

                //给对象中的字段赋值
                for(Map.Entry<String, String> entry : fieldMap.entrySet()){
                    //获取中文字段名
                    String cnNormalName=entry.getKey();
                    //获取英文字段名
                    String enNormalName=entry.getValue();
                    //根据中文字段名获取列号
                    int col=colMap.get(cnNormalName);
                    //获取当前单元格中的内容
                    String content=sheet.getCell(col, i).getContents().toString().trim();
                    //给对象赋值
                    setFieldValueByName(enNormalName, content, entity);
                }

                resultList.add(entity);
            }
        } catch(Exception e){
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelException){
                throw (ExcelException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                e.printStackTrace();
                throw new ExcelException("导入Excel失败");
            }
        }
        return resultList;
    }

    /**
     * @MethodName  : getFieldValueByName
     * @Description : 根据字段名获取字段值
     * @param fieldName 字段名
     * @param o 对象
     * @return  字段值
     */
    private static  Object getFieldValueByName(String fieldName, Object o) throws Exception{

        Object value=null;
        Field field=getFieldByName(fieldName, o.getClass());

        if(field !=null){
            field.setAccessible(true);
            value=field.get(o);
        }else{
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }

        return value;
    }

    /**
     * @MethodName  : getFieldByName
     * @Description : 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段
     */
    private static Field getFieldByName(String fieldName, Class<?>  clazz){
        //拿到本类的所有字段
        Field[] selfFields=clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        for(Field field : selfFields){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz=clazz.getSuperclass();
        if(superClazz!=null  &&  superClazz !=Object.class){
            return getFieldByName(fieldName, superClazz);
        }

        //如果本类和父类都没有，则返回空
        return null;
    }



    /**
     * @MethodName  : getFieldValueByNameSequence
     * @Description :
     * 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     *
     * @param fieldNameSequence  带路径的属性名或简单属性名
     * @param o 对象
     * @return  属性值
     * @throws Exception
     */
    private static  Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception{

        Object value=null;

        //将fieldNameSequence进行拆分
        String[] attributes=fieldNameSequence.split("\\.");
        if(attributes.length==1){
            value=getFieldValueByName(fieldNameSequence, o);
        }else{
            //根据属性名获取属性对象
            Object fieldObj=getFieldValueByName(attributes[0], o);
            String subFieldNameSequence=fieldNameSequence.substring(fieldNameSequence.indexOf(".")+1);
            value=getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;

    }


    /**
     * @MethodName  : setFieldValueByName
     * @Description : 根据字段名给对象的字段赋值
     * @param fieldName  字段名
     * @param fieldValue    字段值
     * @param o 对象
     */
    private static void setFieldValueByName(String fieldName,Object fieldValue,Object o) throws Exception{

        Field field=getFieldByName(fieldName, o.getClass());
        if(field!=null){
            field.setAccessible(true);
            //获取字段类型
            Class<?> fieldType = field.getType();

            //根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType)
                    || (Integer.class == fieldType)) {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType)
                    || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType)
                    || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType)
                    || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType)
                    || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue!= null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character
                            .valueOf(fieldValue.toString().charAt(0)));
                }
            }else if(Date.class==fieldType){
                field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
            }else{
                field.set(o, fieldValue);
            }
        }else{
            throw new ExcelException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }
    }

    /**
     * @MethodName  : setColumnAutoSize
     * @Description : 设置工作表自动列宽和首行加粗
     * @param ws
     */
    private static void setColumnAutoSize(WritableSheet ws,int extraWith){
        //获取本列的最宽单元格的宽度
        for(int i=0;i<ws.getColumns();i++){
            int colWith=0;
            for(int j=0;j<ws.getRows();j++){
                String content=ws.getCell(i,j).getContents().toString();
                int cellWith=content.length();
                if(colWith<cellWith){
                    colWith=cellWith;
                }
            }
            //设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith+extraWith);
        }

    }


    /**
     * @MethodName  : fillSheet
     * @Description : 向工作表中填充数据
     * @param sheet     工作表
     * @param list  数据源
     * @param fieldMap 中英文字段对应关系的Map
     * @param firstIndex    开始索引
     * @param lastIndex 结束索引
     */
    private static <T> void fillSheet(
            WritableSheet sheet,
            List<T> list,
            LinkedHashMap<String,String> fieldMap,
            int firstIndex,
            int lastIndex
    )throws Exception{

        //定义存放英文字段名和中文字段名的数组
        String[] enFields=new String[fieldMap.size()];
        String[] cnFields=new String[fieldMap.size()];

        //填充数组
        int count=0;
        for(Map.Entry<String,String> entry:fieldMap.entrySet()){
            enFields[count]=entry.getKey();
            cnFields[count]=entry.getValue();
            count++;
        }
        //填充表头
        for(int i=0;i<cnFields.length;i++){
            Label label=new Label(i,0,cnFields[i]);
            sheet.addCell(label);
        }

        //填充内容
        int rowNo=1;
        for(int index=firstIndex;index<=lastIndex;index++){
            //获取单个对象
            T item=list.get(index);
            for(int i=0;i<enFields.length;i++){
                Object objValue=getFieldValueByNameSequence(enFields[i], item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                Label label =new Label(i,rowNo,fieldValue);
                sheet.addCell(label);
            }

            rowNo++;
        }

        //设置自动列宽
        setColumnAutoSize(sheet, 5);
    }

//    总结
//    导入和导出方法都是通过传一个fieldMap参数（类的英文属性和Excel的中文列头的对应关系）来连接实体类和Excel的
//    导出的时候可以选择导出到本地文件系统或导出到浏览器，也可以自定义每个工作表的大小
//    导入的时候可以自定义业务主键组合uniqueFields，这样就可以检测Excel中是否有重复行了




}
