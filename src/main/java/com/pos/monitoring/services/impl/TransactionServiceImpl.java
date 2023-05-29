package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.TransactionPageableSearch;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.entities.Transaction;
import com.pos.monitoring.exceptions.ErrorCode;
import com.pos.monitoring.exceptions.LocalizedApplicationException;
import com.pos.monitoring.repositories.TransactionRepository;
import com.pos.monitoring.services.TransactionService;
import com.pos.monitoring.utils.DaoUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Page<Transaction> getAll(TransactionPageableSearch pageableSearch) {
        return transactionRepository.findAll((Specification<Transaction>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(pageableSearch.getMfo())) {
                predicates.add(cb.equal(root.get("mfo"), pageableSearch.getMfo()));
            }

            if (!ObjectUtils.isEmpty(pageableSearch.getParentMfo())) {
                predicates.add(cb.equal(root.get("parentMfo"), pageableSearch.getParentMfo()));
            }

            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, DaoUtils.toPaging(pageableSearch));
    }

    @Override
    public Transaction get(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new LocalizedApplicationException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
