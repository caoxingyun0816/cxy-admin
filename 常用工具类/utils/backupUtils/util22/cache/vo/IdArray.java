package com.wondertek.mam.util.backupUtils.util22.cache.vo;

import java.io.Serializable;
import java.util.Arrays;

public class IdArray implements Serializable {

	private static final long serialVersionUID = 1L;
	public Long[] ids;

	public IdArray() {
	}

	public IdArray(Long id1) {
		this.ids = new Long[] { id1 };
	}

	public IdArray(Long id1, Long id2) {
		this.ids = new Long[] { id1, id2 };
	}

	public IdArray(Long id1, Long id2, Long id3) {
		this.ids = new Long[] { id1, id2, id3 };
	}

	public IdArray(Long[] ids) {
		this.ids = ids;
	}

	public Long[] getIds() {
		return ids;
	}

	public void setIds(Long[] ids) {
		this.ids = ids;
	}

	@Override
	public String toString() {
		String res ;
		if (ids != null) {
			res = "[";
			for (Long id : ids) {
				res += id + ",";
			}
			res += "]";
		} else {
			res = "NULL";
		}
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(ids);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdArray other = (IdArray) obj;
		if (!Arrays.equals(ids, other.ids))
			return false;
		return true;
	}

}
