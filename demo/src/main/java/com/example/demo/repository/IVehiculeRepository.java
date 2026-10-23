package com.example.demo.repository;

import com.example.demo.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehiculeRepository extends JpaRepository<Vehicule, Long> {
}
