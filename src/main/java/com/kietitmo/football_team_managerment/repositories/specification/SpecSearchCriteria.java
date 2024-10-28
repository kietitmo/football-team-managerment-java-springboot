package com.kietitmo.football_team_managerment.repositories.specification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;

    public SpecSearchCriteria(final String key, final SearchOperation operation, final Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String key, String operation, String value) {
        SearchOperation searchOperation = SearchOperation.getSimpleOperation(operation.charAt(0));
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
    }

}
