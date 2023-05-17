package slcd.boost.boost.Users;

import jakarta.persistence.criteria.*;
import lombok.Getter;
import org.apache.catalina.User;
import org.springframework.data.jpa.domain.Specification;
import slcd.boost.boost.General.DTOs.SearchCriteria;
import slcd.boost.boost.General.Entities.SubdivisionEntity;
import slcd.boost.boost.Users.Entities.TeamLeadersEntity;
import slcd.boost.boost.Users.Entities.UserEntity;

public class UserSpecification implements Specification<UserEntity> {
    private final SearchCriteria criteria;

    public UserSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Join<UserEntity, TeamLeadersEntity> teamLeaderJoin =
                root.join("teamLeaders", JoinType.LEFT)
                        .join("id").join("user");

        Join<UserEntity, SubdivisionEntity> subdivisionJoin =
                root.join("subdivision", JoinType.LEFT);

        if(criteria.getKey().equalsIgnoreCase("teamLeader")){
            return builder.equal(teamLeaderJoin.get("id"), Long.valueOf(criteria.getValue()));
        }
        else if(criteria.getKey().equals("subdivision")){
            return builder.equal(subdivisionJoin.get("id"), Long.valueOf(criteria.getValue()));
        }
        else if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
