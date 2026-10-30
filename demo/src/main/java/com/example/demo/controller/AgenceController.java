package com.example.demo.controller;

import com.example.demo.entity.Agence;
import com.example.demo.service.IAgenceService;
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
}
