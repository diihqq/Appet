package modelo;

import java.util.Date;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Medication extends Event {
    private Date startDate;
    private Date endDate;
    private int dailyFrequence;

    public Medication(int eventId, Alert alert, Pet pet, String name, String observations, Boolean sync, Boolean alertFlag, Date startDate, Date endDate, int dailyFrequence) {
        super(eventId, alert, pet, name, observations, sync, alertFlag);
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyFrequence = dailyFrequence;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDailyFrequence() {
        return dailyFrequence;
    }

    public void setDailyFrequence(int dailyFrequence) {
        this.dailyFrequence = dailyFrequence;
    }
}
