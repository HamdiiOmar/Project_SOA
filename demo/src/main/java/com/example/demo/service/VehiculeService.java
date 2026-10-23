package com.example.demo.service;

import com.example.demo.entity.Vehicule;
import java.util.List;

public interface VehiculeService {

    List<Vehicule> getAll();

    Vehicule add(Vehicule vehicule);

    void delete(Long id);

    Vehicule update(Long id, Vehicule data);

    Vehicule getVehicule(Long id);
}
