package modelo;

/**
 * Created by Alefe on 15/06/2016.
 */
public class Notification {
    private int notificationId;
    private Usuario usuario;
    private String message;

    public Notification(int notificationId, Usuario usuario, String message) {
        this.notificationId = notificationId;
        this.usuario = usuario;
        this.message = message;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
