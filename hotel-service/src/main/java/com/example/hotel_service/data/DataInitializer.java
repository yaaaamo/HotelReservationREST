package com.example.hotel_service.data;

import com.example.hotel_service.model.Agency;
import com.example.hotel_service.model.Chambre;
import com.example.hotel_service.model.Hotel;
import com.example.hotel_service.repository.AgencyRepository;
import com.example.hotel_service.repository.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Bean
  CommandLineRunner initDatabase(HotelRepository hotelRepo) {
    return args -> {

      if (hotelRepo.count() == 0) {

        // créer l’hôtel spécifique
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

        // ajouter les mêmes chambres pour tous les hôtels

        hotel.addChambre(new Chambre("101", "SIMPLE", 1, 80.0));
        hotel.addChambre(new Chambre("102", "SIMPLE", 1, 80.0));
        hotel.addChambre(new Chambre("201", "DOUBLE", 2, 120.0));
        hotel.addChambre(new Chambre("202", "DOUBLE", 2, 120.0));
        hotel.addChambre(new Chambre("301", "SUITE", 2, 200.0));
        hotel.addChambre(new Chambre("401", "FAMILIALE", 4, 250.0));


        hotelRepo.save(hotel);

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
        // AGENCY1 : 10% reduction
        agencyRepo.save(new Agency("AGENCE1", "Agence Paris", "secret1", 0.90));

        // AGENCY2 : 20% reduction
        agencyRepo.save(new Agency("AGENCE2", "Agence Lyon", "secret2", 0.80));
      }
    };
  }


}
