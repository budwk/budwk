package com.budwk.app.sys.commons.task;

import org.nutz.boot.AppContext;
import org.nutz.dao.impl.NutDao;
import org.nutz.ioc.Ioc;
import org.quartz.utils.ConnectionProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author wizzer@qq.com
 */
public class NutConnectionProvider implements ConnectionProvider {

    protected DataSource dataSource;

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            Ioc ioc = AppContext.getDefault().getIoc();
            dataSource = ioc.get(NutDao.class, "dao").getDataSource();
        }
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {

    }

    @Override
    public void initialize() throws SQLException {
        // 初始化获取不到dao对象，改在 getConnection() 里获取数据源
    }


}