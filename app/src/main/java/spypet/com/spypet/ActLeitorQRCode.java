package spypet.com.spypet;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.security.Permission;
import java.util.jar.*;

import controlador.Requisicao;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Felipe on 13/08/2016.
 */
public class ActLeitorQRCode extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String solicitante;
    private static final int CAMERA_PERMISSIONS_REQUEST = 1;
    private AlertDialog.Builder dialogo;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent i = getIntent();
        solicitante = i.getStringExtra("Solicitante");

        //Verifica permissões somente se a API for 23 ou maior
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            verificaPermissao();
        }

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
    public void handleResult(final Result resultado) {
        //Recupera resultado do escaneamento.
        String url = resultado.getText();

        //Constrói mensagem de diálogo.
        dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
        dialogo.setIcon(R.mipmap.ic_launcher);

        if(url.contains(Requisicao.urlBase)) {
            //Envia notificação ao usuário e apresenta mensagem ao usuário
            dialogo.setMessage("Uma notificação foi enviada ao dono do animal!");
            dialogo.setTitle("Obrigado!");
            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chamaTelaAnterior();
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

    //Verifica se o aplicativo tem permissão para acessar a câmera
    @TargetApi(Build.VERSION_CODES.M)
    public void verificaPermissao(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //Verifica se o usuário selecionou a opções de não perguntar novamente.
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST);
            }else{
                //Constrói mensagem de diálogo.
                dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
                dialogo.setIcon(R.mipmap.ic_launcher);
                //Apresenta mensagem de aviso ao usuário
                dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso a câmera!");
                dialogo.setTitle("Aviso!");
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chamaTelaAnterior();
                    }
                });
                AlertDialog alerta = dialogo.create();
                alerta.show();
            }
        }
    }

    // Callback da requisição de permissão
    @Override
    public void onRequestPermissionsResult(int codigoRequisicao,
                                           String permissoes[],
                                           int[] resultados) {
        // Verifica se esse retorno de resposta é referente a requisição de permissão da CAMERA
        if (codigoRequisicao == CAMERA_PERMISSIONS_REQUEST) {
            if (resultados.length == 1 && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                //Permissão concedida
            } else {
               chamaTelaAnterior();
            }
        }else{
            super.onRequestPermissionsResult(codigoRequisicao, permissoes, resultados);
        }
    }

    public void chamaTelaAnterior(){
        //Chama tela que solicitou a leitura
        Intent tela;
        if(solicitante.equals(ActLogin.class.toString())) {
            tela = new Intent(ActLeitorQRCode.this, ActLogin.class);
        }else{
            tela = new Intent(ActLeitorQRCode.this, ActPrincipal.class);
        }
        startActivity(tela);
    }
}
