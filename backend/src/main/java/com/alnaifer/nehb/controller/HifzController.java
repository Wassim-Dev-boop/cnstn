package com.alnaifer.nehb.controller;

import com.alnaifer.nehb.model.*;
import com.alnaifer.nehb.service.HifzProgressService;
import com.alnaifer.nehb.service.HifzEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion du Hifz (mémorisation du Coran)
 * Implémente le cycle Sabaq / Sabqi / Manzil
 */
@RestController
@RequestMapping("/api/v1/hifz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HifzController {

    private final HifzProgressService hifzProgressService;
    private final HifzEvaluationService hifzEvaluationService;

    /**
     * Assigner un nouveau Sabaq à un élève
     * POST /api/v1/hifz/assign-sabaq
     */
    @PostMapping("/assign-sabaq")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<HifzProgress> assignSabaq(
            @RequestParam String studentId,
            @RequestParam Integer juzNumber,
            @RequestParam String suraName,
            @RequestParam Integer ayatStart,
            @RequestParam Integer ayatEnd
    ) {
        HifzProgress progress = hifzProgressService.assignNewSabaq(
                studentId, juzNumber, suraName, ayatStart, ayatEnd
        );
        return ResponseEntity.ok(progress);
    }

    /**
     * Évaluer un Sabaq - Note la récitation de l'élève
     * POST /api/v1/hifz/evaluate
     */
    @PostMapping("/evaluate")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<HifzEvaluation> evaluateSabaq(
            @RequestParam String progressId,
            @RequestParam String grade, // A, B, C, D
            @RequestBody Map<String, Object> tajwidErrors
    ) {
        HifzEvaluation evaluation = hifzEvaluationService.evaluateSabaq(
                progressId, grade, tajwidErrors
        );
        return ResponseEntity.ok(evaluation);
    }

    /**
     * Obtenir la progression Hifz d'un élève
     * GET /api/v1/hifz/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE') or hasRole('PARENT') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<HifzProgress>> getStudentProgress(
            @PathVariable String studentId
    ) {
        List<HifzProgress> progresses = hifzProgressService.getStudentProgress(studentId);
        return ResponseEntity.ok(progresses);
    }

    /**
     * Obtenir les Sabaqs en attente d'évaluation pour un enseignant
     * GET /api/v1/hifz/pending-evaluations
     */
    @GetMapping("/pending-evaluations")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<HifzProgress>> getPendingEvaluations(
            @RequestParam(required = false) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<HifzProgress> pending = hifzProgressService.getPendingEvaluations(targetDate);
        return ResponseEntity.ok(pending);
    }

    /**
     * Obtenir les Sabqis à réviser (fenêtre glissante 15 jours)
     * GET /api/v1/hifz/sabqi-revision
     */
    @GetMapping("/sabqi-revision")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE')")
    public ResponseEntity<List<HifzProgress>> getSabqiRevision(
            @RequestParam String studentId
    ) {
        List<HifzProgress> sabqiList = hifzProgressService.getSabqiRevision(studentId);
        return ResponseEntity.ok(sabqiList);
    }

    /**
     * Obtenir les Manzils planifiés pour révision (SRS - Répétition Espacée)
     * GET /api/v1/hifz/manzil-revision
     */
    @GetMapping("/manzil-revision")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE')")
    public ResponseEntity<List<HifzProgress>> getManzilRevision(
            @RequestParam String studentId,
            @RequestParam(required = false) LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        List<HifzProgress> manzilList = hifzProgressService.getManzilRevision(studentId, targetDate);
        return ResponseEntity.ok(manzilList);
    }

    /**
     * Obtenir les statistiques de progression par Juz
     * GET /api/v1/hifz/stats/by-juz
     */
    @GetMapping("/stats/by-juz")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE') or hasRole('PARENT')")
    public ResponseEntity<Map<Integer, Double>> getProgressByJuz(
            @RequestParam String studentId
    ) {
        Map<Integer, Double> stats = hifzProgressService.getProgressByJuz(studentId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Vérifier si un élève peut recevoir un nouveau Sabaq
     * (basé sur le seuil critique du Sabqi)
     * GET /api/v1/hifz/can-assign-new-sabaq
     */
    @GetMapping("/can-assign-new-sabaq")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Boolean> canAssignNewSabaq(
            @RequestParam String studentId
    ) {
        boolean canAssign = hifzProgressService.canAssignNewSabaq(studentId);
        return ResponseEntity.ok(canAssign);
    }

    /**
     * Obtenir l'historique des évaluations d'une portion
     * GET /api/v1/hifz/{progressId}/evaluations
     */
    @GetMapping("/{progressId}/evaluations")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE') or hasRole('PARENT')")
    public ResponseEntity<List<HifzEvaluation>> getEvaluationHistory(
            @PathVariable String progressId
    ) {
        List<HifzEvaluation> evaluations = hifzEvaluationService.getEvaluationHistory(progressId);
        return ResponseEntity.ok(evaluations);
    }

    /**
     * Obtenir le radar chart des erreurs Tajwid pour un élève
     * GET /api/v1/hifz/tajwid-radar/{studentId}
     */
    @GetMapping("/tajwid-radar/{studentId}")
    @PreAuthorize("hasRole('ENSEIGNANT') or hasRole('ELEVE') or hasRole('PARENT')")
    public ResponseEntity<Map<String, Double>> getTajwidRadarChart(
            @PathVariable String studentId
    ) {
        Map<String, Double> radarData = hifzEvaluationService.getTajwidErrorAnalysis(studentId);
        return ResponseEntity.ok(radarData);
    }
}
