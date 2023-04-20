package com.budwk.starter.tdengine.maker;

import org.nutz.dao.DaoException;
import org.nutz.dao.impl.jdbc.BlobValueAdaptor;
import org.nutz.dao.jdbc.Jdbcs;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

public class ResultMapBuilder {

    public static void create(Map<String, Object> re, ResultSet rs, ResultSetMetaData meta){
        String name = null;
        int i = 0;
        try {
            if (meta == null)
                meta = rs.getMetaData();
            int count = meta.getColumnCount();
            for (i = 1; i <= count; i++) {
                name = meta.getColumnLabel(i);
                switch (meta.getColumnType(i)) {
                    case Types.TIMESTAMP: {
                        re.put(name, rs.getLong(i));
                        break;
                    }
                    case Types.DATE: {
                        re.put(name, rs.getTimestamp(i));
                        break;
                    }
                    case Types.CLOB: {
                        re.put(name, rs.getString(i));
                        break;
                    }
                    case Types.BLOB: {
                        re.put(name, new BlobValueAdaptor(Jdbcs.getFilePool()).get(rs, name));
                        break;
                    }
                    case Types.BINARY:
                        re.put(name, rs.getObject(i) == null ? rs.getObject(i) : new String((byte[]) rs.getObject(i), StandardCharsets.UTF_8));
                        break;
                    default:
                        re.put(name, rs.getObject(i));
                        break;
                }
            }
        } catch (SQLException e) {
            if (name != null) {
                throw new DaoException(String.format("Column Name=%s, index=%d", name, i), e);
            }
            throw new DaoException(e);
        }
    }
}
