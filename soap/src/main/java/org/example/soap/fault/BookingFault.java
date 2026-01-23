package org.example.soap.fault;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="BookingFault")
public class BookingFault {
    public String code;
    public String message;
}