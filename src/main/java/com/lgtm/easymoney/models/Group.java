package com.lgtm.easymoney.models;

import com.lgtm.easymoney.configs.Consts;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name="grp", // "group" or "groups" will cause SQL syntax errors
        uniqueConstraints=@UniqueConstraint(name=Consts.GROUP_NAME_CONSTRAINT, columnNames={"name"})
)
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Group implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
        name = "grp_user",
        joinColumns = @JoinColumn(name = "gid"),
        inverseJoinColumns = @JoinColumn(name = "uid")
    )
    Set<User> groupUsers;
}
