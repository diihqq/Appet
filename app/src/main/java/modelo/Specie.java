package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Specie {
    private int specieId;
    private String name;

    public Specie(int specieId, String name) {
        this.specieId = specieId;
        this.name = name;
    }

    public int getSpecieId() {
        return specieId;
    }

    public void setSpecieId(int specieId) {
        this.specieId = specieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
