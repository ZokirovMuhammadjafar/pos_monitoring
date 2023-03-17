package com.pos.monitoring.repositories.system;

import com.pos.monitoring.config.ConstantQueries;
import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.utils.ClassToMapUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.ObjectUtils;

import java.sql.*;
import java.util.*;

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

    private Connection connection;



    @SneakyThrows
    public List<Machine> getAllChangeMachines() {
        if (ObjectUtils.isEmpty(connection)) {
            getConnection();
        }
        PreparedStatement preparedStatement = connection.prepareStatement(ConstantQueries.GET_ALL_CHANGE_MACHINES);
        List<Map<String, Object>> list = getResultQuery(preparedStatement);
        return ClassToMapUtils.mapToClassList(list,Machine.class);
    }

    private List<Map<String, Object>> getResultQuery(PreparedStatement preparedStatement) throws SQLException {
        List<Map<String, Object>> list = new LinkedList<>();
        ResultSet resultSet=null;
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
        }
        return list;
    }

    @SneakyThrows
    public List<Machine> getAllMachinesFirst(int a) {
        getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(ConstantQueries.GET_ALL_MACHINES_FIRST);
        preparedStatement.setInt(1,a);
        List<Map<String, Object>> list = getResultQuery(preparedStatement);
        return ClassToMapUtils.mapToClassList(list,Machine.class);
    }


    @SneakyThrows
    private void getConnection() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        driverManagerDataSource.setDriverClassName(DRIVER);
        connection = driverManagerDataSource.getConnection();
    }
}
