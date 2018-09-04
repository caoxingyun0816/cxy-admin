package com.wondertek.mam.util.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



/**
 * 动态解析excel方法类(兼容excel2003与excel2007）
 * @author gy
 */

public class ExcelParseTool {

	private String filePath;
	private File file;
	private int index;
	private Workbook workBook;
	private Sheet sheet;
	private List<String> columnHeaderList;
	private List<List<String>> listData;
	private List<Map<String, String>> mapData;
	private boolean flag;
    private int rowNum;
    private int cellNum;
    private int firstCellNum;
	public int getCellNum() {
		return cellNum;
	}
	public int getFirstCellNum() {
		return firstCellNum;
	}

	public ExcelParseTool(String filePath, int sheetIndex) {
		super();
		this.filePath = filePath;
		this.index = sheetIndex;
		this.flag = false;
		this.load(false);
	}

	public ExcelParseTool(File file, int sheetIndex) {
		super();
		this.file = file;
		this.index = sheetIndex;
		this.flag = false;
		this.load(true);
	}

	private void load(boolean isFile) {
		FileInputStream inStream = null;
		try {

			inStream = (isFile ?  new FileInputStream(file): new FileInputStream(new File(filePath)));
			workBook = WorkbookFactory.create(inStream);
			sheet = workBook.getSheetAt(index);
			rowNum=sheet.getLastRowNum();
			Row firstRow = sheet.getRow(0);
			if(firstRow != null) {
				firstCellNum = firstRow.getLastCellNum();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getRowNum() {
		return rowNum;
	}

	@SuppressWarnings("deprecation")
	private String getCellValue(Cell cell) {
		String cellValue = "";
		// DataFormatter formatter = new DataFormatter();
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:// 读取的格式为字符串
				cellValue = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC:// 读取的格式为数组
				// 如果格式为日期格式，自定义格式输出
				if (DateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					if (date != null) {
						cellValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
					} else {
						cellValue = "";
					}
				} else {
					// 如果格式为数值，自定义格式输出
					cellValue = new DecimalFormat().format(cell.getNumericCellValue());
					if (cellValue.contains(",")) {
						cellValue = cellValue.replace(",", "");
					}
				}
				break;
			case Cell.CELL_TYPE_FORMULA:
				// 导入时如果为公式生成的数据则无值
				cellValue = "";
				break;
			// 导入时如果为空
			case Cell.CELL_TYPE_BLANK:
				break;
			case Cell.CELL_TYPE_ERROR:
				cellValue = "";
				break;
			// 导入时如果为BOOLEAN型 自定义格式输出
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = (cell.getBooleanCellValue() == true ? "Y" : "N");
				break;
			default:
				cellValue = "";
			}
		}
		return cellValue.trim();
	}

	private void getSheetData() {
		listData = new ArrayList<List<String>>();
		mapData = new ArrayList<Map<String, String>>();
		columnHeaderList = new ArrayList<String>();
		int numOfRows = sheet.getLastRowNum() + 1;
		for (int i = 0; i < numOfRows; i++) {
			Row row = sheet.getRow(i);
			Map<String, String> map = new HashMap<String, String>();
			List<String> list = new ArrayList<String>();
			if (row != null) {
				for (int j = 0; j < (cellNum=row.getLastCellNum()); j++) {
					Cell cell = row.getCell(j);
					if (i == 0) {
						columnHeaderList.add(getCellValue(cell));
					} else if(columnHeaderList.size() > j){
						map.put(columnHeaderList.get(j), this.getCellValue(cell));
					}
					list.add(this.getCellValue(cell));
				}
			}
			if (i > 0) {
				mapData.add(map);
			}
			listData.add(list);

		}
		flag = true;
	}

	//
	public String getCellData(int row, int col) {
		if (row <= 0 || col <= 0) {
			return null;
		}
		if (!flag) {
			this.getSheetData();
		}
		if (listData.size() >= row && listData.get(row - 1).size() >= col) {
			return listData.get(row - 1).get(col - 1);
		} else {
			return null;
		}
	}

	public String getCellData(int row, String headerName) {
		if (row <= 0) {
			return null;
		}
		if (!flag) {
			this.getSheetData();
		}
		if (mapData.size() >= row && mapData.get(row - 1).containsKey(headerName)) {
			return mapData.get(row - 1).get(headerName);
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		ExcelParseTool eh = new ExcelParseTool(new File("D:\\tpl_pldr.xlsm"), 0);
		ExcelParseTool eh1 = new ExcelParseTool("D:\\201801183(1)+-+副本 (1).xls", 3);
		System.out.println(eh1.getRowNum());
		// ExcelParseTool eh1 = new ExcelParseTool("D:\\201801183(1)+-+副本
		// (1).xls", 3);
		// System.out.println(eh.hashCode()==eh1.hashCode());
		// System.out.println(eh.getCellData(1, 1));
		// System.out.println(eh.getCellData(1, "test1"));
	/*	
		String cellResult = null;
		
	    // List list = new LinkedList<>();
		for(int i=2;i<eh1.getRowNum()+1;i++){
			int Count = 1;
		while ((cellResult = (eh1.getCellData(i, Count))) != null) {
			if(cellResult.trim().length()!=0){
			System.out.println(cellResult);
			}
			//list.add(cellResult);
			Count++;

		
		}	
		//System.out.println(list.size());

	}*/
		
}
}