package com.scttech.cs600.module7.repository.term;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.scttech.cs600.module7.model.term.Term;

import jakarta.persistence.EntityManager;

/**
 * Runs the {@link TermRepository} against a real, throwaway Postgres container (via
 * Testcontainers) so the {@code terms_end_after_start} CHECK constraint from
 * docs/module4/README.md is exercised by the actual database, not just application code.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TermRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private EntityManager entityManager;

    private Term newFall2026() {
        return new Term("Fall 2026", LocalDate.of(2026, 8, 24), LocalDate.of(2026, 12, 18));
    }

    @Test
    void createsAndReadsATerm() {
        Term saved = termRepository.saveAndFlush(newFall2026());

        assertThat(saved.getId()).isNotNull();

        Optional<Term> found = termRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Fall 2026");
        assertThat(found.get().getStartDate()).isEqualTo(LocalDate.of(2026, 8, 24));
        assertThat(found.get().getEndDate()).isEqualTo(LocalDate.of(2026, 12, 18));
    }

    @Test
    void findsATermByName() {
        termRepository.saveAndFlush(newFall2026());

        Optional<Term> found = termRepository.findByName("Fall 2026");

        assertThat(found).isPresent();
        assertThat(found.get().getStartDate()).isEqualTo(LocalDate.of(2026, 8, 24));
    }

    @Test
    void rejectsATermWhoseEndDateIsNotAfterItsStartDate() {
        Term backwards = new Term("Broken Term", LocalDate.of(2026, 12, 18), LocalDate.of(2026, 8, 24));

        assertThatThrownBy(() -> termRepository.saveAndFlush(backwards))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("terms_end_after_start");
    }

    @Test
    void listsAllTerms() {
        termRepository.saveAndFlush(newFall2026());
        termRepository.saveAndFlush(new Term("Spring 2027", LocalDate.of(2027, 1, 12), LocalDate.of(2027, 5, 7)));

        List<Term> all = termRepository.findAll();

        assertThat(all).hasSize(2);
    }

    @Test
    void termsAreEqualById() {
        Term saved = termRepository.saveAndFlush(newFall2026());
        Term other = termRepository
                .saveAndFlush(new Term("Spring 2027", LocalDate.of(2027, 1, 12), LocalDate.of(2027, 5, 7)));

        entityManager.clear();
        Term reloaded = termRepository.findById(saved.getId()).orElseThrow();

        assertThat(reloaded)
                .isNotSameAs(saved)
                .isEqualTo(saved)
                .hasSameHashCodeAs(saved)
                .isNotEqualTo(other);
    }

    @Test
    void unsavedTermsAreOnlyEqualToThemselves() {
        Term first = newFall2026();
        Term second = newFall2026();

        assertThat(first).isEqualTo(first).isNotEqualTo(second);
    }
}
