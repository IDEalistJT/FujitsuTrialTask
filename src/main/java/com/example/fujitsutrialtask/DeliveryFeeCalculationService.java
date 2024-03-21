package com.example.fujitsutrialtask;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryFeeCalculationService {

    private final WeatherDataRepository weatherDataRepository;

    public DeliveryFeeCalculationService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @Transactional(readOnly = true) // Ensure read-only transaction when fetching weather data
    public double calculateDeliveryFee(String city, String vehicleType) {
        // Fetch the latest weather data for the specified city
        WeatherData latestWeatherData = weatherDataRepository.findLatestDataByCity(city);

        // Apply business rules to calculate regional base fee (RBF)
        double baseFee = calculateRegionalBaseFee(city, vehicleType);

        // Apply additional fees based on weather conditions
        double additionalFees = calculateAdditionalFees(latestWeatherData, vehicleType);

        // Calculate total delivery fee
        return baseFee + additionalFees;
    }

    private double calculateRegionalBaseFee(String city, String vehicleType) {
        // Implement business rules to calculate regional base fee (RBF) based on city and vehicle type
        // Sample implementation:
        // You can use switch-case or if-else statements to determine RBF based on city and vehicle type
        // Replace this with your actual logic
        return 0.0; // Placeholder value, replace with your implementation
    }

    private double calculateAdditionalFees(WeatherData weatherData, String vehicleType) {
        // Implement business rules to calculate additional fees based on weather conditions
        // Sample implementation:
        // You can check weather data attributes like air temperature, wind speed, and weather phenomenon
        // and apply fees accordingly
        // Replace this with your actual logic
        return 0.0; // Placeholder value, replace with your implementation
    }
}
