package models;

import java.time.LocalDate;
import java.util.*;

public class Expense {
    private int id;
    private final int groupId;
    private final User payer;
    private final Money amount;
    private final List<User> participants;
    private final LocalDate date;
    private final String description;

    public Expense(int groupId, User payer, Money amount, List<User> participants, LocalDate date, String description) throws IllegalArgumentException{
        if(amount == null || amount.getAmount() <= 0) throw new IllegalArgumentException("Amount must be > 0");
        if(payer == null) throw new IllegalArgumentException("Payer is required.");
        if(participants == null) throw new IllegalArgumentException("Participants list cannot be null.");
        if(groupId < 0) throw new IllegalArgumentException("Group ID cannot be negative.");

        this.groupId = groupId;
        LinkedHashSet<User> unique = new LinkedHashSet<>(participants);
        unique.add(payer);
        this.participants = new ArrayList<>(unique);

        this.amount = amount;
        this.date = (date != null ? date : LocalDate.now());
        this.description = (description != null ? description.trim() : "");
        this.payer = payer;
    }

    public Expense(int id, int groupId, User payer, Money amount, List<User> participants, LocalDate date, String description) throws IllegalArgumentException {
        this(groupId, payer, amount, participants, date, description);
        this.id = id;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public int getGroupId() {return groupId;}

    public User getPayer() {
        return payer;
    }

    public Money getAmount() {
        return amount;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void addParticipant(User participant) throws IllegalArgumentException {
        if(participant == null) {
            throw new IllegalArgumentException("Participant is required");
        }
        participants.add(participant);
    }
}
