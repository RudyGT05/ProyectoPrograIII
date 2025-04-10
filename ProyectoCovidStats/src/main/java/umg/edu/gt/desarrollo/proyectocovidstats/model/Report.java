package umg.edu.gt.desarrollo.proyectocovidstats.model;


import javax.persistence.*;
import java.time.LocalDate;

@Entity

public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private int confirmed;
    private int deaths;
    private int recovered;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public int getConfirmed() {
        return confirmed;
    }
    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }
    public int getDeaths() {
        return deaths;
    }
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }



}