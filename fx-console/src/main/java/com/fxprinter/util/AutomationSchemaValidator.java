package com.fxprinter.util;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 自动化工具
 * @author dongyu
 * @version 1.0
 * @date 2025-07-16 16:39:50
 * @since jdk1.8
 */
public class AutomationSchemaValidator {

    private final JdbcTemplate jdbcTemplate;

    public AutomationSchemaValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void registerEntity(Collection<Class<?>> entityClasses) {
        for (Class<?> ac : entityClasses) {
            //获取字段
            List<FieldMeta> fields = new ArrayList<>();
            for (Field field : ac.getDeclaredFields()) {
                boolean fieldIsExist = field.isAnnotationPresent(TableField.class);
                boolean idIsExist = field.isAnnotationPresent(TableId.class);
                if (!fieldIsExist && !idIsExist) {
                    continue;
                }
                StringBuilder dataStr = new StringBuilder();
                //获取类型
                String columnName;
                String columnType = javaToH2Type(field.getType());
                dataStr.append(columnType).append(" ");
                if (idIsExist) {
                    TableId tableId = field.getAnnotation(TableId.class);
                    IdType idType = tableId.type();
                    dataStr.append(" PRIMARY KEY ");
                    if (IdType.AUTO.equals(idType)) {
                        dataStr.append("AUTOINCREMENT").append(" ");
                    }
                    columnName = tableId.value().toUpperCase();
                }else {
                    columnName = field.getAnnotation(TableField.class).value().toUpperCase();
                }
                fields.add(new FieldMeta(columnName, columnType,  dataStr.toString()));

            }
            registerEntity(ac, fields);
        }
    }

    private String javaToH2Type(Class<?> type) {
        if (type == String.class) return "VARCHAR(500)";
        if (type == Long.class || type == long.class) return "BIGINT";
        if (type == Integer.class || type == int.class) return "INTEGER";
        if (type == Boolean.class || type == boolean.class) return "BOOLEAN";
        if (type == Date.class || type == java.sql.Timestamp.class) return "TIMESTAMP";
        if (type == BigDecimal.class) return "DECIMAL(19,4)";
        return "VARCHAR(500)"; // 默认类型
    }

    // 注册实体类字段元数据
    public void registerEntity(Class<?> entityClass, List<FieldMeta> fields) {
        processEntity(entityClass, fields);
    }


    private void processEntity(Class<?> entity, List<FieldMeta> fields) {
        String tableName = resolveTableName(entity);
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()){
            // 检查表是否存在
            if (!isTableExists(tableName, connection)) {
                createTable(entity, fields);
                return;
            }
            // 检查字段
            Map<String, Integer> dbColumns = getTableColumns(tableName, connection);
            for (FieldMeta field : fields) {
                Integer dbType = dbColumns.get(field.columnName.toUpperCase());

                if (dbType == null) {
                    // 字段不存在 - 添加字段
                    addColumn(tableName, field);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }

    private boolean isTableExists(String tableName, Connection connection) throws Exception{
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet tables = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        }
    }

    private Map<String, Integer> getTableColumns(String tableName, Connection connection) throws Exception {
        Map<String, Integer> columns = new HashMap<>();
        DatabaseMetaData meta = connection.getMetaData();

        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME").toUpperCase();
                int dataType = rs.getInt("DATA_TYPE");
                columns.put(colName, dataType);
            }
        }
        return columns;
    }

    private void createTable(Class<?> entity, List<FieldMeta> fields) {
        String tableName = resolveTableName(entity);
        StringBuilder sql = new StringBuilder("CREATE TABLE ")
                .append(tableName)
                .append(" (");

        for (FieldMeta field : fields) {
            sql.append(field.columnName)
                    .append(" ")
                    .append(field.sqlStatement)
                    .append(", ");
        }

        // 移除末尾逗号
        sql.setLength(sql.length() - 2);
        sql.append(")");
        jdbcTemplate.execute(sql.toString());
    }

    private void addColumn(String tableName, FieldMeta field) {
        jdbcTemplate.execute("ALTER TABLE " + tableName +
                " ADD COLUMN " + field.columnName + " " + field.sqlStatement);
    }

    private String resolveTableName(Class<?> entity) {
        return entity.getAnnotation(TableName.class).value();
    }

    // 字段元数据内部类
    public record FieldMeta(String columnName, String jdbcType, String sqlStatement) {
    }
}
