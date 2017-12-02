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
 * Created by cyeniceri on 04/02/2017.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"ingredients", "recipes", "shopLists"})
@ToString(exclude = {"ingredients", "recipes", "shopLists"}, callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "PRINCIPALS")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class Principal extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SURNAME")
    private String surname;

    @Column(name = "GSM")
    private String gsm;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Recipe> recipes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "principal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShopList> shopLists = new HashSet<>();
}
