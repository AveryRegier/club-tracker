package com.github.averyregier.club.domain.club;

/**
 * Created by avery on 8/22/15.
 */
public enum RegistrationSection {
    parent,
    household {
        @Override
        public Registered find(Person person) {
            return person.getFamily().orElse(null);
        }
    },
    child;

    public Registered find(Person person) {
        return person;
    };

    public static RegistrationSection findSection(String name) {
        switch (name) {
            case "About Myself":
            case "About Parent":
                return parent;
            case "About Your Household":
                return household;
            default:
                return child;
        }
    }
}
