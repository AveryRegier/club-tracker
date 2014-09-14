package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.*;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.RewardBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.*;

/**
 * Created by rx39789 on 9/12/2014.
 */
public class TnTCurriculum {

    public static final Curriculum curriculum = new Curriculum() {
        private final Book START_ZONE_1 = new BookBuilder(0)
                .addReward(new RewardBuilder())
                .addSectionGroup(new SectionGroupBuilder(0)
                        .addReward(new RewardBuilder()
                                .addSection(new SectionBuilder(1, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(2, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(3, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(4, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(5, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(6, TnTSectionTypes.regular.get()))
                                .addSection(new SectionBuilder(7, TnTSectionTypes.regular.get()))
                        )).build();

        @Override
        public List<Book> getBooks() {
            return Arrays.asList(START_ZONE_1);
        }

        @Override
        public List<AgeGroup> getAgeGroups() {
            return Arrays.asList(THIRD_GRADE, FOURTH_GRADE, FIFTH_GRADE, SIXTH_GRADE);
        }

        @Override
        public Set<SectionType> getSectionTypes() {
            return null;
        }

        @Override
        public Optional<Section> lookup(String sectionId) {
            return null;
        }

        @Override
        public List<Book> recommendedBookList(AgeGroup age) {
            return getBooks();
        }

        @Override
        public List<Translation> getSupportedTranslations(Locale locale) {
            return null;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getShortCode() {
            return null;
        }
    };

    public static Curriculum get() {
        return curriculum;
    }
}
