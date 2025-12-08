package models;

import java.util.Objects;

public class User {
    private int id;
    private final String name;
    private final String email;

    public User(String name, String email) {
        this.id = 0;
        this.name = name;
        this.email = email;
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    @Override
    public boolean equals(Object o){
        if(this == o) return true;

        if(o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(email.trim().toLowerCase(), user.email.trim().toLowerCase());
    }

    @Override
    public int hashCode(){
        return Objects.hash(email);
    }

    public String toString() {
        return String.format(
                name + ": " + email
        );
    }
}
