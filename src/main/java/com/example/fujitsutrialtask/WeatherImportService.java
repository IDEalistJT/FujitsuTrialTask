package com.example.fujitsutrialtask;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.containsElement;

@Service
public class WeatherImportService {

    private final WeatherDataRepository weatherDataRepository;

    public WeatherImportService(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    /**
     * Use restTemplate to make HTTP request to the weather portal
     * Save WeatherData entity to the database using WeatherDataRepository
     */
    @Scheduled(cron = "15 * * * * *") // Cron expression for running the task every hour, 15 minutes after full hour
    public void importWeatherData() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        try {
            String response = restTemplate.getForObject(url, String.class);
            List<WeatherData> data = parseWeatherDataFromXML(response);
            for (WeatherData weatherData : data) {
                if (!weatherDataRepository.existsByStationNameAndTimestamp(weatherData.getStationName(), weatherData.getTimestamp())) {
                    weatherDataRepository.save(weatherData); // Save each WeatherData object to the database
                }
            }
            System.out.println("Weather data fetched successfully:\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the XML response and map it to WeatherData entities
     * @param xmlData String of the XMl response
     * @return List of weatherData to then save
     */
    private List<WeatherData> parseWeatherDataFromXML(String xmlData) {
        List<WeatherData> weatherDataList = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlData));
            Document document = builder.parse(inputSource);

            Long timestamp = Long.valueOf(document.getDocumentElement().getAttribute("timestamp"));
            NodeList stationNodes = document.getElementsByTagName("station");
            for (int i = 0; i < stationNodes.getLength(); i++) {
                Element stationElement = (Element) stationNodes.item(i);
                WeatherData weatherData = new WeatherData();
                String name = stationElement.getElementsByTagName("name").item(0).getTextContent();
                if (containsElement(new String[]{"Tallinn-Harku", "Tartu-Tõravere", "Pärnu"}, name)) {
                    weatherData.setStationName(name);
                    weatherData.setWmoCode(stationElement.getElementsByTagName("wmocode").item(0).getTextContent());
                    weatherData.setAirTemperature(Double.parseDouble(stationElement.getElementsByTagName("airtemperature").item(0).getTextContent()));
                    weatherData.setWindSpeed(Double.parseDouble(stationElement.getElementsByTagName("windspeed").item(0).getTextContent()));
                    weatherData.setWeatherPhenomenon(stationElement.getElementsByTagName("phenomenon").item(0).getTextContent());
                    weatherData.setTimestamp(timestamp);
                    weatherDataList.add(weatherData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(weatherDataList.size());
        return weatherDataList;
    }

}
