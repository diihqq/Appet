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
public class ActCadastroVacina extends AppCompatActivity {
    private TextView etNomeVacina;
    private EditText etDataAplicacao;
    private EditText etDataValidade;
    private ImageView ivFotoPet;
   // private TextView etFrequenciaAnual;
    private TextView etQtdDoses;
    private TextView etEventoObservacoes;
    private Spinner spAnimal;
    //private Spinner spAlerta;
    private Spinner spAplicada;
    private Button btInscrever;
    private String nome;
    private String email;
    private ProgressDialog pd;
    private ArrayList<Alerta> alertas = new ArrayList<>();
    private ArrayList<Animal> animais = new ArrayList<>();
    private ArrayList<String> aplicadas = new ArrayList<>();
    private Usuario usuario_t = new Usuario(0,"","","","","");
    private Especie especie_t = new Especie(0,"");
    private Raca raca_t = new Raca(0,"","",especie_t);
    private Animal animal_escolhido;
    private Alerta alerta_escolhido;
    private int aplicada;
    private int processos = 0;
    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_vacina);

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
        etNomeVacina = (TextView)findViewById(R.id.etNomeVacina);
        etDataAplicacao = (EditText)findViewById(R.id.etDataAplicacao);
        etDataValidade  = (EditText)findViewById(R.id.etDataValidade);
        //etFrequenciaAnual = (TextView)findViewById(R.id.etFrequenciaAnual);
        etQtdDoses = (TextView)findViewById(R.id.etQtdDoses);
        etEventoObservacoes = (TextView)findViewById(R.id.etEventoObservacoes);

        //Seta calendário nas datas de aplicação e validade
        setaCalendario();

        //Carrega spinners da tela com os valores
        CarregaSpinners();

        btInscrever = (Button)findViewById(R.id.btCadastrar);

        //Cadastra evento e medicamento
        btInscrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verifica se todas as informações foram fornecidas
                if(etNomeVacina.getText().toString().trim().equals("") || spAplicada.getSelectedItemPosition() == 0
                || spAnimal.getSelectedItemPosition() == 0){
                    Toast.makeText(getBaseContext(), "Preencha todas as informações!", Toast.LENGTH_LONG).show();
                }else{
                    try {

                        //Gera objeto para ser autenticado pela API.
                        JSONObject usuarioJsonEvento = new JSONObject();

                        //Evento
                        usuarioJsonEvento.put("Nome", etNomeVacina.getText().toString().trim());
                        if (!etEventoObservacoes.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("Observacoes", etEventoObservacoes.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("Observacoes", "");

                        usuarioJsonEvento.put("FlagAlerta","1");
                        //usuarioJsonEvento.put("idAlerta", alerta_escolhido.getidAlerta());
                        usuarioJsonEvento.put("idAlerta", "1");
                        usuarioJsonEvento.put("idAnimal", animal_escolhido.getIdAnimal());
                        usuarioJsonEvento.put("Tipo","Vacina");

                        //Vacina
                        usuarioJsonEvento.put("Aplicada", aplicada);

                        /* if (!etFrequenciaAnual.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("FrequenciaAnual",etFrequenciaAnual.getText().toString().trim());
                        else*/
                        usuarioJsonEvento.put("FrequenciaAnual","0");

                        if (!etQtdDoses.getText().toString().trim().equals(""))
                            usuarioJsonEvento.put("QtdDoses",etQtdDoses.getText().toString().trim());
                        else
                            usuarioJsonEvento.put("QtdDoses","0");

                        //Se ambas datas preenchidas, compara datas
                        if (!etDataAplicacao.getText().toString().trim().equals("") &&
                                !etDataValidade.getText().toString().trim().equals(""))
                        {
                            Date aplDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataAplicacao.getText().toString());
                            Date valDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataValidade.getText().toString());

                            //Data de aplicação > validade
                            if (aplDate.after(valDate))
                            {
                                Toast.makeText(getBaseContext(), "Data de Aplicação maior do que a Data de Validade!", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String aplData = formatter.format(aplDate);
                                usuarioJsonEvento.put("DataAplicacao",aplData);
                                String valData = formatter.format(valDate);
                                usuarioJsonEvento.put("DataValidade", valData);

                                pd = ProgressDialog.show(ActCadastroVacina.this, "", "Por favor, aguarde...", false);
                                processos++;

                                //Insere usuário na API
                                new RequisicaoAsyncTask().execute("InsereEvento", "0", usuarioJsonEvento.toString());
                            }
                        }
                        else
                        {
                            //Se data de aplicação preenchida => Preenche dado
                            if (!etDataAplicacao.getText().toString().trim().equals("")) {
                                Date aplDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataAplicacao.getText().toString());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String aplData = formatter.format(aplDate);
                                usuarioJsonEvento.put("DataAplicacao", aplData);
                            }
                            else
                                usuarioJsonEvento.put("DataAplicacao", "");

                            //Se data de validade preenchida => Preenche dado
                            if (!etDataValidade.getText().toString().trim().equals(""))
                            {
                                Date valDate = new SimpleDateFormat("dd/MM/yyyy").parse(etDataValidade.getText().toString());
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                String valData = formatter.format(valDate);
                                usuarioJsonEvento.put("DataValidade", valData);
                            }
                            else
                                usuarioJsonEvento.put("DataValidade", "");

                            pd = ProgressDialog.show(ActCadastroVacina.this, "", "Por favor, aguarde...", false);
                            processos++;

                            //Insere usuário na API
                            new RequisicaoAsyncTask().execute("InsereEvento", "0", usuarioJsonEvento.toString());

                        }

                    }catch (Exception ex){
                        Log.e("Erro", ex.getMessage());
                        Toast.makeText(ActCadastroVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
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
                Intent intentU = new Intent(ActCadastroVacina.this, ActAtualizarUsuario.class);
                startActivity(intentU);
                return true;
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActCadastroVacina.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent = new Intent(ActCadastroVacina.this, ActNotificacoes.class);
                startActivity(intent);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActCadastroVacina.this, ActLogin.class);
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

                etDataAplicacao.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etDataAplicacao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActCadastroVacina.this, date, myCalendar
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

                etDataValidade.setText(sdf.format(myCalendar.getTime()));
            }

        };

        etDataValidade.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ActCadastroVacina.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    public void CarregaSpinners()
    {
        //Carrega spinner de alertas
        /*alertas.clear();
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
        pd = ProgressDialog.show(ActCadastroVacina.this, "", "Por favor, aguarde...", false);
        processos++;
        new RequisicaoAsyncTask().execute("ListaAlertas", "0", "");
*/
        //Carrega spinner de animais
        animais.clear();
        animais.add(new Animal(0, "Selecione o animal", "0", "0", "0", 0, "0", "0", "0", true,"0","0", usuario_t, raca_t));
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
                    animal_escolhido = (Animal)spAnimal.getItemAtPosition(position);

                    if (animal_escolhido != null)
                    {
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
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActCadastroVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega spinner de vacina aplicada
        aplicadas.clear();
        aplicadas.add("Vacina Aplicada?");
        aplicadas.add("Sim");
        aplicadas.add("Não");
        spAplicada = (Spinner) findViewById(R.id.spAplicada);
        ArrayAdapter adAplicada = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, aplicadas) {
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
        spAplicada.setAdapter(adAplicada);

        //Adiciona evento de item selecionado no spinner de vacina aplicada
        spAplicada.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (position == 1)
                        aplicada = 1;
                    else
                        aplicada = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                Toast.makeText(ActCadastroVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(JSONArray resultado) {

               try {
                //Verifica se foi obtido algum resultado
                if (resultado.length() == 0) {
                    Toast.makeText(ActCadastroVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                } else {
                    //Verifica se o objeto retornado é do tipo mensagem
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json))
                    {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActCadastroVacina.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Verifica se o usuário foi inserido com sucesso
                        if (metodo == "InsereEvento" && msg.getCodigo() == 7) {

                            pd.dismiss();

                            //Chama tela principal
                            Intent principal = new Intent(ActCadastroVacina.this, ActPrincipal.class);
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
                Toast.makeText(ActCadastroVacina.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }

}
