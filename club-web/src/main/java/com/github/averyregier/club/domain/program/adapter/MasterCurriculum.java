package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;
import com.github.averyregier.club.domain.utility.builder.Later;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/23/14.
 */
public class MasterCurriculum extends CurriculumAdapter {
    private List<Curriculum> series;

    public MasterCurriculum(String shortCode, String name, List<Curriculum> series, Later<Curriculum> parentCurriculum) {
        super(shortCode, name, collectBooks(series), parentCurriculum, null);
        this.series = series;
    }

    private static List<Book> collectBooks(List<Curriculum> series) {
        return series.stream().flatMap(s->s.getBooks().stream()).collect(Collectors.toList());
    }

    @Override
    public Optional<Section> lookup(String sectionId) {
        if(sectionId.startsWith(getShortCode()+":")) {
            String substring = sectionId.substring(getShortCode().length() + 1);
            return series.stream()
                    .map(s->s.lookup(substring))
                    .filter(Optional::isPresent)
                    .findFirst().orElseGet(Optional::empty);
        } else return Optional.empty();
    }

    @Override
    public List<Curriculum> getSeries() {
        return series;
    }

    @Override
    public Optional<Curriculum> findCurriculum(String curriculum) {
        String[] split = curriculum.split(":");
        String cShortCode = split[split.length - 1];
        return series.stream()
                .filter(s->s.getShortCode().equals(cShortCode))
                .findFirst();
    }
}
