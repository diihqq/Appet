package spypet.com.spypet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import controlador.GerenciadorSharedPreferences;
import controlador.TransformacaoCirculo;
import modelo.Animal;

/**
 * Created by Felipe on 04/10/2016.
 */
public class ActPetPerdido extends AppCompatActivity {

    private Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_perdido);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_pata);
        setSupportActionBar(t);

        try {
            Intent i = getIntent();
            JSONObject json = new JSONObject(i.getStringExtra("Animal"));
            animal = Animal.jsonToAnimal(json);
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPetPerdido.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }

        //Carrega informações do cachorro
        ImageView ivFotoPet = (ImageView) findViewById(R.id.ivFotoPet);
        Picasso.with(ActPetPerdido.this).load(animal.getFoto()).transform(new TransformacaoCirculo()).into(ivFotoPet);

        TextView tvNome = (TextView) findViewById(R.id.tvNome);
        tvNome.setText(animal.getNome());

        TextView tvGenero = (TextView) findViewById(R.id.tvGenero);
        tvGenero.setText(animal.getGenero());

        TextView tvCor = (TextView) findViewById(R.id.tvCor);
        tvCor.setText(animal.getCor());

        TextView tvPorte = (TextView) findViewById(R.id.tvPorte);
        tvPorte.setText(animal.getPorte());

        TextView tvCaracteristicas = (TextView) findViewById(R.id.tvCaracteristicas);
        tvCaracteristicas.setText(animal.getCaracteristicas());

        TextView tvNomeDono = (TextView) findViewById(R.id.tvNomeDono);
        tvNomeDono.setText(animal.getUsuario().getNome());

        TextView tvTelefoneDono = (TextView) findViewById(R.id.tvTelefoneDono);
        tvTelefoneDono.setText(animal.getUsuario().getTelefone());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        MenuItem item = menu.findItem(R.id.menuNotificacao);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuConfiguracoes:
                return true;
            case R.id.menuSair:
                //Limpa SharedPreferences
                GerenciadorSharedPreferences.setEmail(getBaseContext(), "");

                //Chama tela de login
                Intent principal = new Intent(ActPetPerdido.this, ActLogin.class);
                startActivity(principal);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
