package com.example.demo.service;

import com.example.demo.entity.Paiement;
import java.util.List;

public interface IPaiementService {

    Paiement findById(Long id);

    List<Paiement> findAll();
}
