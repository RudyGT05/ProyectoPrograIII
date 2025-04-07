package umg.edu.gt.desarrollo.proyectocovidstats.service;

import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    private final ApiClient apiClient;

    public ApiService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public void fetchCovidData() {
        System.out.println("Fetching COVID-19 data...");

        try {
            // Obtener regiones
            String regions = apiClient.getRegions();
            System.out.println("Regions: " + regions);

            // Obtener provincias para un país específico (GTM)
            String provinces = apiClient.getProvinces("GTM");
            System.out.println("Provinces for GTM: " + provinces);

            // Obtener reporte para una fecha específica
            String report = apiClient.getReport("GTM", "2022-04-16");
            System.out.println("Report for GTM on 2022-04-16: " + report);

        } catch (Exception e) {
            System.out.println("❌ Error al consumir la API:");
            e.printStackTrace();
        }
    }
}
