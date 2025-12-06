package com.example.hotel_service.repository;

import com.example.hotel_service.model.Chambre;
import com.example.hotel_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

  @Query("SELECT COUNT(r) FROM Reservation r " +
          "WHERE r.chambre = :chambre " +
          "AND r.dateArrivee < :endDate " +
          "AND r.dateDepart > :startDate")
  long countOverlappingReservations(@Param("chambre") Chambre chambre,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

  List<Reservation> findByAgenceId(String agenceId);

  // Find reservations within a date range
  @Query("SELECT r FROM Reservation r " +
          "WHERE r.dateArrivee <= :endDate " +
          "AND r.dateDepart >= :startDate " +
          "ORDER BY r.dateArrivee")
  List<Reservation> findByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

  // Find all reservations ordered by date
  @Query("SELECT r FROM Reservation r ORDER BY r.dateArrivee")
  List<Reservation> findAllOrderByDateArrivee();
}
