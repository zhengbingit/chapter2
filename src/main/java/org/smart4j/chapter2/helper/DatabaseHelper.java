package org.smart4j.chapter2.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.util.CollectionUtil;
import org.smart4j.chapter2.util.PropsUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhengbinMac on 2017/3/25.
 */
public class DatabaseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

    // 线程连接
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL;
    // DbUtils
    private static final QueryRunner QUERY_RUNNER;
    // 线程池
    private static final BasicDataSource DATA_SOURCE;

    static {
        CONNECTION_THREAD_LOCAL = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();

        Properties conf = PropsUtil.loadProps("config.properties");
        // 设置连接池，使用 DBCP 来获取数据库连接
        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(conf.getProperty("jdbc.driver"));
        DATA_SOURCE.setUrl(conf.getProperty("jdbc.url"));
        DATA_SOURCE.setUsername(conf.getProperty("jdbc.username"));
        DATA_SOURCE.setPassword(conf.getProperty("jdbc.password"));
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        // 首先从 ThreadLocal 中获取
        Connection connection = CONNECTION_THREAD_LOCAL.get();
        // 若不存在，则创建一个新的 Connection，并最终将其放入 ThreadLocal 中
        if (connection == null) {
            try {
                connection = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
            } finally {
                CONNECTION_THREAD_LOCAL.set(connection);
            }
        }
        return connection;
    }

    /**
     * 批量查询实体
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList = null;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure", e);
        }
        return entityList;
    }

    /**
     * 查询单个实体
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity = null;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            e.printStackTrace();
        }
        return entity;
    }

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result = null;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行更新语句（update、insert 和 delete）
     */
    public static int executeUpdate(String sql, Object... params) {
        int updateRows = 0;
        try {
            Connection conn = getConnection();
            updateRows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            e.printStackTrace();
        }
        return updateRows;
    }

    /**
     * 插入实体
     * @param entityClass
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }
        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder colums = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");

        // 插入实体的字段名，和字段值的占位符
        for (String colum : fieldMap.keySet()) {
            colums.append(colum).append(", ");
            values.append("?, ");
        }
        colums.replace(colums.lastIndexOf(", "), colums.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += colums + " VALUES " + values;

        // 插入实体的值
        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     * @param entityClass
     * @param id
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionUtil.isEmpty(fieldMap)) {
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }
        // 更具 fieldMap 拼接出更新 SQL 语句
        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        // 更新实体的字段
        for (String colums : fieldMap.keySet()) {
            columns.append(columns).append("=?, ");
        }

        // 去掉 SQL 最后一个 ', '
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id=?";

        // 更新实体的值
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id); // 增加主键 id
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 删除实体
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELTE FROM " + getTableName(entityClass) + "WHERE id=?";
        return executeUpdate(sql, id) == 1;
    }

    /**
     * 获取操作表的表名，即实体的类名
     */
    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }
}
