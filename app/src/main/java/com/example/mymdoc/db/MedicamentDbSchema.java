package com.example.mymdoc.db;

public class MedicamentDbSchema {
    public static final class MedicamentTable {
        public static final String NAME = "medicaments";

        public static final class Cols {
            public static final String ID = "id";
            public static final String NOM = "nom";
            public static final String DESCRIPTION = "description";
            public static final String DUREE = "duree";
            public static final String PRISE_MATIN = "prise_matin";
            public static final String PRISE_MIDI = "prise_midi";
            public static final String PRISE_SOIR = "prise_soir";
        }
    }
}

