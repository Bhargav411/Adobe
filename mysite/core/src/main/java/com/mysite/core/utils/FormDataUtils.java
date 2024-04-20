package com.mysite.core.utils;


import java.util.Map;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.commons.jcr.JcrUtil;

public class FormDataUtils {

    private static final String NODE_TYPE = "nt:unstructured";
    private static final String NODE_PREFIX = "/content/usergenerated/";

    public static void saveFormData(SlingHttpServletRequest request, Map<String, String[]> parameterMap)
            throws RepositoryException {
        // Generate a unique ID for the node
        String nodeId = UUID.randomUUID().toString();

        // Get the session
        Session session = request.getResourceResolver().adaptTo(Session.class);

        // Construct the node path
        String nodePath = NODE_PREFIX + nodeId;

        // Create the node
        Node node = JcrUtil.createPath(nodePath, NODE_TYPE, session);

        // Set properties with request parameters
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue()[0]; // Assuming single value for simplicity
            node.setProperty(paramName, paramValue);
        }

        // Save changes
        session.save();
    }
}