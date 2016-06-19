package modelo;

import modelo.enums.AlertLevel;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Alert {
    private int alertId;
    private AlertLevel alertLevel;
    private int frequence;

    public Alert(int idAlert, AlertLevel alertLevel, int frequence) {
        this.alertId = idAlert;
        this.alertLevel = alertLevel;
        this.frequence = frequence;
    }

    public int getIdAlert() {
        return alertId;
    }

    public void setIdAlert(int idAlert) {
        this.alertId = idAlert;
    }

    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }
}
