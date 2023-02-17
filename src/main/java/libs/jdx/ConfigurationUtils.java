package libs.jdx;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junodx.api.util.UrlClientConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationUtils {
    String applicationConfigurationFileName = "application.properties";

    private ObjectMapper mapper;
    private Properties props;
    private InputStream inStream;

    public UrlClientConnection getJunoConnectionFromConfiguration() {
        UrlClientConnection connection = new UrlClientConnection();
        props = new Properties();

        try {
            inStream = getClass().getClassLoader().getResourceAsStream(applicationConfigurationFileName);
            if (inStream != null) {
                props.load(inStream);

                connection.setClientId(props.getProperty("clientId"));
                connection.setClientSecret(props.getProperty("clientSecret"));
                connection.setUrl(props.getProperty("baseUrl"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return connection;
    }

    public void loadConfiguration() throws IOException {
        try {
            inStream = getClass().getClassLoader().getResourceAsStream(applicationConfigurationFileName);
            if (inStream != null) {
                props.load(inStream);

                // ...load properties here

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inStream.close();
        }
    }
}
