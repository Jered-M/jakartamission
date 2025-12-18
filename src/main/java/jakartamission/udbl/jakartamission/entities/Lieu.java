package jakartamission.udbl.jakartamission.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité JPA pour représenter un lieu en Indonésie
 */
@Entity
@Table(name = "lieu")
public class Lieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String description;
    private double longitude;
    private double latitude;

    public Lieu() {
    }

    public Lieu(String nom, String description, double longitude, double latitude) {
        this.nom = nom;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId() { 
        return id; 
    }

    public String getNom() { 
        return nom; 
    }

    public void setNom(String nom) { 
        this.nom = nom; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }

    public double getLongitude() { 
        return longitude; 
    }

    public void setLongitude(double longitude) { 
        this.longitude = longitude; 
    }

    public double getLatitude() { 
        return latitude; 
    }

    public void setLatitude(double latitude) { 
        this.latitude = latitude; 
    }
}
