package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Reward;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionGroup;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
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

    private boolean isCompleted(SectionGroup g) {
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
        Set<Reward> rewards;

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
            rewards = new HashSet<>();
            Optional<SectionGroup> g = getSection().getRewardGroup();
            if (g.isPresent()) {
                SectionGroup rg = g.get();
                if (rg.getCompletionReward().isPresent() && isCompleted(rg)) {
                    rewards.add(rg.getCompletionReward().get());
                }
            }
            Book book = getSection().getGroup().getBook();
            if(book.getCompletionReward().isPresent()) {
                boolean bookAward = allCompleted(book.getSections().stream()
                        .filter(s -> {
                                    return s.getSectionType().requiredForBookReward();
                                }));

                if (bookAward) {
                    rewards.add(book.getCompletionReward().get());
                }
            }
        }
    }
}
