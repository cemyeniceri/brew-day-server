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
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cyeniceri on 02/12/2017.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"principal", "recipePosts"})
@ToString(exclude = {"principal", "recipePosts"}, callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "RECIPES")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class Recipe extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DETAIL")
    private String detail;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PRINCIPAL_OID", nullable = false)
    private Principal principal;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipePost> recipePosts = new HashSet<>();
}
