package com.CriStru.orurodeliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.CriStru.orurodeliveryapp.Adapters.Categorias.CategoriasAdapter;
import com.CriStru.orurodeliveryapp.Adapters.Categorias.ItemClickSupport;
import com.CriStru.orurodeliveryapp.Adapters.Scroll.AdapterScroll;
import com.CriStru.orurodeliveryapp.Adapters.Scroll.Promociones;
import com.CriStru.orurodeliveryapp.Models.Categoria;
import com.CriStru.orurodeliveryapp.UI.CategoriasDialogActivity;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private TextView txtUserName, txtUserEmail, txtTypeUser;

    private DatabaseReference dbOruro;
    private FirebaseAuth mAuth;
    private NavigationView mNavigationView;
    private FloatingActionButton mShopAction;
    private CategoriasAdapter mAdapter;
    private RecyclerView mRecyclerView;
    int maxPages=0,page;
    long timeInterval=5000;
    private ArrayList<Categoria> categoriaList = new ArrayList<>();
    private ArrayList<Promociones> promocionesArrayList = new ArrayList<>();
    private ViewPager2 scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = findViewById(R.id.drawer);
        mNavigationView = findViewById(R.id.navView);
        mShopAction = findViewById(R.id.shopFloating_Button3);
        mToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        scrollView = findViewById(R.id.picker);
        scrollView.setMinimumHeight(pxToDp(1440));

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }


        mShopAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goShopScreen();
            }
        });

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable menuIcon = getResources().getDrawable(R.drawable.ic_menu);
        View headerView = mNavigationView.getHeaderView(0);
        txtUserName = (TextView) headerView.findViewById(R.id.name_user);
        txtUserEmail = headerView.findViewById(R.id.email_user);
        txtTypeUser = headerView.findViewById(R.id.type_user);
        menuIcon.setColorFilter(getResources().getColor(R.color.colorWhiter), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(menuIcon);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.open();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = findViewById(R.id.recyclerviewCategorias);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbOruro = FirebaseDatabase.getInstance().getReference();

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                TextView idCategoria = (TextView) v.findViewById(R.id.idCategorias);
                Intent intent = new Intent(MainActivity.this, SubCategoriasActivity.class);
                intent.putExtra("idCategoria", idCategoria.getText().toString());
                startActivity(intent);
            }
        });


        getCategoriasFromFirebase();
    }


    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public void getCategoriasFromFirebase() {
        dbOruro.child("Categorias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    categoriaList.clear();
                    for (DataSnapshot ds :
                            dataSnapshot.getChildren()) {
                        String Nombre = ds.child("nombre").getValue().toString();
                        String Descripcion = ds.child("descripcion").getValue().toString();
                        String FotoUrl = ds.child("fotoUrl").getValue().toString();
                        String id = ds.getKey();
                        categoriaList.add(new Categoria(id, Nombre, Descripcion, FotoUrl));
                        promocionesArrayList.add(new Promociones(id,FotoUrl));
                    }
                    mAdapter = new CategoriasAdapter(categoriaList, R.layout.categorias_card, getApplicationContext());
                    mRecyclerView.setAdapter(mAdapter);

                    AdapterScroll scroll= new AdapterScroll(promocionesArrayList,R.layout.itemscroll_card, getApplicationContext());
                    scrollView.setAdapter(scroll);
                    maxPages = scroll.getItemCount();
                    final Handler handler = new Handler();
                    Runnable runable = new Runnable() {
                        @Override
                        public void run() {
                            //this will select next page number
                            page = page==maxPages? 0 : ++page;
                            //this will change the page to concrete page number
                            scrollView.setCurrentItem(page);
                            //this will execute this code after timeInterval
                            handler.postDelayed(this, timeInterval);
                        }
                    };
                    handler.postDelayed(runable, timeInterval);
                    TabLayout tabLayout = findViewById(R.id.tabDots);
                    new TabLayoutMediator(tabLayout, scrollView,new TabLayoutMediator.TabConfigurationStrategy(){
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            tab.setText("");
                        }
                    }).attach();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
       // Bundle extras=getIntent().getExtras();
        String[] name ={""};
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (user.getDisplayName() != null){
                name = user.getDisplayName().split(" ");
                mToolbar.setTitle("¡Bienvenido " + name[0] + "!");
                String name1 = user.getDisplayName();
                txtUserName.setText(name1);
            }
            else {/*
                if (!extras.getString("DisplayName").equals("")){
                    name = extras.getString("DisplayName").split(" ");
                    mToolbar.setTitle("Bienvenido " + name[0] + "!");
                    String name1 = extras.getString("DisplayName");
                    txtUserName.setText(name1);
                }*/
            }
            String email = user.getEmail();
            txtUserEmail.setText(email);
        } else {
            goLoginScreen();
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void goShopScreen(){
        Intent intent = new Intent(MainActivity.this, ShopActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.shop_Button){
            goShopScreen();
        }
        if (id == R.id.logout_Button) {
            logout();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_superior_icons, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.add);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbOruro.child("Usuario").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("tipo").getValue().toString().equals("USR")){
                        menuItem.setVisible(false);
                        txtTypeUser.setText("Cliente");
                        menuItem.setEnabled(false);
                    }
                    else if (dataSnapshot.child("tipo").getValue().toString().equals("ADM")){
                        menuItem.setVisible(true);
                        txtTypeUser.setText("Administrador");
                        menuItem.setEnabled(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, CategoriasDialogActivity.class);
                intent.putExtra("idCategoriaDialog", "");
                startActivity(intent);
                return true;
            case R.id.search:
                Intent intent2 = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

