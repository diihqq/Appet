package spypet.com.spypet;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

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
public class ActCadastroMedicamentoPet extends AppCompatActivity {
    private EditText etNomeMedicamento;
    private EditText etInicio;
    private EditText etFim;
    private EditText etHorasDeEspera;
    private EditText etEventoObservacoes;
    private TextView tvAnimal;
    private ImageView ivFotoPet;
   // private Spinner spAnimal;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private Usuario usuario_t = new Usuario(0,"","","","","");
    private Especie especie_t = new Especie(0,"");
    private Raca raca_t = new Raca(0,"","",especie_t);
    private Animal animal_escolhido;
    private int processos = 0;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento_pet);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Recupera os parâmetros passados na chamada dessa tela
        try{
            Intent i = getIntent();
            nome = i.getStringExtra("Nome");
            email = i.getStringExtra("Email");

            JSONObject json2 = new JSONObject(i.getStringExtra("Animal"));
            animal_escolhido = Animal.jsonToAnimal(json2);

        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
        }

        //Recupera objetos da tela

        etNomeMedicamento = (EditText)findViewById(R.id.etNomeMedicamento);
        etInicio = (EditText)findViewById(R.id.etInicio);
        etFim = (EditText)findViewById(R.id.etFim);
        etHorasDeEspera = (EditText)findViewById(R.id.etHorasDeEspera);
        etEventoObservacoes = (EditText)findViewById(R.id.etEventoObservacoes);
        tvAnimal = (TextView)findViewById(R.id.tvAnimal);

        //Seta calendário nas datas de inicio e fim
        setaCalendario();

        //Carrega spinners da tela com os valores
        carregaDados();

        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNomeMedicamento.getText().toString().trim().equals("") || etInicio.getText().toString().trim().equals("") ||
                        etFim.getText().toString().trim().equals("")){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try
                    {
                        Date initDate = new SimpleDateFormat("dd/MM/yyyy").parse(etInicio.getText().toString());
                        Date fimDate = new SimpleDateFormat("dd/MM/yyyy").parse(etFim.getText().toString());

                        //Data de inicio > fim
                        if (initDate.after(fimDate))
                        {
                            Toast.makeText(getBaseContext(), "Data de Início maior do que a Data de Fim!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            //Gera objeto para ser autenticado pela API.
                            JSONObject usuarioJsonEvento = new JSONObject();

                            //Evento
                            usuarioJsonEvento.put("Nome", etNomeMedicamento.getText().toString().trim());
                            if (!etEventoObservacoes.getText().toString().trim().equals(""))
                                usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                            else
                                usuarioJsonEvento.put("Observacoes", "");

                            usuarioJsonEvento.put("FlagAlerta", "1");
                            //usuarioJsonEvento.put("idAlerta", alerta_escolhido.getidAlerta());
                            usuarioJsonEvento.put("idAlerta", "1");
                            usuarioJsonEvento.put("idAnimal", animal_escolhido.getIdAnimal());
                            usuarioJsonEvento.put("Tipo", "Medicamento");

                            //Medicamento

                            //Converte data pra yyyy-MM-dd
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String dataInicio = formatter.format(initDate);
                            usuarioJsonEvento.put("Inicio", dataInicio);

                            //Converte data pra yyyy-MM-dd
                            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                            String dataFim = formatter2.format(fimDate);
                            usuarioJsonEvento.put("Fim", dataFim);

                            //usuarioJsonEvento.put("FrequenciaDiaria", etFrequenciaDiaria.getText().toString().trim());
                            usuarioJsonEvento.put("FrequenciaDiaria", "0");

                            if (!etHorasDeEspera.getText().toString().trim().equals(""))
                                usuarioJsonEvento.put("HorasDeEspera", etHorasDeEspera.getText().toString().trim());
                            else
                                usuarioJsonEvento.put("HorasDeEspera", "");

                            //Insere usuário na API
                            pd = ProgressDialog.show(ActCadastroMedicamentoPet.this, "", "Por favor, aguarde...", false);
                            processos++;
                            new RequisicaoAsyncTask().execute("InsereEvento", "0", usuarioJsonEvento.toString());
                        }
                    }
                    catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActCadastroMedicamentoPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
                Intent intentU = new Intent(ActCadastroMedicamentoPet.this, ActAtualizarUsuario.class);
                startActivity(intentU);
                return true;
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActCadastroMedicamentoPet.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActCadastroMedicamentoPet.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActCadastroMedicamentoPet.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setaCalendario()
    {
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

                etInicio.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etInicio.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActCadastroMedicamentoPet.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                etFim.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etFim.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActCadastroMedicamentoPet.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void carregaDados()
    {
        //Carrega foto do pet selecionado.
        ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        Picasso.with(getBaseContext()).load(animal_escolhido.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);

        //Carrega nome do animal
        tvAnimal = (TextView) findViewById(R.id.tvAnimal);
        tvAnimal.setText(animal_escolhido.getNome());
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
                Toast.makeText(ActCadastroMedicamentoPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActCadastroMedicamentoPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json))
                    {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroMedicamentoPet.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "InsereEvento" && msg.getCodigo() == 7) {

                            pd.dismiss();

                            //Chama tela principal
                            Intent principal = new Intent(ActCadastroMedicamentoPet.this, ActPrincipal.class);
                            startActivity(principal);
                        }
                    }
                    else
                    {
                        //Verifica qual foi o método chamado
                        if(metodo == "ListaAlertas") {
                            //Recupera alertas
                            for(int i=0;i<resultado.length();i++){
                                alertas.add(Alerta.jsonToAlerta(resultado.getJSONObject(i)));
                            }
                        }

                        //Verifica qual foi o método chamado
                        if(metodo == "ListaAnimaisDoUsuario") {
                            //Recupera animais do usuário
                            for(int i=0;i<resultado.length();i++){
                                animais.add(Animal.jsonToAnimal(resultado.getJSONObject(i)));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActCadastroMedicamentoPet.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }

        }
    }

}
