package com.scttech.cs600.module7.repository.term;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scttech.cs600.module7.model.term.Term;

public interface TermRepository extends JpaRepository<Term, UUID> {
    Optional<Term> findByName(String name);
}
