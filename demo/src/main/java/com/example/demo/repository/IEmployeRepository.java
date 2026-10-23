package com.example.demo.repository;

import com.example.demo.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}
