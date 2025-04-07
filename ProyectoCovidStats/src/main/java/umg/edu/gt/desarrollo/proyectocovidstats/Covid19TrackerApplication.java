package umg.edu.gt.desarrollo.proyectocovidstats;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import umg.edu.gt.desarrollo.proyectocovidstats.service.ApiService;

@SpringBootApplication
public class Covid19TrackerApplication {

    private final ApiService apiService;

    // Constructor con inyección de dependencias
    @Autowired
    public Covid19TrackerApplication(ApiService apiService) {
        this.apiService = apiService;
    }

    // Método para obtener los datos de la API después de la inicialización de la aplicación
    @PostConstruct
    public void fetchData() {
        // Asegúrate de que fetchCovidData esté correctamente implementado en ApiService
        apiService.fetchCovidData();
    }

    public static void main(String[] args) {
        SpringApplication.run(Covid19TrackerApplication.class, args);
    }
}
