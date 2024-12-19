package com.example.mymdoc.models;

public class PriseMedicament {
    private String nomMedicament;
    private String dateDebut;
    private String dateFin;
    private boolean priseMatin;
    private boolean priseMidi;
    private boolean priseSoir;

    public PriseMedicament(String nomMedicament, String dateDebut, String dateFin, boolean priseMatin, boolean priseMidi, boolean priseSoir) {
        this.nomMedicament = nomMedicament;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.priseMatin = priseMatin;
        this.priseMidi = priseMidi;
        this.priseSoir = priseSoir;
    }

    // Getters
    public String getNomMedicament() {
        return nomMedicament;
    }

    public String getDateDebut() {
        return dateDebut;
    }

    public String getDateFin() {
        return dateFin;
    }

    public boolean isPriseMatin() {
        return priseMatin;
    }

    public boolean isPriseMidi() {
        return priseMidi;
    }

    public boolean isPriseSoir() {
        return priseSoir;
    }
}

