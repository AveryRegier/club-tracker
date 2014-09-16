package com.github.averyregier.club.domain.program.adapter;

import com.github.averyregier.club.domain.program.BookVersion;
import com.github.averyregier.club.domain.program.Translation;

import java.time.Year;
import java.util.Locale;

/**
 * Created by avery on 9/13/2014.
 */
public class BookVersionAdapter implements BookVersion {
    private final int major;
    private final int minor;
    private final Translation translation;
    private final Locale locale;
    private final Year year;

    public BookVersionAdapter(int major, int minor, Translation translation, Locale locale, Year year) {
        this.major = major;
        this.minor = minor;
        this.translation = translation;
        this.locale = locale;
        this.year = year;
    }

    @Override
    public int major() {
        return major;
    }

    @Override
    public int minor() {
        return minor;
    }

    @Override
    public Translation getTranslation() {
        return translation;
    }

    @Override
    public Locale getLanguage() {
        return locale;
    }

    @Override
    public Year getPublicationYear() {
        return year;
    }

    @Override
    public String toString() {
        return "v"+major+"."+minor;
    }
}
