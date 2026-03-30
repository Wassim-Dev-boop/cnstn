package com.alnaifer.nehb.model;

/**
 * Rôles utilisateurs selon la matrice RBAC du cahier des charges
 */
public enum UserRole {
    ADMINISTRATEUR,  // CRUD complet sur tous les modules
    ENSEIGNANT,      // Ustad - Gestion pédagogique
    ELEVE,           // Talib - Accès à sa progression
    PARENT           // Tuteur - Suivi des enfants
}
