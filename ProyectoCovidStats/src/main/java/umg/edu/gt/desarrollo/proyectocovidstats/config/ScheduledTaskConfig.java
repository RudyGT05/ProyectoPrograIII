package umg.edu.gt.desarrollo.proyectocovidstats.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTaskConfig {

    // Se ejecutará 15 segundos después del arranque
    @Scheduled(initialDelay = 15000, fixedRate = Long.MAX_VALUE)
    public void fetchCovidData() {
        // Aquí invocarás el servicio que realiza las peticiones a la API
        System.out.println("Fetching COVID-19 data...");
        // ApiService.fetchCovidData()  // Llama al servicio que gestionará la API
    }
}