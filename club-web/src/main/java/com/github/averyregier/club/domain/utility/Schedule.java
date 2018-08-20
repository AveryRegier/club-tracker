package com.github.averyregier.club.domain.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

public class Schedule<C extends HasTimezone, E extends HasId> {
    private static final Logger log = LoggerFactory.getLogger(Schedule.class);
    private C container;
    private List<Scheduled<C, E>> list;

    public Schedule(C container, List<Scheduled<C, E>> list) {
        this.container = container;
        this.list = list;
    }

    /**
     * This will destructively schedule toSchedule into a Schedule object.
     * The leftovers in toSchedule were not able to be scheduled on the given dates.
     */
    public static <C extends HasTimezone, I extends HasId> Schedule<C, I> generate(
            C container, Collection<LocalDate> dates, List<I> toSchedule) {
        List<Scheduled<C, I>> list = new ArrayList<>();
        for (LocalDate date : dates) {
            if(!toSchedule.isEmpty()) {
                I item = toSchedule.remove(0);
                list.add(new Scheduled<>(container, date, item));
            }
        }

        if (!toSchedule.isEmpty()) {
            log.warn("Schedule for " + container.getId() + " could not schedule the following: " +
                    toSchedule.stream().map(HasId::getId).collect(joining(", ")));
        }

        return new Schedule<>(container, unmodifiableList(list));
    }

    public List<Scheduled<C, E>> getList() {
        return list;
    }

    public Optional<E> getNextEvent() {
        LocalDate now = getToday();
        return list.stream()
                .filter(s->!s.getDate().isBefore(now))
                .map(Scheduled::getEvent)
                .findFirst();
    }

    public Stream<E> getPreviousEvents() {
        LocalDate now = getToday();
        return list.stream()
                .filter(s->s.getDate().isBefore(now))
                .map(Scheduled::getEvent);
    }

    public Optional<E> getEventAt(LocalDate date) {
        return list.stream()
                .filter(s->s.getDate().equals(date))
                .map(Scheduled::getEvent)
                .findFirst();
    }

    public LocalDate getToday() {
        return LocalDate.now(container.getTimeZone());
    }

    public Stream<E> getAvailableEvents() {
         return Stream.concat(getNextEvent().map(Stream::of).orElse(Stream.empty()), getPreviousEvents());
    }

    public C getContainer() {
        return container;
    }
}
