package com.pos.monitoring.repositories.system;

import com.pos.monitoring.entities.DailyTerminalInfo;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.repositories.system.queries.ConstantQueries;
import com.pos.monitoring.utils.ReflectionUtils;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Repository
public  class  Connection8005 {

    @Value("${8005.connection-db.url}")
    private String URL;
    @Value("${8005.connection-db.driver}")
    private String DRIVER;
    @Value("${8005.connection-db.password}")
    private String PASSWORD;
    @Value("${8005.connection-db.username}")
    private String USERNAME;
    private Connection connection;

    private synchronized List<Map<String, Object>>  getResultQuery(PreparedStatement preparedStatement) throws SQLException {
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
            connection.close();
            connection = null;
        }
        return list;
    }

    @SneakyThrows
    public synchronized List<Machine> getAllMachinesChange(int a) {
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ConstantQueries.GET_ALL_CHANGE_MACHINES);
        preparedStatement.setInt(1, a);
        List<Map<String, Object>> list = getResultQuery(preparedStatement);
        return ReflectionUtils.mapToClassList(list, Machine.class);
    }

    @SneakyThrows
    public synchronized List<DailyTerminalInfo>getDailyTerminalInfoAuthCode(){
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ConstantQueries.GET_DAILY_AUTH_CODE);
        List<Map<String, Object>> list = getResultQuery(preparedStatement);
        return ReflectionUtils.mapToClassList(list, DailyTerminalInfo.class);
    }

    @SneakyThrows
    public synchronized List<DailyTerminalInfo>getDailyTerminalInfoFix(){
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ConstantQueries.GET_DAILY_FIX);
        List<Map<String, Object>> list = getResultQuery(preparedStatement);
        return ReflectionUtils.mapToClassList(list, DailyTerminalInfo.class);
    }

    @SneakyThrows
    private synchronized void getConnection() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        driverManagerDataSource.setDriverClassName(DRIVER);
        connection = driverManagerDataSource.getConnection();
    }

}
