package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Breed {
    private int breedId;
    private String name;
    private Specie specie;

    public Breed(int breedId, String name, Specie specie) {
        this.breedId = breedId;
        this.name = name;
        this.specie = specie;
    }

    public int getBreedId() {
        return breedId;
    }

    public void setBreedId(int breedId) {
        this.breedId = breedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Specie getSpecie() {
        return specie;
    }

    public void setSpecie(Specie specie) {
        this.specie = specie;
    }
}
