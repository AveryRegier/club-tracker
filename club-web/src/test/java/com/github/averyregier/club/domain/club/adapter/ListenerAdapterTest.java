package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Clubber;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ListenerAdapterTest {

    @Test
    public void noClubbersQuickList() {
        ListenerAdapter listener = new ListenerAdapter(new PersonAdapter());
        listener.setClubGroup(new ProgramAdapter("en_US", null, "AWANA"));
        Set<Clubber> quickList = listener.getQuickList();
        assertNotNull(quickList);
        assertTrue(quickList.isEmpty());
    }

}