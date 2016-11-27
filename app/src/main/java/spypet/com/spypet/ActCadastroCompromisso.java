package spypet.com.spypet;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Alerta;
import modelo.Animal;
import modelo.Especie;
import modelo.Mensagem;
import modelo.Raca;
import modelo.Usuario;

/**
 * Created by Diogo on 01/10/2016.
 */
public class ActCadastroCompromisso extends AppCompatActivity {
    private TextView etNomeCompromisso;
    private TextView etNomeLocal;
    private EditText etData;
    private EditText etHora;
    private TextView etEventoObservacoes;
    private ImageView ivFotoPet;
    //private TextView etFlagAlerta;
    //private TextView etAlerta
    private Spinner spAnimal;
    //private Spinner spAlerta;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private Usuario usuario_t = new Usuario(0, "", "", "", "", "");
    private Especie especie_t = new Especie(0, "");
    private Raca raca_t = new Raca(0, "", "", especie_t);
    private Animal animal_escolhido;
    private Alerta alerta_escolhido;
    private int processos = 0;
    Calendar myCalendar = Calendar.getInstance();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_compromisso);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        Intent i = getIntent();
        nome = i.getStringExtra("Nome");
        email = i.getStringExtra("Email");

        //Recupera objetos da tela
        etNomeCompromisso = (TextView) findViewById(R.id.etNomeCompromisso);
        etNomeLocal = (TextView) findViewById(R.id.etNomeLocal);
        etData = (EditText) findViewById(R.id.etData);
        etHora = (EditText) findViewById(R.id.etHora);
        etEventoObservacoes = (TextView) findViewById(R.id.etEventoObservacoes);

        //Seta calendário na data
        setaCalendario();

        //Carrega spinners da tela com os valores
        CarregaSpinners();

        btInscrever = (Button) findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if (etNomeCompromisso.getText().toString().trim().equals("") || etNomeLocal.getText().toString().trim().equals("") ||
                        etData.getText().toString().trim().equals("")  || etHora.getText().toString().trim().equals("")
                        || spAnimal.getSelectedItemPosition() == 0) {
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJsonEvento = new JSONObject();

                        //Evento
                        usuarioJsonEvento.put("Nome", etNomeCompromisso.getText().toString().trim());
                        if (!etEventoObservacoes.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("Observacoes", "");

                        usuarioJsonEvento.put("FlagAlerta", "1");
                        //usuarioJsonEvento.put("idAlerta", alerta_escolhido.getidAlerta());
                        usuarioJsonEvento.put("idAlerta", "1");
                        usuarioJsonEvento.put("idAnimal", animal_escolhido.getIdAnimal());
                        usuarioJsonEvento.put("Tipo", "Compromisso");

                        //Medicamento
                        usuarioJsonEvento.put("NomeLocal", etNomeLocal.getText().toString().trim());
                        usuarioJsonEvento.put("Latitude", "x");
                        usuarioJsonEvento.put("Longitude", "x");

                        //Converte data pra yyyy-MM-dd
                        Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(etData.getText().toString());
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        String parsedDate = formatter.format(initDate);

                        usuarioJsonEvento.put("DataHora", parsedDate + " " + etHora.getText());

                        //Insere usuário na API
                        new RequisicaoAsyncTask().execute("InsereEvento", "0", usuarioJsonEvento.toString());

                    } catch (Exception ex) {
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            case R.id.menuAjuda:
                Intent intentA = new Intent();
                intentA.setAction(Intent.ACTION_VIEW);
                intentA.addCategory(Intent.CATEGORY_BROWSABLE);
                intentA.setData(Uri.parse(getString(R.string.Manual)));
                startActivity(intentA);
                return true;
            case R.id.menuUsuario:
                Intent intentU = new Intent(ActCadastroCompromisso.this, ActAtualizarUsuario.class);
                startActivity(intentU);
                return true;
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActCadastroCompromisso.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActCadastroCompromisso.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActCadastroCompromisso.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setaCalendario() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etData.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActCadastroCompromisso.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etHora.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                final TimePickerDialog mTimePicker = new TimePickerDialog(ActCadastroCompromisso.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hora = "";
                        String min = "";
                        if (selectedHour >= 0 && selectedHour < 10)
                            hora = "0" + String.valueOf(selectedHour);
                        else
                            hora = String.valueOf(selectedHour);

                        if (selectedMinute >= 0 && selectedMinute < 10)
                            min = "0" + String.valueOf(selectedMinute);
                        else
                            min = String.valueOf(selectedMinute);

                        etHora.setText(hora + ":" + min + ":00");

                    }
                }, hour, minute, true);

                TextView tvTitle = new TextView(ActCadastroCompromisso.this);
                tvTitle.setBackgroundColor(Color.WHITE);
                tvTitle.setText("");
                mTimePicker.setCustomTitle(tvTitle);

                mTimePicker.show();

            }
        });
    }

    public void CarregaSpinners() {
       /* //Carrega spinner de alertas
        alertas.clear();
        alertas.add(new Alerta(0, "Selecione o alerta",0));
        spAlerta = (Spinner) findViewById(R.id.spAlerta);
        ArrayAdapter adAlerta = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alertas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if (position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                } else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spAlerta.setAdapter(adAlerta);

        //Adiciona evento de item selecionado no spinner de alerta
        spAlerta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    alerta_escolhido = (Alerta) spAlerta.getItemAtPosition(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega lista de alertas
        pd = ProgressDialog.show(ActCadastroCompromisso.this, "", "Por favor, aguarde...", false);
        processos++;
        new RequisicaoAsyncTask().execute("ListaAlertas", "0", "");

        */
        //Carrega spinner de animais
        animais.clear();
        animais.add(new Animal(0, "Selecione o animal", "0", "0", "0", 0, "0", "0", "0", true, "0", "0", usuario_t, raca_t));
        spAnimal = (Spinner) findViewById(R.id.spAnimal);
        ArrayAdapter adAnimal = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, animais) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if (position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                } else {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                } else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spAnimal.setAdapter(adAnimal);

        //Adiciona evento de item selecionado no spinner de animal
        spAnimal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    animal_escolhido = (Animal) spAnimal.getItemAtPosition(position);

                    if (animal_escolhido != null) {
                        //Carrega foto do pet selecionado.
                        ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
                        Picasso.with(getBaseContext()).load(animal_escolhido.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega lista de animais
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListaAnimaisDoUsuario", "0", json.toString());
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActCadastroCompromisso Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://spypet.com.spypet/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ActCadastroCompromisso Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://spypet.com.spypet/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
                Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {

            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroCompromisso.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "InsereEvento" && msg.getCodigo() == 7) {
                            //Chama tela principal
                            Intent principal = new Intent(ActCadastroCompromisso.this, ActPrincipal.class);
                            startActivity(principal);
                        }
                    } else {
                        //Verifica qual foi o método chamado
                        if (metodo == "ListaAlertas") {
                            //Recupera alertas
                            for (int i = 0; i < resultado.length(); i++) {
                                alertas.add(Alerta.jsonToAlerta(resultado.getJSONObject(i)));
                            }
                        }

                        //Verifica qual foi o método chamado
                        if (metodo == "ListaAnimaisDoUsuario") {
                            //Recupera animais do usuário
                            for (int i = 0; i < resultado.length(); i++) {
                                animais.add(Animal.jsonToAnimal(resultado.getJSONObject(i)));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroCompromisso.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if (processos == 0) {
                pd.dismiss();

            }
        }
    }

}
