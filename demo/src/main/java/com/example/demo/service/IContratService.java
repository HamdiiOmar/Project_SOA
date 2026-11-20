package com.example.demo.service;

import com.example.demo.entity.Contrat;
import com.example.demo.entity.Paiement;
import java.util.List;

public interface IContratService {

    Contrat create(Contrat contrat);

    Contrat findById(Long id);

    List<Contrat> findAll();

    Contrat update(Long id, Contrat contrat);

    void deleteById(Long id);

    Contrat creerContrat(Long reservationId, Contrat contrat);

    Paiement ajouterPaiement(Long contratId, Paiement paiement);

    void retirerPaiement(Long contratId, Long paiementId);
}
