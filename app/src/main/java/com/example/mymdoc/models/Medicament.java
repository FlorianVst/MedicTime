package com.example.mymdoc.models;

public class Medicament {

    private int id;
    private String nom;
    private String description;
    private int duree;
    private boolean priseMatin;
    private boolean priseMidi;
    private boolean priseSoir;

    // Constructeur sans description
    public Medicament(String nom, int duree, boolean priseMatin, boolean priseMidi, boolean priseSoir) {
        this.nom = nom;
        this.description = ""; // Valeur par défaut pour la description
        this.duree = duree;
        this.priseMatin = priseMatin;
        this.priseMidi = priseMidi;
        this.priseSoir = priseSoir;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }

    public String getDescription() { return description; }

    public int getDuree() { return duree; }

    public boolean isPriseMatin() { return priseMatin; }

    public boolean isPriseMidi() { return priseMidi; }

    public boolean isPriseSoir() { return priseSoir; }

}







