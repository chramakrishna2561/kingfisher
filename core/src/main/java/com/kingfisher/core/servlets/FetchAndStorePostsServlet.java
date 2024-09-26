package com.kingfisher.core.servlets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class }, property = {
        "sling.servlet.paths=/bin/getAllPostsAndStore",
        "sling.servlet.methods=GET"
})
public class FetchAndStorePostsServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FetchAndStorePostsServlet.class);
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";
    private static final String STORAGE_PATH = "/content/mydata/posts"; // Store posts in /content/mydata/posts

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(API_URL);
        ResourceResolver resourceResolver = null;

        try {
            // Get the resource resolver using the "jaya" subservice user
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "jaya");

            resourceResolver = resolverFactory.getServiceResourceResolver(param);
            Session session = resourceResolver.adaptTo(Session.class);

            if (session == null) {
                LOG.error("Unable to get session from resource resolver.");
                response.getWriter().write("{\"error\": \"Unable to obtain JCR session\"}");
                return;
            }

            // Execute the GET request to fetch data from the API
            LOG.debug("Fetching data from API...");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                // Convert the response entity to a string
                String result = EntityUtils.toString(entity);
                LOG.debug("API response: " + result);

                // Parse the JSON array from the result
                JSONArray jsonArray = new JSONArray(result);

                // Fetch the parent node where the data will be stored
                LOG.debug("Fetching resource for path: " + STORAGE_PATH);
                Resource parentResource = resourceResolver.getResource(STORAGE_PATH);

                if (parentResource == null) {
                    // If the parent resource doesn't exist, create it
                    LOG.debug("Parent resource does not exist. Creating structure at: " + STORAGE_PATH);
                    Resource contentResource = resourceResolver.getResource("/content");
                    if (contentResource == null) {
                        LOG.error("Unable to find /content node. Cannot proceed.");
                        response.getWriter().write("{\"error\": \"Unable to find /content node\"}");
                        return;
                    }
                    Node contentNode = contentResource.adaptTo(Node.class);
                    Node postsNode = contentNode.addNode("mydata", "nt:unstructured").addNode("posts",
                            "nt:unstructured");
                    session.save(); // Save the changes to create the nodes
                    parentResource = resourceResolver.getResource(STORAGE_PATH); // Reload the resource after saving
                    LOG.debug("Parent resource created successfully.");
                }

                // Store each post as a child node under the specified path
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject postObject = jsonArray.getJSONObject(i);
                    String postId = String.valueOf(postObject.getInt("id"));
                    Resource postResource = parentResource.getChild(postId);

                    if (postResource == null) {
                        // If the post resource does not exist, create it
                        LOG.debug("Creating new post node for post ID: " + postId);
                        postResource = resourceResolver.create(parentResource, postId, null);
                    }

                    // Store the data in the post node
                    ModifiableValueMap properties = postResource.adaptTo(ModifiableValueMap.class);
                    properties.put("userId", postObject.getInt("userId"));
                    properties.put("id", postObject.getInt("id"));
                    properties.put("title", postObject.getString("title"));
                    properties.put("body", postObject.getString("body"));
                }

                // Save the session to persist the changes
                LOG.debug("Saving session after data storage.");
                session.save();

                // Send success response
                response.getWriter().write("Data successfully stored in CRX!");
            } else {
                LOG.error("Entity from API response is null.");
                response.getWriter().write("{\"error\": \"API response is null\"}");
            }
        } catch (Exception e) {
            LOG.error("Error fetching or storing data", e);
            response.getWriter().write("{\"error\": \"Unable to fetch or store posts\"}");
        } finally {
            // Close the resource resolver
            if (resourceResolver != null) {
                resourceResolver.close();
            }
            httpClient.close(); // Close the HTTP client
        }
    }
}
