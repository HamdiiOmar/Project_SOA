package com.example.demo.controller;

import com.example.demo.entity.Employe;
import com.example.demo.service.IEmployeService;
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
@RequestMapping("/employes")
public class EmployeController {

    private final IEmployeService employeService;

    public EmployeController(IEmployeService employeService) {
        this.employeService = employeService;
    }

    @GetMapping
    public List<Employe> getAll() {
        return employeService.findAll();
    }

    @GetMapping("/{id}")
    public Employe getEmploye(@PathVariable Long id) {
        return employeService.findById(id);
    }

    @PostMapping
    public Employe add(@RequestBody Employe employe) {
        return employeService.create(employe);
    }

    @PutMapping("/{id}")
    public Employe update(@PathVariable Long id, @RequestBody Employe employe) {
        return employeService.update(id, employe);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeService.deleteById(id);
    }
}
