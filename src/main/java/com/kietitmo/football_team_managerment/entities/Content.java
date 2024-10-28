package com.kietitmo.football_team_managerment.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "contents")
public class Content extends AbstractEntity {
    String title;
    String description;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    private Set<Vote> votes;

    public int getTotalVotes() {
        if (votes == null || votes.isEmpty()) {
            return 0;
        }
        // Tính tổng vote nếu có
        return votes.stream().mapToInt(Vote::getVoteType).sum();
    }
}

