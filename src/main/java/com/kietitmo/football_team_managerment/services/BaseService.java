package com.kietitmo.football_team_managerment.services;

import com.kietitmo.football_team_managerment.dto.response.PageResponse;
import com.kietitmo.football_team_managerment.repositories.specification.GenericSpecificationsBuilder;
import com.kietitmo.football_team_managerment.utils.AppConst;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class BaseService<T, ID, RESPONSE, REPOSITORY extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>> {
    private static final String SEARCH_SPEC_OPERATOR = AppConst.SEARCH_SPEC_OPERATOR;

    protected abstract REPOSITORY getRepository();

    public PageResponse<List<RESPONSE>> advanceSearchWithSpecifications(Pageable pageable, String[] searchTerms) {
        if (searchTerms != null) {
            var builder = new GenericSpecificationsBuilder<T>();

            Pattern pattern = Pattern.compile(SEARCH_SPEC_OPERATOR);
            for (String s : searchTerms) {
                Matcher matcher = pattern.matcher(s);
                if (matcher.find()) {
                    builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
                }
            }

            Page<T> results = getRepository().findAll(Objects.requireNonNull(builder.build()), pageable);
            return convertToPageResponse(results, pageable);
        }

        Page<T> results = getRepository().findAll(pageable);
        return convertToPageResponse(results, pageable);
    }

    protected abstract PageResponse<List<RESPONSE>> convertToPageResponse(Page<T> items, Pageable pageable);
}
