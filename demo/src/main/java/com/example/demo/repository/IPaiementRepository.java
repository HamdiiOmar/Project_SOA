package com.example.demo.repository;

import com.example.demo.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPaiementRepository extends JpaRepository<Paiement, Long> {
}
