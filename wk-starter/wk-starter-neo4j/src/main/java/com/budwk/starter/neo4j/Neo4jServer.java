package com.budwk.starter.neo4j;

import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wizzer.cn
 */
@IocBean(create = "init", depose = "close")
@Slf4j
public class Neo4jServer {
    @Inject
    private PropertiesProxy conf;

    public static final String PRE = "neo4j.";

    @PropDoc(value = "Neo4j uri", defaultValue = "")
    public static final String PROP_URL = PRE + "uri";

    @PropDoc(value = "Neo4j 用户名", defaultValue = "")
    public static final String PROP_USERNAME = PRE + "username";

    @PropDoc(value = "Neo4j 密码", defaultValue = "")
    public static final String PROP_PASSWORD = PRE + "password";

    private Driver driver;

    public void init() {
        driver = GraphDatabase.driver(conf.get(PROP_URL), AuthTokens.basic(conf.get(PROP_USERNAME), conf.get(PROP_PASSWORD)));
    }

    /**
     * 执行查询
     * String query = "MATCH (n:Person) RETURN n.name AS name, n.age AS age";
     *
     * @param query      查询语句
     * @param parameters 传递参数
     * @return
     */
    public List<Map<String, Object>> executeReadQuery(String query, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(query, parameters);
                List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> recordMap = record.asMap();
                    resultList.add(recordMap);
                }
                return resultList;
            });
        } catch (Exception e) {
            log.error("Failed to execute query: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 执行更新
     *
     * @param query      查询语句
     * @param parameters 传递参数
     * @return
     */
    public List<Map<String, Object>> executeWriteQuery(String query, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                Result result = tx.run(query, parameters);
                List<Map<String, Object>> resultList = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> recordMap = record.asMap();
                    resultList.add(recordMap);
                }
                return resultList;
            });
        } catch (Exception e) {
            log.error("Failed to execute query: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 创建节点
     *
     * @param label      标签
     * @param properties 属性
     */
    public String createNode(String label, Map<String, Object> properties) {
        StringBuilder query = new StringBuilder("CREATE (n:");
        query.append(label);
        query.append(" {");
        int i = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            query.append(entry.getKey());
            query.append(": $");
            query.append(entry.getKey());
            if (i < properties.size() - 1) {
                query.append(", ");
            }
            i++;
        }
        query.append("})");

        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                Result result = tx.run(query.toString(), properties);
                return result.single().get("nodeId").asString();
            });
        } catch (Exception e) {
            log.error("Failed to execute query: " + e.getMessage());
            return null;
        }
    }

    /**
     * 更新节点
     *
     * @param label      标签
     * @param id         主键
     * @param properties 属性
     */
    public void updateNode(String label, String id, Map<String, Object> properties) {
        StringBuilder query = new StringBuilder("MATCH (n:");
        query.append(label);
        query.append(" {id: $id}) SET ");
        int i = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            query.append("n.");
            query.append(entry.getKey());
            query.append(" = $");
            query.append(entry.getKey());
            if (i < properties.size() - 1) {
                query.append(", ");
            }
            i++;
        }

        Map<String, Object> parameters = new HashMap<>(properties);
        parameters.put("id", id);

        executeWriteQuery(query.toString(), parameters);
    }

    /**
     * 删除节点
     *
     * @param label 标签
     * @param id    主键
     */
    public void deleteNode(String label, String id) {
        String query = "MATCH (n:" + label + " {id: $id}) DETACH DELETE n";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        executeWriteQuery(query, parameters);
    }

    /**
     * 通过属性查询结果
     *
     * @param label      标签
     * @param properties 属性
     * @return
     */
    public List<Map<String, Object>> queryNodesByProperties(String label, Map<String, Object> properties) {
        StringBuilder query = new StringBuilder("MATCH (n:");
        query.append(label);
        query.append(") WHERE ");
        int i = 0;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            query.append("n.");
            query.append(entry.getKey());
            query.append(" = $");
            query.append(entry.getKey());
            if (i < properties.size() - 1) {
                query.append(" AND ");
            }
            i++;
        }
        query.append(" RETURN n");

        return executeReadQuery(query.toString(), properties);
    }

    /**
     * 创建标签关系
     *
     * @param sourceLabel            源节点的标签
     * @param relationshipType       关系类型
     * @param targetLabel            目标节点的标签
     * @param relationshipProperties 关系的属性，一个包含属性键值对的映射。如果不需要属性，可以将其设置为null。
     */
    public void createRelationshipByLabel(String sourceLabel, String relationshipType, String targetLabel, Map<String, Object> relationshipProperties) {
        StringBuilder query = new StringBuilder("MATCH (source:");
        query.append(sourceLabel);
        query.append("), (target:");
        query.append(targetLabel);
        query.append(") CREATE (source)-[rel:");
        query.append(relationshipType);
        query.append(" ");
        if (relationshipProperties != null && !relationshipProperties.isEmpty()) {
            query.append("{");
            int i = 0;
            for (Map.Entry<String, Object> entry : relationshipProperties.entrySet()) {
                query.append(entry.getKey());
                query.append(": $");
                query.append(entry.getKey());
                if (i < relationshipProperties.size() - 1) {
                    query.append(", ");
                }
                i++;
            }
            query.append("}");
        }
        query.append("]->(target)");

        executeWriteQuery(query.toString(), relationshipProperties);
    }

    /**
     * 根据属性查询关系结果
     *
     * @param sourceLabel      源节点的标签
     * @param relationshipType 关系类型
     * @param targetLabel      目标节点的标签
     * @param propertyKey      属性键
     * @param propertyValue    属性值
     * @return
     */
    public List<Map<String, Object>> queryNodesWithRelationByProperty(String sourceLabel, String relationshipType, String targetLabel, String propertyKey, Object propertyValue) {
        StringBuilder query = new StringBuilder("MATCH (source:");
        query.append(sourceLabel);
        query.append(")-[rel:");
        query.append(relationshipType);
        query.append("]->(target:");
        query.append(targetLabel);
        query.append(") WHERE target.");
        query.append(propertyKey);
        query.append(" = $");
        query.append(propertyKey);
        query.append(" RETURN source, rel, target");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(propertyKey, propertyValue);

        return executeReadQuery(query.toString(), parameters);
    }

    public void close() {
        if (driver != null) {
            driver.close();
        }
    }
}
