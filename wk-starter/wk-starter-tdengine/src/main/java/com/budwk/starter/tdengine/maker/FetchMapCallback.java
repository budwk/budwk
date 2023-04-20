package com.budwk.starter.tdengine.maker;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.NutMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchMapCallback implements SqlCallback {
    @Override
    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        if (null != rs && rs.next()) {
            NutMap re = new NutMap();
            ResultMapBuilder.create(re, rs, null);
            return re;
        }
        return null;
    }


}
