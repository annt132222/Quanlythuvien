package uet.group1.librarymanagement.Entities;

public abstract class Person {
    protected int id;
    protected String password;
    protected String name;
    protected String address;
    protected int phoneNumber;
    static int currentIdNumber = 0;

    public Person(int idNum, String name, String address, int phoneNumber) {
        currentIdNumber++;
        if (idNum == -1) {
            this.id = currentIdNumber;
        } else {
            this.id = idNum;
        }

        this.password = Integer.toString(id);
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    /**
     * printInfo of Person.
     */
    public void printInfo() {

    }

    /**
     * Setter function.
     */
    public void setAddress(String a) {
        this.address = a;
    }

    public void setPhoneNumber(int p) {
        this.phoneNumber = p;
    }

    public void setName(String n) {
        this.name = n;
    }

    /**
     * Getter function.
     */
    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAddress() {
        return this.address;
    }

    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    public int getId() {
        return this.id;
    }
}
