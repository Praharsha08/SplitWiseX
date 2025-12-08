package db;

import exceptions.UserException;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static void saveUser(User user) throws UserException, RuntimeException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());

            preparedStatement.executeUpdate();

            try(ResultSet rs = preparedStatement.getGeneratedKeys()){
                if(rs.next()) {
                    int newID = rs.getInt(1);
                    user.setId(newID);
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserException("User with email " + user.getEmail() + " already exists.");
        } catch (SQLException e) {
            throw new RuntimeException("Database error while saving user: " + e.getMessage(), e);
        }
    }

    public static User findByEmail(String email) throws RuntimeException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try(Connection conn = DBConnection.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }
    }

    public static List<User> findAllUsers() throws RuntimeException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                ));
            }

            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading all users: " + e.getMessage(), e);
        }
    }



}
