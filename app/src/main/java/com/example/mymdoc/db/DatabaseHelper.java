package com.example.mymdoc.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import com.example.mymdoc.models.Medicament;
import com.example.mymdoc.db.MedicamentDbSchema.MedicamentTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "medicaments.db";
    private static final int DATABASE_VERSION = 4; // Incrémentez cette version si nécessaire

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate() appelé - début de la création de la base de données");
        // Code pour créer les tables
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MedicamentTable.NAME + " (" +
                MedicamentTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MedicamentTable.Cols.NOM + " TEXT, " +
                MedicamentTable.Cols.DESCRIPTION + " TEXT, " +
                MedicamentTable.Cols.DUREE + " INTEGER, " +
                MedicamentTable.Cols.PRISE_MATIN + " INTEGER, " +
                MedicamentTable.Cols.PRISE_MIDI + " INTEGER, " +
                MedicamentTable.Cols.PRISE_SOIR + " INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS prises_medicaments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nom_medicament TEXT, " +
                "date_debut TEXT, " +
                "date_fin TEXT, " +
                "prise_matin INTEGER, " +
                "prise_midi INTEGER, " +
                "prise_soir INTEGER)");

        Log.d("DatabaseHelper", "Tables créées avec succès : medicaments et prises_medicaments");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPriseMedicament(String nomMedicament, String startDate, String endDate, boolean matin, boolean midi, boolean soir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nom_medicament", nomMedicament);
        values.put("date_debut", startDate);
        values.put("date_fin", endDate);
        values.put("prise_matin", matin ? 1 : 0);
        values.put("prise_midi", midi ? 1 : 0);
        values.put("prise_soir", soir ? 1 : 0);

        long result = db.insert("prises_medicaments", null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Erreur lors de l'insertion de la prise de médicament");
        } else {
            Log.d("DatabaseHelper", "Prise de médicament ajoutée avec succès");
        }

        db.close();
    }

    public List<String> generateDateRange(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            while (!calendar.getTime().after(end)) {
                dateList.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1); // Ajouter un jour
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateList;
    }

    public List<String> getPrisesParJourEtPeriode(int mois, int annee) {
        List<String> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Requête pour récupérer les prises du mois
        String query = "SELECT * FROM prises_medicaments WHERE " +
                "substr(date_debut, 4, 2) = ? AND substr(date_debut, 7, 4) = ? " +
                "ORDER BY date_debut";

        String moisFormat = String.format("%02d", mois);
        Cursor cursor = db.rawQuery(query, new String[]{moisFormat, String.valueOf(annee)});

        try {
            if (cursor.moveToFirst()) {
                Map<String, Map<String, Set<String>>> joursMap = new LinkedHashMap<>();

                do {
                    @SuppressLint("Range") String nomMedicament = cursor.getString(cursor.getColumnIndex("nom_medicament"));
                    @SuppressLint("Range") String dateDebut = cursor.getString(cursor.getColumnIndex("date_debut"));
                    @SuppressLint("Range") String dateFin = cursor.getString(cursor.getColumnIndex("date_fin"));
                    @SuppressLint("Range") boolean priseMatin = cursor.getInt(cursor.getColumnIndex("prise_matin")) == 1;
                    @SuppressLint("Range") boolean priseMidi = cursor.getInt(cursor.getColumnIndex("prise_midi")) == 1;
                    @SuppressLint("Range") boolean priseSoir = cursor.getInt(cursor.getColumnIndex("prise_soir")) == 1;

                    // Générer les dates entre dateDebut et dateFin
                    List<String> dateRange = generateDateRange(dateDebut, dateFin);

                    for (String date : dateRange) {
                        joursMap.putIfAbsent(date, new LinkedHashMap<>());
                        Map<String, Set<String>> periodesMap = joursMap.get(date);

                        // Ajouter les médicaments aux périodes concernées
                        if (priseMatin) {
                            periodesMap.putIfAbsent("Matin", new HashSet<>());
                            periodesMap.get("Matin").add(nomMedicament);
                        }
                        if (priseMidi) {
                            periodesMap.putIfAbsent("Midi", new HashSet<>());
                            periodesMap.get("Midi").add(nomMedicament);
                        }
                        if (priseSoir) {
                            periodesMap.putIfAbsent("Soir", new HashSet<>());
                            periodesMap.get("Soir").add(nomMedicament);
                        }
                    }
                } while (cursor.moveToNext());

                // Construire la liste finale pour affichage
                for (Map.Entry<String, Map<String, Set<String>>> jourEntry : joursMap.entrySet()) {
                    String date = jourEntry.getKey();
                    result.add("Jour : " + date);

                    Map<String, Set<String>> periodesMap = jourEntry.getValue();
                    for (Map.Entry<String, Set<String>> periodeEntry : periodesMap.entrySet()) {
                        String periode = periodeEntry.getKey();
                        String medicaments = String.join(", ", periodeEntry.getValue());
                        result.add(periode + " : " + medicaments);
                    }
                }
            }
        } finally {
            cursor.close();
            db.close();
        }

        return result;
    }

    // Méthode pour ajouter un médicament
    @SuppressLint("Range")
    public void addMedicament(Medicament medicament) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("DatabaseHelper", "Chemin de la base de données : " + db.getPath());

        ContentValues values = new ContentValues();
        values.put(MedicamentTable.Cols.NOM, medicament.getNom());
        values.put(MedicamentTable.Cols.DESCRIPTION, medicament.getDescription());
        values.put(MedicamentTable.Cols.DUREE, medicament.getDuree());
        values.put(MedicamentTable.Cols.PRISE_MATIN, medicament.isPriseMatin() ? 1 : 0);
        values.put(MedicamentTable.Cols.PRISE_MIDI, medicament.isPriseMidi() ? 1 : 0);
        values.put(MedicamentTable.Cols.PRISE_SOIR, medicament.isPriseSoir() ? 1 : 0);

        long result = db.insert(MedicamentTable.NAME, null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Erreur lors de l'insertion de " + medicament.getNom());
        } else {
            Log.d("DatabaseHelper", "Médicament ajouté : " + medicament.getNom());
        }

        Cursor cursor = db.query(MedicamentTable.NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d("DatabaseHelper", "Enregistrement : " + cursor.getString(cursor.getColumnIndex(MedicamentTable.Cols.NOM)));
            } while (cursor.moveToNext());
        } else {
            Log.d("DatabaseHelper", "Aucun enregistrement trouvé après insertion.");
        }
        cursor.close();
        db.close();
    }

    public List<String> getAllMedicamentNames() {
        List<String> medicamentNames = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                MedicamentTable.NAME,
                new String[]{MedicamentTable.Cols.NOM},
                null,
                null,
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(MedicamentTable.Cols.NOM));
                    medicamentNames.add(name);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();

        return medicamentNames;
    }

}









