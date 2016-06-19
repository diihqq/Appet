package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Event {
    private int eventId;
    private Alert alert;
    private Pet pet;
    private String name;
    private String observations;
    private Boolean sync;
    private Boolean alertFlag;

    public Event(int eventId, Alert alert, Pet pet, String name, String observations, Boolean sync, Boolean alertFlag) {
        this.eventId = eventId;
        this.alert = alert;
        this.pet = pet;
        this.name = name;
        this.observations = observations;
        this.sync = sync;
        this.alertFlag = alertFlag;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public Boolean getAlertFlag() {
        return alertFlag;
    }

    public void setAlertFlag(Boolean alertFlag) {
        this.alertFlag = alertFlag;
    }
}
