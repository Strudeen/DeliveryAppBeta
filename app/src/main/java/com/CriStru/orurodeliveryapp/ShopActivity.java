package com.CriStru.orurodeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.CriStru.orurodeliveryapp.Adapters.Carrito.CarritoAdapter;
import com.CriStru.orurodeliveryapp.Adapters.Categorias.CategoriasAdapter;
import com.CriStru.orurodeliveryapp.Models.Carrito;
import com.CriStru.orurodeliveryapp.Models.Categoria;
import com.CriStru.orurodeliveryapp.UI.CategoriasDialogActivity;
import com.facebook.login.widget.LoginButton;
import com.orm.SugarContext;

import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity implements DataTransferInterface {

    private CarritoAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView tvprecioTotal;
    private ArrayList<Carrito> categoriaList;
    private Float precioTotal=0f;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SugarContext.init(this);
        setContentView(R.layout.activity_shop);
        mRecyclerView = findViewById(R.id.recyclerCarrito);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Carrito> carritos = Carrito.listAll(Carrito.class);
        Log.d("PrecioTotal",""+precioTotal);
        categoriaList = new ArrayList<>(carritos);
        mAdapter = new CarritoAdapter(R.layout.carrito_card,categoriaList,ShopActivity.this,this);
        precioTotal=mAdapter.precioTotal;
        mRecyclerView.setAdapter(mAdapter);
        tvprecioTotal = findViewById(R.id.tvPrecioTotal);
        mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setTitle("Carrito de Compras");
        final Drawable menuIcon = getResources().getDrawable(R.drawable.ic_back);
        menuIcon.setColorFilter(getResources().getColor(R.color.colorWhiter), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menuIcon);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvprecioTotal.setText(precioTotal + " Bs");

    }

    @Override
    public void onSetValues(float al) {
        tvprecioTotal.setText(""+al + " Bs");
        precioTotal = al;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirmar:
                List<Carrito> carritos=Carrito.listAll(Carrito.class);
                if (carritos.size()>=1){
                    Intent intent = new Intent(ShopActivity.this, FormPedidoActivity.class);
                    intent.putExtra("preciototal", tvprecioTotal.getText().toString());
                    intent.putExtra("direccion", "");
                    intent.putExtra("idUbicacion","");
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(ShopActivity.this, "Debes tener almenos 1 producto en tu carrito", Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}