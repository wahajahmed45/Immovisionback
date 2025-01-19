package com.example.immovision;
import com.example.immovision.services.GeocodingService;
import com.example.immovision.services.GeocodingService.Coordinates;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class GeocodingServiceTest {

    @Autowired
    private GeocodingService geocodingService;

    private MockWebServer mockWebServer;
    private OkHttpClient client;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockWebServer = new MockWebServer();
        client = new OkHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testGetCoordinatesSuccess() throws IOException {
        String address = "1600 Amphitheatre Parkway, Mountain View, CA";
        String jsonResponse = "{\"results\":[{\"geometry\":{\"location\":{\"lat\":37.4224082,\"lng\":-122.0856086}}}],\"status\":\"OK\"}";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse));
        mockWebServer.start();

        String url = mockWebServer.url("/").toString();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            JsonNode location = jsonNode.get("results").get(0).get("geometry").get("location");
            double lat = location.get("lat").asDouble();
            double lng = location.get("lng").asDouble();

            Coordinates coordinates = geocodingService.getCoordinates(address);
            assertEquals(lat, coordinates.getLat());
            assertEquals(lng, coordinates.getLng());
        }
    }

    @Test
    void testGetCoordinatesFailure() throws IOException {
        String address = "Invalid Address";
        String jsonResponse = "{\"status\":\"ZERO_RESULTS\"}";

        mockWebServer.enqueue(new MockResponse().setBody(jsonResponse).setResponseCode(404));
        mockWebServer.start();

        String url = mockWebServer.url("/").toString();
        Request request = new Request.Builder().url(url).build();

        assertThrows(IOException.class, () -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }
}