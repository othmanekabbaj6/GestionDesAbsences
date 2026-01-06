package com.gestiondesabsences.gestiondesabsences.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassEntity {
    private int id;
    private String className;

    private SchoolYear schoolYear; // linked school year
}
