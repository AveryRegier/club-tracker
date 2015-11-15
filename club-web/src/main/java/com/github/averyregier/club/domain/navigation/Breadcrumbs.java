package com.github.averyregier.club.domain.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by avery on 11/8/15.
 */
public class Breadcrumbs {
    private List<Breadcrumb> list = new ArrayList<>();

    public Breadcrumbs(Breadcrumb root) {
        list.add(root);
    }

    public List<Breadcrumb> getList() {
        return list;
    }

    public void mark(Breadcrumb crumb) {
        assert(crumb != null);
        for (int i = 0; i < list.size(); i++) {
            Breadcrumb p = list.get(i);
            if (p.equals(crumb)) {
                list = list.subList(0, i);
            }
        }
        list.add(crumb);
    }

    public String show(String classValue, String separator) {
        return list.subList(0, list.size()-1).stream()
                .map(b->"<a class=\""+classValue+"\" href=\""+b.getPath()+"\">"+b.getName()+"</a>")
                .collect(Collectors.joining(separator));
    }
}
