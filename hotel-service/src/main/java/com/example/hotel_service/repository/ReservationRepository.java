package com.example.hotel_service.repository;

import com.example.hotel_service.model.Chambre;
import com.example.hotel_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
  @Query("SELECT COUNT(r) FROM Reservation r " +
          "WHERE r.chambre = :chambre " +
          "AND r.dateArrivee < :endDate " +
          "AND r.dateDepart > :startDate")
  long countOverlappingReservations(@Param("chambre") Chambre chambre,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
