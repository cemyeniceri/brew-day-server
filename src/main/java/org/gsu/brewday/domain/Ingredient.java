package org.gsu.brewday.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by cyeniceri on 01/12/2017.
 */
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "INGREDIENTS")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class Ingredient extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "AMOUNT")
    private String amount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PRINCIPAL_OID", nullable = false)
    private Principal principal;
}
