package spypet.com.spypet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import controlador.Requisicao;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Felipe on 13/08/2016.
 */
public class ActLeitorQRCode extends Activity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String solicitante;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent i = getIntent();
        solicitante = i.getStringExtra("Solicitante");

        //Inicia escaneamento
        mScannerView = new ZXingScannerView(ActLeitorQRCode.this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Inicia escaneamento
        mScannerView.setResultHandler(ActLeitorQRCode.this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();

        //Para escaneamento
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result resultado) {
        //Recupera resultado do escaneamento.
        String url = resultado.getText();

        //Constrói mensagem de diálogo.
        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
        dialogo.setIcon(R.mipmap.ic_launcher);

        if(url.contains(Requisicao.urlBase)) {
            //Envia notificação ao usuário e apresenta mensagem ao usuário
            dialogo.setMessage("Uma notificação foi enviada ao dono do animal!");
            dialogo.setTitle("Obrigado!");
            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Chama tela que solicitou a leitura
                    Intent tela;
                    if(solicitante.equals(ActLogin.class.toString())) {
                        tela = new Intent(ActLeitorQRCode.this, ActLogin.class);
                    }else{
                        tela = new Intent(ActLeitorQRCode.this, ActPrincipal.class);
                    }
                    startActivity(tela);
                }
            });
        }else{
            //Apresenta mensagem de aviso ao usuário
            dialogo.setMessage("Esse formato de QRCode não é reconhecido pelo aplicativo!");
            dialogo.setTitle("Aviso!");
            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Volta para o escaneamento
                    mScannerView.resumeCameraPreview(ActLeitorQRCode.this);
                }
            });
        }

        AlertDialog alerta = dialogo.create();
        alerta.show();
    }
}
