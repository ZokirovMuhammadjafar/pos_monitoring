package com.pos.monitoring.repositories.system.specifications;

import com.pos.monitoring.entities.Machine;
import com.pos.monitoring.entities.enums.MachineState;
import com.pos.monitoring.entities.enums.SynchronizeType;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class MachineSpecification {
    public static Specification<Machine> machineStatusIn(List<MachineState> machineState) {
        return (root, query, criteriaBuilder) -> {
            Predicate state = criteriaBuilder.and(root.get("state").in(machineState));
            return state;
        };
    }

    public static Specification<Machine> getBySingleMfo(String mfo) {
        return (root, query, criteriaBuilder) -> {
            Predicate mfoPredicate = criteriaBuilder.equal(root.get("branchMfo"),mfo);
            return mfoPredicate;
        };
    }
    public static Specification<Machine>machinaSyncType(SynchronizeType synchronizeType){
        return (root, query, criteriaBuilder) -> {
            Predicate mfoPredicate = criteriaBuilder.equal(root.get("synchronizationType"),synchronizeType);
            return mfoPredicate;
        };
    }

    public static Specification<Machine>machineOrderBy(String name){
        return (root, query, criteriaBuilder) -> {
            CriteriaQuery<?> criteriaQuery = query.orderBy(criteriaBuilder.desc(root.get(name)));
            Predicate restriction = criteriaQuery.getRestriction();
            return restriction;
        };
    }

    public static Specification<Machine> isTransaction(boolean b) {
        return (root, query, criteriaBuilder) -> {
            Predicate mfoPredicate = criteriaBuilder.equal(root.get("syncedTransaction"),b);
            return mfoPredicate;
        };
    }
}
