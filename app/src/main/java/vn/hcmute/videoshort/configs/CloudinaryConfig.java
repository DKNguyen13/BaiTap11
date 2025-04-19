package vn.hcmute.videoshort.configs;

import org.w3c.dom.Document;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class CloudinaryConfig {
    private String cloudName;
    private String apiKey;
    private String apiSecret;

    public CloudinaryConfig() {
        try {
            InputStream input = getClass().getClassLoader().getResourceAsStream("cloudinary.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();

            cloudName = doc.getElementsByTagName("cloud_name").item(0).getTextContent();
            apiKey = doc.getElementsByTagName("api_key").item(0).getTextContent();
            apiSecret = doc.getElementsByTagName("api_secret").item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getCloudinaryConfigMap() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return config;
    }
}
