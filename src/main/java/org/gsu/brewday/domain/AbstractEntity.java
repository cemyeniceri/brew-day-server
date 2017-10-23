package org.gsu.brewday.domain;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Created by cyeniceri on 17/01/17.
 */

@Data
@MappedSuperclass
@DynamicUpdate
@DynamicInsert
@SelectBeforeUpdate
abstract class AbstractEntity implements Serializable {

    @Id
    @GenericGenerator(name = "db-uuid", strategy = "org.gsu.brewday.config.UuidIdentifierGenerator")
    @GeneratedValue(generator = "db-uuid")
    @Column(name = "OBJID", nullable = false, updatable = false)
    private String objId;
}