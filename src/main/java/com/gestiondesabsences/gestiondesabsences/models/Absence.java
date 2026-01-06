package com.gestiondesabsences.gestiondesabsences.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Absence {
    private int id;
    private LocalDate date;

    private Student student;
    private Module module;
}
