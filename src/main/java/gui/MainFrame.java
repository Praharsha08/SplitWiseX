package gui;

import db.ExpenseDAO;
import db.GroupDAO;
import db.UserDAO;
import logic.ExpenseManagement;
import models.CurrencyCode;
import models.Expense;
import models.Group;
import models.Money;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("SplitWiseX");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("SplitWiseX", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));

        JButton createUserBtn = new JButton("Create User");
        JButton createGroupBtn = new JButton("Create Group");
        JButton addExpenseBtn = new JButton("Add Expense");
        JButton showBalanceBtn = new JButton("Show Balances");
        JButton historyBtn = new JButton("Expense History");

        Font btnFont = new Font("Arial", Font.BOLD, 18);

        createUserBtn.setFont(btnFont);
        createGroupBtn.setFont(btnFont);
        addExpenseBtn.setFont(btnFont);
        showBalanceBtn.setFont(btnFont);
        historyBtn.setFont(btnFont);

        panel.add(createUserBtn);
        panel.add(createGroupBtn);
        panel.add(addExpenseBtn);
        panel.add(showBalanceBtn);
        panel.add(historyBtn);

        setLayout(new BorderLayout());

        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // CREATE USER
        createUserBtn.addActionListener(e -> {

            try {

                String name = JOptionPane.showInputDialog(this, "Enter User Name:");

                if(name == null || name.isBlank()) {
                    return;
                }

                String email = JOptionPane.showInputDialog(this, "Enter Email:");

                if(email == null || email.isBlank()) {
                    return;
                }

                User user = new User(name, email);

                UserDAO.saveUser(user);

                JOptionPane.showMessageDialog(
                        this,
                        "User Created Successfully!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );
            }
        });

        // CREATE GROUP
        createGroupBtn.addActionListener(e -> {

            try {

                String groupName = JOptionPane.showInputDialog(this, "Enter Group Name:");

                if(groupName == null || groupName.isBlank()) {
                    return;
                }

                Group group = new Group(groupName);

                GroupDAO.saveGroup(group);

                JOptionPane.showMessageDialog(
                        this,
                        "Group Created Successfully!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );
            }
        });

        // ADD EXPENSE
        addExpenseBtn.addActionListener(e -> {

            try {

                String groupName = JOptionPane.showInputDialog(this, "Enter Group Name:");

                if(groupName == null || groupName.isBlank()) {
                    return;
                }

                Group group = GroupDAO.findGroup(groupName);

                if(group == null) {
                    JOptionPane.showMessageDialog(this, "Group Not Found");
                    return;
                }

                String payerEmail = JOptionPane.showInputDialog(this, "Enter Payer Email:");

                User payer = UserDAO.findByEmail(payerEmail);

                if(payer == null) {
                    JOptionPane.showMessageDialog(this, "User Not Found");
                    return;
                }

                String description = JOptionPane.showInputDialog(this, "Enter Expense Description:");

                String amountText = JOptionPane.showInputDialog(this, "Enter Amount:");

                double amount = Double.parseDouble(amountText);

                String[] currencies = {"INR", "USD", "EUR"};

                String selectedCurrency = (String) JOptionPane.showInputDialog(
                        this,
                        "Select Currency",
                        "Currency",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        currencies,
                        "INR"
                );

                List<User> participants = group.getMembers();

            Expense expense = new Expense(
            0,
            group.getId(),
            payer,
            new Money(
                    (long)(amount * 100),
                    CurrencyCode.valueOf(selectedCurrency)
            ),
            participants,
            LocalDate.now(),
            description
);

                ExpenseDAO.saveExpense(expense);

                JOptionPane.showMessageDialog(
                        this,
                        "Expense Added Successfully!"
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );
            }
        });

        // SHOW BALANCES
        showBalanceBtn.addActionListener(e -> {

            try {

                String groupName = JOptionPane.showInputDialog(this, "Enter Group Name:");

                if(groupName == null || groupName.isBlank()) {
                    return;
                }

                Group group =GroupDAO.findGroup(groupName);

                if(group == null) {
                    JOptionPane.showMessageDialog(this, "Group Not Found");
                    return;
                }

                Map<User, Double> balances =
                        ExpenseManagement.calculateBalances(group);

                StringBuilder result = new StringBuilder();

                for(Map.Entry<User, Double> entry : balances.entrySet()) {

                    result.append(entry.getKey().getName())
                            .append(" : ")
                            .append(String.format("%.2f", entry.getValue()))
                            .append("\n");
                }

                JOptionPane.showMessageDialog(
                        this,
                        result.toString()
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );
            }
        });

        // EXPENSE HISTORY
        historyBtn.addActionListener(e -> {

            try {

                String groupName = JOptionPane.showInputDialog(this, "Enter Group Name:");

                if(groupName == null || groupName.isBlank()) {
                    return;
                }

                Group group = GroupDAO.findGroup(groupName);

                if(group == null) {
                    JOptionPane.showMessageDialog(this, "Group Not Found");
                    return;
                }

                StringBuilder history = new StringBuilder();

                for(Expense expense : group.getExpenses()) {

                    history.append("Description: ")
                            .append(expense.getDescription())
                            .append("\n");

                    history.append("Amount: ")
                            .append(expense.getAmount().getAmount() / 100.0)
                            .append("\n");

                    history.append("Payer: ")
                            .append(expense.getPayer().getName())
                            .append("\n");

                    history.append("-------------------------\n");
                }

                JOptionPane.showMessageDialog(
                        this,
                        history.toString()
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage()
                );
            }
        });
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();
            frame.setVisible(true);

        });
    }
}