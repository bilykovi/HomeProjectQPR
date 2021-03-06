package pl.luxoft.qpr.bilykov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * The entity class for the "income_categories" database table.
 *
 * @author Stefansky Alex
 * @author Bondar Dmytro
 */
@Data
@Entity
@Table(name = "income_categories")
@EqualsAndHashCode(exclude = {"incomes", "children"})
public class IncomeCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "CATEGORY_ID_GENERATOR", sequenceName = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CATEGORY_ID_GENERATOR")
    @Column(name = "category_id")
    private Integer categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private IncomeCategory parentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotEmpty
    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY)
    private Set<IncomeCategory> children;

    @JsonIgnore
    @OneToMany(mappedBy = "incomeCategory", fetch = FetchType.LAZY)
    private Set<Income> incomes;

}