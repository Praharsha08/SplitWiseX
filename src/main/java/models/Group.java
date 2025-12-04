package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private final ArrayList<User> members;
    private final ArrayList<Expense> expenses;

    public Group(String name){
        this.id = 0;
        this.name = name;
        this.members = new ArrayList<>();
        this.expenses = new ArrayList<>();
    }

    public Group(int id, String name){
        this(name);
        this.id = id;
    }

    public String getName(){ return name; }
    public List<User> getMembers() { return Collections.unmodifiableList(members); }
    public List<Expense> getExpenses() { return Collections.unmodifiableList(expenses); }
    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public User findMemberByName(String name) {
        return members.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasMember(String name){
        return members.stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(name));
    }

    // To change: the method should return boolean and not print anything
    public boolean addMember(User member) {
        if(member == null) {
            throw new IllegalArgumentException("Member required");
        }
        boolean exists = members.stream()
                        .anyMatch(u -> u == member);
        if(exists){
            return false;
        }
        members.add(member);
        return true;
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public void rename(String name){
        this.name = name;
    }

    public String toString(){
        return String.format(
                " - " + name
                + ": [\n"
                + "\tMembers: " + members.size()
                + "\n\tExpenses: " + expenses.size()
                + "\n]"
        );
    }
}
