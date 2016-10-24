package spypet.com.spypet;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Line;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import modelo.Animal;
import modelo.EstabelecimentoFavorito;
import modelo.Evento;
import modelo.Mensagem;
import modelo.Notificacao;
import modelo.Place;
import modelo.PlacesService;
import modelo.Usuario;

public class ActTelaMapa extends AppCompatActivity implements OnMapReadyCallback,LocationListener,GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener,GoogleMap.OnMyLocationButtonClickListener
{

    private GoogleMap gMapa;
    private final String TAG = getClass().getSimpleName();
    private ProgressDialog pd;
    private Spinner spLocalType;
    private ArrayAdapter<String> adpLocalType;
    private LocationManager locationManager;
    private boolean isUpdateLocation = false;
    private Location location;
    private final int LOCATION_PERMISSIONS_REQUEST = 1;
    private AlertDialog.Builder dialogo;
    private int processos = 0;
    private EstabelecimentoFavorito estabelecimentoFavorito = null;
    private LinearLayout llSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tela_mapa);

        //Recupera layout que contém o spinner
        llSpinner = (LinearLayout)findViewById(R.id.llSpinner);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Verifica se algum estabelecimento favorito foi passado como parâmetro
        try{
            Intent i = getIntent();
            String e = i.getStringExtra("EstabelecimentoFavorito");
            if(!e.equals("")){
                JSONObject json = new JSONObject(e);
                estabelecimentoFavorito = EstabelecimentoFavorito.jsonToEstabelecimentoFavorito(json);
            }else{
                estabelecimentoFavorito = null;
            }
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActTelaMapa.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        if (gMapa != null) {
            gMapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        //Esconde spinner se o usuário selecionou algum estabelecimento favorito
        if(estabelecimentoFavorito != null){
            llSpinner.setVisibility(View.GONE);
        }else {
            llSpinner.setVisibility(View.VISIBLE);

            spLocalType = (Spinner) findViewById(R.id.spLocalType);
            adpLocalType = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            adpLocalType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adpLocalType.addAll(Arrays.asList("Selecione um tipo", "Pet Shop", "Clínica Veterinária"));
            spLocalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (gMapa != null) {
                        String item = (String) parent.getItemAtPosition(position);
                        switch (item) {
                            case "Pet Shop":
                                if (location != null) {
                                    gMapa.clear();
                                    new GetPlaces(ActTelaMapa.this, "pet_store").execute();
                                }
                                break;
                            case "Clínica Veterinária":
                                if (location != null) {
                                    gMapa.clear();
                                    new GetPlaces(ActTelaMapa.this, "veterinary_care").execute();
                                }
                                break;
                            case "Selecione um tipo":
                                if (location != null) {
                                    gMapa.clear();
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(location.getLatitude(), location
                                                    .getLongitude())) // Configura o centro do mapa para
                                            // o local atual
                                            .zoom(13) // Configura o zoom
                                            .tilt(20) // Configura o tilt para 30 graus
                                            .build(); // Cria a posição da câmera
                                    gMapa.animateCamera(CameraUpdateFactory
                                            .newCameraPosition(cameraPosition));
                                }
                                break;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spLocalType.setAdapter(adpLocalType);
            spLocalType.setSelection(0);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        setUpdateLocation();

        //nas API > 22 tem de solicitar a permissão do usuário a cada vez que o software é usado
        if (android.os.Build.VERSION.SDK_INT > 22) {
            verificaPermissao();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActTelaMapa.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActTelaMapa.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");

                //Chama tela de login
                Intent principal = new Intent(ActTelaMapa.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        gMapa = googleMap;
        //para adicionar os botões de zoom
        gMapa.getUiSettings().setZoomControlsEnabled(true);
        //antes precisa checar se as permissões foram concedidas no AndroidManifest
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)
        {
            //botão para centralizar o mapa no local que o usuário se encontra
            googleMap.setMyLocationEnabled(true);
        }

        //adiciona os eventos ao mapa
        gMapa.setOnMapClickListener(this);
        gMapa.setOnMapLongClickListener(this);
        gMapa.setOnMyLocationButtonClickListener(this);

        if(estabelecimentoFavorito != null){
            addEstFavoritoNoMapa();
        }
    }

    private void showMsgGPSDesabilitado()
    {
        AlertDialog.Builder msg = new AlertDialog.Builder(this);

        msg.setMessage("Deseja habilitar o serviço de localização?")
                .setCancelable(false)
                .setPositiveButton("Ir para a tela de configuração",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        });
        msg.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = msg.create();
        alert.show();
    }

    private void setUpdateLocation() {
        //é necessário checar se as permissões foram concedidas ao instalar ou usar o app
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(ActTelaMapa.this);
            //verifica se a localização está habilitada
            if (!ActTelaMapa.this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showMsgGPSDesabilitado();
            }
            else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 10, ActTelaMapa.this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        0, 10, ActTelaMapa.this);

            }
            String provider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(provider);
        }
    }


    @Override
    public void onLocationChanged(Location loc) {
        Log.e(TAG, "location update : " + location);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(ActTelaMapa.this);
            location = loc;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //chamado quando o usuário habilita a localização
    @Override
    public void onProviderEnabled(String provider) {
        setUpdateLocation();
    }

    //chamado quando o usuário desabilita a localização
    @Override
    public void onProviderDisabled(String provider)
    {
        setUpdateLocation();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if(isUpdateLocation)
        {
            setUpdateLocation();
        }
    }

    @Override
    protected void onStop()
    {
        if ( ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED )
            locationManager.removeUpdates(this); //finalizar a atualização
        super.onStop();
    }

    @Override
    public void onMapClick(LatLng latLng)
    {

    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {

    }

    //Verifica se o aplicativo tem permissão para acessar a localização
    @TargetApi(Build.VERSION_CODES.M)
    public void verificaPermissao(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Verifica se o usuário selecionou a opções de não perguntar novamente.
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
            }else{
                //Constrói mensagem de diálogo.
                dialogo = new AlertDialog.Builder(ActTelaMapa.this);
                dialogo.setIcon(R.mipmap.ic_launcher);
                //Apresenta mensagem de aviso ao usuário
                dialogo.setMessage("Para usar essa função é necessário que o aplicativo tenha permissão de acesso a localização!");
                dialogo.setTitle("Aviso!");
                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ActTelaMapa.this, ActPrincipal.class);
                        startActivity(intent);
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
        if (codigoRequisicao == LOCATION_PERMISSIONS_REQUEST) {
            if (resultados.length == 1 && resultados[0] == PackageManager.PERMISSION_GRANTED) {
                //Permissão concedida
            } else {
                Intent intent = new Intent(ActTelaMapa.this, ActPrincipal.class);
                startActivity(intent);
            }
        }else {
            super.onRequestPermissionsResult(codigoRequisicao, permissoes, resultados);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {

        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            PlacesService service = new PlacesService(
                    "AIzaSyDS0bQ8j_cxndeL-07vvd0KvhHWJvkCYss");

            ArrayList<Place> findPlaces = service.findPlaces(location.getLatitude(), // -23.189174
                    location.getLongitude(), places); // -45.787756

            for (int i = 0; i < findPlaces.size(); i++) {

                Place placeDetail = findPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            for (int i = 0; i < result.size(); i++) {

                MarkerOptions mo = new MarkerOptions();
                mo.title(result.get(i).getName());
                mo.snippet(result.get(i).getVicinity());
                mo.position(new LatLng(result.get(i).getLatitude(), result.get(i).getLongitude()));

                if(spLocalType.getSelectedItem().toString().equals("Pet Shop")){
                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pata2));
                }else{
                    mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vacina));
                }

                gMapa.addMarker(mo);
                gMapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        LinearLayout info = new LinearLayout(context);
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(context);
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(context);
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });



            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                    // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            gMapa.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            gMapa.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    EstabelecimentoFavorito est;
                    boolean flagFavorito = false;
                    for(int i=0;i<ActPrincipal.listaEstabelecimentosFavoritos.size();i++){
                        est = ActPrincipal.listaEstabelecimentosFavoritos.get(i);
                        if(est.getNome().equals(marker.getTitle())){
                            flagFavorito = true;
                            break;
                        }
                    }

                    if(flagFavorito){
                        Toast.makeText(getBaseContext(),"Estabelecimento já adicionado aos favoritos!",Toast.LENGTH_LONG).show();
                    }else {
                        final Marker m = marker;

                        //Monta caixa de dialogo de confirmação adição aos favoritos.
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActTelaMapa.this);
                        dialogo.setTitle("Aviso!")
                                .setMessage("Você gostaria de adicionar esse estabelecimento aos favoritos?")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        String tipo = spLocalType.getSelectedItem().toString();
                                        String endereco = m.getSnippet();

                                        JSONObject json = new JSONObject();
                                        try {
                                            json.put("Nome", m.getTitle().toString());
                                            json.put("Latitude", String.valueOf(m.getPosition().latitude));
                                            json.put("Longitude", String.valueOf(m.getPosition().longitude));
                                            json.put("idUsuario", ActPrincipal.usuarioLogado.getIdUsuario());
                                            json.put("Tipo", tipo);
                                            json.put("Endereco", endereco);
                                        } catch (Exception ex) {
                                            Log.e("Erro", ex.getMessage());
                                            Toast.makeText(ActTelaMapa.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                                        }

                                        pd = ProgressDialog.show(ActTelaMapa.this, "", "Por favor, aguarde...", false);
                                        processos++;
                                        new ActTelaMapa.RequisicaoAsyncTask().execute("InsereEstabelecimentoFavorito", "0", json.toString());
                                    }
                                })
                                .setNegativeButton("Não", null);
                        AlertDialog alerta = dialogo.create();
                        alerta.show();
                    }
                }
            });

        }

    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;
        private int id;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActTelaMapa.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActTelaMapa.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                    } else {

                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActTelaMapa.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            //remove dialogo de progresso da tela
            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }

    public void addEstFavoritoNoMapa(){
        //Posiciona a camera no estabelecimento favrito
        gMapa.clear();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.parseDouble(estabelecimentoFavorito.getLatitude()), Double.parseDouble(estabelecimentoFavorito.getLongitude())))
                .zoom(13) // Configura o zoom
                .tilt(20) // Configura o tilt para 30 graus
                .build(); // Cria a posição da câmera
        gMapa.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //Adiciona estabelecimento favorito no mapa
        MarkerOptions mo = new MarkerOptions();
        mo.title(estabelecimentoFavorito.getNome());
        mo.snippet(estabelecimentoFavorito.getEndereco());
        mo.position(new LatLng(Double.parseDouble(estabelecimentoFavorito.getLatitude()), Double.parseDouble(estabelecimentoFavorito.getLongitude())));

        if(estabelecimentoFavorito.getTipo().equals("Pet Shop")){
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pata2));
        }else{
            mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vacina));
        }

        gMapa.addMarker(mo);
    }
}
