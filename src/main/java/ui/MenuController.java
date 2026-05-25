package ui;

import exceptions.GroupNotFoundException;
import exceptions.UserException;
import logic.ExpenseManagement;
import models.CurrencyCode;
import models.Expense;
import models.Group;
import models.User;

import java.util.*;

import static ui.ConsoleUtil.safeReadLine;

public class MenuController {
    private final Scanner scanner;

    public MenuController() {
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("Welcome to Smart Expense Splitter.");
        System.out.println("----------------------------------");

        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    handleCreateUser();
                    break;
                case 2:
                    createGroup();
                    break;
                case 3:
                    handleAddMemberToGroup();
                    break;
                case 4:
                    printUsers();
                    break;
                case 5:
                    printGroups();
                    break;
                case 6:
                    handleAddExpense();
                    break;
                case 7:
                    handleShowBalances();
                    break;
                case 8:
                    handleShowHistory();
                    break;
                case 9:
                    System.out.println("Exiting application...");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("1. Create user");
        System.out.println("2. create group");
        System.out.println("3. Add member to group");
        System.out.println("4. List users");
        System.out.println("5. List groups");
        System.out.println("6. Add expense to group");
        System.out.println("7. Show group balance");
        System.out.println("8. Show expense history");
        System.out.println("9. Exit");
        System.out.println("\nEnter your choice: ");
    }

