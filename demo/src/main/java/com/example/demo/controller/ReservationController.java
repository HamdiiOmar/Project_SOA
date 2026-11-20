package com.example.demo.controller;

import com.example.demo.entity.Contrat;
import com.example.demo.entity.Reservation;
import com.example.demo.service.IContratService;
import com.example.demo.service.IReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final IReservationService reservationService;
    private final IContratService contratService;

    public ReservationController(IReservationService reservationService, IContratService contratService) {
        this.reservationService = reservationService;
        this.contratService = contratService;
    }

    @GetMapping
    public List<Reservation> getAll() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public Reservation getReservation(@PathVariable Long id) {
        return reservationService.findById(id);
    }

    @PostMapping
    public Reservation add(@RequestBody Reservation reservation) {
        return reservationService.create(reservation);
    }

    @PutMapping("/{id}")
    public Reservation update(@PathVariable Long id, @RequestBody Reservation reservation) {
        return reservationService.update(id, reservation);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reservationService.deleteById(id);
    }

    @Operation(summary = "Crée le contrat d'une réservation, avec ses paiements éventuels")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @PostMapping("/{id}/contrat")
    public ResponseEntity<Contrat> creerContrat(@PathVariable Long id, @RequestBody Contrat contrat) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.creerContrat(id, contrat));
    }

    @Operation(summary = "Crée une réservation pour un client et un véhicule donnés")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable")
    })
    @PostMapping("/client/{clientId}/vehicule/{vehiculeId}")
    public ResponseEntity<Reservation> creerReservation(@PathVariable Long clientId,
            @PathVariable Long vehiculeId, @RequestBody Reservation reservation) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.creerReservation(clientId, vehiculeId, reservation));
    }
}
