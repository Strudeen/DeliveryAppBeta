package com.CriStru.orurodeliveryapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CriStru.orurodeliveryapp.Adapters.Categorias.ItemClickSupport;
import com.CriStru.orurodeliveryapp.Adapters.Productos.ItemClickSupportProductos;
import com.CriStru.orurodeliveryapp.Models.Pedido;
import com.CriStru.orurodeliveryapp.Models.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PedidosFragment extends Fragment {
    private ArrayList<String> pedidosArray,pedidosArray2,getPedidosArray,getPedidosArray2;
    private ArrayList<Pedido> data;
    private DatabaseReference mDataBase;
    private Context context;
    private ListView listView;
    private boolean pedidosState;
    SharedPreferences sharedPref;
    int i=1;
    public PedidosFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pedidosArray=new ArrayList<>();
        pedidosArray2=new ArrayList<>();
        getPedidosArray=new ArrayList<>();
        getPedidosArray2=new ArrayList<>();
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        data = new ArrayList<>();
        context = getActivity();
        mDataBase= FirebaseDatabase.getInstance().getReference();
        if (getArguments() != null){
            pedidosState = getArguments().getBoolean("PEDIDOSESTADO");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pedidos_list, container, false);

        Log.d("context45",""+context);
        listView = (ListView) view.findViewById(R.id.pedidos_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, DetallesPedidoActivity.class);
                if (pedidosState == true){
                    intent.putExtra("IdPedido",getPedidosArray.get(i));
                }
                else {
                    intent.putExtra("IdPedido",getPedidosArray2.get(i));
                }
                startActivity(intent);
            }
        });
        LlenarDatos();

        return view;
    }

    private void LlenarDatos() {
        mDataBase.child("Pedidos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    pedidosArray.clear();
                    pedidosArray2.clear();
                    getPedidosArray.clear();
                    getPedidosArray2.clear();
                    data.clear();
                    i=0;
                    for (DataSnapshot ds:
                            dataSnapshot.getChildren()) {
                        String id=ds.getKey();
                        String dly="";
                        boolean estado = false;
                        if (ds.child("DLY").exists()){
                            dly = ds.child("DLY").getValue().toString();
                            mDataBase.child("Usuario").child(dly).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        SharedPreferences.Editor editor = sharedPref.edit();
                                        editor.putString("nombre_dly", dataSnapshot.child("nombre").getValue().toString());
                                        editor.commit();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            if(ds.child("estado").exists() && ds.child("estado").getValue().toString().equals("true")){
                                estado = true;
                            }
                            dly = sharedPref.getString("nombre_dly", "");

                        }
                        data.add(new Pedido(id,estado,dly));
                        i++;
                    }
                    for (int j = 0; j<data.size();j++) {
                        if (data.get(j).isCompletado()){
                            pedidosArray.add("Pedido: "+j+"\n Completado por: "+ data.get(j).getDly());
                            getPedidosArray.add(data.get(j).getId());
                        }
                        else{
                            if (data.get(j).getDly().equals("")){
                                pedidosArray2.add("Pedido: "+j);
                                getPedidosArray2.add(data.get(j).getId());
                            }
                            else {
                                pedidosArray2.add("Pedido: "+j+ "\n Tomado por: "+data.get(j).getDly());
                                getPedidosArray2.add(data.get(j).getId());
                            }

                        }
                    }
                    if (pedidosState == true){
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, android.R.id.text1,pedidosArray);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }else {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                android.R.layout.simple_list_item_1, android.R.id.text1,pedidosArray2);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                    }
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
