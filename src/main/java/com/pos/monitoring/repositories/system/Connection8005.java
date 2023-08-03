package com.pos.monitoring.repositories.system;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.repositories.system.queries.ConstantQueries;
import com.pos.monitoring.utils.ReflectionUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class Connection8005 {

    @Value("${8005.connection-db.url}")
    private String URL;
    @Value("${8005.connection-db.driver}")
    private String DRIVER;
    @Value("${8005.connection-db.password}")
    private String PASSWORD;
    @Value("${8005.connection-db.username}")
    private String USERNAME;
    private ConcurrentMap<String, Connection> connectionMap = new ConcurrentHashMap<>(2);

    private synchronized List<Map<String, Object>> getResultQuery(PreparedStatement preparedStatement, String name) throws SQLException {
        List<Map<String, Object>> list = new LinkedList<>();
        ResultSet resultSet;
        try {
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<String> columns = new LinkedList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(metaData.getColumnName(i));
                }
                while (resultSet.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    for (String column : columns) {
                        item.put(column, resultSet.getString(column));
                    }
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionMap.get(name).close();
            connectionMap.remove(name);
        }
        return list;
    }

    public synchronized List<Map<String, Object>> getReportSingleMFO(final String mfo) throws SQLException {
         getConnection("REPORT");
         PreparedStatement preparedStatement=connectionMap.get("REPORT").prepareStatement(ConstantQueries.REPORT_QUERY_7005);
        preparedStatement.setString(1, mfo);
        preparedStatement.setString(2, mfo);
        return getResultQuery(preparedStatement, "REPORT");
    }

    @SneakyThrows
    public synchronized List<Machine> getAllMachinesChange(int a) {
        getConnection("ALL");
        PreparedStatement preparedStatement = connectionMap.get("ALL").prepareStatement(ConstantQueries.GET_ALL_CHANGE_MACHINES);
        preparedStatement.setInt(1, a);
        List<Map<String, Object>> list = getResultQuery(preparedStatement, "ALL");
        return ReflectionUtils.mapToClassList(list, Machine.class);
    }


    @SneakyThrows
    private synchronized void getConnection(String name) {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        driverManagerDataSource.setDriverClassName(DRIVER);
        connectionMap.put(name, driverManagerDataSource.getConnection());
    }

    @SneakyThrows
    public List<Machine> getAllMachinesChangeWithBanksChosen(int i) {
        getConnection("BANKS_CHOSEN");
        PreparedStatement preparedStatement = connectionMap.get("BANKS_CHOSEN").prepareStatement(ConstantQueries.GET_ALL_CHANGE_MACHINES_WITH_BANKS_CHOSEN);
        preparedStatement.setInt(1, i);
        List<Map<String, Object>> list = getResultQuery(preparedStatement, "BANKS_CHOSEN");
        return ReflectionUtils.mapToClassList(list, Machine.class);
    }
}
