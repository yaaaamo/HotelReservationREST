package com.example.hotel_service.controller;

import com.example.hotel_service.model.Hotel;
import com.example.hotel_service.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HotelController {

  @Autowired
  private HotelRepository hotelRepository;

  private static final String uri = "hotelservice/api";

  @GetMapping(uri+"/hotels")
  public List<Hotel> getAllHotels(){
    return hotelRepository.findAll();
  }

  @GetMapping(uri+"/hotels/count") public String count() {
    return String.format("{\"%s\": %d}", "count", hotelRepository.count());
  }




}
