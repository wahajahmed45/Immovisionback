package com.example.immovision.services;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GeocodingService {

    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Coordinates getCoordinates(String address) throws IOException {
        String url = NOMINATIM_API_URL + "?q=" + address + "&format=json&limit=1";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            JsonNode location = jsonNode.get(0);
            double lat = location.get("lat").asDouble();
            double lng = location.get("lon").asDouble();

            return new Coordinates(lat, lng);
        }
    }

    public static class Coordinates {
        private final double lat;
        private final double lng;

        public Coordinates(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}