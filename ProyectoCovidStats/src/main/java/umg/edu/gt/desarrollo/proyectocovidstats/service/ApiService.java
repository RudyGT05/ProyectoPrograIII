package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umg.edu.gt.desarrollo.proyectocovidstats.config.ScheduledTaskConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;
import org.springframework.stereotype.Service;

@Service
public class ApiService {
    private static final Logger logger = LogManager.getLogger(ApiService.class);

    private final ApiClient apiClient;

    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void fetchCovidData() {
        logger.info("Fetching COVID-19 data...");

        try {
            // Obtener regiones
            String regions = apiClient.getRegions();
            logger.info("Regions: " + regions);

            // Obtener provincias para un país específico (GTM)
            String provinces = apiClient.getProvinces("GTM");
            logger.info("Provinces for GTM: " + provinces);

            // Obtener reporte para una fecha específica
            String report = apiClient.getReport("GTM", "2022-04-16");
            logger.info("Report for GTM on 2022-04-16: " + report);

        } catch (Exception e) {
            logger.error("❌ Error al consumir la API:");
            e.printStackTrace();
        }
    }
}
