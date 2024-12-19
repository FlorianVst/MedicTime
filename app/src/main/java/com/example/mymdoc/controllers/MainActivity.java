package com.example.mymdoc.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymdoc.db.DatabaseHelper;
import com.example.mymdoc.R;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewPrises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de la ListView
        listViewPrises = findViewById(R.id.listViewPrises);

        // Charger les données initiales dans la liste
        refreshList();

        // Ajouter un bouton pour ouvrir AjoutPrisesMedocActivity
        Button ajouterButton = findViewById(R.id.ajouterButton);
        ajouterButton.setOnClickListener(v -> {
            // Lancez AjoutPrisesMedocActivity
            Intent intent = new Intent(MainActivity.this, AjoutPrisesMedocActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualiser la liste lorsque l'activité reprend
        refreshList();
    }

    // Méthode pour rafraîchir la liste des prises
    private void refreshList() {
        // Obtenez une instance de DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Récupérez le mois et l'année actuels
        Calendar calendar = Calendar.getInstance();
        int moisActuel = calendar.get(Calendar.MONTH) + 1; // Les mois sont indexés à partir de 0
        int anneeActuelle = calendar.get(Calendar.YEAR);

        // Récupérer les données structurées
        List<String> prisesPourLeMois = databaseHelper.getPrisesParJourEtPeriode(moisActuel, anneeActuelle);

        // Configurer un adaptateur simple pour afficher les données
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, prisesPourLeMois);
        listViewPrises.setAdapter(adapter);

        Log.d("MainActivity", "Liste des prises mise à jour.");
    }
}





