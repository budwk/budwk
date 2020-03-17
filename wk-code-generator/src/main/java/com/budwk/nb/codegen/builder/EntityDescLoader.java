package com.budwk.nb.codegen.builder;

import org.nutz.dao.entity.annotation.*;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer(wizzer.cn)
 * @date 2020/3/11
 */
public class EntityDescLoader extends Loader {

    private static final Log log = Logs.get();

    @Override
    public Map<String, TableDescriptor> loadTables(Ioc ioc, String basePackageName, String basePath, String baseUri, String servPackageName, String modPackageName, List<String> needTables, String tableNamePrefix) throws Exception {
        Map<String, TableDescriptor> tables = new HashMap<String, TableDescriptor>();
        List<Class<?>> ks = Scans.me().scanPackage(modPackageName);
        for (Class thatClass : ks) {
            Mirror<?> mirror = Mirror.me(thatClass);
            Table tableAnno = mirror.getAnnotation(Table.class);
            if (tableAnno == null) {
                continue;
            }
            String tableName = tableAnno.value();
            if (tableNamePrefix.endsWith("*")) {
                if (!tableName.toLowerCase().startsWith(tableNamePrefix.substring(0, tableNamePrefix.length() - 1))) {
                    continue;
                }
            } else if (!needTables.contains(tableName.toLowerCase())) {
                continue;
            }
            String entityName = thatClass.getSimpleName();
            TableDescriptor table = new TableDescriptor(tableName, entityName, basePackageName, baseUri, servPackageName, modPackageName);

            Comment comment = mirror.getAnnotation(Comment.class);
            if (comment != null) {
                table.setLabel(comment.value());
            } else {
                table.setLabel(entityName);
            }

            tables.put(tableName.toLowerCase(), table);
            tables.put(entityName.toLowerCase(), table);
            Field[] fields = mirror.getFields();
            for (Field field : fields) {
                ColumnDescriptor column = new ColumnDescriptor();
                String fieldName = field.getName();
                if (fieldName.equals("createdAt") || fieldName.equals("createdBy") || fieldName.equals("updatedAt") || fieldName.equals("updatedBy") || fieldName.equals("delFlag")) {
                    continue;
                }
                column.setFieldName(fieldName);
                Annotation[] annotations = field.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Comment) {
                        column.setLabel(((Comment) annotation).value());
                        column.setComment(((Comment) annotation).value());
                    }
                    if (annotation instanceof Id || annotation instanceof Name) {
                        column.primary = true;
                        table.setPkType(column.getSimpleJavaTypeName());
                        column.columnName = fieldName;
                    }
                    if (annotation instanceof ColDefine) {
                        ColType colType = ((ColDefine) annotation).type();
                        column.setColumnType(colType.name());
                        column.dataType = colType.name();
                    }
                    if (annotation instanceof Column) {
                        String columnName = ((Column) annotation).value();
                        if (Strings.isBlank(columnName)) {
                            column.columnName = fieldName;
                        } else {
                            column.columnName = columnName;
                        }
                    }


                }
                if (Strings.isEmpty(column.getLabel())) {
                    column.setLabel(fieldName);
                }
                table.addColumn(column);
            }


        }
        return tables;
    }
}
