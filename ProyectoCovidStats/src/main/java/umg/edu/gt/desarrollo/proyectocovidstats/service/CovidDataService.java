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
import java.util.*;

@Service
public class CovidDataService {
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    // Internal HashMaps for temporary work
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
        List<Region> regionsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            String iso = node.get("iso").asText();

            List<Region> existingList = regionRepository.findByIso(iso);
            Region region;

            if (!existingList.isEmpty()) {
                // If it exists, we update the data
                region = existingList.get(0);
                region.setName(node.get("name").asText());
            } else {
                //If it doesn't exist, we create a new one
                region = new Region();
                region.setIso(iso);
                region.setName(node.get("name").asText());
                regionsToSave.add(region);
            }

            isoRegionMap.put(region.getIso(), region);
        }

        // We only save the new regions
        if (!regionsToSave.isEmpty()) {
            regionRepository.saveAll(regionsToSave);
        }
    }

    public void saveProvinces(String json, String isoCode) throws Exception {
        Region region = isoRegionMap.get(isoCode);
        if (region == null) {
            // We try to search for it in the DB if it is not on the map
            List<Region> existingList = regionRepository.findByIso(isoCode);
            if (existingList.isEmpty()) {
                throw new Exception("Region not found for ISO code: " + isoCode);
            }
            region = existingList.get(0);
            isoRegionMap.put(isoCode, region);
        }

        JsonNode root = objectMapper.readTree(json).get("data");
        List<Province> provincesToSave = new ArrayList<>();

        // Clear the province map for this region before adding new ones
        // This avoids problems if saveProvinces is run multiple times
        for (Iterator<Map.Entry<String, Province>> it = nameProvinceMap.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, Province> entry = it.next();
            if (entry.getValue().getRegion().getId().equals(region.getId())) {
                it.remove();
            }
        }

        for (JsonNode node : root) {
            String provinceName = node.get("province").asText().isEmpty() ? "N/A" : node.get("province").asText();

            Province province = provinceRepository.findByNameAndRegion(provinceName, region);

            if (province == null) {
                province = new Province();
                province.setName(provinceName);
                province.setRegion(region);
                provincesToSave.add(province);
            }

            // Save both with the exact name and with the name in lowercase for better matching
            nameProvinceMap.put(provinceName, province);
            nameProvinceMap.put(provinceName.toLowerCase(), province);

            // Si hay un nombre alternativo, también lo guardamos
            if (node.has("name")) {
                String altName = node.get("name").asText();
                if (!altName.isEmpty() && !altName.equals(provinceName)) {
                    nameProvinceMap.put(altName, province);
                    nameProvinceMap.put(altName.toLowerCase(), province);
                }
            }
        }

        if (!provincesToSave.isEmpty()) {
            provinceRepository.saveAll(provincesToSave);
            System.out.println("They were saved " + provincesToSave.size() + " new provinces");
        }

        System.out.println("Total number of provinces on the temporary map: " + nameProvinceMap.size());
    }

    public void saveReports(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json).get("data");
        List<Report> reportsToSave = new ArrayList<>();

        for (JsonNode node : root) {
            // First we check if the JSON structure has the province correctly
            if (!node.has("region") || !node.get("region").has("province")) {
                System.out.println("Node without province information: " + node.toString());
                continue;
            }

            String provinceName = node.get("region").get("province").asText();
            // If provinceName is empty, we try to use region.name as a fallback
            if (provinceName.isEmpty() || provinceName.equals("N/A")) {
                if (node.get("region").has("name")) {
                    provinceName = node.get("region").get("name").asText();
                } else {
                    provinceName = "N/A";
                }
            }

            Province province = nameProvinceMap.get(provinceName);

            if (province == null) {
                // If it's not on the map, we try to search the DB for a similar name
                System.out.println("Province not found on the temporary map: " + provinceName);
                List<Province> similarProvinces = provinceRepository.findByNameContainingIgnoreCase(provinceName);
                if (!similarProvinces.isEmpty()) {
                    province = similarProvinces.get(0);
                    // Added to the map for future reference
                    nameProvinceMap.put(provinceName, province);
                    System.out.println("Similar province found: " + province.getName());
                } else {
                    System.out.println("No province similar to: " + provinceName);
                    continue;
                }
            }

            try {
                LocalDate reportDate = LocalDate.parse(node.get("date").asText());
                Report existingReport = reportRepository.findByProvinceAndDate(province, reportDate);

                if (existingReport != null) {
                    // We update the existing report
                    existingReport.setConfirmed(node.get("confirmed").asInt());
                    existingReport.setDeaths(node.get("deaths").asInt());
                    existingReport.setRecovered(node.get("recovered").asInt());
                    reportsToSave.add(existingReport);
                } else {
                    // We create a new report
                    Report report = new Report();
                    report.setDate(reportDate);
                    report.setConfirmed(node.get("confirmed").asInt());
                    report.setDeaths(node.get("deaths").asInt());
                    report.setRecovered(node.get("recovered").asInt());
                    report.setProvince(province);
                    reportsToSave.add(report);
                }
            } catch (Exception e) {
                System.out.println("Error processing report: " + e.getMessage() + " - For province: " + provinceName);
            }
        }

        if (!reportsToSave.isEmpty()) {
            reportRepository.saveAll(reportsToSave);
            System.out.println("They were saved " + reportsToSave.size() + " reports");
        } else {
            System.out.println("No reports found to save");
        }
    }
}