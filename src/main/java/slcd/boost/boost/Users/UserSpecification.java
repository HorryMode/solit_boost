package slcd.boost.boost.Users;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import slcd.boost.boost.General.DTOs.SearchCriteria;
import slcd.boost.boost.Users.Entities.TeamLeadersEntity;
import slcd.boost.boost.Users.Entities.UserEntity;


public class UserSpecification implements Specification<UserEntity> {
    private final SearchCriteria criteria;

    public UserSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<UserEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Join<TeamLeadersEntity, UserEntity> teamLeaderJoin =
                root.join("teamLeaders", JoinType.LEFT)
                .join("id")
                .join("teamLeader");

        if(criteria.getKey().equalsIgnoreCase("teamLeader")){
            return builder.equal(teamLeaderJoin.get("id"), criteria.getValue());
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
