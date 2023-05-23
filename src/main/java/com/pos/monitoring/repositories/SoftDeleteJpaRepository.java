package com.pos.monitoring.repositories;

import com.pos.monitoring.entities.AbstractEntity;
import com.pos.monitoring.exceptions.ErrorCode;
import com.pos.monitoring.exceptions.LocalizedApplicationException;
import com.pos.monitoring.exceptions.ValidatorException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@NoRepositoryBean
public interface SoftDeleteJpaRepository<T extends AbstractEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    @Override
    @Transactional
    @Modifying
    default void deleteById(Long id) {
        findById(id).orElseThrow(() -> new ValidatorException("ENTITY_NOT_FOUND", List.of(id))).setDeleted(true);
    }

    @Override
    @Transactional
    default void delete(T entity) {
        entity.setDeleted(true);
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Transactional
    @Modifying
    default void softDeleteAll() {
        this.deleteAll(findAll());
    }

}
