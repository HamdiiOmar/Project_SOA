package com.example.demo.controller;

import com.example.demo.entity.Contrat;
import com.example.demo.service.IContratService;
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
}
