package com.example.demo.service.implementation;

import com.example.demo.entity.Equipement;
import com.example.demo.entity.Vehicule;
import com.example.demo.exception.BusinessRuleException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IEquipementRepository;
import com.example.demo.repository.IVehiculeRepository;
import com.example.demo.service.IVehiculeService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;

    public VehiculeServiceImpl(IVehiculeRepository vehiculeRepository, IEquipementRepository equipementRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.equipementRepository = equipementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule findById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> findAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    public Vehicule create(Vehicule vehicule) {
        if (vehicule.getIdVehicule() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant doit être null");
        }
        valider(vehicule);
        return vehiculeRepository.save(vehicule);
    }

    @Override
    public Vehicule update(Long id, Vehicule vehicule) {
        Vehicule existant = findById(id);
        valider(vehicule);
        existant.setImmatriculation(vehicule.getImmatriculation());
        existant.setMarque(vehicule.getMarque());
        existant.setModele(vehicule.getModele());
        existant.setCategorie(vehicule.getCategorie());
        existant.setTarifJournalier(vehicule.getTarifJournalier());
        existant.setStatut(vehicule.getStatut());
        existant.setAgence(vehicule.getAgence());
        return vehiculeRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    private void valider(Vehicule vehicule) {
        if (vehicule.getTarifJournalier() != null && vehicule.getTarifJournalier().signum() < 0) {
            throw new IllegalArgumentException("Le tarif journalier ne peut pas être négatif");
        }
    }

    @Override
    public Vehicule affecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = findById(vehiculeId);
        Equipement equipement = chargerEquipement(equipementId);
        if (vehicule.getEquipements().contains(equipement)) {
            throw new BusinessRuleException("L'équipement " + equipementId + " est déjà affecté au véhicule " + vehiculeId);
        }
        vehicule.addEquipement(equipement);
        return vehicule;
    }

    @Override
    public Vehicule desaffecterEquipement(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = findById(vehiculeId);
        Equipement equipement = chargerEquipement(equipementId);
        if (!vehicule.getEquipements().contains(equipement)) {
            throw new BusinessRuleException("L'équipement " + equipementId + " n'est pas affecté au véhicule " + vehiculeId);
        }
        vehicule.removeEquipement(equipement);
        return vehicule;
    }

    private Equipement chargerEquipement(Long id) {
        return equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
    }
}
