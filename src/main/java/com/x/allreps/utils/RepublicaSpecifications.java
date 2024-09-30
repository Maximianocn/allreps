package com.x.allreps.utils;

import com.x.allreps.model.Republica;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RepublicaSpecifications {

    public static Specification<Republica> hasCidade(String cidade) {
        return (root, query, builder) -> builder.equal(root.join("localizacao").get("cidade"), cidade);
    }

    public static Specification<Republica> hasValorMenorOuIgual(Double valorMaximo) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("valor"), valorMaximo);
    }

    public static Specification<Republica> hasGeneroPreferencial(String generoPreferencial) {
        return (root, query, builder) -> builder.equal(root.get("generoPreferencial"), generoPreferencial);
    }

    public static Specification<Republica> hasVagasDisponiveis(Integer vagasDisponiveis) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("vagasDisponiveis"), vagasDisponiveis);
    }

    public static Specification<Republica> hasComodidades(List<String> comodidades) {
        return (root, query, builder) -> {
            Expression<String> comodidadesExpression = root.join("comodidades");
            Predicate predicate = comodidadesExpression.in(comodidades);
            query.groupBy(root.get("id"));
            query.having(builder.equal(builder.count(root.get("id")), comodidades.size()));
            return predicate;
        };
    }
}

