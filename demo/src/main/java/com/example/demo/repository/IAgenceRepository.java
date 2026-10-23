package com.example.demo.repository;

import com.example.demo.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}
