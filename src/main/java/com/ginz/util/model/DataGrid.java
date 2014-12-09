package com.ginz.util.model;

import java.util.List;

/**
 * @author 孙宇
 */
public class DataGrid implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long total;// 总记录数
	private List rows;// 每行记录
	private List footer;
	private List groups;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List rows) {
		this.rows = rows;
	}

	public List getFooter() {
		return footer;
	}

	public void setFooter(List footer) {
		this.footer = footer;
	}

	public List getGroups() {
		return groups;
	}

	public void setGroups(List groups) {
		this.groups = groups;
	}
	
}
