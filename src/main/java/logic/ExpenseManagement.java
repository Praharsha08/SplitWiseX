package logic;

import db.DBConnection;
import db.ExpenseDAO;
import db.GroupDAO;
import db.UserDAO;
import exceptions.GroupNotFoundException;
import exceptions.UserException;
import models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public final class ExpenseManagement {

    private ExpenseManagement() {
    }

    public static Group createGroup(String name) throws IllegalArgumentException, RuntimeException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }
        Group group = new Group(name);

        if(GroupDAO.hasGroup(name)) {
            return null;
        }

        GroupDAO.saveGroup(group);

        return group;
    }

    public static List<Group> getGroups() {
        return GroupDAO.findAllGroups();
    }

    public static List<User> getMembers() throws RuntimeException {
        return UserDAO.findAllUsers();
    }

    public static Group getGroup(String groupName) throws IllegalArgumentException, GroupNotFoundException {
        if(groupName == null || groupName.trim().isEmpty()){
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }

        Group group = GroupDAO.findGroup(groupName);


        return group;
    }

    public static User getMember(String memberEmail) throws RuntimeException {
        return UserDAO.findAllUsers().stream().filter(u -> u.getEmail().equalsIgnoreCase(memberEmail)).findFirst().orElse(null);
    }

    public static User createMember(String name, String email) throws UserException {
        if (name.trim().isEmpty() || email.trim().isEmpty()) {
            throw new UserException("Name and email required.");
        }
        User user = new User(name, email);
        UserDAO.saveUser(user);

        return user;
    }

    public static User getUserByEmail(String email) throws UserException, RuntimeException {
        if(email == null || email.isEmpty()) {
            throw new UserException("User cannot be null or empty.");
        }

        return UserDAO.findByEmail(email);
    }

    public static boolean hasGroup(String groupName) throws IllegalArgumentException, GroupNotFoundException {
        if(groupName == null || groupName.isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }

        return GroupDAO.findGroup(groupName) != null;
    }

    public static boolean validateCurrency(String currencyStr) throws IllegalArgumentException {
        if(currencyStr == null || currencyStr.trim().isEmpty()) throw new IllegalArgumentException("Currency code cannot be empty or null.");

        try {
            CurrencyCode currencyCode = CurrencyCode.valueOf(currencyStr.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean validateAmount(String amount) {
        if(amount == null || amount.trim().isEmpty()) throw new IllegalArgumentException("Amount cannot be empty or null.");

        try{
            double decimalAmount = Double.parseDouble(amount);
            return decimalAmount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean addMemberToGroup(String groupName, User user) throws IllegalArgumentException, GroupNotFoundException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Group name cannot be null or empty.");
        }

        Group group = GroupDAO.findGroup(groupName);

        if(group == null) {
            throw new IllegalArgumentException("Group not found");
        }

        if(group.addMember(user)) {
            return GroupDAO.addMemberToGroup(group.getId(), user.getId());
        }

        return false;
    }

    public static void handleAddExpense(String groupName, String payerEmail, List<String> participantsEmails, String amount, String currency, String description) throws IllegalArgumentException, GroupNotFoundException, UserException, NumberFormatException {
        if (groupName == null || payerEmail == null || description == null || participantsEmails == null || participantsEmails.isEmpty()) {
            throw new IllegalArgumentException("Group name, payer's email, description, and participants emails are required.");
        }

        Group group = getGroup(groupName);
        if (group == null) {
            throw new GroupNotFoundException("Group {" + groupName + "} wasn't found");
        }

        User payerMember = getMember(payerEmail);
        if (payerMember == null) {
            throw new UserException("User with email {" + payerEmail + "} wasn't found");
        }

        List<User> participants = new ArrayList<>();
        for (String email: participantsEmails) {
            User member = getMember(email);
            if(member == null) {
                throw new UserException("User with email {" + email + "} wasn't found.");
            }
            participants.add(member);
        }

        double decimalAmount = Double.parseDouble(amount);
        long cents = (long) (decimalAmount * 100);


        Money groupMoney = new Money(cents, CurrencyCode.valueOf(currency.toUpperCase()));

        Expense expense = new Expense(group.getId(), payerMember, groupMoney, participants, LocalDate.now(), description);

        ExpenseDAO.saveExpense(expense);

        group.addExpense(expense);
    }

    public static Map<User, Long> calculateBalances(Group group) {
        System.out.println(group);
        HashMap<User, Long> balances = new HashMap<>();

        if(group == null || group.getExpenses().isEmpty()) {
            return balances;
        }



        for(Expense expense: group.getExpenses()) {
            User payer = expense.getPayer();

            long totalAmount = expense.getAmount().getAmount();
            balances.merge(payer, totalAmount, Long::sum);

            List<User> participants = expense.getParticipants();
            List<Money> shares = SplitUtil.splitEqual(expense.getAmount(), participants.size());

            for(int i = 0; i < participants.size(); i++) {
                User participant = participants.get(i);
                long shareAmount = shares.get(i).getAmount();

                balances.merge(participant, -shareAmount, Long::sum);
            }
        }

        return balances;
    }
}
