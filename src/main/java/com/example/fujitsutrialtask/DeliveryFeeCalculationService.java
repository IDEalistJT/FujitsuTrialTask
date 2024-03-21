package com.example.fujitsutrialtask;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.util.ObjectUtils.containsElement;

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
        switch (city) {
            case "Tallinn" -> {
                return switch (vehicleType) {
                    case "Car" -> 4;
                    case "Scooter" -> 3.5;
                    case "Bike" -> 3;
                    default -> throw new RuntimeException("Invalid vehicle type");
                };
            }
            case "Tartu" -> {
                return switch (vehicleType) {
                    case "Car" -> 3.5;
                    case "Scooter" -> 3;
                    case "Bike" -> 2.5;
                    default -> throw new RuntimeException("Invalid vehicle type");
                };
            }
            case "Pärnu" -> {
                return switch (vehicleType) {
                    case "Car" -> 3;
                    case "Scooter" -> 2.5;
                    case "Bike" -> 2;
                    default -> throw new RuntimeException("Invalid vehicle type");
                };
            }
            default -> throw new RuntimeException("Invalid city");
        }
    }

    private double calculateAdditionalFees(WeatherData weatherData, String vehicleType) {
        double fee = 0.0;
        if (vehicleType.equals("Scooter") || vehicleType.equals("Bike")){
            if (weatherData.getAirTemperature() < -10) fee += 1;
            else if (weatherData.getAirTemperature() <= 0) fee += 0.5;
        }
        if (vehicleType.equals("Bike")){
            if (weatherData.getWindSpeed() > 20) throw new RuntimeException("Usage of selected vehicle type is forbidden");
            else if (weatherData.getWindSpeed() > 10) fee += 0.5;
        }
        if (vehicleType.equals("Scooter") || vehicleType.equals("Bike")){
            if (containsElement(new String[]{"Light snow shower", "Moderate snow shower", "Heavy snow shower", "Light sleet", "Moderate sleet", "Light snowfall", "Moderate snowfall", "Heavy snowfall", "Blowing snow", "Drifting snow"}, weatherData.getWeatherPhenomenon())) fee += 1;
            else if (containsElement(new String[]{"Light shower", "Moderate shower", "Heavy shower", "Light rain", "Moderate rain", "Heavy rain"}, weatherData.getWeatherPhenomenon())) fee += 0.5;
            else if (containsElement(new String[]{"Glaze", "Hail", "Thunder", "Thunderstorm"}, weatherData.getWeatherPhenomenon())) throw new RuntimeException("Usage of selected vehicle type is forbidden");
        }
        return fee;
    }
}
