package com.pos.monitoring.services.impl;

import com.pos.monitoring.dtos.pageable.TransactionCalculatePageableSearch;
import com.pos.monitoring.entities.TerminalModel;
import com.pos.monitoring.entities.TransactionCalculate;
import com.pos.monitoring.repositories.TransactionCalculateRepository;
import com.pos.monitoring.services.TransactionCalculateService;
import com.pos.monitoring.utils.DaoUtils;
import com.pos.monitoring.utils.TimeUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionCalculateServiceImpl implements TransactionCalculateService {

    private final TransactionCalculateRepository transactionCalculateRepository;

    @Override
    public Page<TransactionCalculate> getAll(TransactionCalculatePageableSearch search) {
        String today = TimeUtils.toYYYYmmDD(new Date());
        return transactionCalculateRepository.findAll((Specification<TransactionCalculate>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!ObjectUtils.isEmpty(search.getMfos())) {
                predicates.add(root.get("mfo").in(search.getMfos()));
            }

            if (!ObjectUtils.isEmpty(search.getCalculateType())) {
                predicates.add(cb.equal(root.get("calculateType"), search.getCalculateType()));
            }

            predicates.add(cb.equal(root.get("today"), today));

            predicates.add(cb.equal(root.get("deleted"), Boolean.FALSE));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, DaoUtils.toPaging(search));
    }
}
