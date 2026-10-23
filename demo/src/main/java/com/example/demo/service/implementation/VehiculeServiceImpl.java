package com.example.demo.service.implementation;

import com.example.demo.entity.Vehicule;
import com.example.demo.repository.IVehiculeRepository;
import com.example.demo.service.VehiculeService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehiculeServiceImpl implements VehiculeService {

    private final IVehiculeRepository vehiculeRepository;

    public VehiculeServiceImpl(IVehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule add(Vehicule vehicule) {
        vehicule.setIdVehicule(null);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public void delete(Long id) {
        vehiculeRepository.delete(getVehicule(id));
    }

    @Override
    public Vehicule update(Long id, Vehicule data) {
        Vehicule existing = getVehicule(id);
        existing.setImmatriculation(data.getImmatriculation());
        existing.setMarque(data.getMarque());
        existing.setModele(data.getModele());
        existing.setCategorie(data.getCategorie());
        existing.setTarifJournalier(data.getTarifJournalier());
        existing.setStatut(data.getStatut());
        existing.setAgence(data.getAgence());
        return vehiculeRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicule " + id + " not found"));
    }
}
