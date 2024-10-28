package com.kietitmo.football_team_managerment.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kietitmo.football_team_managerment.enums.RoleInEvent;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "event_participants")
public class EventParticipant extends AbstractEntity{
    @ManyToOne
    @JoinColumn(name = "event.id")
    @JsonBackReference
    Event event;

    @ManyToOne
    User user;

    @Column(nullable = false)
    RoleInEvent roleInEvent;
}
