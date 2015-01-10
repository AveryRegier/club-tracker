package com.github.averyregier.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.view.UserBean;

import java.util.Set;
import java.util.function.Consumer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by avery on 12/27/14.
 */
public class TestUtility {
    public static User getUser(Consumer<UserBean> fn) {
        UserBean userBean = new UserBean();
        fn.accept(userBean);
        User user = new User();
        user.update(userBean);
        return user;
    }

    public static void assertEmpty(Set<?> set) {
        assertNotNull(set);
        assertTrue(set.isEmpty());
    }

    public static boolean anyEqual(int item, int... expected) {
        for(int i: expected) {
            if(item == i) return true;
        }
        return false;
    }
}
