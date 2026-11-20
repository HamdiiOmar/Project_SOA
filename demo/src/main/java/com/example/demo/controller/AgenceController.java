package com.example.demo.controller;

import com.example.demo.entity.Agence;
import com.example.demo.entity.Vehicule;
import com.example.demo.service.IAgenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agences")
public class AgenceController {

    private final IAgenceService agenceService;

    public AgenceController(IAgenceService agenceService) {
        this.agenceService = agenceService;
    }

    @GetMapping
    public List<Agence> getAll() {
        return agenceService.findAll();
    }

    @GetMapping("/{id}")
    public Agence getAgence(@PathVariable Long id) {
        return agenceService.findById(id);
    }

    @PostMapping
    public Agence add(@RequestBody Agence agence) {
        return agenceService.create(agence);
    }

    @PutMapping("/{id}")
    public Agence update(@PathVariable Long id, @RequestBody Agence agence) {
        return agenceService.update(id, agence);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        agenceService.deleteById(id);
    }

    @Operation(summary = "Affecte un employé à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé affecté, agence renvoyée avec ses employés"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @PostMapping("/{agenceId}/employes/{employeId}")
    public Agence affecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        return agenceService.affecterEmploye(agenceId, employeId);
    }

    @Operation(summary = "Désaffecte un employé d'une agence (l'employé n'est pas supprimé)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé désaffecté"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @DeleteMapping("/{agenceId}/employes/{employeId}")
    public Agence desaffecterEmploye(@PathVariable Long agenceId, @PathVariable Long employeId) {
        return agenceService.desaffecterEmploye(agenceId, employeId);
    }

    @Operation(summary = "Affecte un véhicule à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule affecté, agence renvoyée avec ses véhicules"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @PostMapping("/{agenceId}/vehicules/{vehiculeId}")
    public Agence affecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        return agenceService.affecterVehicule(agenceId, vehiculeId);
    }

    @Operation(summary = "Désaffecte un véhicule d'une agence (le véhicule n'est pas supprimé)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule désaffecté"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @DeleteMapping("/{agenceId}/vehicules/{vehiculeId}")
    public Agence desaffecterVehicule(@PathVariable Long agenceId, @PathVariable Long vehiculeId) {
        return agenceService.desaffecterVehicule(agenceId, vehiculeId);
    }

    @Operation(summary = "Transfère un véhicule vers une autre agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule transféré"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @PostMapping("/{agenceCibleId}/vehicules/{vehiculeId}/transfert")
    public Vehicule transfererVehicule(@PathVariable Long agenceCibleId, @PathVariable Long vehiculeId) {
        return agenceService.transfererVehicule(vehiculeId, agenceCibleId);
    }
}
