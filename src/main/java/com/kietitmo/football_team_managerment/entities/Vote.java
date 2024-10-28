package com.kietitmo.football_team_managerment.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"content_id", "user_id"})
})
@Entity
public class Vote extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "content_id", nullable = false)
    Content content;

    int voteType; // 1 for upvote, -1 for downvote

    @Column(nullable = false)
    boolean valid;
}
