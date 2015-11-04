package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.program.Award;
import com.github.averyregier.club.domain.program.Catalogued;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.program.SectionHolder;
import com.github.averyregier.club.domain.utility.Named;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.averyregier.club.domain.utility.UtilityMethods.stream;

/**
 * Created by avery on 9/6/2014.
 */
public abstract class ClubberRecord {
    private Signing signing = null;

    protected ClubberRecord() {}

    protected ClubberRecord(Signing initialSigning) {
        this.signing = initialSigning;
    }

    public abstract Section getSection();
    public abstract Clubber getClubber();
    public Signing sign(Listener byListener, String note) {
        if(signing == null) {
            signing = new RecordSigning(byListener, note);
            ((RecordSigning)signing).calculateAwards();
        }
        return signing;
    }

    public List<AwardPresentation> unSign() {
        if(mayBeUnsigned()) {
            List<AwardPresentation> awardPresentations = getSection().getAwards().stream()
                    .flatMap(this::removePresentationsOf)
                    .collect(Collectors.toList());

            if(awardPresentations.stream().anyMatch(p->!p.notPresented())) {
                throw new IllegalStateException("This has already been presented so you can't un-sign it");
            }

            unSign(awardPresentations);
            return awardPresentations;
        }
        return Collections.emptyList();
    }

    protected void unSign(List<AwardPresentation> awardPresentations) {
        this.signing = null;
    }

    public boolean mayBeUnsigned() {
        return getSigning().isPresent() &&
                (matchingAwards().anyMatch(AwardPresentation::notPresented) ||
                 !matchingAwards().findFirst().isPresent());
    }

    private Stream<AwardPresentation> matchingAwards() {
        return getSection().getAwards().stream()
                .flatMap((a) -> getSignings(a).flatMap(s -> getMatchingAwards(a, s)));
    }

    private Stream<AwardPresentation> removePresentationsOf(Award a) {
        Map<Signing, Optional<AwardPresentation>> map = getSignings(a)
                .collect(Collectors.toMap(Function.identity(),
                        signing -> getMatchingAwards(a, signing).findFirst()));

        map.entrySet().stream().filter(e->e.getValue().isPresent())
                .forEach(e -> e.getKey().getCompletionAwards().remove(e.getValue().get()));

        return map.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Stream<Signing> getSignings(Award a) {
        return a.getSections().stream()
                .flatMap(s -> stream(getClubber()
                        .getRecord(Optional.of(s))
                        .flatMap(ClubberRecord::getSigning)));
    }

    private Stream<AwardPresentation> getMatchingAwards(Award a, Signing signing) {
        return signing.getCompletionAwards().stream()
                .filter(ca -> ca.forAccomplishment().equals(a));
    }

    public Signing catchup(Listener byListener, String note, LocalDate date) {
        if(signing == null) {
            signing = new RecordSigning(byListener, note, date);
            ((RecordSigning)signing).calculateAwards();
        }
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
        private final LocalDate date;

        RecordSigning(Listener byListener, String note) {
            this(byListener, note, LocalDate.now());
        }

        RecordSigning(Listener byListener, String note, LocalDate date) {
            this.byListener = byListener;
            this.note = UtilityMethods.killWhitespace(note);
            this.date = date;
        }

        @Override
        public LocalDate getDate() {
            return date;
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
                    .filter(ClubberRecord.this::isCompleted)
                    .map(AwardPresentationAdapter::new)
                    .collect(Collectors.toSet());
        }

        private class AwardPresentationAdapter implements AwardPresentation {
            private final String id = UUID.randomUUID().toString();
            private final Award award;
            private final Optional<Catalogued> token;
            private Ceremony ceremony;

            public AwardPresentationAdapter(Award award) {
                this.award = award;
                this.token = select();
            }

            @Override
            public Person to() {
                return getClubber();
            }

            @Override
            public Named forAccomplishment() {
                return award;
            }

            @Override
            public LocalDate earnedOn() {
                return signing.getDate();
            }

            @Override
            public Ceremony presentedAt() {
                return ceremony;
            }

            @Override
            public Optional<Catalogued> token() {
                return token;
            }

            @Override
            public void presentAt(Ceremony ceremony) {
                this.ceremony = ceremony;
                persistAward(this);
            }

            private Optional<Catalogued> select() {
                return Optional.ofNullable(award.select(c->getClubber().getAwards().stream()
                        .filter(a -> a.token().isPresent())
                        .allMatch(a -> !a.token().get().equals(c))));
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getShortCode() {
                return award.getName();
            }

            @Override
            public ClubberRecord record() {
                return ClubberRecord.this;
            }

            @Override
            public boolean notPresented() {
                return ceremony == null;
            }
        }
    }

    protected void persistAward(AwardPresentation awardPresentation) {}
}
