package com.gestiondesabsences.gestiondesabsences.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolYear {
    private int id;
    private String yearLevel;

    private Major major;


    private List<Student> students;
    private List<Module> modules;
    private List<ClassEntity> classes;

    @Override
    public String toString() {
        return yearLevel;
    }
}
