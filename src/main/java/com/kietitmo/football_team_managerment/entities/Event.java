package com.kietitmo.football_team_managerment.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public abstract class Event extends AbstractEntity{
    @OneToMany(mappedBy = "event")
    @JsonManagedReference
    Set<EventParticipant> participants;

    @OneToMany(mappedBy = "event")
    @JsonManagedReference
    Set<Content> contents;

}
