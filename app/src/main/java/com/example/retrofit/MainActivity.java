package com.example.retrofit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.retrofit.api.CepService;
import com.example.retrofit.model.CEP;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button botaoRecuperar;
    private TextView textoResultado;
    private EditText campoCep;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botaoRecuperar = findViewById(R.id.main_botaoCep);
        textoResultado = findViewById(R.id.main_resultado_cep);
        campoCep = findViewById(R.id.main_campo_cep);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        botaoRecuperar.setOnClickListener(view -> {
            recuperarCep();
        });

    }

    private void recuperarCep() {
        String cepFormatado = campoCep.getText().toString();
        CepService cepService = retrofit.create(CepService.class);
        Call<CEP> cepCall = cepService.recuperarCep(cepFormatado);
        cepCall.enqueue(new Callback<CEP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<CEP> call, Response<CEP> response) {
                if (response.isSuccessful()) {
                    textoResultado.setVisibility(View.VISIBLE);
                    CEP cep = response.body();
                    if (cep.getLogradouro() != null) {
                        textoResultado.setText("Logradouro: " + cep.getLogradouro()
                                + "\n" + "Bairro: " + cep.getBairro()
                                + "\n" + "Estado: " + cep.getLocalidade()
                                + "\n" + "UF: " + cep.getUf()
                                + "\n" + "CEP: " + cep.getCep());
                        fecharTeclado();
                        Log.i("TAG", "sucesso ");
                    } else {
                        Toast.makeText(MainActivity.this, "Cep invalido", Toast.LENGTH_SHORT).show();
                        campoCep.setText("");
                        campoCep.requestFocus();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Cep nao encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CEP> call, Throwable t) {
                Log.i("TAG", "falha ");


                Toast.makeText(MainActivity.this, "Cep nao encontrado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fecharTeclado(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm.isActive())
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
