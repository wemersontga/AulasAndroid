package com.wcasoft.instagramclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wcasoft.instagramclone.R;
import com.wcasoft.instagramclone.activity.CadastroActivity;
import com.wcasoft.instagramclone.adapter.AdapterPesquisa;
import com.wcasoft.instagramclone.helper.ConfiguraFirebase;
import com.wcasoft.instagramclone.model.Usuario;


import java.util.ArrayList;
import java.util.List;

public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recycleViewPesquisa;

    private List<Usuario> listaUsuarios;
    private DatabaseReference usuariosRef;
    private AdapterPesquisa adapterPesquisa;

    Usuario user = new Usuario();
    String textoDigitado;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PesquisaFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PesquisaFragment newInstance(String param1, String param2) {
        PesquisaFragment fragment = new PesquisaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recycleViewPesquisa = view.findViewById(R.id.recyclerPesquisa);


        listaUsuarios = new ArrayList<>();
        usuariosRef = ConfiguraFirebase.getReferenciaFireBase().child("usuarios");

        // Configura recyclerView;
        recycleViewPesquisa.setHasFixedSize(true);
        recycleViewPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        recycleViewPesquisa.setAdapter(adapterPesquisa);

        // Configura SearchView.
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //Não esta funcionando.
                Log.i("onQueryTextSubmit","texto digitado: " + query);

                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) { // Esta funcionando.

                Log.d("onQueryTextChange","texto digitado: " + newText);

                textoDigitado = newText;
                nomeFormatado();

                Log.d("onQueryTextChange","SearchViewPesquisa: " + textoDigitado);

                pesquisarUsuarios(user.getNome()); // O valor passado aqui é usado na pesquisa;

                Log.d("onQueryTextChange","user.getNome: " + user.getNome());
                return true;
            }
        });

        return view;

    }

    public void pesquisarUsuarios(String texto) {

        //limpa usuarios.
        listaUsuarios.clear();

        if(texto.length() >= 2){ // Se searchViewPesquisa não for vazio faz a pesquisa;

            Query query = usuariosRef.orderByChild("nome").startAt(texto).endAt(texto + "\uf8ff");

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    //limpa a lista para não gerar duplicidade na pesquisa;
                    listaUsuarios.clear();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        listaUsuarios.add(ds.getValue(Usuario.class));

                    }

                    //Notifica o adapter das mudanças dos dados;
                    adapterPesquisa.notifyDataSetChanged();

                    int total = listaUsuarios.size();
                    Log.i("Total de usuários","total: " + total);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {


                }
            });

        }
    }




    public void nomeFormatado() {

        //String nome = user.getNome();
        ArrayList<String> lista = new ArrayList<String>();
        ArrayList<String> listaFinal = new ArrayList<String>();

        String replaceOne = "";
        String ordenaString = "";
        boolean referencia = false;
        String aux = "";

        if(textoDigitado.length() == 0) { // Se o campo de pesquisa for vazio;
            textoDigitado = "*";
        }

            char ch = textoDigitado.charAt(0);
            String str = String.valueOf(ch).toUpperCase();


            for (int i = 0; i < textoDigitado.length(); i++) {

                char filtro = textoDigitado.charAt(i); // Divide a String em caracteres;
                aux = String.valueOf(filtro);

                lista.add(aux);

            }

// ******************* UpperCase usando Listas: *********************
        if( !lista.get(0).equals(" ")) {
            replaceOne = lista.get(0).toUpperCase();
            listaFinal.add(replaceOne);

            System.out.println("+--------------------------+");
            System.out.println("Primeira letra UpperCase(): " + replaceOne);

        }

        for( int a = 1; a< lista.size(); a++ ) {

            if( !lista.get(a).equals(" ")) { //adiciona todos menos os espaços;
                listaFinal.add(lista.get(a));

                if(referencia == true){

                    listaFinal.remove(a);
                    listaFinal.add(lista.get(a).toUpperCase());
                    referencia = false;
                }

            }

            if( lista.get(a).equals(" ") ) { //adiciona todos os espaços;
                listaFinal.add(lista.get(a));

                referencia = true;

            }

        }

        for (int b = 0; b < listaFinal.size(); b++){

            System.out.println("listaFinal lf: " + listaFinal.get(b));

            ordenaString = ordenaString + listaFinal.get(b);
        }

        user.setNome(ordenaString);

    }
}

