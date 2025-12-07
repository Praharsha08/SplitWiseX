package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Group {
    private int id;
    private String name;
    private ArrayList<User> members;
    private ArrayList<Expense> expenses;

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

    public Group(int id, String name, ArrayList<Expense> expenses, ArrayList<User> members) {
        this(id, name);
        this.expenses = expenses;
        this.members = members;
    }

    public String getName(){ return name; }
    public List<User> getMembers() { return Collections.unmodifiableList(members); }
    public List<Expense> getExpenses() { return Collections.unmodifiableList(expenses); }
    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

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
