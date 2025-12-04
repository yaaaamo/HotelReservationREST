package com.example.hotel_service.data;

import com.example.hotel_service.model.Agency;
import com.example.hotel_service.model.AvailabilityWindow;
import com.example.hotel_service.model.Chambre;
import com.example.hotel_service.model.Hotel;
import com.example.hotel_service.repository.AgencyRepository;
import com.example.hotel_service.repository.AvailabilityWindowRepository;
import com.example.hotel_service.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

  private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

  // infos hôtel injectées depuis application-*.properties
  @Value("${hotel.name}")
  private String hotelName;

  @Value("${hotel.stars}")
  private int hotelStars;

  @Value("${hotel.ville}")
  private String hotelVille;

  @Value("${hotel.pays}")
  private String hotelPays;

  @Value("${hotel.rue}")
  private String hotelRue;

  @Value("${hotel.numero}")
  private String hotelNumero;

  @Value("${hotel.latitude}")
  private Double hotelLat;

  @Value("${hotel.longitude}")
  private Double hotelLng;

  @Value("${hotel.image-url}")
  private String roomImageUrl;

  @Bean
  CommandLineRunner initDatabase(HotelRepository hotelRepo, AvailabilityWindowRepository winRepo) {
    return args -> {

      if (hotelRepo.count() == 0) {

        Hotel hotel = new Hotel(
                hotelName,
                hotelStars,
                hotelPays,
                hotelVille,
                hotelRue,
                hotelNumero,
                hotelLat,
                hotelLng
        );

        //  créer les chambres
        Chambre c101 = new Chambre("101", "SIMPLE", 1, 80.0);
        Chambre c102 = new Chambre("102", "SIMPLE", 1, 80.0);
        Chambre c201 = new Chambre("201", "DOUBLE", 2, 120.0);
        Chambre c202 = new Chambre("202", "DOUBLE", 2, 120.0);
        Chambre c301 = new Chambre("301", "SUITE", 2, 200.0);
        Chambre c401 = new Chambre("401", "FAMILIALE", 4, 250.0);


        // même image pour tout le monde
        c101.setImageUrl(roomImageUrl);
        c102.setImageUrl(roomImageUrl);
        c201.setImageUrl(roomImageUrl);
        c202.setImageUrl(roomImageUrl);
        c301.setImageUrl(roomImageUrl);
        c401.setImageUrl(roomImageUrl);

        // les rattacher à l’hôtel
        hotel.addChambre(c101);
        hotel.addChambre(c102);
        hotel.addChambre(c201);
        hotel.addChambre(c202);
        hotel.addChambre(c301);
        hotel.addChambre(c401);

        hotelRepo.save(hotel);

        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 20),
                1,
                c101
        ));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 20),
                1,
                c102
        ));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 20),
                2,
                c201
        ));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 10),
                LocalDate.of(2025, 12, 20),
                2,
                c202
        ));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 30),
                3,
                c301
        ));
        winRepo.save(new AvailabilityWindow(
                LocalDate.of(2025, 12, 20),
                LocalDate.of(2025, 12, 30),
                4,
                c401
        ));



        logger.info("Hôtel créé: {} ({} étoiles) à {}", hotelName, hotelStars, hotelVille);
      } else {
        logger.info("Base déjà initialisée, aucun hôtel créé.");
      }
    };
  }

  @Bean
  CommandLineRunner initAgencies(AgencyRepository agencyRepo) {
    return args -> {
      if (agencyRepo.count() == 0) {
        agencyRepo.save(new Agency("AGENCE1", "Agence Paris", "secret1", 0.90));
        agencyRepo.save(new Agency("AGENCE2", "Agence Lyon", "secret2", 0.80));
      }
    };
  }
}
