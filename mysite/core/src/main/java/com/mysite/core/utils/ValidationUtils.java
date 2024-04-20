package com.mysite.core.utils;

import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationUtils.class);

    public static boolean validateParams(Map<String, String[]> parameterMap, HttpServletResponse response) throws IOException {
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            if (paramValues == null || paramValues.length == 0 || StringUtils.isEmpty(paramValues[0])) {
                LOGGER.error("Missing parameter: {}", paramName);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Missing parameter: " + paramName);
                return false;
            }
            if (!isValidParameter(paramName, paramValues[0])) {
                LOGGER.error("Invalid value for parameter {}: {}", paramName, paramValues[0]);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid value for parameter " + paramName);
                return false;
            }
        }
        return true;
    }

    private static boolean isValidParameter(String paramName, String paramValue) {
        switch (paramName) {
            case "firstName":
            case "lastName":
                return isValidName(paramValue);
            case "email":
                return isValidEmail(paramValue);
            // Add more cases for additional parameters and their validations as needed
            default:
                LOGGER.warn("Unknown parameter: {}", paramName);
                return true; // Assume unknown parameters are valid
        }
    }

    private static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }

    private static boolean isValidEmail(String email) {
        // Simple email validation regex
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
