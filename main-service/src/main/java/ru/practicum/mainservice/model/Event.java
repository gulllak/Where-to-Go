package ru.practicum.mainservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.dto.Location;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "annotation")
    private String annotation;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_id")
    private Category category;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private EventState state = EventState.PENDING;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "latitude")),
            @AttributeOverride(name = "lon", column = @Column(name = "longitude"))
    })
    private Location location;

    @Builder.Default
    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "initiator_id")
    private User initiator;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "confirmed_participants")
    private int confirmedParticipants;

    @Transient
    public boolean isPublished() {
        return state.equals(EventState.PUBLISHED);
    }

    @Transient
    public boolean isCanceled() {
        return state.equals(EventState.CANCELED);
    }

    @Transient
    public boolean isPending() {
        return state.equals(EventState.PENDING);
    }

    @Transient
    public boolean isAvailable() {
        return (participantLimit - confirmedParticipants) > 0;
    }
}
