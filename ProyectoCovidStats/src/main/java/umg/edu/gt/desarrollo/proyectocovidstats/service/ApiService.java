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
    private final CovidDataService covidDataService;

    public ApiService(ApiClient apiClient, CovidDataService covidDataService) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
    }

    public void fetchCovidData() {
        logger.info("Fetching COVID-19 data...");

        try {
            // 🔹 Obtener regiones
            String regions = apiClient.getRegions();
            logger.info("Regions: " + regions);
            covidDataService.saveRegions(regions); // ✅ Guardar en base de datos

            // 🔹 Provincias
            String provinces = apiClient.getProvinces("GTM");
            logger.info("Provinces for GTM: " + provinces);
            covidDataService.saveProvinces(provinces, "GTM"); // ✅ Guardar en base de datos

            // 🔹 Reporte
            String report = apiClient.getReport("GTM", "2022-04-17");
            logger.info("Report for GTM on 2022-04-16: " + report);
            covidDataService.saveReports(report); // ✅ Guardar en base de datos

        } catch (Exception e) {
            logger.error("❌ Error al consumir la API o guardar en la BD:");
            e.printStackTrace();
        }
    }
}