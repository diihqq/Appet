package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class FavoriteEstablishment {
    private int establishmentId;
    private Usuario usuario;
    private String name;
    private String latitude;
    private String longitude;

    public FavoriteEstablishment(int establishmentId, Usuario usuario, String name, String latitude, String longitude) {
        this.establishmentId = establishmentId;
        this.usuario = usuario;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
