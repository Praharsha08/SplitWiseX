package db;

import exceptions.GroupException;
import exceptions.GroupNotFoundException;
import models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDAO {
    public static void saveGroup(Group group) throws RuntimeException {
        String sql = "INSERT INTO `groups` (name) VALUES (?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ptsmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ptsmt.setString(1, group.getName());
            ptsmt.executeUpdate();
            try (ResultSet rs = ptsmt.getGeneratedKeys()) {
                if (rs.next()) {
                    group.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    public static List<Group> findAllGroups() throws RuntimeException {
        String sql = "SELECT * FROM `groups`";
        List<Group> groups = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                int groupId = rs.getInt("id");
                String groupName = rs.getString("name");
                ArrayList<Expense> expenses = findExpenses(groupId, conn);
                ArrayList<User> members = findMembers(groupId, conn);
                groups.add(new Group(groupId, groupName, expenses, members));
            }

            return groups;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    public static Group findGroup(String groupName) throws GroupNotFoundException {

        String sql = "SELECT * FROM `groups` " +
                "WHERE name = ?";
        Group group = null;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, groupName);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int groupId = rs.getInt("id");

                ArrayList<Expense> expenses = findExpenses(groupId, conn);
                ArrayList<User> members = findMembers(groupId, conn);

                group = new Group(groupId, groupName, expenses, members);
            }

            return group;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    private static ArrayList<User> findMembers(int groupId, Connection conn) throws SQLException {
        ArrayList<User> members = new ArrayList<>();

        String sql = "SELECT u.* FROM users u " +
                "JOIN group_members gm ON u.id=gm.user_id " +
                "WHERE gm.group_id = ?";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, groupId);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            User user = new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"));
            members.add(user);
        }

        return members;
    }

    private static ArrayList<Expense> findExpenses(int groupId, Connection conn) throws SQLException {
        String sql = "SELECT " +
                "e.id AS expense_id, " +
                "e.group_id, " +
                "e.amount, " +
                "e.currency, " +
                "e.`description`, " +
                "e.expense_date, " +
                "u.id AS payer_id, " +
                "u.`name` AS payer_name, " +
                "u.email AS payer_email, " +
                "p.user_id AS part_id, " +
                "pu.`name` AS part_name, " +
                "pu.email AS part_email " +
                "FROM expenses AS e " +
                "JOIN users AS u ON e.payer_id=u.id " +
                "LEFT JOIN expense_participants AS p ON e.id=p.expense_id " +
                "LEFT JOIN users AS pu ON p.user_id=pu.id " +
                "WHERE e.group_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, groupId);

            try (ResultSet rs = statement.executeQuery()) {
                Map<Integer, Expense> expenseMap = new HashMap<>();
                while (rs.next()) {
                    int expenseId = rs.getInt("expense_id");
                    Expense expense = expenseMap.get(expenseId);
                    if (expense == null) {
                        User payer = new User(
                                rs.getInt("payer_id"),
                                rs.getString("payer_name"),
                                rs.getString("payer_email")
                        );

                        long amountCents = rs.getLong("amount");
                        CurrencyCode currencyCode = CurrencyCode.valueOf(rs.getString("currency"));

                        Money amount = new Money(amountCents, currencyCode);

                        LocalDate date = rs.getDate("expense_date").toLocalDate();
                        String description = rs.getString("description");

                        expense = new Expense(expenseId, payer, amount, new ArrayList<>(), date, description);

                        expenseMap.put(expenseId, expense);
                    }
                    int partId = rs.getInt("part_id");
                    if (partId != 0) {
                        String partMame = rs.getString("part_name");
                        String partEmail = rs.getString("part_email");

                        User participant = new User(partId, partMame, partEmail);
                        expense.addParticipant(participant);
                    }
                }

                return new ArrayList<>(expenseMap.values());
            }
        }
    }

    public static boolean hasGroup(String name) throws RuntimeException {
        String sql = "SELECT COUNT(*) FROM `groups` WHERE name = ?";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }
}
