package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.DbConsts;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * schema for groups.
 */
@Entity
@Table(
        name = "grp", // "group" or "groups" will cause SQL syntax errors
        uniqueConstraints = @UniqueConstraint(
                name = DbConsts.GROUP_NAME_CONSTRAINT,
                columnNames = {"name"})
)
@Data
@NoArgsConstructor
public class Group implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column
  private String description;

  @ManyToMany(cascade = CascadeType.MERGE)
  @JoinTable(
          name = "grp_user",
          joinColumns = @JoinColumn(name = "gid"),
          inverseJoinColumns = @JoinColumn(name = "uid")
  )
  private Set<User> groupUsers;
}