    private int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleCreateUser() {
        String username = promptForString("Enter username (Type '0' to cancel): ", "0");
        if (username == null) {
            System.out.println("Operation canceled.");
            return;
        }

        String email = promptForString("Enter email (Type '0' to cancel): ", "0");
        if (email == null) {
            System.out.println("Operation canceled.");
            return;
        }

        try {
            User newUser = ExpenseManagement.createMember(username, email);
            System.out.println("Created user: " + newUser.getName());
        } catch (UserException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printUsers() {
        try {
            List<User> members = ExpenseManagement.getMembers();
            if (members.isEmpty()) {
                System.out.println("No members yet. Please create users first.");
                return;
            }
            for (User member : members) {
                System.out.println(member);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createGroup() {

        String groupName = safeReadLine(scanner, "Enter group name: ");
        if (groupName == null || groupName.trim().isEmpty()) {
            System.out.println("Group name cannot be null or empty.");
            return;
        }

        try {
            Group newGroup = ExpenseManagement.createGroup(groupName);
            if (newGroup == null) {
                System.out.println("Group with the name [" + groupName + "] already exists. Please choose a different name.");
            } else {
                System.out.println("Group created: " + groupName);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printGroups() {
        List<Group> groups = ExpenseManagement.getGroups();
        if (groups.isEmpty()) {
            System.out.println("There is no groups to print yet. Please create a group first.");
            return;
        }
        for (Group group : groups) {
            System.out.println(group);
        }
    }

    private void handleAddMemberToGroup() {
        Group group = promptForGroup();
        if (group == null) {
            System.out.println("Operation canceled.");
            return;
        }

        User member = promptForUser();
        if (member == null) {
            System.out.println("Operation canceled.");
            return;
        }

        String groupName = group.getName();
        try {
            if (ExpenseManagement.addMemberToGroup(groupName, member)) {
                System.out.println("User {" + member.getEmail() + "} Added successfully to group {" + groupName + "}.");
            } else {
                System.out.println("Couldn't add user {" + member.getEmail() + "} to group {" + groupName + "}.\nPlease try again.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (GroupNotFoundException e) {
            System.out.println("Group [" + groupName + "] wasn't found.");
        }
    }

    private void handleAddExpense() {
        Group group = promptForGroup();
        if (group == null) {
            System.out.println("Operation canceled.");
            return;
        }

        User payer = promptForUser();
        if (payer == null) {
            System.out.println("Operation canceled.");
            return;
        }

        String expenseDescription = promptForString("Enter a short description (Type '0' to cancel): ", "0");
        if (expenseDescription == null) {
            System.out.println("Operation canceled");
            return;
        }

        String amountStr = promptForAmount();
        if (amountStr == null) {
            System.out.println("Operation canceled.");
            return;
        }

        CurrencyCode currencyCode = promptForCurrencyCode();
        if (currencyCode == null) {
            System.out.println("Operation canceled.");
            return;
        }

        List<String> participantsEmails = readParticipantsEmails();

        try {
            ExpenseManagement.handleAddExpense(group.getName(), payer.getEmail(), participantsEmails, amountStr, currencyCode.name(), expenseDescription);
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid amount. Please enter a number like 125.50.");
        } catch (GroupNotFoundException | UserException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleShowBalances() {
        Group group = promptForGroup();
        if (group == null) {
            System.out.println("Operation canceled.");
            return;
        }

        System.out.println("\n--- Calculating balances ---");
        Map<User, Double> balances = ExpenseManagement.calculateBalances(group);

        if (balances.isEmpty()) {
            System.out.println("No expenses found for this group.");
            return;
        }
    }

    private void handleShowHistory(){
        Group group = promptForGroup();
        if(group == null) {
            System.out.println("Operation canceled.");
            return;
        }

        List<Expense> expenses = group.getExpenses();
        if(expenses.isEmpty()) {
            System.out.println("No expenses recorded for this group yet.");
            return;
        }

        System.out.println("\n--- Expenses History ---\n");
        System.out.printf("%-5s | %-12s | %-20s | %-15s | %s%n",
                "ID", "Date", "Description", "Amount", "Payer");

        for(Expense e: expenses) {
            System.out.printf("%-5d | %-12s | %-20s | %-15.2f | %s%n",
                    e.getId(),
                    e.getDate(),
                    truncate(e.getDescription(), 20),
                    e.getAmount().getAmount() / 100.0,
                    e.getPayer().getName()
            );
        }

        System.out.println("-------------------------------------");
    }

    private String truncate(String str, int width) {
        if(str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }

    private Group promptForGroup() {
        while (true) {
            String groupName = safeReadLine(scanner, "Enter group name (Type '0' to cancel): ");
            if (groupName == null || groupName.trim().isEmpty()) {
                System.out.println("Group name cannot be empty or null.");
                continue;
            }

            if (groupName.equals("0")) return null;

            try {
                Group group = ExpenseManagement.getGroup(groupName);
                if (group == null) {
                    System.out.println("Group not found, try again.");
                    continue;
                }
                return group;
            } catch (GroupNotFoundException e) {
                System.out.println("Group [" + groupName + "] not found, try again.");
                return null;
            }
        }
    }

    private User promptForUser() {
        try {
            while (true) {
                String userEmail = safeReadLine(scanner, "Enter member's email (Type '0' to cancel): ");
                if (userEmail == null || userEmail.trim().isEmpty()) {
                    System.out.println("Member's email cannot be null or empty. Please try again.");
                    continue;
                }

                if (userEmail.equals("0")) return null;

                User member = ExpenseManagement.getMember(userEmail);
                if (member == null) {
                    System.out.println("Member with email '" + userEmail + "' wasn't found. Please try again.");
                    continue;
                }
                return member;
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private List<String> readParticipantsEmails() {
        List<String> participantsEmails = new ArrayList<>();

        while (true) {
            String memberEmail = safeReadLine(scanner, "Enter participant's email (Type '0' to stop): ");

            if (memberEmail == null) {
                System.out.println("Input error. Stopping participant entry.");
                break;
            }

            if (memberEmail.equals("0")) {
                break;
            }

            if (memberEmail.isEmpty()) {
                System.out.println("Email cannot be empty. Please try again.");
                continue;
            }

            try {
                User member = ExpenseManagement.getMember(memberEmail);
                if(member == null) {
                    System.out.println("Member with email '" + memberEmail + "' wasn't found. Please try again.");
                    continue;
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                continue;
            }

            participantsEmails.add(memberEmail);
        }

        return participantsEmails;
    }

    private CurrencyCode promptForCurrencyCode() {
        while (true) {
            String currencyStr = safeReadLine(scanner, "Enter currency (Type '0' to cancel): ");
            if (currencyStr == null || currencyStr.isEmpty()) {
                System.out.println("Empty or invalid currency. Please try again.");
                continue;
            }

            if (currencyStr.equals("0"))
                return null;

            try {
                if (!ExpenseManagement.validateCurrency(currencyStr.toUpperCase())) {
                    System.out.println("Invalid currency. Please try again.");
                    continue;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid currency. Please try again.");
                continue;
            }

            return CurrencyCode.valueOf(currencyStr.toUpperCase());
        }
    }

    private String promptForString(String prompt, String exitCode) {
        while (true) {
            String str = safeReadLine(scanner, prompt);
            if (str == null || str.isEmpty()) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            if (str.equalsIgnoreCase(exitCode)) return null;

            return str;
        }
    }

    public String promptForAmount() {
        while (true) {
            String amountStr = safeReadLine(scanner, "Enter amount (Type 'c' to cancel): ");
            if (amountStr == null || amountStr.trim().isEmpty()) {
                System.out.println("Amount cannot be empty or null. Please try again.");
                continue;
            }

            if (amountStr.equalsIgnoreCase("c")) return null;

            if (!ExpenseManagement.validateAmount(amountStr)) {
                System.out.println("Invalid amount. Must be a positive number (e.g., 10.50).");
                continue;
            }

            return amountStr;
        }
    }
}
