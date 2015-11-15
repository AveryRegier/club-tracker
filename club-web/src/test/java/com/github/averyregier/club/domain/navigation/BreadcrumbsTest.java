package com.github.averyregier.club.domain.navigation;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by avery on 11/8/15.
 */
public class BreadcrumbsTest {
    private final Breadcrumb root = new Breadcrumb("Home", "/");
    private final Breadcrumb crumb = new Breadcrumb("Secondary", "/secondary");
    private final Breadcrumb crumb3 = new Breadcrumb("Tertiary", "/tertiary");

    @Test
    public void rootCrumb() {
        assertRoot();
    }

    private Breadcrumbs assertRoot() {
        Breadcrumbs breadcrumbs = new Breadcrumbs(root);
        List<Breadcrumb> crumbs = breadcrumbs.getList();
        assertEquals(root, crumbs.get(0));
        assertEquals(1, crumbs.size());
        return breadcrumbs;
    }

    @Test
    public void secondCrumb() {
        Breadcrumbs breadcrumbs = assertRoot();
        Breadcrumb root = breadcrumbs.getList().get(0);
        breadcrumbs.mark(crumb);
        List<Breadcrumb> crumbs = breadcrumbs.getList();
        assertEquals(root, crumbs.get(0));
        assertEquals(crumb, crumbs.get(1));
        assertEquals(2, crumbs.size());
    }

    @Test
    public void backToRoot() {
        Breadcrumbs breadcrumbs = assertRoot();
        breadcrumbs.mark(crumb);
        breadcrumbs.mark(root);
        List<Breadcrumb> crumbs = breadcrumbs.getList();
        assertEquals(root, crumbs.get(0));
        assertEquals(1, crumbs.size());
    }

    @Test
    public void back() {
        Breadcrumbs breadcrumbs = assertRoot();
        breadcrumbs.mark(crumb);
        breadcrumbs.mark(crumb3);
        breadcrumbs.mark(crumb);
        List<Breadcrumb> crumbs = breadcrumbs.getList();
        assertEquals(crumb, crumbs.get(1));
        assertEquals(2, crumbs.size());
    }

    @Test
    public void urlAndName() {
        Breadcrumb breadcrumb = new Breadcrumb("Name", "/url");
        assertEquals("Name", breadcrumb.getName());
        assertEquals("/url", breadcrumb.getPath());
    }

    @Test
    public void equalsHonored() {
        Breadcrumbs breadcrumbs = assertRoot();
        breadcrumbs.mark(crumb);
        breadcrumbs.mark(crumb3);
        breadcrumbs.mark(new Breadcrumb(new String(crumb.getName()), new String(crumb.getPath())));
        List<Breadcrumb> crumbs = breadcrumbs.getList();
        assertEquals(crumb, crumbs.get(1));
        assertEquals(2, crumbs.size());
    }

    @Test
    public void showNothing() {
        Breadcrumbs breadcrumbs = assertRoot();
        String html = breadcrumbs.show("className", " -> ");
        assertEquals("", html);
    }

    @Test
    public void showRootOnly() {
        Breadcrumbs breadcrumbs = assertRoot();
        breadcrumbs.mark(crumb);
        String html = breadcrumbs.show("className", " -> ");
        assertEquals("<a class=\"className\" href=\""+root.getPath()+"\">"+root.getName()+"</a>", html);
    }

    @Test
    public void showAncestors() {
        Breadcrumbs breadcrumbs = assertRoot();
        breadcrumbs.mark(crumb);
        breadcrumbs.mark(crumb3);
        String html = breadcrumbs.show("className", " -> ");
        assertEquals("<a class=\"className\" href=\""+root.getPath()+"\">"+root.getName()+"</a>" +
                " -> <a class=\"className\" href=\""+crumb.getPath()+"\">"+crumb.getName()+"</a>", html);
    }
}