package com.example.demo.controller;

import com.example.demo.entity.Equipement;
import com.example.demo.service.IEquipementService;
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
@RequestMapping("/equipements")
public class EquipementController {

    private final IEquipementService equipementService;

    public EquipementController(IEquipementService equipementService) {
        this.equipementService = equipementService;
    }

    @GetMapping
    public List<Equipement> getAll() {
        return equipementService.findAll();
    }

    @GetMapping("/{id}")
    public Equipement getEquipement(@PathVariable Long id) {
        return equipementService.findById(id);
    }

    @PostMapping
    public Equipement add(@RequestBody Equipement equipement) {
        return equipementService.create(equipement);
    }

    @PutMapping("/{id}")
    public Equipement update(@PathVariable Long id, @RequestBody Equipement equipement) {
        return equipementService.update(id, equipement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        equipementService.deleteById(id);
    }
}
