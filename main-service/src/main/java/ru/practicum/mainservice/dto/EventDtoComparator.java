package ru.practicum.mainservice.dto;

import java.util.Comparator;

public class EventDtoComparator implements Comparator<EventShortDto> {
    private final String sortCriteria;

    public EventDtoComparator(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }

    public static EventDtoComparator of(String criteria) {
        return new EventDtoComparator(criteria);
    }

    @Override
    public int compare(EventShortDto o1, EventShortDto o2) {
        switch (sortCriteria) {
            case "EVENT_DATE":
                return o1.getEventDate().compareTo(o2.getEventDate());
            case "VIEWS":
                return Long.compare(o1.getViews(), o2.getViews());
            default:
                throw new IllegalStateException("Unknown sort criteria: " + sortCriteria);
        }
    }
}
