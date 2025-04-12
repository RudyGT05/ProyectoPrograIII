package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;
import org.springframework.stereotype.Service;


@Service
public class ApiService {

    private static final Logger logger = LogManager.getLogger(ApiService.class);
    private final ApiClient apiClient;
    private final CovidDataService covidDataService;
    private final AppConfig appConfig;

    public ApiService(ApiClient apiClient, CovidDataService covidDataService, AppConfig appConfig) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
        this.appConfig = appConfig;
    }

    public void fetchCovidData() {
        logger.info("Fetching COVID-19 data...");

        String countryIso = appConfig.getCountryIso();
        String reportDate = appConfig.getReportDate();

        logger.info("countryIso desde AppConfig: '{}'", countryIso);
        logger.info("reportDate desde AppConfig: '{}'", reportDate);


        try {
            // 🔹 Get regions
            String regions = apiClient.getRegions();
            logger.info("Regions: " + regions);
            covidDataService.saveRegions(regions); // ✅ Save to database

            // 🔹 Get Provinces
            String provinces = apiClient.getProvinces(countryIso);
            logger.info("Provinces for {}: {}", countryIso, provinces);
            covidDataService.saveProvinces(provinces, countryIso);

            // 🔹 Get Report
            String report = apiClient.getReport(countryIso, reportDate);
            logger.info("Report for {} on {}: {}", countryIso, reportDate, report);
            covidDataService.saveReports(report);

        } catch (Exception e) {
            logger.error("❌ Error consuming the API or saving to the DB:");

        }
    }
}