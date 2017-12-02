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
 * Created by cyeniceri on 03/12/2017.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"principal", "shopListIngredients"})
@ToString(exclude = {"principal", "shopListIngredients"}, callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
@Table(name = "SHOP_LIST")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "objId")
public class ShopList extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "NAME")
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "shopList", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShopListIngredient> shopListIngredients = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "PRINCIPAL_OID", nullable = false)
    private Principal principal;
}
