package com.budwk.starter.common.page;

/**
 * @author wizzer@qq.com
 */
public interface Paginable {

	/**
	 * 总记录数
	 * 
	 * @return 总记录数
	 */
	public int getTotalCount();

	/**
	 * 总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPage();

	/**
	 * 每页记录数
	 * 
	 * @return 每页记录数
	 */
	public int getPageSize();

	/**
	 * 当前页号
	 * 
	 * @return 当前页号
	 */
	public int getPageNo();

	/**
	 * 是否第一页
	 * 
	 * @return 是否第一页
	 */
	public boolean isFirstPage();

	/**
	 * 是否最后一页
	 * 
	 * @return 是否最后一页
	 */
	public boolean isLastPage();

	/**
	 * 返回下页的页号
	 * @return 返回下页的页号
	 */
	public int getNextPage();

	/**
	 * 返回上页的页号
	 * @return 返回上页的页号
	 */
	public int getPrePage();

}
