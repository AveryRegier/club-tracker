package com.github.averyregier.club;

import com.github.averyregier.club.domain.User;
import com.github.averyregier.club.view.UserBean;

import java.util.function.Consumer;

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
}
