package com.wondertek.mam.util.superutil.thirdparty.excel;

import com.wondertek.mam.util.superutil.j2se.ReflectUtils;
import com.wondertek.mobilevideo.core.util.StringUtil;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 根据Bean上的Excel注解写入到Excel
 * @author Goofy 2015/9/16 ReWrite
 */
public class BeanToExcel {

	private static final Logger log = Logger.getLogger(BeanToExcel.class);
	/**
	 * 获得Workbook对象
	 * 
	 * @param list
	 *            数据集合
	 * @return Workbook
	 * @throws Exception
	 */
	public static <T> Workbook getWorkBook(List<T> list, ExcelDataFormatter edf) throws Exception {
		// 创建工作簿
		Workbook wb = new SXSSFWorkbook();
		if (list == null || list.size() == 0)
			return wb;
		// 创建一个工作表sheet
		Sheet sheet = wb.createSheet();
		// 申明行
		Row row = sheet.createRow(0);
		// 申明单元格
		Cell cell = null;
		CreationHelper createHelper = wb.getCreationHelper();
		Field[] fields = ReflectUtils.getClassFieldsAndSuperClassFields(list.get(0).getClass());
		XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
		titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// 设置前景色
		titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(159, 213, 183)));
		titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
		Font font = wb.createFont();
		font.setColor(HSSFColor.BROWN.index);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// 设置字体
		titleStyle.setFont(font);
		int columnIndex = 0;
		Excel excel = null;
		for (Field field : fields) {
			field.setAccessible(true);
			excel = field.getAnnotation(Excel.class);
			if (excel == null || excel.skip() == true) {
				continue;
			}
			// 列宽注意乘256
			sheet.setColumnWidth(columnIndex, excel.width() * 256);
			// 写入标题
			cell = row.createCell(columnIndex);
			cell.setCellStyle(titleStyle);
			cell.setCellValue(excel.name());
			columnIndex++;
		}
		int rowIndex = 1;
		CellStyle cs = wb.createCellStyle();
		for (T t : list) {
			row = sheet.createRow(rowIndex);
			columnIndex = 0;
			Object o = null;
			for (Field field : fields) {
				field.setAccessible(true);
				// 忽略标记skip的字段
				excel = field.getAnnotation(Excel.class);
				if (excel == null || excel.skip() == true) {
					continue;
				}
				// 数据
				cell = row.createCell(columnIndex);
				o = field.get(t);
				// 如果数据为空，跳过
				if (o == null){
					columnIndex++;
					continue;
				}else if (o instanceof Date) {
					// 处理日期类型
					// excel.dateFormat()获取注解的日期格式，默认yyyy-MM-dd HH:mm:ss
					cs.setDataFormat(createHelper.createDataFormat().getFormat(excel.dateFormat()));
					cell.setCellStyle(cs);
					cell.setCellValue((Date) field.get(t));
				} else if (o instanceof Double || o instanceof Float) { // 浮点数
					cell.setCellValue(field.get(t).toString());
					if (excel.precision() != -1) {
						cell.setCellValue(new BigDecimal(field.get(t).toString()).setScale(excel.precision(), excel.round() == true ? BigDecimal.ROUND_HALF_UP : BigDecimal.ROUND_FLOOR).toString());
					}
				} else if (o instanceof BigDecimal) { // BigDecimal
					cell.setCellValue((field.get(t).toString()));
					if (excel.precision() != -1) {
						cell.setCellValue(new BigDecimal(field.get(t).toString()).setScale(excel.precision(), excel.round() == true ? BigDecimal.ROUND_HALF_UP : BigDecimal.ROUND_FLOOR).toString());
					}
				} else if (o instanceof Boolean) { // 布尔类型
					Boolean bool = (Boolean) field.get(t);
					if (edf == null) {
						cell.setCellValue(bool);
					} else {
						Map<String, String> map = edf.get(field.getName());
						if (map == null || map.size() == 0) {
							cell.setCellValue(bool);
						} else {
							cell.setCellValue(map.get(bool.toString().toLowerCase()));
						}
					}
				} else if (o instanceof Integer) { // 整型
					Integer intValue = (Integer) field.get(t);
					if (edf == null) {
						cell.setCellValue(intValue);
					} else {
						Map<String, String> map = edf.get(field.getName());
						if (map == null || map.size() == 0) {
							cell.setCellValue(intValue);
						} else {
							cell.setCellValue(map.get(intValue.toString()));
						}
					}
				} else if (o instanceof Long) { // Long
					Long intValue = (Long) field.get(t);
					if (edf == null) {
						cell.setCellValue(intValue);
					} else {
						Map<String, String> map = edf.get(field.getName());
						if (map == null || map.size() == 0) {
							cell.setCellValue(intValue);
						} else {
							cell.setCellValue(map.get(intValue.toString()));
						}
					}
				} else if (o instanceof String) { // Long
					String intValue = (String) field.get(t);
					if (edf == null) {
						cell.setCellValue(intValue);
					} else {
						Map<String, String> map = edf.get(field.getName());
						if (map == null || map.size() == 0) {
							cell.setCellValue(intValue);
						} else {
							cell.setCellValue(map.get(intValue.toString()));
						}
					}
				} else {
					cell.setCellValue(field.get(t).toString());
				}
				columnIndex++;
			}
			rowIndex++;
		}
		return wb;
	}

	/**
	 * 将数据写入到EXCEL文档
	 * 
	 * @param list
	 *            数据集合
	 * @param edf
	 *            数据格式化，比如有些数字代表的状态，像是0:女，1：男，或者0：正常，1：锁定，变成可读的文字
	 *            该字段仅仅针对Boolean,Integer两种类型作处理
	 * @param filePath
	 *            文件路径
	 * @throws Exception
	 */
	public static <T> void writeToFile(List<T> list, ExcelDataFormatter edf, String filePath) throws Exception {
		// 创建并获取工作簿对象
		Workbook wb = getWorkBook(list, edf);
		// 写入到文件
		FileOutputStream out = new FileOutputStream(filePath);
		wb.write(out);
		out.close();
	}
}
