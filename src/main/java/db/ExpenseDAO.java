package db;

import exceptions.UserException;
import models.CurrencyCode;
import models.Expense;
import models.Money;
import models.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {
    public static void saveExpense(Expense expense) throws RuntimeException {
        String expenseSQL = "INSERT INTO expenses (group_id, payer_id, amount, currency, description, expense_date)" +
                " VALUES(?, ?, ?, ?, ?, ?)";
        String participantsSQL = "INSERT INTO expense_participants VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement expenseStatement = null;
        PreparedStatement participantsStatement = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            expenseStatement = conn.prepareStatement(expenseSQL, Statement.RETURN_GENERATED_KEYS);
            participantsStatement = conn.prepareStatement(participantsSQL);

            expenseStatement.setInt(1, expense.getGroupId());
            expenseStatement.setInt(2, expense.getPayer().getId());
            expenseStatement.setLong(3, expense.getAmount().getAmount());
            expenseStatement.setString(4, expense.getAmount().getCurrency().name());
            expenseStatement.setString(5, expense.getDescription());
            expenseStatement.setDate(6, Date.valueOf(expense.getDate()));
            expenseStatement.executeUpdate();

            try(ResultSet rs = expenseStatement.getGeneratedKeys()) {
                if(rs.next()) {
                    expense.setId(rs.getInt(1));
                } else {
                    throw new RuntimeException("Creating expense failed. No ID obtained.");
                }
            }


            for(User participant: expense.getParticipants()) {
                participantsStatement.setInt(1, expense.getId());
                participantsStatement.setInt(2, participant.getId());

                participantsStatement.addBatch();
            }

            participantsStatement.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            if(conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Server Error: " + e.getMessage());
        } finally {
            try {
                if(participantsStatement != null) participantsStatement.close();
                if(expenseStatement != null) expenseStatement.close();
                if(conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
