package modelo;

import java.util.Date;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Compromise extends Event{
    private String latitude;
    private String longitude;
    private Date compromiseDate;

    public Compromise(int eventId, Alert alert, Pet pet, String name, String observations, Boolean sync, Boolean alertFlag, String latitude, String longitude, Date compromiseDate) {
        super(eventId, alert, pet, name, observations, sync, alertFlag);
        this.latitude = latitude;
        this.longitude = longitude;
        this.compromiseDate = compromiseDate;
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

    public Date getCompromiseDate() {
        return compromiseDate;
    }

    public void setCompromiseDate(Date compromiseDate) {
        this.compromiseDate = compromiseDate;
    }
}
