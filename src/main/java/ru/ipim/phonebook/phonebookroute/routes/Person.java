package ru.ipim.phonebook.phonebookroute.routes;

import lombok.*;
import java.io.Serializable;

// Запись для импорта в справочник телефонная книга
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Person implements Serializable {
    private String firstName;
    private String lastName;
    private String mobilePhone;
    private String workPhone;
    private String email;
    private String birthdate;
    private String company;
    private String jobTitle;
    private String address;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }

}
