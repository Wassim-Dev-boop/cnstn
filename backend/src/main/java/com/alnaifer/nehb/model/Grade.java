package com.alnaifer.nehb.model;

/**
 * Système de notation selon le cahier des charges
 * Échelle paramétrable pour l'évaluation du Sabaq
 */
public enum Grade {
    A(4.0, "Excellent"),      // Note >= 90%
    B(3.0, "Très Bien"),      // Note >= 75% - Déclencheur pour passage à Sabqi
    C(2.0, "Bien"),           // Note >= 60%
    D(1.0, "À améliorer"),    // Note < 60% - Maintien du Sabaq
    F(0.0, "Échec");          // Note < 40% - Révision nécessaire

    private final double gpa;
    private final String description;

    Grade(double gpa, String description) {
        this.gpa = gpa;
        this.description = description;
    }

    public double getGpa() {
        return gpa;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Vérifie si la note permet le passage à Sabqi selon le cahier des charges
     * Règle: Si Note >= B, le statut passe à "Sabqi"
     */
    public boolean isPassingForSabqi() {
        return this == A || this == B;
    }

    /**
     * Vérifie si la note nécessite une consolidation
     */
    public boolean needsConsolidation() {
        return this == D || this == F;
    }
}
