package com.mysite.core.services;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.mysite.core.services.EventsRegistrationService;
import com.mysite.core.services.impl.EventsRegistrationServiceImpl;

import io.wcm.testing.mock.aem.junit5.AemContext;

class EventsRegistrationServiceImplTest {

    private AemContext context;
    private EventsRegistrationService service;

    @BeforeEach
    void setUp() {
        context = new AemContext();
        context.registerService(HttpClient.class, Mockito.mock(HttpClient.class));
        context.registerService(EventsRegistrationService.class, new EventsRegistrationServiceImpl());
        service = context.getService(EventsRegistrationService.class);
    }

    @Test
    void testSubmitToMockAPI_SuccessfulResponse() throws IOException, InterruptedException {
        // Success json Values
        String jsonString = "[{\"firstName\": \"Bhargav\",\"lastName\": \"Thogata\",\"email\": \"bhargavt.ece@gmail.com\"}]";
        HttpClient httpClient = context.getService(HttpClient.class);
        HttpResponse<String> successfulResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(successfulResponse.statusCode()).thenReturn(200);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(successfulResponse);

        // Act
        boolean result = service.submitToMockAPI(jsonString);

        // Assert
        assertTrue(result);
    }

    @Test
    void testSubmitToMockAPI_UnsuccessfulResponse() throws IOException, InterruptedException {
        // Arrange
        String jsonString = "{\"key\": \"value\"}";
        HttpClient httpClient = context.getService(HttpClient.class);
        HttpResponse<String> unsuccessfulResponse = Mockito.mock(HttpResponse.class);
        Mockito.when(unsuccessfulResponse.statusCode()).thenReturn(404);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(unsuccessfulResponse);

        // Act
        boolean result = service.submitToMockAPI(jsonString);

        // Assert
        assertFalse(result);
    }

    @Test
    void testSubmitToMockAPI_ExceptionThrown() throws IOException, InterruptedException {
        // Arrange
        String jsonString = "{\"key\": \"value\"}";
        HttpClient httpClient = context.getService(HttpClient.class);
        Mockito.when(httpClient.send(Mockito.any(HttpRequest.class), Mockito.eq(HttpResponse.BodyHandlers.ofString()))).thenThrow(IOException.class);

        // Act
        boolean result = service.submitToMockAPI(jsonString);

        // Assert
        assertFalse(result);
    }
}
