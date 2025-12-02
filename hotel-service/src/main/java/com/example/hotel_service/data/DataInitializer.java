package com.example.hotel_service.data;

import com.example.hotel_service.model.Chambre;
import com.example.hotel_service.model.Hotel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.hotel_service.repository.HotelRepository;

@Configuration
public class DataInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

  @Bean
  public CommandLineRunner initDatabase(HotelRepository hotelRepository) {
    return args -> {

      String hotelName = System.getProperty("hotel.name", "Hotel Paradise");
      int etoiles = Integer.parseInt(System.getProperty("hotel.etoiles", "4"));
      String ville = System.getProperty("hotel.ville", "Paris");

      Hotel hotel = new Hotel(
              hotelName,
              etoiles,
              "France",
              ville,
              "Avenue des Champs-Élysées",
              "123",
              48.8566,
              2.3522
      );

      hotel.addChambre(new Chambre("101", "SIMPLE", 1, 80.0));
      hotel.addChambre(new Chambre("102", "SIMPLE", 1, 80.0));
      hotel.addChambre(new Chambre("201", "DOUBLE", 2, 120.0));
      hotel.addChambre(new Chambre("202", "DOUBLE", 2, 120.0));
      hotel.addChambre(new Chambre("301", "SUITE", 2, 200.0));
      hotel.addChambre(new Chambre("401", "FAMILIALE", 4, 250.0));

      hotelRepository.save(hotel);
      logger.info("Hôtel créé: {} ({} étoiles) à {}", hotelName, etoiles, ville);
    };
  }

}
