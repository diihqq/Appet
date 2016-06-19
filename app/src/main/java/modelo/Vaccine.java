package modelo;

import java.util.Date;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Vaccine {
    private Date aplicationDate;
    private Date validity;
    private Boolean applied;
    private int anualFrequence;

    public Vaccine(Date aplicationDate, Date validity, Boolean applied, int anualFrequence) {
        this.aplicationDate = aplicationDate;
        this.validity = validity;
        this.applied = applied;
        this.anualFrequence = anualFrequence;
    }

    public Date getAplicationDate() {
        return aplicationDate;
    }

    public void setAplicationDate(Date aplicationDate) {
        this.aplicationDate = aplicationDate;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public int getAnualFrequence() {
        return anualFrequence;
    }

    public void setAnualFrequence(int anualFrequence) {
        this.anualFrequence = anualFrequence;
    }
}
