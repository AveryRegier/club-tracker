package com.github.averyregier.club.domain.navigation;

/**
 * Created by avery on 11/8/15.
 */
public class Breadcrumb {
    private final String name;
    private final String path;

    public Breadcrumb(String name, String path) {
        assert(name != null);
        assert(path != null);
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Breadcrumb) {
            Breadcrumb other = (Breadcrumb)obj;
            if(name.equals(other.getName()) && path.equals(other.getPath())) return true;
        }
        return false;
    }
}
