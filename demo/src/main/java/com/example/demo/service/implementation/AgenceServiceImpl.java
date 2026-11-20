package com.example.demo.service.implementation;

import com.example.demo.entity.Agence;
import com.example.demo.entity.Employe;
import com.example.demo.entity.Vehicule;
import com.example.demo.exception.BusinessRuleException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IAgenceRepository;
import com.example.demo.repository.IEmployeRepository;
import com.example.demo.repository.IVehiculeRepository;
import com.example.demo.service.IAgenceService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;
    private final IVehiculeRepository vehiculeRepository;

    public AgenceServiceImpl(IAgenceRepository agenceRepository, IEmployeRepository employeRepository, IVehiculeRepository vehiculeRepository) {
        this.agenceRepository = agenceRepository;
        this.employeRepository = employeRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    public Agence create(Agence agence) {
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant doit être null");
        }
        valider(agence);
        return agenceRepository.save(agence);
    }

    @Override
    public Agence update(Long id, Agence agence) {
        Agence existant = findById(id);
        valider(agence);
        existant.setNom(agence.getNom());
        existant.setVille(agence.getVille());
        existant.setAdresse(agence.getAdresse());
        existant.setTelephone(agence.getTelephone());
        return agenceRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private void valider(Agence agence) {
        if (agence.getNom() == null || agence.getNom().isBlank()) {
            throw new IllegalArgumentException("Le nom de l'agence est obligatoire");
        }
    }

    @Override
    public Agence affecterEmploye(Long agenceId, Long employeId) {
        Agence agence = findById(agenceId);
        Employe employe = chargerEmploye(employeId);
        if (employe.getAgence() != null) {
            throw new BusinessRuleException("L'employé " + employeId + " est déjà affecté à l'agence "
                    + employe.getAgence().getIdAgence());
        }
        agence.addEmploye(employe);
        return agence;
    }

    @Override
    public Agence desaffecterEmploye(Long agenceId, Long employeId) {
        Agence agence = findById(agenceId);
        Employe employe = chargerEmploye(employeId);
        if (employe.getAgence() == null || !agenceId.equals(employe.getAgence().getIdAgence())) {
            throw new BusinessRuleException("L'employé " + employeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeEmploye(employe);
        return agence;
    }

    @Override
    public Agence affecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = findById(agenceId);
        Vehicule vehicule = chargerVehicule(vehiculeId);
        if (vehicule.getAgence() != null) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId + " est déjà affecté à l'agence "
                    + vehicule.getAgence().getIdAgence());
        }
        agence.addVehicule(vehicule);
        return agence;
    }

    @Override
    public Agence desaffecterVehicule(Long agenceId, Long vehiculeId) {
        Agence agence = findById(agenceId);
        Vehicule vehicule = chargerVehicule(vehiculeId);
        if (vehicule.getAgence() == null || !agenceId.equals(vehicule.getAgence().getIdAgence())) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId + " n'est pas affecté à l'agence " + agenceId);
        }
        agence.removeVehicule(vehicule);
        return agence;
    }

    @Override
    public Vehicule transfererVehicule(Long vehiculeId, Long agenceCibleId) {
        Vehicule vehicule = chargerVehicule(vehiculeId);
        Agence cible = findById(agenceCibleId);
        Agence source = vehicule.getAgence();
        if (source == null) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId
                    + " n'est affecté à aucune agence : utilisez l'affectation");
        }
        if (agenceCibleId.equals(source.getIdAgence())) {
            throw new BusinessRuleException("Le véhicule " + vehiculeId + " est déjà dans l'agence " + agenceCibleId);
        }
        source.removeVehicule(vehicule);
        cible.addVehicule(vehicule);
        return vehicule;
    }

    private Employe chargerEmploye(Long id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
    }

    private Vehicule chargerVehicule(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
    }
}
