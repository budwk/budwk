package com.budwk.starter.common.page;

import com.budwk.starter.common.openapi.annotation.ApiModel;
import com.budwk.starter.common.openapi.annotation.ApiModelProperty;
import org.nutz.json.JsonField;
import org.nutz.lang.Lang;

import java.util.List;

/**
 * @author wizzer@qq.com
 */
@ApiModel(description = "分页数据")
public class Pagination extends SimplePage implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @JsonField(ignore = true)
    public Class<?> aClass;

    public Pagination() {
    }

    /**
     * 构造器
     *
     * @param pageNo     页码
     * @param pageSize   每页几条数据
     * @param totalCount 总共几条数据
     */
    public Pagination(int pageNo, int pageSize, int totalCount) {
        super(pageNo, pageSize, totalCount);
    }

    /**
     * 构造器
     *
     * @param pageNo     页码
     * @param pageSize   每页几条数据
     * @param totalCount 总共几条数据
     * @param list       分页内容
     */
    public Pagination(int pageNo, int pageSize, int totalCount, List<?> list) {
        super(pageNo, pageSize, totalCount);
        this.list = list;
    }

    /**
     * 构造器
     *
     * @param pageNo     页码
     * @param pageSize   每页几条数据
     * @param totalCount 总共几条数据
     * @param list       分页内容
     * @param list       实体类
     */
    public Pagination(int pageNo, int pageSize, int totalCount, List<?> list, Class<?> tClass) {
        super(pageNo, pageSize, totalCount);
        this.list = list;
        this.aClass = tClass;
    }

    /**
     * 第一条数据位置
     *
     * @return int
     */
    public int getFirstResult() {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 当前页的数据
     */
    @ApiModelProperty(description = "列表数据")
    public List<?> list;

    /**
     * 获得分页内容
     *
     * @return List
     */
    public <T> List<?> getList() {
        return list;
    }

    /**
     * @param classOfT 列表容器內的元素类型
     * @param <T>      列表容器內的元素类型
     * @return List<T>
     */
    public <T> List<T> getList(Class<T> classOfT) {
        return Lang.collection2list(list, classOfT);
    }


    /**
     * 设置分页内容
     *
     * @param list 列表对象
     */
    public void setList(List<?> list) {
        this.list = list;
    }
}
