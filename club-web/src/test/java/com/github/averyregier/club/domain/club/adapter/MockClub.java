package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.program.Curriculum;

import java.util.UUID;

/**
 * Created by avery on 2/25/15.
 */
public class MockClub extends ClubAdapter {
    private final Program program;
    private String id = UUID.randomUUID().toString();

    public MockClub(Curriculum series, Program program) {
        super(series);
        this.program = program;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    public String getId() {
        return id;
    }
}
