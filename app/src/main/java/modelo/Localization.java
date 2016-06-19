package modelo;

import java.util.Date;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Localization {
    private int localizationId;
    private Pet pet;
    private Date localizationDate;
    private String latitude;
    private String longitude;

    public Localization(int localizationId, Pet pet, Date localizationDate, String latitude, String longitude) {
        this.localizationId = localizationId;
        this.pet = pet;
        this.localizationDate = localizationDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLocalizationId() {
        return localizationId;
    }

    public void setLocalizationId(int localizationId) {
        this.localizationId = localizationId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Date getLocalizationDate() {
        return localizationDate;
    }

    public void setLocalizationDate(Date localizationDate) {
        this.localizationDate = localizationDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
