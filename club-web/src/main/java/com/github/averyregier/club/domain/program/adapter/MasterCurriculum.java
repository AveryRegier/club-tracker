package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.Book;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Section;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by avery on 9/23/14.
 */
public class MasterCurriculum extends CurriculumAdapter {
    private List<Curriculum> series;

    public MasterCurriculum(String shortCode, List<Curriculum> series) {
        super(shortCode, collectBooks(series), null);
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
                    .filter(o->o.isPresent())
                    .findFirst().orElse(Optional.empty());
        } else return Optional.empty();
    }

    @Override
    public List<Curriculum> getSeries() {
        return series;
    }
}
