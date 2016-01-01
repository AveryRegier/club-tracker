package com.github.averyregier.club.domain.club;

import com.github.averyregier.club.domain.utility.HasUUID;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by avery on 12/28/15.
 */
public interface Note extends HasUUID {
    Person getWrittenBy();
    String getText();

    ZonedDateTime getCreationTime();
    ZonedDateTime getLastUpdatedTime();

    void update(Person writtenBy, String note);
    Note reply(Person person, String s);

    List<Note> getReplies();

    Optional<String> getReplyTo();
}
