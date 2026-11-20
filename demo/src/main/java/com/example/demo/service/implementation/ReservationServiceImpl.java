package com.example.demo.service.implementation;

import com.example.demo.entity.Client;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.Vehicule;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IClientRepository;
import com.example.demo.repository.IReservationRepository;
import com.example.demo.repository.IVehiculeRepository;
import com.example.demo.service.IReservationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;

    public ReservationServiceImpl(IReservationRepository reservationRepository, IClientRepository clientRepository, IVehiculeRepository vehiculeRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation create(Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant doit être null");
        }
        valider(reservation);
        return reservationRepository.save(reservation);
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        Reservation existant = findById(id);
        valider(reservation);
        existant.setDateDebut(reservation.getDateDebut());
        existant.setDateFin(reservation.getDateFin());
        existant.setStatut(reservation.getStatut());
        existant.setVehicule(reservation.getVehicule());
        existant.setClient(reservation.getClient());
        existant.setContrat(reservation.getContrat());
        return reservationRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        Reservation existant = findById(id);
        existant.retirerContrat();
        reservationRepository.delete(existant);
    }

    @Override
    public Reservation creerReservation(Long clientId, Long vehiculeId, Reservation reservation) {
        if (reservation.getIdReservation() != null) {
            throw new IllegalArgumentException("Création refusée : l'identifiant doit être null");
        }
        valider(reservation);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", vehiculeId));
        Reservation nouvelle = new Reservation();
        nouvelle.setDateDebut(reservation.getDateDebut());
        nouvelle.setDateFin(reservation.getDateFin());
        nouvelle.setStatut(reservation.getStatut());
        client.addReservation(nouvelle);
        vehicule.addReservation(nouvelle);
        return reservationRepository.save(nouvelle);
    }

    private void valider(Reservation reservation) {
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null && reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
