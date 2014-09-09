package com.github.averyregier.club.domain.club;

import java.util.List;
import java.util.Optional;

/**
 * Created by rx39789 on 9/5/2014.
 */
public interface Name {
    public String getGivenName();
    public String getSurname();
    public List<String> getMiddleNames();
    public Optional<String> getTitle();
    public String getFriendlyName();
    public String getHonorificName();
    public String getFullName();
}
