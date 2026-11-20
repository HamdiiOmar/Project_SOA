package com.example.demo.controller;

import com.example.demo.entity.Contrat;
import com.example.demo.entity.Paiement;
import com.example.demo.service.IContratService;
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
@RequestMapping("/contrats")
public class ContratController {

    private final IContratService contratService;

    public ContratController(IContratService contratService) {
        this.contratService = contratService;
    }

    @GetMapping
    public List<Contrat> getAll() {
        return contratService.findAll();
    }

    @GetMapping("/{id}")
    public Contrat getContrat(@PathVariable Long id) {
        return contratService.findById(id);
    }

    @PostMapping
    public Contrat add(@RequestBody Contrat contrat) {
        return contratService.create(contrat);
    }

    @PutMapping("/{id}")
    public Contrat update(@PathVariable Long id, @RequestBody Contrat contrat) {
        return contratService.update(id, contrat);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        contratService.deleteById(id);
    }

    @Operation(summary = "Ajoute un paiement à un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Paiement ajouté"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable")
    })
    @PostMapping("/{id}/paiements")
    public ResponseEntity<Paiement> ajouterPaiement(@PathVariable Long id, @RequestBody Paiement paiement) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.ajouterPaiement(id, paiement));
    }

    @Operation(summary = "Retire (supprime) un paiement d'un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @DeleteMapping("/{id}/paiements/{paiementId}")
    public ResponseEntity<Void> retirerPaiement(@PathVariable Long id, @PathVariable Long paiementId) {
        contratService.retirerPaiement(id, paiementId);
        return ResponseEntity.noContent().build();
    }
}
