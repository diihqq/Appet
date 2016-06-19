package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class FavoriteEstablishment {
    private int establishmentId;
    private User user;
    private String name;
    private String latitude;
    private String longitude;

    public FavoriteEstablishment(int establishmentId, User user, String name, String latitude, String longitude) {
        this.establishmentId = establishmentId;
        this.user = user;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(int establishmentId) {
        this.establishmentId = establishmentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
