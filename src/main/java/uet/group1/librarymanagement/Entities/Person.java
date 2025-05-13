package uet.group1.librarymanagement.Entities;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Person {
    private String id;
    private String name;
    private String password;
    private Role role;
    public enum Role {BORROWER, ADMIN}
    protected Map<Integer,Integer> borrowed = new HashMap<>();

    public Person(String id, String name, String password, Role role) {
        this.id   = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }


    public Role getRole() {
        return this.role;
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password= password;
    }
    public String getId()   { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

}
