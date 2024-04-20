package com.mysite.core.services.impl;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.stream.JsonParsingException;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysite.core.services.EventsRegistrationService;

@Component(immediate = true, service = EventsRegistrationService.class)
public class EventsRegistrationServiceImpl implements EventsRegistrationService {
	
	Logger LOGGER = LoggerFactory.getLogger(getClass());

    public boolean submitToMockAPI(String jsonString) {
        // Validate the JSON string
        if (!isValidJson(jsonString)) {
        	LOGGER.error("Invalid JSON string: {}",jsonString);
            return false;
        }

        HttpClient httpClient = HttpClient.newHttpClient();

        // Create the HTTP request with JSON payload
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gk037.wiremockapi.cloud/api/v1/events"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        try {
            // Send the HTTP request and retrieve the response
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

            // Check the response status code
            int statusCode = response.statusCode();
            if (statusCode >= 200 && statusCode < 300) {
                // Successful HTTP response (2xx status code)
                return true;
            } else {
                // Unsuccessful HTTP response
            	LOGGER.error("HTTP request failed with status code: {}", statusCode);
            	if(LOGGER.isDebugEnabled()) {
            		LOGGER.debug("Response body: {}", response.body());
            	}
                return false;
            }
        } catch (IOException | InterruptedException e) {
            // Exception occurred while sending the HTTP request
        	LOGGER.error("Exception occured :::{}",e);
            return false;
        }
    }

    @SuppressWarnings("unused")
    private boolean isValidJson(String jsonString) {
        try {
            // Attempt to parse the JSON string
            JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
			JsonStructure jsonStructure = jsonReader.read();
            jsonReader.close();
            // If parsing succeeds, the JSON string is valid
            return true;
        } catch (JsonParsingException e) {
            // JSON parsing exception occurred, indicating invalid JSON
        	LOGGER.error("Exception occured :::{}",e);
            return false;
        }
    }
}
