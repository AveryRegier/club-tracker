package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Note;
import com.github.averyregier.club.domain.club.Person;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by avery on 12/28/15.
 */
public class NoteAdapter implements Note {
    private Person writtenBy;
    private String text;
    private final ZonedDateTime createdTime;
    private String replyToNoteId;
    private ZonedDateTime updateTime;
    private final String id;
    private final List<Note> replies = loadReplies();

    protected ArrayList<Note> loadReplies() {
        return new ArrayList<>();
    }

    public NoteAdapter(Person writtenBy, String text) {
        this.id = UUID.randomUUID().toString();
        update(writtenBy, text);
        this.createdTime = this.updateTime;
    }

    NoteAdapter(Person writtenBy, String text, String replyToNoteId) {
        this.replyToNoteId = replyToNoteId;
        this.id = UUID.randomUUID().toString();
        update(writtenBy, text);
        this.createdTime = this.updateTime;
    }

    public NoteAdapter(String id, Person writtenBy, String text, ZonedDateTime createdTime, ZonedDateTime updateTime, String replyToNoteId) {
        this.id = id;
        this.writtenBy = writtenBy;
        this.text = text;
        this.updateTime = updateTime;
        this.createdTime = createdTime;
        this.replyToNoteId = replyToNoteId;
    }

    public void update(Person writtenBy, String note) {
        this.writtenBy = writtenBy;
        this.text = note;
        this.updateTime = ZonedDateTime.now();
    }

    @Override
    public ZonedDateTime getCreationTime() {
        return createdTime;
    }

    @Override
    public Person getWrittenBy() {
        return writtenBy;
    }

    @Override
    public Note reply(Person person, String text) {
        Note reply = new NoteAdapter(person, text, id);
        replies.add(reply);
        return reply;
    }

    @Override
    public List<Note> getReplies() {
        return replies;
    }

    @Override
    public String getId() {
        return id;
    }

    public ZonedDateTime getLastUpdatedTime() {
        return updateTime;
    }

    public String getText() {
        return text;
    }

    @Override
    public Optional<String> getReplyTo() {
        return Optional.ofNullable(replyToNoteId);
    }
}
