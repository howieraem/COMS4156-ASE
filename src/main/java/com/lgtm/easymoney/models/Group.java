package com.lgtm.easymoney.models;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="grp") // "group" or "groups" will cause SQL syntax errors
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

    @ManyToMany
    @JoinTable(
        name = "grp_user",
        joinColumns = @JoinColumn(name = "gid"),
        inverseJoinColumns = @JoinColumn(name = "uid")
    )
    Set<User> groupUsers;
}
