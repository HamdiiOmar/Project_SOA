package com.example.demo.repository;

import com.example.demo.entity.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}
