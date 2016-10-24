package spypet.com.spypet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import controlador.GerenciadorSharedPreferences;
import controlador.Requisicao;
import controlador.TransformacaoCirculo;
import modelo.Animal;
import modelo.EstabelecimentoFavorito;
import modelo.Evento;
import modelo.Mensagem;
import modelo.Notificacao;
import modelo.Usuario;

/**
 * Created by Felipe on 05/06/2016.
 */
public class ActPrincipal extends AppCompatActivity {

    public int tabSelecionada;
    public static Usuario usuarioLogado;
    private ProgressDialog pd;
    private ArrayList<Animal> listaPets = new ArrayList<>();
    private ArrayList<Animal> listaPetsPerdidos = new ArrayList<>();
    private ArrayList<Evento> listaEventos = new ArrayList<>();
    public static ArrayList<EstabelecimentoFavorito> listaEstabelecimentosFavoritos = new ArrayList<>();
    private ImageView ivFotoAnimal;
    public static ArrayList<Notificacao> listaNotificacoes = new ArrayList<>();
    private int processos = 0;
    ListView lvConfiguracoes;
    ArrayAdapter<Animal> adpConfiguracoes;
    ArrayAdapter<Animal> adpPetsPerdidos;
    ArrayAdapter<EstabelecimentoFavorito> adpEstabelecimentosFavoritos;
    ListView lvFavoritos;

    ListView lvEventos;
    ArrayAdapter<Evento> adpEventos;
    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        pd = ProgressDialog.show(ActPrincipal.this, "", "Por favor, aguarde...", false);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        //Adiciona as opções nas tabs
        configuraTabs();

        //Monta lista de animais perdidos
        listaPetsPerdidos();

        //Monta lista de compromissos
        listaCompromissos();

        //Monta lista de pets
        listaPets();

        //Monta lista lugares favoritos
        listaFavoritos();

