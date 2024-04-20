package com.mysite.core.servlets;

import java.io.IOException;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysite.core.services.EventsRegistrationService;
import com.mysite.core.utils.FormDataUtils;
import com.mysite.core.utils.ValidationUtils;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(resourceTypes = "mysite/components/page", methods = HttpConstants.METHOD_POST, selectors = "formHandler", extensions = "html")
@ServiceDescription("Event Registration Servlet")

public class EventLandingServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = -6456681538458121350L;
	Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Reference
	EventsRegistrationService eventsRegistrationService;

	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		// Validate request parameters
		Map<String, String[]> parameterMap = request.getParameterMap();
		if (!ValidationUtils.validateParams(parameterMap, response)) {
            return; // Exit method if validation fails
        }

		// Construct JSON payload
		ArrayNode jsonPayload = createJsonPayload(parameterMap);

		// Submit form data to mock API
		boolean success = eventsRegistrationService.submitToMockAPI(jsonPayload.toString());

		// Send response based on API submission result
		if (success) {
			// Create a node and save properties with request params
			try {
				FormDataUtils.saveFormData(request, parameterMap);
			} catch (RepositoryException e) {
				LOGGER.error("RepositoryException occured. Unable to save data in JCR :: {}",e);
			}
			response.getWriter().write("Form data submitted successfully!");
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.getWriter().write("Failed to submit form data to mock API.");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private ArrayNode createJsonPayload(Map<String, String[]> parameterMap) {
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode jsonPayloadArray = objectMapper.createArrayNode();
		ObjectNode jsonPayload = objectMapper.createObjectNode();
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String paramName = entry.getKey();
			String paramValue = entry.getValue()[0]; // Assuming single value for simplicity
			jsonPayload.put(paramName, paramValue);
		}
		jsonPayloadArray.add(jsonPayload);
		return jsonPayloadArray;
	}

}
