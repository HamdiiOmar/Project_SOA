package com.example.demo.controller;

import com.example.demo.entity.Maintenance;
import com.example.demo.service.IMaintenanceService;
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
@RequestMapping("/maintenances")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;

    public MaintenanceController(IMaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<Maintenance> getAll() {
        return maintenanceService.findAll();
    }

    @GetMapping("/{id}")
    public Maintenance getMaintenance(@PathVariable Long id) {
        return maintenanceService.findById(id);
    }

    @PostMapping
    public Maintenance add(@RequestBody Maintenance maintenance) {
        return maintenanceService.create(maintenance);
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id, @RequestBody Maintenance maintenance) {
        return maintenanceService.update(id, maintenance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        maintenanceService.deleteById(id);
    }
}
