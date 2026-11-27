package com.example.demo.controller;

import com.example.demo.entity.Vehicule;
import com.example.demo.service.IVehiculeService;
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
@RequestMapping("/vehicules")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    public VehiculeController(IVehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public List<Vehicule> getAll() {
        return vehiculeService.findAll();
    }

    @GetMapping("/{id}")
    public Vehicule getVehicule(@PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @PostMapping
    public Vehicule add(@RequestBody Vehicule vehicule) {
        return vehiculeService.create(vehicule);
    }

    @PutMapping("/{id}")
    public Vehicule update(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        return vehiculeService.update(id, vehicule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @Operation(summary = "Affecte un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté, véhicule renvoyé avec ses équipements"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @PostMapping("/{id}/equipements/{equipementId}")
    public Vehicule affecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        return vehiculeService.affecterEquipement(id, equipementId);
    }

    @Operation(summary = "Retire un équipement d'un véhicule (l'équipement n'est pas supprimé)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement retiré"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable"),
            @ApiResponse(responseCode = "409", description = "Opération refusée par une règle de gestion")
    })
    @DeleteMapping("/{id}/equipements/{equipementId}")
    public Vehicule desaffecterEquipement(@PathVariable Long id, @PathVariable Long equipementId) {
        return vehiculeService.desaffecterEquipement(id, equipementId);
    }

    @Operation(summary = "Crée un véhicule et l'affecte à une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé et affecté"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource introuvable")
    })
    @PostMapping("/agence/{agenceId}")
    public ResponseEntity<Vehicule> addVehiculeAndAssignToAgence(@PathVariable Long agenceId,
            @RequestBody Vehicule vehicule) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehiculeService.addVehiculeAndAssignToAgence(vehicule, agenceId));
    }
}
