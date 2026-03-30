package com.alnaifer.nehb.model;

/**
 * États du cycle Hifz selon le cahier des charges
 * Machine à états finis pour le suivi de mémorisation
 */
public enum HifzStatus {
    /**
     * SABAQ - Nouvelle leçon en cours d'apprentissage
     * L'élève apprend cette portion pour la première fois
     */
    SABAQ,

    /**
     * SABQI - Révision récente
     * Portion validée mais dans les 15 derniers jours
     * Nécessite une consolidation continue
     */
    SABQI,

    /**
     * MANZIL - Révision ancienne
     * Portion mémorisée depuis plus de 15 jours
     * Système de répétition espacée (SRS) activé
     */
    MANZIL,

    /**
     * VALIDATED - Complètement mémorisé
     * Portion considérée comme acquise définitivement
     */
    VALIDATED,

    /**
     * NEEDS_REVIEW - Nécessite révision
     * L'élève a échoué aux évaluations, retour en SABAQ
     */
    NEEDS_REVIEW
}
