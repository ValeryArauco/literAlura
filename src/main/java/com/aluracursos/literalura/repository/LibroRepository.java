package com.aluracursos.literalura.repository;

import com.aluracursos.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
//    Optional<Libro> findByTituloContainsIgnoreCase(String nombreLibro);
//
//    List<Libro> findTop5ByOrderByNumeroDeDescargasDesc();
//
    @Query("SELECT l FROM Libro l WHERE l.idioma LIKE %:idioma%")
    List<Libro> findByIdioma(String idioma);
}
