package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionHolder;
import com.github.averyregier.club.domain.utility.Named;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by avery on 9/6/2014.
 */
public abstract class ClubberRecord {
    private RecordSigning signing = null;

    public abstract Section getSection();
    public abstract Clubber getClubber();
    public Signing sign(Listener byListener, String note) {
        signing = new RecordSigning(byListener, note);
        signing.calculateAwards();
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
        private Set<AwardPresentation> awards;

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
        public Set<AwardPresentation> getCompletionAwards() {
            return awards;
        }

        void calculateAwards() {
            awards = getSection().getAwards().stream()
                    .filter(r->isCompleted(r))
                    .map(a->new AwardPresentationAdapter(a))
                    .collect(Collectors.toSet());
        }

        private class AwardPresentationAdapter implements AwardPresentation {

            private final Award award;
            private final Optional<Catalogued> token;

            public AwardPresentationAdapter(Award award) {
                this.award = award;
                this.token = select();
            }

            @Override
            public Person to() {
                return null;
            }

            @Override
            public Named forAccomplishment() {
                return null;
            }

            @Override
            public LocalDate earnedOn() {
                return null;
            }

            @Override
            public Ceremony presentedAt() {
                return null;
            }

            @Override
            public Optional<Catalogued> token() {
                return token;
            }

            private Optional<Catalogued> select() {
                Catalogued select = award.select(c->getClubber().getAwards().stream()
                        .filter(a->a.token().isPresent())
                        .allMatch(a->!a.token().get().equals(c)));
                Optional<Catalogued> tOptional = Optional.ofNullable(select);
                return tOptional;
            }
        }
    }
}
