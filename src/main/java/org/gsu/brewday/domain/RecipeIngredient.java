package org.gsu.brewday.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "recipe")
@ToString(exclude = "recipe", callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "RECIPES_INGREDIENTS")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class RecipeIngredient extends IngredientBase implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RECIPE_OID", nullable = false)
    private Recipe recipe;
}