        //Adiciona evento de click no botão de escanear QRCode.
        Button btEscanear = (Button)findViewById(R.id.btEscanear);
        btEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tela = new Intent(ActPrincipal.this, ActLeitorQRCode.class);
                tela.putExtra("Solicitante", ActPrincipal.class.toString());
                startActivity(tela);
            }
        });

        recuperaUsuario();
    }

    //Bloqueia o botão de voltar do android
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.menu != null) {
            recuperaNotificacoes();

            //Monta lista lugares favoritos
            listaFavoritos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        this.menu = menu;
        recuperaNotificacoes();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActPrincipal.this, ActSobre.class);
                startActivity(intent1);
                return true;
            case R.id.menuNotificacao:
                Intent intent2 = new Intent(ActPrincipal.this, ActNotificacoes.class);
                startActivity(intent2);
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(),"");

                //Chama tela de login
                Intent principal = new Intent(ActPrincipal.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Recupera usuário logado
    public void recuperaUsuario(){
        if(ActPrincipal.usuarioLogado == null){
            try {
                processos++;
                JSONObject json = new JSONObject();
                json.put("Email",GerenciadorSharedPreferences.getEmail(getBaseContext()));
                //Chama método para recuperar usuário logado
                new RequisicaoAsyncTask().execute("RecuperaUsuario", "0", json.toString());
            }catch(Exception ex){
                Log.e("Erro", ex.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Recupera notificações
    public void recuperaNotificacoes(){
        try {
            processos++;
            if(!pd.isShowing()){
                pd = ProgressDialog.show(ActPrincipal.this, "", "Por favor, aguarde...", false);
            }
            JSONObject json = new JSONObject();
            json.put("Email",GerenciadorSharedPreferences.getEmail(getBaseContext()));
            //Chama método para recuperar usuário logado
            new RequisicaoAsyncTask().execute("ListaNotificacoesPorUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Monta a lista de pets do usuário
    public void listaPets(){
        adpConfiguracoes = new ArrayAdapter<Animal>(this,R.layout.item_configuracoes){
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_configuracoes, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                final int index = position;

                ImageView ivFotoAnimal = (ImageView) convertView.findViewById(R.id.ivFotoAnimal);
                TextView tvNomeAnimal = (TextView) convertView.findViewById(R.id.tvNomeAnimal);

                Animal animal = (Animal)getItem(position);

                Picasso.with(getContext()).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoAnimal);
                tvNomeAnimal.setText(animal.getNome());

                if (animal.isDesaparecido())
                    tvNomeAnimal.setTextColor(Color.RED);

                //Adiciona evento de click no botão de deletar pet.
                ImageView ivRemover = (ImageView) convertView.findViewById(R.id.ivExcluirPet);
                ivRemover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int index2 = index;

                        //Monta caixa de dialogo de confirmação de deleção.
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                        dialogo.setTitle("Aviso!")
                                .setMessage("Você tem certeza que deseja apagar esse pet? Todos os compromissos relacionados a esse pet também serão apagados.")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        pd = ProgressDialog.show(ActPrincipal.this, "", "Por favor, aguarde...", false);
                                        processos++;
                                        new RequisicaoAsyncTask().execute("ExcluiAnimal", String.valueOf(adpConfiguracoes.getItem(index2).getIdAnimal()), "");
                                    }
                                })
                                .setNegativeButton("Não", null);
                        AlertDialog alerta = dialogo.create();
                        alerta.show();
                    }
                });

                return convertView;
            }
        };
        lvConfiguracoes = (ListView)findViewById(R.id.lvConfiguracoes);
        lvConfiguracoes.setAdapter(adpConfiguracoes);

        //Adiciona o evento de click nos items da lista
        lvConfiguracoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Animal item = (Animal) parent.getItemAtPosition(position);
                    Intent configuracoes = new Intent(ActPrincipal.this, ActPets.class);
                    configuracoes.putExtra("Animal", item.animalToJson().toString());
                    startActivity(configuracoes);
                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Evento click do botão flutuante de adicionar pets
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fbAddPet);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActPrincipal.this, ActCadastroPet.class);
                startActivity(i);
            }
        });


        //Carrega lista de pets do usuário
        processos++;
        listaPets.clear();
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListaAnimaisDoUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Monta a lista de compromissos
    public void listaCompromissos(){

        adpEventos = new ArrayAdapter<Evento>(this,R.layout.item_compromissos){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_compromissos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                final int index = position;

                ImageView ivFotoAnimalCompromisso = (ImageView) convertView.findViewById(R.id.ivFotoAnimalCompromisso);
                ImageView ivTipoEvento = (ImageView) convertView.findViewById(R.id.ivTipoEvento);
                TextView tvNomeCompromisso = (TextView) convertView.findViewById(R.id.tvNomeCompromisso);
                TextView tvInformacao = (TextView) convertView.findViewById(R.id.tvInformacao);

                Evento evento = (Evento)getItem(position);

                Picasso.with(getContext()).load(evento.getAnimal().getFoto()).transform(new TransformacaoCirculo()).into(ivFotoAnimalCompromisso);

                if (evento.getTipo().equals("Compromisso")) {
                    tvNomeCompromisso.setText(evento.getNome());
                    Picasso.with(getContext()).load(R.drawable.ic_compromisso).into(ivTipoEvento);
                    tvInformacao.setText("Local: " + evento.getCompromisso().getNomelocal()
                            + "\nData: " + transformaData(evento.getCompromisso().getDatahora()));
                }
                else if (evento.getTipo().equals("Medicamento")) {
                    tvNomeCompromisso.setText(evento.getNome());
                    Picasso.with(getContext()).load(R.drawable.ic_medicamento).into(ivTipoEvento);
                    tvInformacao.setText("Inicio: " + transformaData(evento.getMedicamento().getInicio()) +  "\nFim: " +
                            transformaData(evento.getMedicamento().getFim()));
                }
                else if (evento.getTipo().equals("Vacina")) {
                    tvNomeCompromisso.setText(evento.getNome());
                    Picasso.with(getContext()).load(R.drawable.ic_vacina).into(ivTipoEvento);
                    tvInformacao.setText("Aplicação: " + transformaData(evento.getVacina().getDataaplicacao()) + "\nValidade: " +
                            transformaData(evento.getVacina().getDatavalidade()));
                }

                //Adiciona evento de click no botão de deletar pet.
                ImageView ivRemover = (ImageView) convertView.findViewById(R.id.ivExcluirCompromisso);
                ivRemover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int index2 = index;

                        //Monta caixa de dialogo de confirmação de deleção.
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                        dialogo.setTitle("Aviso!")
                                .setMessage("Você tem certeza que deseja apagar este evento?")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (adpEventos.getItem(index2).getTipo().equals("Compromisso")) {
                                            new RequisicaoAsyncTask().execute("ExcluiCompromisso", String.valueOf(adpEventos.getItem(index2).getIdEvento()), "");
                                        }
                                        else if (adpEventos.getItem(index2).getTipo().equals("Medicamento")) {
                                            new RequisicaoAsyncTask().execute("ExcluiMedicamento", String.valueOf(adpEventos.getItem(index2).getIdEvento()), "");;
                                        }
                                        else if (adpEventos.getItem(index2).getTipo().equals("Vacina")) {
                                            new RequisicaoAsyncTask().execute("ExcluiVacina", String.valueOf(adpEventos.getItem(index2).getIdEvento()), "");
                                        }
                                    }
                                })
                                .setNegativeButton("Não", null);
                        AlertDialog alerta = dialogo.create();
                        alerta.show();
                    }
                });

                return convertView;
            }
        };

        lvEventos = (ListView)findViewById(R.id.lvCompromissos);
        lvEventos.setAdapter(adpEventos);

        //Evento click do botão flutuante de adicionar compromissos
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fbAddCompromisso);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Monta caixa de dialogo com as opções de eventos.
                CharSequence items[] = new CharSequence[]{"Vacina", "Medicamento", "Compromisso"};
                AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                dialogo.setIcon(R.mipmap.ic_launcher);
                dialogo.setTitle("Novo evento");
                dialogo.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(ActPrincipal.this, ActPrincipal.class);
                        if (which == 0) {
                            i = new Intent(ActPrincipal.this, ActCadastroVacina.class);
                        } else {
                            if (which == 1) {
                                i = new Intent(ActPrincipal.this, ActCadastroMedicamento.class);
                            } else {
                                i = new Intent(ActPrincipal.this, ActCadastroCompromisso.class);
                            }
                        }
                        startActivity(i);
                    }
                });
                dialogo.show();
            }
        });

        //Carrega lista de eventos do pet do usuário
        processos++;
        listaEventos.clear();
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListaEventosPorUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

    }

    public String transformaData(String data1)
    {
        String format = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(data1.replaceAll("-", "/")));
    }


    //Monta a lista de animais perdidos
    public void listaPetsPerdidos(){
        // método chamado para cada item do lvPetsPerdidos
        adpPetsPerdidos = new ArrayAdapter<Animal>(this, R.layout.item_animais_perdidos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_animais_perdidos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                int index = 0;
                if(position == 0) {
                    index = position;
                }else {
                    index = position*2;
                }

                Animal animal1 = null;
                Animal animal2 = null;
                if(index < this.getCount()) {
                    animal1 = getItem(index);
                }
                if (index + 1 < this.getCount()) {
                    animal2 = getItem(index+1);
                }

                ImageView ivAnimal1 = (ImageView) convertView.findViewById(R.id.ivAnimal1);
                ImageView ivAnimal2 = (ImageView) convertView.findViewById(R.id.ivAnimal2);

                if (animal1 != null){
                    Picasso.with(getContext()).load(animal1.getFoto()).into(ivAnimal1);
                    final Animal an1 = animal1;
                    //Adiciona evento de click na foto do pet
                    ivAnimal1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ActPrincipal.this, ActPetPerdido.class);
                                intent.putExtra("Animal", an1.animalToJson().toString());
                                startActivity(intent);
                            }catch (Exception ex){
                                Log.e("Erro", ex.getMessage());
                                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (animal2 != null) {
                    Picasso.with(getContext()).load(animal2.getFoto()).into(ivAnimal2);
                    final Animal an2 = animal2;
                    //Adiciona evento de click na foto do pet
                    ivAnimal2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ActPrincipal.this, ActPetPerdido.class);
                                intent.putExtra("Animal", an2.animalToJson().toString());
                                startActivity(intent);
                            } catch (Exception ex) {
                                Log.e("Erro", ex.getMessage());
                                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                return convertView;
            }
        };

        //Carrega lista de pets do usuário
        processos++;
        listaPetsPerdidos.clear();
        new RequisicaoAsyncTask().execute("ListaAnimaisDesaparecidos", "0", "");

        ListView lvPetsPerdidos = (ListView)findViewById(R.id.lvPetsPerdidos);
        lvPetsPerdidos.setAdapter(adpPetsPerdidos);
    }

    //Monta a lista de pets do usuário
    public void listaFavoritos() {
        //Evento click do botão flutuante de adicionar favoritos
        FloatingActionButton button = (FloatingActionButton)findViewById(R.id.fbAddFavorito);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActPrincipal.this, ActTelaMapa.class);
                i.putExtra("EstabelecimentoFavorito","");
                startActivity(i);
            }
        });


        adpEstabelecimentosFavoritos = new ArrayAdapter<EstabelecimentoFavorito>(this,R.layout.item_favoritos){
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_favoritos, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                ImageView ivFavorito = (ImageView) convertView.findViewById(R.id.ivFavorito);
                TextView tvNomeFavorito = (TextView) convertView.findViewById(R.id.tvNomeFavorito);
                TextView tvEndereco = (TextView) convertView.findViewById(R.id.tvEndereco);

                EstabelecimentoFavorito estabelecimentoFavorito = (EstabelecimentoFavorito)getItem(position);

                tvNomeFavorito.setText(estabelecimentoFavorito.getNome());
                tvEndereco.setText(estabelecimentoFavorito.getEndereco());
                if(estabelecimentoFavorito.getTipo().equals("Pet Shop")) {
                    Picasso.with(getContext()).load(R.drawable.ic_pata2).into(ivFavorito);
                }else{
                    Picasso.with(getContext()).load(R.drawable.ic_vacina).into(ivFavorito);
                }


                final int index = position;

                //Adiciona evento de click no botão de deletar favorito.
                ImageView ivExcluirFavorito = (ImageView) convertView.findViewById(R.id.ivExcluirFavorito);
                ivExcluirFavorito.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final int index2 = index;

                        //Monta caixa de dialogo de confirmação de deleção.
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(ActPrincipal.this);
                        dialogo.setTitle("Aviso!")
                                .setMessage("Você tem certeza que deseja remover esse estabelecimento da lista de favoritos?")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        pd = ProgressDialog.show(ActPrincipal.this, "", "Por favor, aguarde...", false);
                                        processos++;
                                        new RequisicaoAsyncTask().execute("ExcluiEstFavorito", String.valueOf(adpEstabelecimentosFavoritos.getItem(index2).getIdEstabelecimentoFavorito()), "");
                                    }
                                })
                                .setNegativeButton("Não", null);
                        AlertDialog alerta = dialogo.create();
                        alerta.show();
                    }
                });

                return convertView;
            }
        };
        lvFavoritos = (ListView)findViewById(R.id.lvFavoritos);
        lvFavoritos.setAdapter(adpEstabelecimentosFavoritos);

        //Adiciona o evento de click nos items da lista
        lvFavoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    EstabelecimentoFavorito item = (EstabelecimentoFavorito) parent.getItemAtPosition(position);
                    Intent mapa = new Intent(ActPrincipal.this, ActTelaMapa.class);
                    mapa.putExtra("EstabelecimentoFavorito", item.estabelecimentoFavoritoToJson().toString());
                    startActivity(mapa);
                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Carrega lista de favoritos
        processos++;
        listaEstabelecimentosFavoritos.clear();
        try {
            JSONObject json = new JSONObject();
            json.put("Email", GerenciadorSharedPreferences.getEmail(getBaseContext()));
            new RequisicaoAsyncTask().execute("ListaEstFavoritosPorUsuario", "0", json.toString());
        }catch(Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Configura as tabs da tela principal
    public void configuraTabs(){
        //Adiciona as opções nas tabs da tela principal
        TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);

        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("Principal");
        descritor.setContent(R.id.llPrincipal);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_globo, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Compromissos");
        descritor.setContent(R.id.llCompromissos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_calendario, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Favoritos");
        descritor.setContent(R.id.llFavoritos);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_place, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Configuracoes");
        descritor.setContent(R.id.llConfiguracoes);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_pata, getTheme()));
        abas.addTab(descritor);

        //Seta o fundo da primeira tab selecionada
        tabSelecionada = abas.getCurrentTab();
        abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

        abas.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {
                //Seta a cor de fundo da tab selecionada
                TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);
                for (int i = 0; i < abas.getTabWidget().getChildCount(); i++) {
                    abas.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                }
                abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.buttonColorPrimary));

                //Anima a transição de tabs
                View viewSelecionada = abas.getCurrentView();
                if (abas.getCurrentTab() > tabSelecionada)
                {
                    viewSelecionada.setAnimation(direita());
                }
                else
                {
                    viewSelecionada.setAnimation(esquerda());
                }
                tabSelecionada = abas.getCurrentTab();

            }

        });
    }

    //Anima a transição vinda da direita
    public Animation direita() {
        Animation direita = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        direita.setDuration(240);
        direita.setInterpolator(new AccelerateInterpolator());
        return direita;
    }

    //Anima a transição vinda da esquerda
    public Animation esquerda() {
        Animation esquerda = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        esquerda.setDuration(240);
        esquerda.setInterpolator(new AccelerateInterpolator());
        return esquerda;
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
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ActPrincipal.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                        //Se a exclusão foi bem sucedida remove o item da lista
                        if (metodo == "ExcluiAnimal" && msg.getCodigo() == 11) {
                            int index = 0;
                            for (int i = 0; i < listaPets.size(); i++) {
                                if (id == listaPets.get(i).getIdAnimal()) {
                                    index = i;
                                    break;
                                }
                            }
                            listaPets.remove(index);
                            adpConfiguracoes.clear();
                            adpConfiguracoes.addAll(listaPets);
                        }else{
                            //Se a exclusão foi bem sucedida remove o item da lista
                            if (metodo == "ExcluiEstFavorito" && msg.getCodigo() == 11) {
                                int index = 0;
                                for (int i = 0; i < listaEstabelecimentosFavoritos.size(); i++) {
                                    if (id == listaEstabelecimentosFavoritos.get(i).getIdEstabelecimentoFavorito()) {
                                        index = i;
                                        break;
                                    }
                                }
                                listaEstabelecimentosFavoritos.remove(index);
                                adpEstabelecimentosFavoritos.clear();
                                adpEstabelecimentosFavoritos.addAll(listaEstabelecimentosFavoritos);
                            }
                        }
                    } else {
                        //Verifica qual foi o método chamado
                        if (metodo == "RecuperaUsuario") {
                            //Recupera usuário retornado pela API
                            ActPrincipal.usuarioLogado = Usuario.jsonToUsuario(json);
                        } else {
                            if (metodo == "ListaAnimaisDoUsuario") {
                                //Monta lista de animais do usuário logado
                                for (int i = 0; i < resultado.length(); i++) {
                                    listaPets.add(Animal.jsonToAnimal(resultado.getJSONObject(i)));
                                }
                                adpConfiguracoes.clear();
                                adpConfiguracoes.addAll(listaPets);
                            } else {
                                if (metodo == "ListaAnimaisDesaparecidos") {
                                    //Monta lista de animais desaparecidos
                                    for (int i = 0; i < resultado.length(); i++) {
                                        listaPetsPerdidos.add(Animal.jsonToAnimal(resultado.getJSONObject(i)));
                                    }
                                    adpPetsPerdidos.clear();
                                    adpPetsPerdidos.addAll(listaPetsPerdidos);
                                }else{
                                    if(metodo == "ListaNotificacoesPorUsuario"){
                                        listaNotificacoes.clear();
                                        Notificacao notificacao;
                                        boolean lida = true;
                                        //Monta lista de animais desaparecidos
                                        for (int i = 0; i < resultado.length(); i++) {
                                            notificacao = Notificacao.jsonToNotificacao(resultado.getJSONObject(i));
                                            listaNotificacoes.add(notificacao);
                                            if(!notificacao.isLida()){
                                                lida = false;
                                            }
                                        }

                                        //Recupera botão de sino
                                        MenuItem item = menu.findItem(R.id.menuNotificacao);

                                        if(!lida) {
                                            //Seta imagem de alerta
                                            item.setIcon(R.drawable.ic_notificacao_2);
                                        }else{
                                            item.setIcon(R.drawable.ic_notificacao);
                                        }

                                    }
                                    else if(metodo == "ListaEventosPorUsuario")
                                    {
                                        //Monta lista de eventos dos animais do usuário logado
                                        for (int i = 0; i < resultado.length(); i++) {
                                            listaEventos.add(Evento.jsonToEvento(resultado.getJSONObject(i)));
                                        }
                                        adpEventos.clear();
                                        adpEventos.addAll(listaEventos);
                                    }
                                    else if(metodo == "ListaEstFavoritosPorUsuario"){
                                        //Monta lista de estabelecimentos favoritos
                                        for (int i = 0; i < resultado.length(); i++) {
                                            listaEstabelecimentosFavoritos.add(EstabelecimentoFavorito.jsonToEstabelecimentoFavorito(resultado.getJSONObject(i)));
                                        }
                                        adpEstabelecimentosFavoritos.clear();
                                        adpEstabelecimentosFavoritos.addAll(listaEstabelecimentosFavoritos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }

            //remove dialogo de progresso da tela
            processos--;
            if(processos == 0) {
                pd.dismiss();
            }
        }
    }
}

