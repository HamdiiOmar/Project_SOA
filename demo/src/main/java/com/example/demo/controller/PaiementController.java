package com.example.demo.controller;

import com.example.demo.entity.Paiement;
import com.example.demo.service.IPaiementService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paiements")
public class PaiementController {

    private final IPaiementService paiementService;

    public PaiementController(IPaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @GetMapping
    public List<Paiement> getAll() {
        return paiementService.findAll();
    }

    @GetMapping("/{id}")
    public Paiement getPaiement(@PathVariable Long id) {
        return paiementService.findById(id);
    }
}
