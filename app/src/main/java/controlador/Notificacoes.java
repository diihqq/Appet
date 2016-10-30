package controlador;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import spypet.com.spypet.ActNotificacoes;
import spypet.com.spypet.ActPrincipal;
import spypet.com.spypet.R;

/**
 * Created by Felipe on 27/10/2016.
 */

public class Notificacoes {

    private NotificationCompat.Builder builder;
    private Intent resultado;
    TaskStackBuilder stackBuilder;
    PendingIntent resultPendingIntent;
    NotificationManager notificationManager;

    public Notificacoes(Context contexto, String titulo, String conteudo){
        builder = new NotificationCompat.Builder(contexto);
        builder.setSmallIcon(R.drawable.ic_pata);
        builder.setContentTitle(titulo);
        builder.setContentText(conteudo);
        builder.setColor(ContextCompat.getColor(contexto,R.color.colorPrimary));

        resultado = new Intent(contexto, ActNotificacoes.class);
        stackBuilder = TaskStackBuilder.create(contexto);
        stackBuilder.addParentStack(ActNotificacoes.class);
        stackBuilder.addNextIntent(resultado);

        resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void Notificar(int id){
        notificationManager.notify(id,builder.build());
    }
}
