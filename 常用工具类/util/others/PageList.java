package com.wondertek.mam.util.others;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PageList extends AbstractList  {

	private static final int DEFAULT_PAGE_SIZE = 10;

	private boolean init = false;

	private List list = new ArrayList(DEFAULT_PAGE_SIZE);

	private int pageCount;

	private int pageIndex = 1;

	private int pageSize = DEFAULT_PAGE_SIZE;

	private int recordCount;

	private int startRow = 0;
	
	private int start;
	
	private int limit;


	public int getStart() {
		return start;
	}


	public void setStart(int start) {
		this.start = start;
	}


	public int getLimit() {
		return limit;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}


	public boolean setList(List c) {
		list.clear();
		return list.addAll(c);
	}


	@Override
	public Object get(int index) {
		return list.get(index);
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public int getStartRow() {
		if (!init) {
			initialize();
		}
		return startRow;
	}

	public void initialize() {
		if (recordCount % pageSize == 0) {
			pageCount = recordCount / pageSize;
		} else {
			pageCount = recordCount / pageSize + 1;
		}
		if (pageIndex > pageCount) {
			pageIndex = pageCount;
			startRow = (pageIndex - 1) * pageSize;
		} else if (pageIndex < 1) {
			pageIndex = 0;
			startRow = 0;
		}
		start = pageSize*(pageIndex - 1);
		//limit = pageSize*(pageIndex - 1) + pageSize;
		init = true;
	}

	@Override
	public Iterator iterator() {
		return list.iterator();
	}

	public void setPageIndex(int pageNumber) {
		pageIndex = pageNumber;
	}

	public void setPageSize(int newPageSize) {
		if (newPageSize < 1) {
			pageSize = 10;
		} else {
			pageSize = newPageSize;
		}
	}

	public void setRecordCount(int newRecordCount) {
		recordCount = newRecordCount;
	}

	@Override
	public int size() {
		return list.size();
	}

	/**
	 * @return Returns the list.
	 */
	public List getList() {
		return list;
	}
}
