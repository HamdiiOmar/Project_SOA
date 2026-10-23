package com.example.demo.repository;

import com.example.demo.entity.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContratRepository extends JpaRepository<Contrat, Long> {
}
