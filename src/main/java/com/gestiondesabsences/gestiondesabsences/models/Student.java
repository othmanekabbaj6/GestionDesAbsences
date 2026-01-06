package com.gestiondesabsences.gestiondesabsences.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String className;

    private Major major;
    private SchoolYear schoolYear;
    private ClassEntity classEntity;
    // A student can have multiple absences
    private List<Absence> absences;
}
