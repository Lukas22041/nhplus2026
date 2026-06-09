package de.hitec.nhplus.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Caregiver extends Person {
    private SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty weeklyWorkingHours;

    public Caregiver(String firstName, String surname, String phoneNumber, String weeklyWorkingHours) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(0);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.weeklyWorkingHours = new SimpleStringProperty(weeklyWorkingHours);
    }

    public Caregiver(long cid, String firstName, String surname, String phoneNumber, String weeklyWorkingHours) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.weeklyWorkingHours = new SimpleStringProperty(weeklyWorkingHours);
    }

    public long getCid() {
        return cid.get();
    }

    public SimpleLongProperty cidProperty() {
        return cid;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public SimpleStringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getWeeklyWorkingHours() {
        return weeklyWorkingHours.get();
    }

    public SimpleStringProperty weeklyWorkingHoursProperty() {
        return weeklyWorkingHours;
    }

    public void setWeeklyWorkingHours(String weeklyWorkingHours) {
        this.weeklyWorkingHours.set(weeklyWorkingHours);
    }

    @Override
    public String toString() {
        return "Caregiver" + "\nCID: " + this.cid +
                "\nVorname: " + this.getFirstName() +
                "\nNachname: " + this.getSurname() +
                "\nTelefon: " + this.phoneNumber +
                "\nWochenarbeitszeit: " + this.weeklyWorkingHours +
                "\n";
    }
}

