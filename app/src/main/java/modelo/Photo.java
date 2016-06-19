package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Photo {
    private int photoId;
    private Pet pet;
    private String path;

    public Photo(int photoId, Pet pet, String path) {
        this.photoId = photoId;
        this.pet = pet;
        this.path = path;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
