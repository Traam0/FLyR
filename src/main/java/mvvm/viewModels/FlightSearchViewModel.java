package mvvm.viewModels;

import core.networking.HttpRestClient;
import core.networking.HttpRestClientFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightSearchViewModel {

    public FlightSearchViewModel(HttpRestClient restClient, Logger logger) {
        try {
            // Fetch the README.md content
            String content = restClient.get(
                    "/github/gitignore/main/README.md",
                    String.class
            );

            logger.info("Successfully fetched README from GitHub: " + content);

        } catch (Exception e) {
            logger.severe("Failed to fetch content: " + e.getMessage());
            throw e;
        }
    }
}
