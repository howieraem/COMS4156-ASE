package com.lgtm.easymoney.models;

import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="friendship")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Friendship implements Serializable {
    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    @Embeddable
    public static class Key implements Serializable {
        @Column(nullable = false)
        private Long ownerId;

        @Column(nullable = false)
        private Long otherId;
    }

    @EmbeddedId
    private Key key;

    @ManyToOne
    @MapsId("ownerId")
    private User owner;

    @ManyToOne
    @MapsId("otherId")
    private User other;

    @Column(nullable = false)
    private Boolean active = false;
}