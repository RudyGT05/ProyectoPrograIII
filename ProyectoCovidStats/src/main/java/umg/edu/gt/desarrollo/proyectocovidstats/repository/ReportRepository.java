package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;

import java.time.LocalDate;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByProvinceAndDate(Province province, LocalDate date); // Verificación única
}
