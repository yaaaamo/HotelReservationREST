package com.example.hotel_service.repository;
import com.example.hotel_service.model.AvailabilityWindow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityWindowRepository extends JpaRepository<AvailabilityWindow, Long> {

  List<AvailabilityWindow> findByChambreId(Long chambreId);


  List<AvailabilityWindow> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
          LocalDate end, LocalDate start);
}

