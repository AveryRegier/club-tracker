package com.github.averyregier.club.broker;

import com.github.averyregier.club.domain.club.adapter.ProgramAdapter;

import java.util.UUID;

class MockProgram extends ProgramAdapter {
    String id = UUID.randomUUID().toString();

    public MockProgram() {
        super("en", "Any Org Nmae", "AWANA");
    }

    @Override
    public String getId() {
        return id;
    }
}
