package com.example.hotel_service.repository;
import com.example.hotel_service.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, String> {
}

