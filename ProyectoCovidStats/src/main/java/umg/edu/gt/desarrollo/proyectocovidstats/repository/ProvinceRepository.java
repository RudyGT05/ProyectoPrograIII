package umg.edu.gt.desarrollo.proyectocovidstats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;

public interface ProvinceRepository  extends JpaRepository<Province, Long> {


}
