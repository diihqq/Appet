package spypet.com.spypet;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Permission;
import java.util.List;
import java.util.Locale;
import java.util.jar.*;

import controlador.Requisicao;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import modelo.Especie;
import modelo.Mensagem;
import modelo.Raca;

/**
 * Created by Felipe on 13/08/2016.
 */
public class ActLeitorQRCode extends AppCompatActivity implements ZXingScannerView.ResultHandler, LocationListener {

    private ZXingScannerView mScannerView;
    private String solicitante;
    private static final int CAMERA_PERMISSIONS_REQUEST = 1;
    private static final int LOCATION_PERMISSIONS_REQUEST = 2;
    private AlertDialog.Builder dialogo;
    private LocationManager locationManager;
    private ProgressDialog pd;
    private int processos = 0;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent i = getIntent();
        solicitante = i.getStringExtra("Solicitante");

        //Verifica permissões somente se a API for 23 ou maior
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            verificaPermissao();
            verificaPermissaoLocalizacao();
        }

        //Inicia escaneamento
        mScannerView = new ZXingScannerView(ActLeitorQRCode.this);
        setContentView(mScannerView);

        //Atualiza localizações
        Address endereco = recuperaLocalizacao();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    //Pega localização atual do usuário
    public Address recuperaLocalizacao() {
        Address localAtual = null;

        locationManager = (LocationManager) getBaseContext().getSystemService(getBaseContext().LOCATION_SERVICE);

        // Recupera status do GPS
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {

            //Constrói mensagem de diálogo.
            dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
            dialogo.setIcon(R.mipmap.ic_launcher);

            //Envia notificação ao usuário e apresenta mensagem ao usuário
            dialogo.setMessage("Ative o GPS para usar essa função!");
            dialogo.setTitle("Aviso!");
            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chamaTelaAnterior();
                }

            });
            AlertDialog alerta = dialogo.create();
            alerta.show();

        } else {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Geocoder gcd = new Geocoder(ActLeitorQRCode.this, Locale.getDefault());
                List<Address> enderecos = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                localAtual = enderecos.get(0);
            }catch(SecurityException ex){
                Log.e("Erro", ex.getMessage());
                //Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Log.e("Erro", ex.getMessage());
                //Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }

        return localAtual;
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

        if(url.contains(Requisicao.urlBase + "EncontrarAnimal")) {
            Address endereco = recuperaLocalizacao();

            if(endereco != null) {
                try {
                    //Mostra janela de progresso
                    pd = ProgressDialog.show(ActLeitorQRCode.this, "", "Por favor, aguarde...", false);
                    processos++;

                    //Monta JSON
                    JSONObject json = new JSONObject();
                    json.put("Latitude", String.valueOf(endereco.getLatitude()));
                    json.put("Longitude", String.valueOf(endereco.getLongitude()));
                    json.put("Local", String.valueOf(endereco.getLocality()));

                    int id = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1, url.length()));

                    //Envia notificação ao dono do animal
                    new RequisicaoAsyncTask().execute("EncontrarAnimal", String.valueOf(id), json.toString());

                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    //Volta para o escaneamento
                    mScannerView.resumeCameraPreview(ActLeitorQRCode.this);
                }
            }else{
                //Volta para o escaneamento
                mScannerView.resumeCameraPreview(ActLeitorQRCode.this);
                Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }else{
            //Constrói mensagem de diálogo.
            dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
            dialogo.setIcon(R.mipmap.ic_launcher);
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
            AlertDialog alerta = dialogo.create();
            alerta.show();
        }

    }

    //Verifica se o aplicativo tem permissão para acessar a câmera
    @TargetApi(Build.VERSION_CODES.M)
    public void verificaPermissaoLocalizacao(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Verifica se o usuário selecionou a opções de não perguntar novamente.
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
            }else{
                //Constrói mensagem de diálogo.
                dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
                dialogo.setIcon(R.mipmap.ic_launcher);
                //Apresenta mensagem de aviso ao usuário
                dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso a localização! Clique em 'OK' para ir até a tela de permissões do Appet.");
                dialogo.setTitle("Aviso!");
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //chamaTelaAnterior();
                        //Chama tela de permissões do Appet
                        startInstalledAppDetailsActivity(ActLeitorQRCode.this);
                    }
                });
                AlertDialog alerta = dialogo.create();
                alerta.show();
            }
        }
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    //Verifica se o aplicativo tem permissão para acessar a localização
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
                dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso a câmera! Clique em 'OK' para ir até a tela de permissões do Appet.");
                dialogo.setTitle("Aviso!");
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //chamaTelaAnterior();
                        //Chama tela de permissões do Appet
                        startInstalledAppDetailsActivity(ActLeitorQRCode.this);
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
            if (codigoRequisicao == LOCATION_PERMISSIONS_REQUEST) {
                if (resultados.length == 1 && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permissão concedida
                } else {
                    //Constrói mensagem de diálogo.
                    dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
                    dialogo.setIcon(R.mipmap.ic_launcher);
                    //Apresenta mensagem de aviso ao usuário
                    dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso a localização! Clique em 'OK' para ir até a tela de permissões do Appet.");
                    dialogo.setTitle("Aviso!");
                    dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //chamaTelaAnterior();
                            //Chama tela de permissões do Appet
                            startInstalledAppDetailsActivity(ActLeitorQRCode.this);
                        }
                    });
                    AlertDialog alerta = dialogo.create();
                    alerta.show();
                }
            }else {
                super.onRequestPermissionsResult(codigoRequisicao, permissoes, resultados);
            }
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

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                int id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if(resultado.length() == 0){
                    Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }else{

                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if(Mensagem.isMensagem(json)){
                        Mensagem msg = Mensagem.jsonToMensagem(json);

                        if(metodo == "EncontrarAnimal" && msg.getCodigo() == 18){
                            //Constrói mensagem de diálogo.
                            dialogo = new AlertDialog.Builder(ActLeitorQRCode.this);
                            dialogo.setIcon(R.mipmap.ic_launcher);
                            //Envia notificação ao usuário e apresenta mensagem ao usuário
                            dialogo.setMessage("Uma notificação foi enviada ao dono do animal!");
                            dialogo.setTitle("Obrigado!");
                            dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    chamaTelaAnterior();
                                }
                            });
                            AlertDialog alerta = dialogo.create();
                            alerta.show();
                        }else{
                            Toast.makeText(ActLeitorQRCode.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        //Se o retorno não for mensagem
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActLeitorQRCode.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }

            //Volta para o escaneamento
            mScannerView.resumeCameraPreview(ActLeitorQRCode.this);
        }
    }
}
