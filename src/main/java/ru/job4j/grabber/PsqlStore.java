package ru.job4j.grabber;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) throws SQLException {
        try {
            Class.forName(config.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        connection = DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("username"),
                config.getProperty("password")
        );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO post(name, text, link, created) VALUES (?, ?, ?, ?)"
                        + "ON CONFLICT (link) DO NOTHING", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            Timestamp timestampFromLDT = Timestamp.valueOf(post.getCreated());
            statement.setTimestamp(4, timestampFromLDT);
            statement.execute();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> post = new ArrayList<>();
        try (var statement = connection.createStatement()) {
            var selection = statement.executeQuery(String.format(
                    "SELECT * FROM post"));
            while (selection.next()) {
                post.add(create(selection));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (var statement = connection.createStatement()) {
            var selection = statement.executeQuery(String.format(
                    "SELECT * FROM post WHERE id = %s", id));
            if (selection.next()) {
                post = create(selection);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return post;
    }

    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    private Post create(ResultSet resultSet) throws SQLException {
        LocalDateTime localDateTime = resultSet.getTimestamp("created").toLocalDateTime();
        return new Post(resultSet.getInt("id"), resultSet.getString("name"),
                resultSet.getString("text"), resultSet.getString("link"), localDateTime);
    }
}