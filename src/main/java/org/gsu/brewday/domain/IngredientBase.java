package org.gsu.brewday.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@Data
@MappedSuperclass
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
public class IngredientBase extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "NAME")
    private String name;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "AMOUNT")
    private String amount;
}
