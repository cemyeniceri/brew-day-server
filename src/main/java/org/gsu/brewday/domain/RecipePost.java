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

import javax.persistence.*;
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
@Table(name = "RECIPES_POSTS")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class RecipePost extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "POST")
    private String post;

    @ManyToOne(optional = false)
    @JoinColumn(name = "RECIPE_OID", nullable = false)
    private Recipe recipe;
}
