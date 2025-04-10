package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CovidDataService {

    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    // HashMaps internos para trabajo temporal
    private final Map<String, Region> isoRegionMap = new HashMap<>();
    private final Map<String, Province> nameProvinceMap = new HashMap<>();

    public CovidDataService(RegionRepository regionRepository,
                            ProvinceRepository provinceRepository,
                            ReportRepository reportRepository) {
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void saveRegions(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Region> regions = new ArrayList<>();

        for (JsonNode node : root) {
            Region region = new Region();
            region.setIso(node.get("iso").asText());
            region.setName(node.get("name").asText());
            regions.add(region);
            isoRegionMap.put(region.getIso(), region); // Guardar en el HashMap temporal
        }

        regionRepository.saveAll(regions);
    }

    public void saveProvinces(String json, String isoCode) throws Exception {
        Region region = isoRegionMap.get(isoCode);
        if (region == null) {
            throw new Exception("Región no encontrada en el mapa para el código ISO: " + isoCode);
        }

        JsonNode root = objectMapper.readTree(json).get("data");
        List<Province> provinces = new ArrayList<>();

        for (JsonNode node : root) {
            Province province = new Province();
            province.setName(node.get("province").asText());
            province.setRegion(region);
            provinces.add(province);
            nameProvinceMap.put(province.getName(), province); // Guardar en el mapa
        }

        provinceRepository.saveAll(provinces);
    }

    public void saveReports(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Report> reports = new ArrayList<>();

        for (JsonNode node : root) {
            String provinceName = node.get("region").get("province").asText();
            Province province = nameProvinceMap.get(provinceName);

            if (province != null) {
                Report report = new Report();
                report.setDate(LocalDate.parse(node.get("date").asText()));
                report.setConfirmed(node.get("confirmed").asInt());
                report.setDeaths(node.get("deaths").asInt());
                report.setRecovered(node.get("recovered").asInt());
                report.setProvince(province);
                reports.add(report);
            }
        }

        reportRepository.saveAll(reports);
    }
}
