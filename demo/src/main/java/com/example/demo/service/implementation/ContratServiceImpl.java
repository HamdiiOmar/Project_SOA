package com.example.demo.service.implementation;

import com.example.demo.entity.Contrat;
import com.example.demo.entity.Paiement;
import com.example.demo.entity.Reservation;
import com.example.demo.exception.BusinessRuleException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IContratRepository;
import com.example.demo.repository.IPaiementRepository;
import com.example.demo.repository.IReservationRepository;
import com.example.demo.service.IContratService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final IPaiementRepository paiementRepository;

    public ContratServiceImpl(IContratRepository contratRepository, IReservationRepository reservationRepository, IPaiementRepository paiementRepository) {
        this.contratRepository = contratRepository;
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    public Contrat create(Contrat contrat) {
        if (contrat.getIdContrat() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant doit être null");
        }
        return enregistrer(contrat);
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existant = findById(id);
        valider(contrat);
        existant.setDateSignature(contrat.getDateSignature());
        existant.setMontantTotal(contrat.getMontantTotal());
        existant.setValide(contrat.isValide());
        return contratRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        Contrat existant = findById(id);
        if (existant.getReservation() != null) {
            existant.getReservation().retirerContrat();
        }
        contratRepository.delete(existant);
    }

    @Override
    public Contrat creerContrat(Long reservationId, Contrat contrat) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", reservationId));
        if (reservation.getContrat() != null) {
            throw new BusinessRuleException("La réservation " + reservationId + " a déjà un contrat");
        }
        Contrat nouveau = enregistrer(contrat);
        reservation.etablirContrat(nouveau);
        return nouveau;
    }

    @Override
    public Paiement ajouterPaiement(Long contratId, Paiement paiement) {
        Contrat contrat = findById(contratId);
        validerPaiement(paiement);
        contrat.addPaiement(paiement);
        contratRepository.flush();
        return paiement;
    }

    @Override
    public void retirerPaiement(Long contratId, Long paiementId) {
        Contrat contrat = findById(contratId);
        Paiement paiement = contrat.getPaiements().stream()
                .filter(p -> paiementId.equals(p.getIdPaiement()))
                .findFirst()
                .orElseThrow(() -> paiementAbsent(contratId, paiementId));
        contrat.removePaiement(paiement);
    }

    private RuntimeException paiementAbsent(Long contratId, Long paiementId) {
        if (!paiementRepository.existsById(paiementId)) {
            return new ResourceNotFoundException("Paiement", paiementId);
        }
        return new BusinessRuleException("Le paiement " + paiementId + " n'appartient pas au contrat " + contratId);
    }

    private Contrat enregistrer(Contrat source) {
        valider(source);
        Contrat nouveau = new Contrat();
        nouveau.setDateSignature(source.getDateSignature());
        nouveau.setMontantTotal(source.getMontantTotal());
        nouveau.setValide(source.isValide());
        contratRepository.save(nouveau);
        for (Paiement paiement : source.getPaiements()) {
            validerPaiement(paiement);
            nouveau.addPaiement(paiement);
            contratRepository.flush();
        }
        return nouveau;
    }

    private void validerPaiement(Paiement paiement) {
        if (paiement.getIdPaiement() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant du paiement doit être null");
        }
        if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
            throw new IllegalArgumentException("Le montant d'un paiement doit être strictement positif");
        }
    }

    private void valider(Contrat contrat) {
        if (contrat.getMontantTotal() != null && contrat.getMontantTotal().signum() < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
    }
}
