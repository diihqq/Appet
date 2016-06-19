package modelo;

import java.util.Date;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Disappearance {
    private int disappearandeId;
    private Date disappearandeDate;
    private Localization localization;

    public Disappearance(int disappearandeId, Date disappearandeDate, Localization localization) {
        this.disappearandeId = disappearandeId;
        this.disappearandeDate = disappearandeDate;
        this.localization = localization;
    }

    public int getDisappearandeId() {
        return disappearandeId;
    }

    public void setDisappearandeId(int disappearandeId) {
        this.disappearandeId = disappearandeId;
    }

    public Date getDisappearandeDate() {
        return disappearandeDate;
    }

    public void setDisappearandeDate(Date disappearandeDate) {
        this.disappearandeDate = disappearandeDate;
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }
}
