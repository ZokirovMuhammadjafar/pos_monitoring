package com.pos.monitoring.repositories.system;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    public void getChangeMachines() {
        if (ObjectUtils.isEmpty(connection)) {
            getConnection();
        }
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from machine");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println("Machine: " + "{ id=" + resultSet.getLong("id") + ", " + "name=" + resultSet.getString("name") + "}");
        }
        connection.close();
    }

    @SneakyThrows
    private void getConnection() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        driverManagerDataSource.setDriverClassName(DRIVER);
        connection = driverManagerDataSource.getConnection();
    }
}
