package controlador;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import spypet.com.spypet.ActPrincipal;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Felipe on 30/10/2016.
 */

public class Conexao extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean conectado = verificaConexao(context);

        //Se houver conexão com a internet e o serviço nao estiver rodando ele é iniciado
        if (conectado) {
            if (!Servico.processoRodando) {
                Intent i = new Intent(context, Servico.class);
                context.startService(i);
            }
        }
    }

    //Verificação de conexão com a internet
    public static boolean verificaConexao(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean conectado = false;
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            conectado = true;
        }

        return conectado;
    }
}
