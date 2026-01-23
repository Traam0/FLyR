package org.example.model.entities;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.example.soap.adapters.LocalDateAdapter;

import java.time.LocalDate;

@XmlRootElement(name="Client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passportNumber;
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate birthDate;

    public Client() {}

    public Client(int id, String firstName, String lastName, String email,
                  String phone, String passportNumber, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
        this.birthDate = birthDate;
    }

    public int getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}