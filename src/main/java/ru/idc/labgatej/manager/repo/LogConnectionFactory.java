package ru.idc.labgatej.manager.repo;

import java.sql.Connection;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogConnectionFactory {

    @Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getDatabaseConnection() throws Exception {
        return dataSource.getConnection();
    }
}