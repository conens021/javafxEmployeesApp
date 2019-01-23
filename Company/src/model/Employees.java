package model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employees {

    StringProperty name = new SimpleStringProperty(this, "name", "");
    StringProperty address = new SimpleStringProperty(this, "address", "");
    IntegerProperty id = new SimpleIntegerProperty(this, "id", 0);
    IntegerProperty age = new SimpleIntegerProperty(this, "age", 0);
    DoubleProperty income = new SimpleDoubleProperty(this, "income", 0.0);

    //KONSTRUKTORI
    public Employees() {
        this.name.set("");
        this.address.set("");
        this.id.set(0);
        this.age.set(0);
        this.income.set(0.0);
    }

    public Employees(int id, String name, String address, int age, double income) {
        this.id.set(id);
        this.name.set(name);
        this.address.set(address);
        this.age.set(age);
        this.income.set(income);
    }

    public Employees(String name, String address, int age, double income) {
        this.name.set(name);
        this.address.set(address);
        this.age.set(age);
        this.income.set(income);
    }

    //SET METODE
    public void setName(String name) {
        this.name.set(name);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public void setIncome(double income) {
        this.income.set(income);
    }

    //SET METODE
    public String getName() {
        return this.name.get();
    }

    public String getAddress() {
        return this.address.get();
    }

    public int getId() {
        return this.id.get();
    }

    public int getAge() {
        return this.age.get();
    }

    public double getIncome() {
        return this.income.get();
    }

    //Property
    public StringProperty nameProperty() {
        return this.name;
    }

    public StringProperty addressProperty() {
        return this.address;
    }

    public IntegerProperty idProperty() {
        return this.id;
    }

    public IntegerProperty ageProperty() {
        return this.age;
    }

    public DoubleProperty incomeProperty() {
        return this.income;
    }

    public boolean inputIsValid() {
        boolean validate = true;

        if (name.get().isEmpty() || name.get().equals("")) {
            validate = false;
        }
        if (address.get().isEmpty() || name.get().equals("")) {
            validate = false;
        }
        if (age.get() <= 0) {
            validate = false;
        }
        if (income.get() <= 0) {
            validate = false;
        }
        return validate;
    }

    public String saveEmployee() {
        if (inputIsValid()) {
            return "Employee:\n" + "name:" + this.getName() + "\naddress:" + this.getAddress() + "\nage:" + this.getAge() + "\nincome:" + this.getIncome() + "\nHas been created!";
        } else {
            return "Please fill all fields!";
        }
    }

    @Override
    public String toString() {
        return "name:" + this.getName() + "\naddress:" + this.getAddress() + "\nage:" + this.getAge() + "\nincome:" + this.getIncome();
    }
}
