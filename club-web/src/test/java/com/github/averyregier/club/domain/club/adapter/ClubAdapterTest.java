package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Club;
import com.github.averyregier.club.domain.club.Program;
import com.github.averyregier.club.domain.program.adapter.MasterCurriculum;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class ClubAdapterTest {

    @Test
    public void testAsProgram() throws Exception {
        ProgramAdapter program = new ProgramAdapter(null, null, null);
        Club classUnderTest = new ClubAdapter(program, null);
        Optional<Program> programOptional = classUnderTest.asProgram();
        assertNotNull(programOptional);
        assertFalse(programOptional.isPresent());
    }

    @Test
    public void testGetShortName() throws Exception {
        MasterCurriculum curriculum = new MasterCurriculum("a name", Collections.emptyList());
        Club classUnderTest = new ClubAdapter(null, curriculum);
        assertEquals("a name", classUnderTest.getShortName());
    }

    @Test
    public void testGetCurriculum() throws Exception {
        MasterCurriculum curriculum = new MasterCurriculum(null, Collections.emptyList());
        Club classUnderTest = new ClubAdapter(null, curriculum);
        assertEquals(curriculum, classUnderTest.getCurriculum());
    }

    @Test
    public void testGetParentGroup() throws Exception {
        ProgramAdapter program = new ProgramAdapter(null, null, null);
        Club classUnderTest = new ClubAdapter(program, null);
        assertEquals(program, classUnderTest.getParentGroup().get());
    }

    @Test
    public void testGetProgram() throws Exception {
        ProgramAdapter program = new ProgramAdapter(null, null, null);
        Club classUnderTest = new ClubAdapter(program, null);
        assertEquals(program, classUnderTest.getProgram());
    }

    @Test
    public void testAsClub() throws Exception {
        Club classUnderTest = new ClubAdapter(null, null);
        assertEquals(classUnderTest, classUnderTest.asClub().get());
    }
}