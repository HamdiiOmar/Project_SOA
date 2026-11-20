package com.example.demo.service;

import com.example.demo.entity.Agence;
import com.example.demo.entity.Vehicule;
import java.util.List;

public interface IAgenceService {

    Agence create(Agence agence);

    Agence findById(Long id);

    List<Agence> findAll();

    Agence update(Long id, Agence agence);

    void deleteById(Long id);

    Agence affecterEmploye(Long agenceId, Long employeId);

    Agence desaffecterEmploye(Long agenceId, Long employeId);

    Agence affecterVehicule(Long agenceId, Long vehiculeId);

    Agence desaffecterVehicule(Long agenceId, Long vehiculeId);

    Vehicule transfererVehicule(Long vehiculeId, Long agenceCibleId);
}
