package com.example.demo.service;

import com.example.demo.entity.Vehicule;
import java.util.List;

public interface IVehiculeService {

    Vehicule create(Vehicule vehicule);

    Vehicule findById(Long id);

    List<Vehicule> findAll();

    Vehicule update(Long id, Vehicule vehicule);

    void deleteById(Long id);

    Vehicule affecterEquipement(Long vehiculeId, Long equipementId);

    Vehicule desaffecterEquipement(Long vehiculeId, Long equipementId);
}
