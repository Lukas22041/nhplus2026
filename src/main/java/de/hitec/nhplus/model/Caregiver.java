package de.hitec.nhplus.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Caregiver extends Person {
    private SimpleLongProperty cid;
    private final SimpleStringProperty phoneNumber;
    private final SimpleStringProperty weeklyWorkingHours;
    private final SimpleBooleanProperty deleted;
    private final SimpleObjectProperty<LocalDate> deletionScheduledDate;
    private final SimpleObjectProperty<LocalDate> scheduledDeletionDate;

    /**
     * Erstellt eine neue Pflegekraft (ohne Soft-Delete-Felder, Standardwerte).
     */
    public Caregiver(String firstName, String surname, String phoneNumber, String weeklyWorkingHours) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(0);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.weeklyWorkingHours = new SimpleStringProperty(weeklyWorkingHours);
        this.deleted = new SimpleBooleanProperty(false);
        this.deletionScheduledDate = new SimpleObjectProperty<>(null);
        this.scheduledDeletionDate = new SimpleObjectProperty<>(null);
    }

    /**
     * Lädt eine Pflegekraft aus der Datenbank (mit Soft-Delete-Feldern).
     */
    public Caregiver(long cid, String firstName, String surname, String phoneNumber, String weeklyWorkingHours,
                     boolean deleted, LocalDate deletionScheduledDate, LocalDate scheduledDeletionDate) {
        super(firstName, surname);
        this.cid = new SimpleLongProperty(cid);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.weeklyWorkingHours = new SimpleStringProperty(weeklyWorkingHours);
        this.deleted = new SimpleBooleanProperty(deleted);
        this.deletionScheduledDate = new SimpleObjectProperty<>(deletionScheduledDate);
        this.scheduledDeletionDate = new SimpleObjectProperty<>(scheduledDeletionDate);
    }

    /**
     * Abwärtskompatiler Konstruktor (ohne Soft-Delete-Felder).
     */
    public Caregiver(long cid, String firstName, String surname, String phoneNumber, String weeklyWorkingHours) {
        this(cid, firstName, surname, phoneNumber, weeklyWorkingHours, false, null, null);
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

    public boolean isDeleted() {
        return deleted.get();
    }

    public SimpleBooleanProperty deletedProperty() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted.set(deleted);
    }

    public LocalDate getDeletionScheduledDate() {
        return deletionScheduledDate.get();
    }

    public SimpleObjectProperty<LocalDate> deletionScheduledDateProperty() {
        return deletionScheduledDate;
    }

    public void setDeletionScheduledDate(LocalDate date) {
        this.deletionScheduledDate.set(date);
    }

    public LocalDate getScheduledDeletionDate() {
        return scheduledDeletionDate.get();
    }

    public SimpleObjectProperty<LocalDate> scheduledDeletionDateProperty() {
        return scheduledDeletionDate;
    }

    public void setScheduledDeletionDate(LocalDate date) {
        this.scheduledDeletionDate.set(date);
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

