//package com.wondertek.mam.util.others;
//
////下面是java的各种工具，包括获取时间和时间比较，检验集合和字符串是否为空和长度大小等等
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.OutputStream;
//import java.io.Reader;
//import java.io.UnsupportedEncodingException;
//import java.math.BigDecimal;
//import java.text.DateFormat;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.List;
//import java.util.Map;
//
//import jxl.Workbook;
//import jxl.write.Label;
//import jxl.write.WritableCellFormat;
//import jxl.write.WritableFont;
//import jxl.write.WritableSheet;
//import jxl.write.WritableWorkbook;
//
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.lang.CharUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.time.DateFormatUtils;
//import org.apache.commons.validator.GenericValidator;
//
//import sun.misc.BASE64Decoder;
//
//import com.xmld.server.constant.GeneralConstant;
//import com.xmld.server.exception.ServiceException;
//
///**
// * 通用的工具类
// */
//public final class GeneralUtils
//{
//
//    private GeneralUtils()
//    {
//
//    }
//
//    /**
//     * 判断对象是否为null , 为null返回true,否则返回false
//     * @param obj 被判断的对象
//     * @return boolean
//     */
//    public static boolean isNull(Object obj)
//    {
//        return (null == obj) ? true : false;
//    }
//
//    /**
//     * 判断对象是否为null , 为null返回false,否则返回true
//     *
//     * @param obj 被判断的对象
//     * @return boolean
//     */
//    public static boolean isNotNull(Object obj)
//    {
//        return !isNull(obj);
//    }
//
//    /**
//     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回true,否则返回false
//     *
//     * @param str 被判断的字符串
//
//     * @return boolean
//     */
//    public static boolean isNullOrZeroLenght(String str)
//    {
//        return GenericValidator.isBlankOrNull(str);
//    }
//
//    /**
//     * 判断字符串是否为null或者0长度，字符串在判断长度时，先去除前后的空格,空或者0长度返回false,否则返回true
//     *
//     * @param str 被判断的字符串
//
//     * @return boolean
//     */
//    public static boolean isNotNullOrZeroLenght(String str)
//    {
//        return !isNullOrZeroLenght(str);
//    }
//
//    /**
//     * 判断str数组是否为null或者0长度，只要有一个空或者0长度返回false, 否则返回true
//     *
//     * @param str String 字符数组
//     * @return boolean
//     * @author huanghui
//     * @see [类、类#方法、类#成员]
//     */
//    public static boolean isNotNullOrZeroLenght(String... str)
//    {
//        for (String s : str)
//        {
//            if (isNullOrZeroLenght(s))
//            {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 判断str数组是否为null或者0长度，只要有一个空或者0长度返回true, 否则返回false
//     *
//     * @param str String 字符数组
//     * @return boolean
//     * @author huanghui
//     * @see [类、类#方法、类#成员]
//     */
//    public static boolean isNullOrZeroLenght(String... str)
//    {
//        return !isNotNullOrZeroLenght(str);
//    }
//
//    /**
//     * 判断集合对象是否为null或者0大小 , 为空或0大小返回true ,否则返回false
//     *
//     * @param c collection 集合接口
//     * @return boolean 布尔值
//
//     * @author huanghui
//     * @see [类、类#方法、类#成员]
//     */
//    public static boolean isNullOrZeroSize(Collection<? extends Object> c)
//    {
//        return isNull(c) || c.isEmpty();
//    }
//
//    /**
//     * 判断集合对象是否为null或者0大小 , 为空或0大小返回false, 否则返回true
//     *
//     * @param c collection 集合接口
//     * @return boolean 布尔值
//
//     * @author huanghui
//     * @see [类、类#方法、类#成员]
//     */
//    public static boolean isNotNullOrZeroSize(Collection<? extends Object> c)
//    {
//        return !isNullOrZeroSize(c);
//    }
//
//    /**
//     * 判断数字类型是否为null或者0，如果是返回true，否则返回false
//     *
//     * @param number 被判断的数字
//     * @return boolean
//     */
//    public static boolean isNullOrZero(Number number)
//    {
//        if (GeneralUtils.isNotNull(number))
//        {
//            return (number.doubleValue() != 0) ? false : true;
//        }
//        return true;
//    }
//
//    /**
//     * 判断数字类型是否不为null或者0，如果是返回true，否则返回false
//     *
//     * @param number 被判断的数字
//     * @return boolean
//     */
//    public static boolean isNotNullOrZero(Number number)
//    {
//        return !isNullOrZero(number);
//    }
//
//    /**
//     * 将java.util.Date类型转化位String类型
//     *
//     * @param date 要转换的时间
//     * @param format 时间格式
//     * @return 如果转换成功，返回指定格式字符串，如果转换失败，返回null
//     */
//    public static String date2String(Date date, String format)
//    {
//        if (GeneralUtils.isNull(date) || GeneralUtils.isNull(format))
//        {
//            return null;
//        }
//
//        return DateFormatUtils.format(date, format);
//    }
//
//    /**
//     * 将字符串时间转换成java.util.Date类型
//     * @param str 要转换的字符串
//     * @param format 时间格式
//     * @return 如果转换失败，返回null
//     */
//    public static Date string2Date(String str, String format)
//    {
//        if (GeneralUtils.isNull(str) || GeneralUtils.isNull(format))
//        {
//            return null;
//        }
//
//        // 定义日期/时间格式
//        SimpleDateFormat sdf = new SimpleDateFormat(format);
//        Date date;
//
//        try
//        {
//            // 转换日期/时间格式
//            date = sdf.parse(str);
//            // 判断转换前后时间是否一致
//
//            if (!str.equals(sdf.format(date)))
//            {
//                date = null;
//            }
//        }
//        catch (ParseException e)
//        {
//            date = null;
//        }
//
//        return date;
//    }
//
//    /**
//     * 将字符串时间转换时间格式
//     * @param str 要转换的字符串
//     * @param formatF 初始时间格式
//     * @param formatT 目标时间格式
//     * @return 如果转换失败，返回null
//     */
//    public static String String2String(String str, String formatF, String formatT)
//    {
//        return date2String(string2Date(str, formatF), formatT);
//    }
//
//    /**
//     * 验证日期/时间格式
//     * @param dateStr 待验证的字符串
//     * @param format 类型
//     * @return 是返回ture,否则返回false
//     */
//    public static boolean isDateTime(String dateStr, String format)
//    {
//        return GenericValidator.isDate(dateStr, format, true);
//    }
//
//    /**
//     * 判断字符串长度，范围包含min和max的值
//
//     *
//     * @param str String
//     * @param min 最小范围
//
//     * @param max 最大范围
//
//     * @return boolean
//     */
//    public static boolean isInRange(String str, int min, int max)
//    {
//        if (GeneralUtils.isNull(str))
//        {
//            return false;
//        }
//
//        try
//        {
//            int len = str.trim().getBytes(GeneralConstant.CHARACTER_CODING).length;
//            return GenericValidator.isInRange(len, min, max);
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            return false;
//        }
//    }
//
//
//    /**
//     * 判断字符串是否超过最大长度
//
//     * @param str String
//     * @param max 最大长度
//
//     * @return boolean
//     */
//    public static boolean maxLength(String str, int max)
//    {
//        return isInRange(str, 0, max);
//    }
//
//    /**
//     * 判断字符串是否低于最小长度
//
//     * @param str String
//     * @param min 最小长度
//
//     * @return boolean
//     */
//    public static boolean minLength(String str, int min)
//    {
//        return isInRange(str, min, Integer.MAX_VALUE);
//    }
//
//    /**
//     * 获取目录在系统中的绝对路径
//     * @param path 路径
//     * @return Sting
//     */
//    public static String getAbsolutePath(String path)
//    {
//        // 如果路进为null，则认为时当前目录
//
//        path = (GeneralUtils.isNull(path)) ? "" : path;
//        File file = new File(path);
//
//        // 获取完整路径
//        return FilenameUtils.separatorsToUnix(file.getAbsolutePath());
//    }
//
//    /**
//     * 将字符串首字符待大写
//     * @param str 源字符串
//     * @return 首字符大写后的字符串
//     * @see [类、类#方法、类#成员]
//     */
//    public static String firstCharUpper(String str)
//    {
//        char firstChar = CharUtils.toChar(str);
//        String upFirstChar = StringUtils.upperCase(String.valueOf(firstChar));
//        return StringUtils.replaceOnce(str, String.valueOf(firstChar), upFirstChar);
//    }
//
//    /**
//     * Object - > String
//     * @param obj  对象参数
//     * @return  String 字符串
//     */
//    public static String object2String(Object obj)
//    {
//        if (isNull(obj))
//        {
//            return "";
//        }
//        else
//        {
//            return obj.toString();
//        }
//    }
//
//    /**
//     * 返回当前的时间戳
//     * @return 时间戳
//     */
//    public static String getCurrentTimeStamp()
//    {
//        return Long.toString(System.currentTimeMillis() / 1000);
//    }
//
//    /**
//     * 返回当前时间戳
//     * @param pattern 默认为：yyyyMMddHHmmss
//     * @return string  时间字符串
//     */
//    public static String getCurrentTimeStamp(String pattern)
//    {
//        if (isNullOrZeroLenght(pattern))
//        {
//            pattern = GeneralConstant.DATETIME_14;
//        }
//        Date date = new Date(System.currentTimeMillis());
//        return date2String(date, pattern);
//    }
//
//    /**
//     * 返回昨天的日期
//     * @param pattern
//     * @return
//     */
//    public static String getNextDay(String pattern) {
//        if (isNullOrZeroLenght(pattern))
//        {
//            pattern = GeneralConstant.DATETIME_14;
//        }
//        Calendar calendar = Calendar.getInstance();
//        Date dNow = new Date();
//        calendar.setTime(dNow);
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        dNow = calendar.getTime();
//        return date2String(dNow, pattern);
//    }
//    /**
//     * 返回当前时间戳
//     * @return 17位时间戳表示
//     */
//    public static String getCurrentTimeStamp17Bit()
//    {
//        Date date = new Date(System.currentTimeMillis());
//        return date2String(date, GeneralConstant.DATETIME_17);
//    }
//
//    /**
//     * 返回当前时间戳
//     * @return 12位时间戳表示
//     */
//    public static String getCurrentTimeStamp12Bit()
//    {
//        Date date = new Date(System.currentTimeMillis());
//        return date2String(date, GeneralConstant.DATETIME_12_A);
//    }
//
//    /**
//     * 根据格式和间隔时间返回系统当前日期N天前或者N天后的日期
//     * @param pattan 格式化时间
//     * @param days 间隔时间 正数代表之后的日期，负数代表之前的日期
//     * @return
//     * @see [类、类#方法、类#成员]
//     */
//    public static String getNSystemTime(String pattan, int days)
//    {
//        GregorianCalendar currentDate = new GregorianCalendar();
//        currentDate.add(GregorianCalendar.DATE, days);
//        Date date = currentDate.getTime();
//        SimpleDateFormat df = new SimpleDateFormat(pattan);
//        String preDay = df.format(date);
//        return preDay;
//    }
//
//    /**
//     * 根据格式化字符获取时间
//     * @param dateStr 字符串时间
//     * @param pattern 格式化方式
//     * @return Date Date
//     */
//    public static Date getDate(String dateStr, String pattern)
//    {
//        Date date = null;
//        try
//        {
//            date = new SimpleDateFormat(pattern).parse(dateStr);
//        }
//        catch (ParseException e)
//        {
//            return null;
//        }
//        return date;
//    }
//
//    /**
//     * 拼装文件路径
//     * @param basePath 文件路径
//     * @param fullFilenameToAdd 文件名或带部分路径的文件名
//     * @return 返回处理后的字符串
//     */
//    public static String filePathConcat(String basePath, String fullFilenameToAdd)
//    {
//        String path1 = FilenameUtils.separatorsToUnix(GeneralUtils.isNull(basePath) ? "" : basePath);
//        String path2 = FilenameUtils.separatorsToUnix(GeneralUtils.isNull(fullFilenameToAdd) ? "" : fullFilenameToAdd);
//
//        if (GeneralUtils.isNullOrZeroLenght(path1))
//        {
//            return path2;
//        }
//
//        if ((path1.length() - 1) == StringUtils.lastIndexOf(path1, GeneralConstant.SEPARATOR))
//        {
//            path1 = StringUtils.substring(path1, 0, path1.length() - 1);
//        }
//
//        if (0 == StringUtils.indexOf(path2, GeneralConstant.SEPARATOR))
//        {
//            path2 = StringUtils.substring(path2, 1);
//        }
//
//        return path1 + GeneralConstant.SEPARATOR + path2;
//
//    }
//
//    /**
//     * 删除文件
//     * @param filePathname 文件路径
//     * @throws ServiceException 业务异常
//     */
//    public static void deleteFile(String filePathname) throws ServiceException
//    {
//        // 如果路径为空或空字符串，直接返回不做处理
//        if (isNullOrZeroLenght(filePathname))
//        {
//            return;
//        }
//
//        // 定义删除的文件
//
//        File fileObject = new File(filePathname);
//
//        // 如果文件不存在，直接返回
//        if (!fileObject.exists() || !fileObject.isFile())
//        {
//            return;
//        }
//
//        // 如果删除文件失败，则抛出异常
//        if (!fileObject.delete())
//        {
//            throw new ServiceException("");
//        }
//    }
//
//    /**
//     * 将以特定字符作为分隔符的字符串转换为Set集合
//     * @param strToBeConverted - 待转换的字符串
//     * @param separator - 分隔符
//     * @return - 转换后的列表对象
//     */
//    public static List<String> splitStringUseSpecifiedSeparator(String strToBeConverted, String separator)
//    {
//        // 转换后集合对象，初始设置为空。
//
//        List<String> resultList = new ArrayList<String>();
//
//        // 原始字符串为NULL或者为空，直接返回空Set
//        if (StringUtils.isEmpty(strToBeConverted))
//        {
//            return resultList;
//        }
//
//        // 分隔符为NUlL或者空，返回只包含原始字符串的Set
//        if (StringUtils.isEmpty(separator))
//        {
//            resultList.add(strToBeConverted);
//        }
//
//        // 按照指定分隔符拆分字符串
//        String[] arrayString = strToBeConverted.split(separator);
//
//        // 遍历数组，组装到Set集合中。方便调用程序处理。
//
//        for (String str : arrayString)
//        {
//            resultList.add(str);
//        }
//
//        return resultList;
//    }
//
//    /**
//     * 生成EXCEL
//     * @param filePath 文件路径
//     * @param xlContents 需要生成的内容
//     * @param cellConfig 配置
//     * @see [类、类#方法、类#成员]
//     */
//    public static void createExcel(String filePath, List<Object> xlContents, int[] cellConfig)
//    {
//        //        String targetDirectory = ServletActionContext.getServletContext().getRealPath(filePath);
//        File f = new File(filePath);
//        int[] widthConfig = new int[cellConfig.length];
//        int widthTemp = 0;
//
//        try
//        {
//            WritableWorkbook book = Workbook.createWorkbook(f);
//            WritableSheet sheet = book.createSheet("sheet", 0);
//            //            //保护
//            //            sheet.getSettings().setProtected(true);
//            //            //保护密码
//            //            sheet.getSettings().setPassword("xmld");
//            //冻结标题
//            sheet.getSettings().setVerticalFreeze(1);
//
//            for (int i = 0; i < xlContents.size(); i++)
//            {
//                Object iContents = xlContents.get(i);
//
//                if (iContents instanceof String[])
//                {
//                    for (int j = 0; j < ((String[])iContents).length; j++)
//                    {
//                        WritableCellFormat wcf = new WritableCellFormat();
//                        wcf.setAlignment(jxl.format.Alignment.CENTRE);
//
//                        Label label = new Label(j, i, ((String[])iContents)[j], wcf);
//                        sheet.addCell(label);
//
//                        widthTemp = String.valueOf(((String[])iContents)[j]).getBytes("GBK").length + 1;
//                        if (widthTemp > widthConfig[j])
//                        {
//                            widthConfig[j] = widthTemp;
//                        }
//                    }
//                }
//                else
//                {
//                    for (int j = 0; j < ((List<?>)iContents).size(); j++)
//                    {
//                        Object obj = ((List<?>)iContents).get(j);
//
//                        WritableCellFormat wcf = new WritableCellFormat();
//                        if (cellConfig[j] == 1)
//                        {
//                            wcf.setLocked(true);
//                        }
//                        else
//                        {
//                            wcf.setLocked(false);
//                        }
//
//                        if (obj instanceof Double)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Double)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else if (obj instanceof Integer)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Integer)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else if (obj instanceof Long)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Long)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else
//                        {
//                            Label label = new Label(j, i, (String)obj, wcf);
//                            sheet.addCell(label);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < widthConfig.length; i++)
//            {
//                sheet.setColumnView(i, widthConfig[i]);
//                //System.out.println(widthConfig[i]);
//            }
//
//            book.write();
//            book.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *
//     * 生成Excel
//     * 同时写入os中
//     * @param os
//     * @param xlContents 需要生成的内容
//     * @param cellConfig 配置
//     * @param totalMoney 总计
//     * @param param   备份参数
//     * @see [类、类#方法、类#成员]
//     */
//    public static void createExcelAndRet0utDataStream(OutputStream os, List<Object> xlContents, int[] cellConfig,
//                                                      Double totalMoney, String param)
//    {
//
//        int[] widthConfig = new int[cellConfig.length];
//        int widthTemp = 0;
//        try
//        {
//            WritableWorkbook book = Workbook.createWorkbook(os);
//            WritableSheet sheet = book.createSheet("sheet", 0);
//            //保护
//            sheet.getSettings().setProtected(true);
//            //保护密码
//            sheet.getSettings().setPassword("linkage");
//            //冻结标题
//            sheet.getSettings().setVerticalFreeze(1);
//
//            for (int i = 0; i < xlContents.size(); i++)
//            {
//                Object iContents = xlContents.get(i);
//
//                if (iContents instanceof String[])
//                {
//                    for (int j = 0; j < ((String[])iContents).length; j++)
//                    {
//                        WritableCellFormat wcf = new WritableCellFormat();
//                        wcf.setAlignment(jxl.format.Alignment.CENTRE);
//
//                        Label label = new Label(j, i, ((String[])iContents)[j], wcf);
//                        sheet.addCell(label);
//
//                        widthTemp = String.valueOf(((String[])iContents)[j]).getBytes("GBK").length + 1;
//                        if (widthTemp > widthConfig[j])
//                        {
//                            widthConfig[j] = widthTemp;
//                        }
//                    }
//                }
//                else
//                {
//                    for (int j = 0; j < ((List<?>)iContents).size(); j++)
//                    {
//                        Object obj = ((List<?>)iContents).get(j);
//
//                        WritableCellFormat wcf = new WritableCellFormat();
//                        if (cellConfig[j] == 1)
//                        {
//                            wcf.setLocked(true);
//                        }
//                        else
//                        {
//                            wcf.setLocked(false);
//                        }
//
//                        if (obj instanceof Double)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Double)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else if (obj instanceof Integer)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Integer)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else if (obj instanceof Long)
//                        {
//                            jxl.write.Number number = new jxl.write.Number(j, i, (Long)obj, wcf);
//                            sheet.addCell(number);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                        else
//                        {
//                            Label label = new Label(j, i, (String)obj, wcf);
//                            sheet.addCell(label);
//
//                            widthTemp = String.valueOf(obj).getBytes("GBK").length + 1;
//                            if (widthTemp > widthConfig[j])
//                            {
//                                widthConfig[j] = widthTemp;
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (GeneralUtils.isNotNull(param) && "suppProdSell".equals(param))
//            {
//                WritableFont font1 = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD);
//                WritableCellFormat wcf1 = new WritableCellFormat(font1);
//                wcf1.setAlignment(jxl.format.Alignment.RIGHT);
//
//                String obj = "总计(元)：" + String.valueOf(totalMoney) + "元";
//                Label label = new Label(0, xlContents.size(), (String)obj, wcf1);
//                sheet.mergeCells(0, xlContents.size(), cellConfig.length - 1, xlContents.size());
//                sheet.addCell(label);
//            }
//            for (int i = 0; i < widthConfig.length; i++)
//            {
//                sheet.setColumnView(i, widthConfig[i]);
//                //System.out.println(widthConfig[i]);
//            }
//
//            book.write();
//            book.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * 讲字符串转换成byte数组
//     * @param str 字符串
//     * @return 转换后的byte数组
//     */
//    public static byte[] stringToBytes(String str)
//    {
//        try
//        {
//            if (GeneralUtils.isNotNull(str))
//            {
//                return str.getBytes(GeneralConstant.CHARACTER_CODING);
//            }
//            else
//            {
//                return new byte[0];
//            }
//
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            // 该异常不会发生
//
//            return new byte[0];
//        }
//    }
//
//    /**
//     * 讲byte[]转换成字符窜
//     * @param arr byte数组
//     * @return 转换后的字符串
//     */
//    public static String bytesToSting(byte[] arr)
//    {
//        try
//        {
//            return new String(arr, GeneralConstant.CHARACTER_CODING);
//        }
//        catch (UnsupportedEncodingException e)
//        {
//            // 该异常不会发生
//
//            return "";
//        }
//    }
//
//    /**
//     * 大字段转字符串
//     *
//     * @param clob 大字段对象
//     * @return 字符串
//     * @throws Exception 异常
//     */
//    public static String clobToString(java.sql.Clob clob)
//            throws Exception
//    {
//        if (isNull(clob))
//        {
//            return " ";
//        }
//        StringBuffer sb2 = new StringBuffer();
//        Reader instream = null;
//        instream = clob.getCharacterStream();
//        BufferedReader in = new BufferedReader(instream);
//        String line = null;
//        while ((line = in.readLine()) != null)
//        {
//            sb2.append(line);
//        }
//        return sb2.toString();
//    }
//
//    /**
//     * 数量格式化工具
//     * 如果是1.0则格式化为1
//     * 如果是1.10则格式化为1.1
//     * @param number 数字
//     * @return 数字字符串
//     * @see [类、类#方法、类#成员]
//     */
//    public static String doubleToIntString(double number)
//    {
//        String numStr = String.valueOf(number);
//        if (numStr.endsWith(".0"))
//        {
//            numStr = numStr.substring(0, numStr.indexOf(".0"));
//        }
//        return numStr;
//    }
//
//    /**
//     * 格式化GOOGLE地图返回的经纬度串
//     * 源字符串格式为"（111.9009,98.47498）"
//     * 转换过后格式为"111.9009,98.47498"
//     * @param latlng
//     * @return
//     * @see [类、类#方法、类#成员]
//     */
//    public static String formatGoogleLatlng(String latlng)
//    {
//        if (GeneralUtils.isNotNullOrZeroLenght(latlng))
//        {
//            return latlng.replace("(", "").replace(")", "");
//        }
//        return null;
//    }
//
//    public static String numberFormat(Double d)
//    {
//        if (null != d)
//        {
//            DecimalFormat df = new DecimalFormat();
//            String style = "#,##0.00#";
//            df.applyPattern(style);
//            return df.format(d);
//        }
//        else
//        {
//            return null;
//        }
//    }
//
//    /**
//     * 获取当前日期的前一天的时间,如：20120410235959
//     *
//     * @return
//     * @throws Exception
//     */
//    public static String createLastDayfStr()
//            throws Exception
//    {
//        StringBuilder sb = new StringBuilder();
//
//        Calendar rightNow = Calendar.getInstance();
//
//        rightNow.add(Calendar.DATE, -1);
//
//        sb.append(rightNow.get(Calendar.YEAR));
//
//        int currentMonth = rightNow.get(Calendar.MONTH) + 1;
//
//        if (currentMonth < 10)
//        {
//            sb.append("0");
//        }
//
//        sb.append(currentMonth);
//
//        int date = rightNow.get(Calendar.DATE);
//
//        if (date < 10)
//        {
//            sb.append("0");
//        }
//
//        sb.append(date);
//
//        sb.append("235959");
//
//        return sb.toString();
//    }
//
//    /**
//     * 计算时间差 time2-time1
//     * @param unit 返回的日期格式 <br/>
//     *             d:天 h:天-小时 m:天-小时-分 s:天-小时-分-秒<br/>
//     *             H:小时-分-秒 M:分-秒 Hm:小时-分
//     * @param time1 时间1 格式务必为(yyyyyMMddHHmmss )（被减数）
//     * @param time2 时间2 格式务必为(yyyyMMddHHmmss )（减数）
//     * @return 时间差
//     * @throws Exception 异常
//     * @see [类、类#方法、类#成员]
//     */
//    public static String calcTimeDiff(String unit, String time1, String time2)
//            throws Exception
//    {
//        //时间单位(如：不足1天(24小时) 则返回0)，开始时间，结束时间
//        Date date1 = new SimpleDateFormat("yyyyMMddHHmmss").parse(time1);
//        Date date2 = new SimpleDateFormat("yyyyMMddHHmmss").parse(time2);
//        long ltime = date1.getTime() - date2.getTime() < 0 ? date2.getTime() - date1.getTime()
//                : date1.getTime() - date2.getTime();
//        //返回天数
//        long dnum = ltime / 86400000;
//        //返回秒
//        long secnum = ltime / 1000;
//        long hnum = 0;
//        long mnum = 0;
//        long snum = 0;
//        if (secnum < 86400)
//            dnum = 0;
//        long sd = dnum * 24 * 3600;
//        long sh = secnum - sd;
//        hnum = sh / 3600;
//        long sm = sh - hnum * 3600;
//        mnum = sm / 60;
//        snum = sm - mnum * 60;
//        if (unit.equals("d"))
//        {
//            return dnum + "天";
//        }
//        else if (unit.equals("h"))
//        {
//            return dnum + "天" + hnum + "小时";
//        }
//        else if (unit.equals("m"))
//        {
//            return dnum + "天" + hnum + "小时" + mnum + "分";
//        }
//        else if (unit.equals("s"))
//        {
//            return dnum + "天" + hnum + "小时" + mnum + "分" + snum + "秒";
//        }
//        else if (unit.equals("H"))
//        {
//            return dnum * 24 + hnum + "小时" + mnum + "分" + snum + "秒";
//        }
//        else if (unit.equals("M"))
//        {
//            return (dnum * 24 + hnum) * 60 + "分" + snum + "秒";
//        }
//        else if (unit.equals("Hm"))
//        {
//            return dnum * 24 + hnum + "小时" + mnum + "分";
//        }
//        else
//        {
//            return dnum + "天" + hnum + "小时" + mnum + "分" + snum + "秒";
//        }
//    }
//
//    /**
//     * 获取当前时间的上一个月同等时间
//     *
//     * @return String 时间格式:YYYYMMDDHHMMSS
//     * @throws Exception Exception
//     */
//    public static String createLastMonthTime()
//            throws Exception
//    {
//        Calendar calendar = Calendar.getInstance();
//
//        long timeMillis = calendar.getTimeInMillis() - 2592000000l;
//
//        calendar.setTimeInMillis(timeMillis);
//
//        return GeneralUtils.date2String(calendar.getTime(), GeneralConstant.DATETIME_14);
//    }
//
//    /**
//     * 将时间段转化成用于显示的形式，如3600秒转化成1小时
//     * @param time
//     * @return
//     */
//    public static String time2View(Long time)
//    {
//        if (time >= 0 && time < 60)
//        {
//            return time + "秒";
//        }
//        else if (time >= 60 && time < 60 * 60)
//        {
//            return time / 60 + "分钟";
//        }
//        else if (time >= 60 * 60 && time < 60 * 60 * 24)
//        {
//            return time / (60 * 60) + "小时";
//        }
//        else if (time >= 60 * 60 * 24)
//        {
//            return time / (60 * 60 * 24) + "天";
//        }
//        else
//        {
//            return "";
//        }
//    }
//
//    /**
//     * 提供精确的减法运算。
//     * @param v1 被减数
//     * @param v2 减数
//     * @return 两个参数的差
//     */
//    public static double sub(double v1, double v2)
//    {
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.subtract(b2).doubleValue();
//    }
//
//    /**
//     * 提供精确的加法运算。
//     * @param v1 被加数
//     * @param v2 加数
//     * @return 两个参数的和
//     */
//    public static double add(double v1, double v2)
//    {
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.add(b2).doubleValue();
//    }
//
//    /**
//     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
//     *
//     * @param v1
//     *            被除数
//     * @param v2
//     *            除数
//     * @return 两个参数的商
//     */
//
//    public static double div(double v1, double v2)
//    {
//        return div(v1, v2, 10);
//    }
//
//    /**
//     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
//     *
//     * @param v1
//     *            被除数
//     * @param v2
//     *            除数
//     * @param scale
//     *            表示表示需要精确到小数点以后几位。
//     * @return 两个参数的商
//     */
//
//    public static double div(double v1, double v2, int scale)
//    {
//        if (scale < 0)
//        {
//            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
//        }
//        BigDecimal b1 = new BigDecimal(Double.toString(v1));
//        BigDecimal b2 = new BigDecimal(Double.toString(v2));
//        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
//    }
//
//    public static String getParamValueByKey(Map<String, Object> paramMap, String key)
//    {
//        String paramValue = null;
//        try
//        {
//            Object paramObject = paramMap.get(key);
//            paramValue = paramObject.toString();
//        }
//        catch (Exception e)
//        {
//        }
//        return paramValue;
//    }
//
//    /**
//     * 数字不足位数左补0
//     * @param str
//     * @param strLength
//     * @return
//     */
//    public static String addZeroForNum(String str, int strLength)
//    {
//        return addZeroForNum(str, strLength, true);
//    }
//
//    /**
//     * 数字不足位数补0
//     * @param str
//     * @param strLength
//     * @param isLeft    为true时，左补；否则，右补
//     * @return
//     */
//    public static String addZeroForNum(String str, int strLength, Boolean isLeft)
//    {
//        int strLen = str.length();
//        if (strLen < strLength)
//        {
//            while (strLen < strLength)
//            {
//                StringBuffer sb = new StringBuffer();
//                str = isLeft ? sb.append("0").append(str).toString() : sb.append(str).append("0").toString();
//                strLen = str.length();
//            }
//        }
//        return str;
//    }
//
//    // 将 s 进行 BASE64 编码
//    public static String getBASE64(String s)
//    {
//        if (s == null)
//            return null;
//        return (new sun.misc.BASE64Encoder()).encode(s.getBytes());
//    }
//
//    // 将 BASE64 编码的字符串 s 进行解码
//    public static String getFromBASE64(String s)
//    {
//        if (s == null)
//            return null;
//        BASE64Decoder decoder = new BASE64Decoder();
//        try
//        {
//            byte[] b = decoder.decodeBuffer(s);
//            return new String(b);
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }
//
//    //方法名称：isSameWeek(String date1,String date2)
//    //功能描述：判断date1和date2是否在同一周
//    //输入参数：date1,date2
//    //输出参数：
//    //返 回 值：false 或 true
//    //其它说明：主要用到Calendar类中的一些方法
//    //-----------------------------
//    public static boolean isSameWeekOld(String date1, String date2)
//    {
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        Date d1 = null;
//        Date d2 = null;
//        try
//        {
//            d1 = format.parse(date1);
//            d2 = format.parse(date2);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        Calendar cal1 = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        cal1.setTime(d1);
//        cal2.setTime(d2);
//        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
//        //subYear==0,说明是同一年
//        if (subYear == 0)
//        {
//            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
//                return true;
//        }
//        //例子:cal1是"2005-1-1"，cal2是"2004-12-25"
//        //java对"2004-12-25"处理成第52周
//        // "2004-12-26"它处理成了第1周，和"2005-1-1"相同了
//        //大家可以查一下自己的日历
//        //处理的比较好
//        //说明:java的一月用"0"标识，那么12月用"11"
//        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11)
//        {
//            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
//                return true;
//        }
//        //例子:cal1是"2004-12-31"，cal2是"2005-1-1"
//        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)
//        {
//            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
//                return true;
//
//        }
//        return false;
//    }
//
//    /**
//     * 判断两个时间是否在同一周
//     *
//     * @param firstDate
//     * @param secondDate
//     * @return
//     * @throws ParseException
//     */
//    public static boolean isSameWeek(String dateStr1, String dateStr2, String format)
//    {
//        if (isNullOrZeroLenght(format))
//        {
//            format = GeneralConstant.DATETIME_14;
//        }
//        Date firstDate = string2Date(dateStr1, format);
//        Date secondDate = string2Date(dateStr2, format);
//
//        /** 以下先根据第一个时间找出所在周的星期一、星期日, 再对第二个时间进行比较 */
//        Calendar calendarMonday = Calendar.getInstance();
//        calendarMonday.setTime(firstDate);
//
//        // 获取firstDate在当前周的第几天. （星期一~星期日：1~7）
//        int monday = calendarMonday.get(Calendar.DAY_OF_WEEK);
//        if (monday == 0)
//        {
//            monday = 7;
//        }
//
//        // 星期一开始时间
//        calendarMonday.add(Calendar.DAY_OF_MONTH, -monday + 1);
//        calendarMonday.set(Calendar.HOUR, 0);
//        calendarMonday.set(Calendar.MINUTE, 0);
//        calendarMonday.set(Calendar.SECOND, 0);
//
//        // 星期日结束时间
//        Calendar calendarSunday = Calendar.getInstance();
//        calendarSunday.setTime(calendarMonday.getTime());
//        calendarSunday.add(Calendar.DAY_OF_MONTH, 6);
//        calendarSunday.set(Calendar.HOUR, 23);
//        calendarSunday.set(Calendar.MINUTE, 59);
//        calendarSunday.set(Calendar.SECOND, 59);
//
//        //        System.out.println("星期一开始时间：" + datetimeDf.format(calendarMonday.getTime()));
//        //        System.out.println("星期日结束时间：" + datetimeDf.format(calendarSunday.getTime()));
//
//        // 比较第二个时间是否与第一个时间在同一周
//        if (secondDate.getTime() >= calendarMonday.getTimeInMillis()
//                && secondDate.getTime() <= calendarSunday.getTimeInMillis())
//        {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 判断两个时间是否在同一天
//     *
//     * 时间格式为：yyyyMMddHHmmss
//     *
//     * @throws ParseException
//     */
//    public static boolean isSameDay(String dateStr1, String dateStr2)
//    {
//        // 比较第二个时间是否与第一个时间在同一天
//        if (GeneralUtils.isNotNullOrZeroLenght(dateStr1) && GeneralUtils.isNotNullOrZeroLenght(dateStr2)
//                && dateStr1.substring(0, 8).equals(dateStr2.substring(0, 8)))
//        {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 判断两个时间是否在同一月
//     *
//     * 时间格式为：yyyyMMddHHmmss
//     *
//     * @throws ParseException
//     */
//    public static boolean isSameMonth(String dateStr1, String dateStr2)
//    {
//        // 比较第二个时间是否与第一个时间在同一天
//        if (GeneralUtils.isNotNullOrZeroLenght(dateStr1) && GeneralUtils.isNotNullOrZeroLenght(dateStr2)
//                && dateStr1.substring(0, 6).equals(dateStr2.substring(0, 6)))
//        {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取时间差，单位分钟
//     *
//     * 时间格式为：yyyyMMddHHmmss
//     *
//     * @throws ParseException
//     */
//    public static long getTimeDifference(String dateStr1, String dateStr2)
//    {
//        long min = 60;
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        java.util.Date now = null;
//        java.util.Date date = null;
//        try
//        {
//            now = df.parse(dateStr1);
//            date = df.parse(dateStr2);
//            long l = now.getTime() - date.getTime();
//            long day = l / (24 * 60 * 60 * 1000);
//            long hour = (l / (60 * 60 * 1000) - day * 24);
//            min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
//            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//            min = day * 24 * 60 + hour * 60 + min;
//            //            System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
//        }
//        catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
//        //        System.out.println("min:" + min);
//        return min;
//    }
//
//    /**
//     * 获取时间差，单位秒
//     *
//     * 时间格式为：yyyyMMddHHmmss
//     *
//     * @throws ParseException
//     */
//    public static long getSTimeDifference(String dateStr1, String dateStr2)
//    {
//        long s = 60;
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        java.util.Date now = null;
//        java.util.Date date = null;
//        try
//        {
//            now = df.parse(dateStr1);
//            date = df.parse(dateStr2);
//            long l = now.getTime() - date.getTime();
//            long day = l / (24 * 60 * 60 * 1000);
//            long hour = (l / (60 * 60 * 1000) - day * 24);
//            long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
//            s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//            //            min = day * 24 * 60 + hour * 60 + min;
//            s = day * 24 * 60 * 60 + hour * 60 * 60 + min * 60 + s;
//            //            System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
//        }
//        catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
//        //        System.out.println("min:" + min);
//        return s;
//    }
//
//    /**
//     * 获取时间差，单位天
//     *
//     * 时间格式为：yyyyMMddHHmmss
//     *
//     * @throws ParseException
//     */
//    public static long getDayDifference(String dateStr1, String dateStr2)
//    {
//        long min = 60;
//        long day = 0;
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
//        java.util.Date now = null;
//        java.util.Date date = null;
//        try
//        {
//            now = df.parse(dateStr1);
//            date = df.parse(dateStr2);
//            long l = now.getTime() - date.getTime();
//            day = l / (24 * 60 * 60 * 1000);
//            long hour = (l / (60 * 60 * 1000) - day * 24);
//            min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
//            long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//            min = day * 24 * 60 + hour * 60 + min;
//            //            System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒");
//        }
//        catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
//        //        System.out.println("min:" + min);
//        return day;
//    }
//
//    private static final DateFormat datetimeDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    public static void main(String[] args)
//    {
//        // 定义两个时间
//        //            String dateStr1 = "2010-01-27 00:00:00";
//        //            String dateStr2 = "2010-01-30 23:59:59";
//
//        String dateStr1 = "20150528015525";
//        String dateStr2 = "20150528001300";
//        String sss="11";
////        getSTimeDifference(dateStr1, dateStr2);
////        getTimeDifference(dateStr1, dateStr2);
//
//        System.out.println(maxLength(sss, 2));
//
//        // 比较
//        //System.out.println(isSameWeek(dateStr1, dateStr2, GeneralConstant.DATETIME_14) ? "两个时间在同一周" : "两个时间不在同一周");
//
//        //测试1
////        boolean a = isSameWeekOld("2005-1-1", "2005-1-3");
////        if (a)
////        {
////            System.out.println("2005-1-1和2005-1-3是同一周！");
////        }
////        else
////        {
////            System.out.println("2005-1-1和2005-1-3不是同一周！");
////        }
////
////        //测试2
////        boolean b = isSameWeekOld("2005-1-1", "2004-12-25");
////        if (b)
////        {
////            System.out.println("2005-1-1和2004-12-25是同一周！");
////        }
////        else
////        {
////            System.out.println("2005-1-1和2004-12-25不是同一周！");
////        }
////
////        boolean c = isSameWeekOld("2004-12-25", "2005-1-1");
////        if (c)
////        {
////            System.out.println("2004-12-25和2005-1-1是同一周！");
////        }
////        else
////        {
////            System.out.println("2004-12-25和2005-1-1不是同一周！");
////        }
////
////        //测试3
////        boolean d = isSameWeekOld("2005-1-1", "2004-12-26");
////        if (d)
////        {
////            System.out.println("2005-1-1和2004-12-26是同一周！");
////        }
////        else
////        {
////            System.out.println("2005-1-1和2004-12-26不是同一周！");
////        }
////
////        boolean e = isSameWeekOld("2004-12-26", "2005-1-1");
////        if (e)
////        {
////            System.out.println("2004-12-26和2005-1-1是同一周！");
////        }
////        else
////        {
////            System.out.println("2004-12-26和2005-1-1不是同一周！");
////        }
////
////        System.out.println("*************************************");
////        //测试1
////        boolean a1 = isSameWeekOld("2015-1-1", "2015-1-3");
////        if (a1)
////        {
////            System.out.println("2015-1-1和2015-1-3是同一周！");
////        }
////        else
////        {
////            System.out.println("2015-1-1和2015-1-3不是同一周！");
////        }
////
////        //测试2
////        boolean b1 = isSameWeekOld("2015-1-1", "2014-12-25");
////        if (b1)
////        {
////            System.out.println("2015-1-1和2014-12-25是同一周！");
////        }
////        else
////        {
////            System.out.println("2015-1-1和2014-12-25不是同一周！");
////        }
////
////        boolean c1 = isSameWeekOld("2014-12-25", "2015-1-1");
////        if (c1)
////        {
////            System.out.println("2014-12-25和2015-1-1是同一周！");
////        }
////        else
////        {
////            System.out.println("2014-12-25和2015-1-1不是同一周！");
////        }
////
////        //测试3
////        boolean d1 = isSameWeekOld("2015-1-1", "2014-12-26");
////        if (d1)
////        {
////            System.out.println("2015-1-1和2014-12-26是同一周！");
////        }
////        else
////        {
////            System.out.println("2015-1-1和2014-12-26不是同一周！");
////        }
////
////        boolean e1 = isSameWeekOld("2014-12-26", "2015-1-1");
////        if (e1)
////        {
////            System.out.println("2014-12-26和2015-1-1是同一周！");
////        }
////        else
////        {
////            System.out.println("2014-12-26和2015-1-1不是同一周！");
////        }
//
//    }
//
//}