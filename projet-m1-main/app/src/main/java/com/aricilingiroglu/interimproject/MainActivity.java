package com.aricilingiroglu.interimproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isFirstUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check first use
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        //wipe wipe wipe wipe
        //preferences.edit().clear().apply();

        isFirstUse = preferences.getBoolean("is_first_use", true);


        if (isFirstUse) {
            // show message
            setContentView(R.layout.activity_main);
            TextView tvWelcome = findViewById(R.id.welcome);
            TextView useManual = findViewById(R.id.first_use);
            useManual.setVisibility(View.VISIBLE);
            tvWelcome.setVisibility(View.VISIBLE);

            Button btnOk = findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //aller sur la page recherche
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);

                    //ajout info de first use dedans pour eviter a le voir a chaque utilisation
                    preferences.edit().putBoolean("is_first_use", false).apply();
                }
            });
        } else {
            //si on est la alors nb utilisation > 1 On passe directement au page de recherche
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // empeche la retour sur la page
        }
    }
}
