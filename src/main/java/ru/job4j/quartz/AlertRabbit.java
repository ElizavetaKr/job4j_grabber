package ru.job4j.quartz;

import org.quartz.*;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class AlertRabbit {

    private static Connection connect() {
        Connection connection;
        try (InputStream input = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(input);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return connection;
    }

    private static int getInterval() {
        int interval = 0;
        try (InputStream input = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(input);
            interval = Integer.parseInt(config.getProperty("rabbit.interval")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return interval;
    }

    public static void add(Connection connection, long time) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO rabbit(created_date) VALUES (?)")) {
            Timestamp timestamp = new Timestamp(time);
            statement.setTimestamp(1, timestamp);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context.getJobDetail().getJobDataMap().get("store");
            long time = System.currentTimeMillis();
            store.add(time);
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            add(connection, time);
        }
    }
}