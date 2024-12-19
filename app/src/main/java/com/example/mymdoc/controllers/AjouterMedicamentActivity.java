package com.example.mymdoc.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymdoc.db.DatabaseHelper;
import com.example.mymdoc.models.Medicament;
import com.example.mymdoc.R;

public class AjouterMedicamentActivity extends AppCompatActivity {

    private NumberPicker numberPickerDays;
    private Button buttonArrowUp;
    private Button buttonArrowDown;
    private DatabaseHelper databaseHelper; // Pour gérer la base de données

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_medicament);

        // Initialisation de l'instance DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialisation du NumberPicker et des boutons
        numberPickerDays = findViewById(R.id.number_picker_days);
        buttonArrowUp = findViewById(R.id.button_arrow_up);
        buttonArrowDown = findViewById(R.id.button_arrow_down);

        // Configuration du NumberPicker pour des valeurs de 0 à 30
        numberPickerDays.setMinValue(0);
        numberPickerDays.setMaxValue(30);
        numberPickerDays.setValue(7);

        // Incrémenter la valeur avec le bouton "fleche haut"
        buttonArrowUp.setOnClickListener(v -> {
            int currentValue = numberPickerDays.getValue();
            if (currentValue < numberPickerDays.getMaxValue()) {
                numberPickerDays.setValue(currentValue + 1);
            } else {
                Toast.makeText(this, "Valeur maximale atteinte", Toast.LENGTH_SHORT).show();
            }
        });

        // Décrémenter la valeur avec le bouton "fleche bas"
        buttonArrowDown.setOnClickListener(v -> {
            int currentValue = numberPickerDays.getValue();
            if (currentValue > numberPickerDays.getMinValue()) {
                numberPickerDays.setValue(currentValue - 1);
            } else {
                Toast.makeText(this, "Valeur minimale atteinte", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialisation des champs de saisie
        EditText editTextNom = findViewById(R.id.editText_nom);
        CheckBox checkBoxMatin = findViewById(R.id.checkbox_matin);
        CheckBox checkBoxMidi = findViewById(R.id.checkbox_midi);
        CheckBox checkBoxSoir = findViewById(R.id.checkbox_soir);
        Button buttonValider = findViewById(R.id.button_valider);

        // Configuration du clic sur le bouton "Valider"
        buttonValider.setOnClickListener(v -> {
            String nomMedicament = editTextNom.getText().toString();
            int duree = numberPickerDays.getValue();

            boolean isMatin = checkBoxMatin.isChecked();
            boolean isMidi = checkBoxMidi.isChecked();
            boolean isSoir = checkBoxSoir.isChecked();

            // Vérifier que le nom n'est pas vide
            if (nomMedicament.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un nom pour le médicament", Toast.LENGTH_SHORT).show();
                return;
            }

            // Créer un objet Medicament et l'enregistrer dans la base de données
            Medicament medicament = new Medicament(nomMedicament, duree, isMatin, isMidi, isSoir);
            databaseHelper.addMedicament(medicament);

            // Afficher une notification pour indiquer que l'enregistrement est réussi
            Toast.makeText(this, "Médicament ajouté avec succès", Toast.LENGTH_SHORT).show();

            // Renvoyer les données à l'activité appelante pour mettre à jour l'interface
            Intent resultIntent = new Intent();
            resultIntent.putExtra("nom_medicament", nomMedicament);
            resultIntent.putExtra("duree_medicament", duree);
            resultIntent.putExtra("prise_matin", isMatin);
            resultIntent.putExtra("prise_midi", isMidi);
            resultIntent.putExtra("prise_soir", isSoir);
            setResult(RESULT_OK, resultIntent);

            // Fermer l'activité
            finish();
        });
    }
}





