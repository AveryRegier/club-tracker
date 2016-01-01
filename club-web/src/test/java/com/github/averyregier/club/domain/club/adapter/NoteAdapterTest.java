package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Note;
import com.github.averyregier.club.domain.club.Person;
import org.junit.Test;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by avery on 12/29/15.
 */
public class NoteAdapterTest {
    private Person person1 = mock(Person.class);
    private Person person2 = mock(Person.class);

    @Test
    public void newNote() {
        String text = "Some text";
        Note note = new NoteAdapter(person1, text);
        assertNewNote(note, text, person1);
        assertTrue(note.getReplies().isEmpty());
        assertFalse(note.getReplyTo().isPresent());
    }

    @Test
    public void persistedNote() {
        String text = "Some text";
        Clock now = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        ZonedDateTime createdTime = ZonedDateTime.now(now);
        ZonedDateTime updatedTime = ZonedDateTime.now(Clock.offset(now, Duration.ofHours(-1)));
        String id = UUID.randomUUID().toString();
        Note note = new NoteAdapter(id, person1, text, createdTime, updatedTime, null);
        assertEquals(person1, note.getWrittenBy());
        assertEquals(text, note.getText());
        assertEquals(createdTime, note.getCreationTime());
        assertEquals(updatedTime, note.getLastUpdatedTime());
        assertEquals(id, note.getId());
        assertTrue(note.getReplies().isEmpty());
        assertFalse(note.getReplyTo().isPresent());
    }

    @Test
    public void updatedNote() throws InterruptedException {
        Person person = mock(Person.class);
        String text = "Other text";
        Note note = new NoteAdapter(mock(Person.class), "Some text");
        Thread.sleep(1l);
        note.update(person, text);
        assertEquals(person, note.getWrittenBy());
        assertNotEquals(note.getCreationTime(), note.getLastUpdatedTime());
        assertEquals(text, note.getText());
    }

    @Test
    public void reply() {
        Note note = new NoteAdapter(person1, "Some text");

        String text = "A Reply";
        Note reply = note.reply(person2, text);
        assertNewNote(reply, text, person2);
        assertNotEquals(note.getId(), reply.getId());
        List<Note> replies = note.getReplies();
        assertTrue(replies.contains(reply));
        assertEquals(1, replies.size());
        assertEquals(Optional.of(note.getId()), reply.getReplyTo());
    }

    private void assertNewNote(Note note, String text, Person person) {
        assertEquals(person, note.getWrittenBy());
        assertEquals(note.getCreationTime(), note.getLastUpdatedTime());
        assertEquals(text, note.getText());
    }
}