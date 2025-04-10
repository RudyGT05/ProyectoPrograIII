package umg.edu.gt.desarrollo.proyectocovidstats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;

import java.util.List;

public interface ProvinceRepository  extends JpaRepository<Province, Long> {
    Province findByNameAndRegion(String name, Region region);
    // Additional method to search by similar name
    @Query("SELECT p FROM Province p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Province> findByNameContainingIgnoreCase(@Param("name") String name);
}
