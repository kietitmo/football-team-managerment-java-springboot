package com.kietitmo.football_team_managerment.repositories.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class GenericSpecificationsBuilder<T> {

    private final List<SpecSearchCriteria> params;

    public GenericSpecificationsBuilder() {
        params = new ArrayList<>();
    }

    public GenericSpecificationsBuilder<T> with(final String key, final String operation, final Object value) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        params.add(new SpecSearchCriteria(key, searchOperation, value));

        return this;
    }

    public Specification<T> build() {
        if (params.isEmpty())
            return null;

        Specification<T> result = new GenericSpecification<>(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new GenericSpecification<>(params.get(i)));
        }

        return result;
    }
}