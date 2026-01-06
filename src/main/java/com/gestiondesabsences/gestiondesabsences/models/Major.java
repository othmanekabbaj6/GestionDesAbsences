package com.gestiondesabsences.gestiondesabsences.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Major {
    private int id;
    private String name;


    private List<SchoolYear> schoolYears;

    @Override
    public String toString() {
        return name;
    }
}