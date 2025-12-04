package db;

import exceptions.GroupException;
import exceptions.GroupNotFoundException;
import models.Group;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroupDAO {
    public static void saveGroup(Group group) throws RuntimeException {
        String sql = "INSERT INTO `groups` (name) VALUES (?)";

        try(Connection conn = DBConnection.getConnection()) {
            PreparedStatement ptsmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ptsmt.setString(1, group.getName());
            ptsmt.executeUpdate();
            try(ResultSet rs = ptsmt.getGeneratedKeys()){
                if(rs.next()) {
                    group.setId(rs.getInt(1));
                }
            }
        }  catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    public static List<Group> findAllGroups () throws RuntimeException{
        String sql = "SELECT * FROM `groups`";
        List<Group> groups = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);

            while (rs.next()) {
                groups.add(new Group(rs.getInt("id"), rs.getString("name")));
            }

            for(Group group: groups) {
                String groupSQL = "SELECT u.* FROM users u " +
                        "JOIN group_members gm ON u.id=gm.user_id " +
                        "WHERE gm.group_id = ?";

                PreparedStatement groupStatement = conn.prepareStatement(groupSQL);
                groupStatement.setInt(1, group.getId());

                ResultSet groupRs = groupStatement.executeQuery();
                while (groupRs.next()) {
                    group.addMember(new User(groupRs.getInt("id"), groupRs.getString("name"), groupRs.getString("email")));
                }
            }

            return groups;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    public static Group findGroup(String groupName) throws GroupNotFoundException {
        String groupSQL = "SELECT * FROM `groups` WHERE name = ?";
        Group group = null;

        try(Connection conn = DBConnection.getConnection()) {
            PreparedStatement groupStatement = conn.prepareStatement(groupSQL);
            groupStatement.setString(1, groupName);

            ResultSet rs = groupStatement.executeQuery();
            if(rs.next()) {
                group = new Group(rs.getInt("id"), rs.getString("name"));
            } else {
                return null;
            }

            String membersSQL = "SELECT u.* FROM users u " +
                    "JOIN group_members gm ON u.id=gm.user_id " +
                    "WHERE gm.group_id = ?";

            PreparedStatement membersStatement = conn.prepareStatement(membersSQL);
            membersStatement.setInt(1, group.getId());
            ResultSet membersRs = membersStatement.executeQuery();

            while (membersRs.next()) {
                User user = new User(membersRs.getInt("id"), membersRs.getString("name"), membersRs.getString("email"));
                group.addMember(user);
            }

            System.out.println("Group members: " + group.getMembers().size());
            return group;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

    public static boolean hasGroup(String name) throws RuntimeException {
        String sql = "SELECT COUNT(*) FROM `groups` WHERE name = ?";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Server Error: " + e.getMessage());
        }
    }

//    public static void removeGroup(String groupName) {
//        String sql =
//    }
}
