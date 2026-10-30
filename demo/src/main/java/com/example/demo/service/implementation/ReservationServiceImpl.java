package com.example.demo.service.implementation;

import com.example.demo.entity.Reservation;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.IReservationRepository;
import com.example.demo.service.IReservationService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;

    public ReservationServiceImpl(IReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
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
        if (existant.getContrat() != null) {
            existant.getContrat().setReservation(null);
        }
        reservationRepository.delete(existant);
    }

    private void valider(Reservation reservation) {
        if (reservation.getDateDebut() != null && reservation.getDateFin() != null && reservation.getDateFin().isBefore(reservation.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
