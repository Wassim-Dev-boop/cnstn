package com.alnaifer.nehb.repository;

import com.alnaifer.nehb.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité StudentProfile
 */
@Repository
public interface StudentRepository extends JpaRepository<StudentProfile, String> {

    @Query("SELECT s FROM StudentProfile s JOIN s.user u WHERE u.id = :userId")
    Optional<StudentProfile> findByUserId(@Param("userId") String userId);

    @Query("SELECT s FROM StudentProfile s JOIN s.user u WHERE u.email = :email")
    Optional<StudentProfile> findByUserEmail(@Param("email") String email);

    @Query("SELECT s FROM StudentProfile s WHERE s.isActive = true ORDER BY s.user.lastName, s.user.firstName")
    List<StudentProfile> findAllActive();

    @Query("SELECT s FROM StudentProfile s JOIN s.halaqas h WHERE h.id = :halaqaId AND s.isActive = true")
    List<StudentProfile> findByHalaqaId(@Param("halaqaId") String halaqaId);

    @Query("SELECT s FROM StudentProfile s JOIN s.classes c WHERE c.id = :classId AND s.isActive = true")
    List<StudentProfile> findByClassId(@Param("classId") String classId);

    @Query("SELECT s FROM StudentProfile s JOIN s.parentProfile p WHERE p.id = :parentId AND s.isActive = true")
    List<StudentProfile> findByParentId(@Param("parentId") String parentId);

    @Query("SELECT COUNT(s) FROM StudentProfile s WHERE s.isActive = true")
    long countActiveStudents();

    @Query("SELECT s FROM StudentProfile s WHERE s.memorizationLevel = :level AND s.isActive = true")
    List<StudentProfile> findByMemorizationLevel(@Param("level") String level);
}
