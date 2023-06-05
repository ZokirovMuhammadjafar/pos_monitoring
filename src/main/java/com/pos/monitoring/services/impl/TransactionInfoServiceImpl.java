package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.TransactionInfoPageableSearch;
import com.pos.monitoring.entities.TransactionInfo;
import com.pos.monitoring.exceptions.ErrorCode;
import com.pos.monitoring.exceptions.LocalizedApplicationException;
import com.pos.monitoring.repositories.TransactionInfoRepository;
import com.pos.monitoring.services.TransactionInfoService;
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
public class TransactionInfoServiceImpl implements TransactionInfoService {

    private final TransactionInfoRepository transactionInfoRepository;

    @Override
    public Page<TransactionInfo> getAll(TransactionInfoPageableSearch pageableSearch) {
        return transactionInfoRepository.findAll((Specification<TransactionInfo>) (root, query, cb) -> {
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
    public TransactionInfo get(Long id) {
        return transactionInfoRepository.findById(id).orElseThrow(() -> new LocalizedApplicationException(ErrorCode.ENTITY_NOT_FOUND));
    }
}
