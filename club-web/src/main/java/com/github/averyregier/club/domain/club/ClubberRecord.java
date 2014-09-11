package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionHolder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rx39789 on 9/6/2014.
 */
public abstract class ClubberRecord {
    private RecordSigning signing = null;

    public abstract Section getSection();
    public abstract Clubber getClubber();
    public Signing sign(Listener byListener, String note) {
        signing = new RecordSigning(byListener, note);
        signing.calculateRewards();
        return signing;
    }

    private boolean isCompleted(SectionHolder g) {
        return allCompleted(g.getSections().stream());
    }

    private boolean allCompleted(Stream<Section> stream) {
        return stream.allMatch(s -> {
            Optional<ClubberRecord> record = getClubber().getRecord(Optional.of(s));
            return record.isPresent() && record.get().getSigning().isPresent();
        });
    }

    public Optional<Signing> getSigning() {
        return Optional.ofNullable(signing);
    }

    @Override
    public String toString() {
        return getClubber().getId()+" "+getSection().getId();
    }

    private class RecordSigning implements Signing {
        private final Listener byListener;
        private final String note;
        private Set<Reward> rewards;

        public RecordSigning(Listener byListener, String note) {
            this.byListener = byListener;
            this.note = note;
        }

        @Override
        public LocalDate getDate() {
            return LocalDate.now();
        }

        @Override
        public Listener by() {
            return byListener;
        }

        @Override
        public String getNote() {
            return note;
        }

        @Override
        public Set<Reward> getCompletionRewards() {
            return rewards;
        }

        void calculateRewards() {
            rewards = getSection().getRewards().stream()
                    .filter(r->isCompleted(r))
                    .collect(Collectors.toSet());
        }
    }
}
