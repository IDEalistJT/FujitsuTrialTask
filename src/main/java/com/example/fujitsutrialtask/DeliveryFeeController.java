package com.example.fujitsutrialtask;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryFeeController {

    private final DeliveryFeeCalculationService deliveryFeeCalculationService;

    public DeliveryFeeController(DeliveryFeeCalculationService deliveryFeeCalculationService) {
        this.deliveryFeeCalculationService = deliveryFeeCalculationService;
    }

    @GetMapping("/delivery-fee")
    public ResponseEntity<?> calculateDeliveryFee(
            @RequestParam String city,
            @RequestParam String vehicleType
    ) {
        try {
            double deliveryFee = deliveryFeeCalculationService.calculateDeliveryFee(city, vehicleType);
            return ResponseEntity.ok(deliveryFee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
