package com.budwk.starter.common.page;

import org.nutz.dao.pager.Pager;

import java.io.Serializable;

/**
 * @author wizzer@qq.com
 */
public class OffsetPager extends Pager implements Serializable {

    private static final long serialVersionUID = -1385308131663113162L;

    protected int offset = -1;

    protected OffsetPager() {
    }

    /**
     * 构建一个指定偏移量及大小的Pager
     *
     * @param offset 偏移量
     * @param size   数据大小
     */
    public OffsetPager(int offset, int size) {
        super();
        this.offset = offset;
        setPageSize(size);
    }

    /**
     * 覆盖超类的计算得到的offset
     */
    @Override
    public int getOffset() {
        if (offset > -1) {
            return offset;
        }
        return super.getOffset();
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}