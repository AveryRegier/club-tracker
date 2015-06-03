package com.github.averyregier.club.domain;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.ClubGroup;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.club.adapter.ClubAdapter;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.Programs;

import java.util.*;

/**
 * Created by avery on 9/6/2014.
 */
public class ClubManager {
    private Map<String, Club> clubs = new LinkedHashMap<String, Club>();
    public Optional<Club> lookup(String id) {
        return Optional.ofNullable(clubs.get(id));
    }

    public Collection<Club> getClubs() {
        return clubs.values();
    }

    public Club createClub(ClubGroup parent, Curriculum series) {
        String id = UUID.randomUUID().toString();
        ClubAdapter clubAdapter = new PersistedClub(series, id, parent);
        clubs.put(id, clubAdapter);
        return clubAdapter;
    }

    public Optional<Club> injectClub(String id, String parentId, String curriculum) {
        Optional<Curriculum> series = Programs.find(curriculum);
        return series.map(s -> {
            Club clubAdapter = new PersistedClub(
                    s, id, (ClubGroup) clubs.get(parentId));
            clubs.put(id, clubAdapter);
            return clubAdapter;
        });
    }

    private static class PersistedClub extends ClubAdapter {
        private final String id;
        private final ClubGroup parent;

        public PersistedClub(Curriculum series, String id, ClubGroup parent) {
            super(series);
            this.id = id;
            this.parent = parent;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Optional<ClubGroup> getParentGroup() {
            return Optional.ofNullable(parent);
        }

        @Override
        public Program getProgram() {
            return getParentGroup()
                    .map(ClubGroup::getProgram)
                    .orElse(null);
        }
    }
}
