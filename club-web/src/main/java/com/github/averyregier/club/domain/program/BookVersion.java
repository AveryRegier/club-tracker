package com.github.averyregier.club.domain.program;

import java.time.Year;
import java.util.Locale;

/**
 * Created by avery on 9/12/2014.
 */
public interface BookVersion {
    public int major();
    public int minor();
    public Translation getTranslation();
    public Locale getLanguage();
    public Year getPublicationYear();
}
