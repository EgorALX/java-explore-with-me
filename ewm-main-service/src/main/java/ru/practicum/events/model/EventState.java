package ru.practicum.events.model;

import java.util.Optional;

public enum EventState {

    PENDING,

    PUBLISHED,

    CANCELED;

    public static Optional<EventState> of(String stringState) {
        for (EventState state : EventState.values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
