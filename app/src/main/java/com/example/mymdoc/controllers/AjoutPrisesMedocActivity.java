package com.example.mymdoc.controllers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mymdoc.db.DatabaseHelper;
import com.example.mymdoc.models.Medicament;
import com.example.mymdoc.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AjoutPrisesMedocActivity extends AppCompatActivity {

    private static final int ADD_MEDICAMENT_REQUEST_CODE = 1; // Code pour identifier l'ajout de médicaments
    private Button buttonSelectStartDate;
    private Button buttonSelectEndDate;
    private Button buttonValider;
    private Button addMedicamentButton;
    private List<Medicament> medicamentList = new ArrayList<>(); // Initialisation de la liste des médicaments
    private TextView selectedStartDate;
    private TextView selectedEndDate;
    private Spinner spinnerMedicaments; // Référence au Spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_prises_medicament);

        // Initialisation des boutons et TextViews pour la sélection des dates et validation
        buttonSelectStartDate = findViewById(R.id.button_select_start_date);
        buttonSelectEndDate = findViewById(R.id.button_select_end_date);
        buttonValider = findViewById(R.id.button_valider);
        addMedicamentButton = findViewById(R.id.add_medicament_button);
        selectedStartDate = findViewById(R.id.button_select_start_date);
        selectedEndDate = findViewById(R.id.button_select_end_date);
        spinnerMedicaments = findViewById(R.id.spinner_medicaments); // Initialisation du Spinner

        // Initialisez votre DatabaseHelper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        // Récupérez les noms des médicaments
        List<String> medicamentNames = databaseHelper.getAllMedicamentNames();

        // Créez un ArrayAdapter pour le Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                medicamentNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Assignez l'adaptateur au Spinner
        spinnerMedicaments.setAdapter(adapter);

        // Configurer les événements de clic pour la sélection des dates
        buttonSelectStartDate.setOnClickListener(v -> showDatePickerDialog("start"));
        buttonSelectEndDate.setOnClickListener(v -> showDatePickerDialog("end"));

        // Configurer l'événement de clic pour le bouton de validation
        buttonValider.setOnClickListener(v -> saveSelections());

        // Configurer l'événement de clic pour ajouter un médicament
        addMedicamentButton.setOnClickListener(v -> {
            Intent intent = new Intent(AjoutPrisesMedocActivity.this, AjouterMedicamentActivity.class);
            startActivityForResult(intent, ADD_MEDICAMENT_REQUEST_CODE);
        });
    }

    // Gérer le résultat de l'activité AjouterMedicamentActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEDICAMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Récupérer les données du médicament depuis l'Intent
            String nomMedicament = data.getStringExtra("nom_medicament");
            int dureeMedicament = data.getIntExtra("duree_medicament", 0);
            boolean priseMatin = data.getBooleanExtra("prise_matin", false);
            boolean priseMidi = data.getBooleanExtra("prise_midi", false);
            boolean priseSoir = data.getBooleanExtra("prise_soir", false);

            // Créer un nouvel objet Medicament sans description et l'ajouter à la liste
            Medicament medicament = new Medicament(nomMedicament, dureeMedicament, priseMatin, priseMidi, priseSoir);
            medicamentList.add(medicament);

            // Message de confirmation
            Toast.makeText(this, "Médicament ajouté : " + nomMedicament, Toast.LENGTH_SHORT).show();

            // Mettre à jour le Spinner avec le nouveau médicament
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerMedicaments.getAdapter();
            adapter.add(nomMedicament);
            adapter.notifyDataSetChanged();
        }
    }

    // Affiche un DatePickerDialog pour sélectionner les dates de début et de fin
    private void showDatePickerDialog(String dateType) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (dateType.equals("start")) {
                        selectedStartDate.setText("Début : " + selectedDate);
                    } else if (dateType.equals("end")) {
                        selectedEndDate.setText("Fin : " + selectedDate);
                    }
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    // Sauvegarde les sélections actuelles (affiche un Toast en guise de confirmation)
    private void saveSelections() {
        String selectedMedicament = spinnerMedicaments.getSelectedItem().toString();
        String startDate = selectedStartDate.getText().toString().replace("Début : ", "").trim();
        String endDate = selectedEndDate.getText().toString().replace("Fin : ", "").trim();

        boolean isMatin = ((CheckBox) findViewById(R.id.checkbox_matin)).isChecked();
        boolean isMidi = ((CheckBox) findViewById(R.id.checkbox_midi)).isChecked();
        boolean isSoir = ((CheckBox) findViewById(R.id.checkbox_soir)).isChecked();

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner les dates de début et de fin", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.addPriseMedicament(selectedMedicament, startDate, endDate, isMatin, isMidi, isSoir);

        Toast.makeText(this, "Prises de médicament enregistrées avec succès", Toast.LENGTH_SHORT).show();
        finish();
    }
}












