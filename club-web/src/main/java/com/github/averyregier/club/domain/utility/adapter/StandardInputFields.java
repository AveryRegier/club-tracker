package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;

import java.util.Locale;

import static com.github.averyregier.club.domain.utility.InputField.Type.integer;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;

/**
 * Created by rx39789 on 10/3/2014.
 */
public enum StandardInputFields {
    name() {
        @Override
        public InputFieldGroupBuilder create(Locale locale) {
            return buildGroup(this)
                    .name("Name")
                    .field(f->f.name("Given").id("given").type(text))
                    .field(f->f.name("Middle").id("middle").type(text))
                    .field(f->f.name("Surname").id("surname").type(text))
                    .field(f->f.name("Friendly").id("friendly").type(text))
                    .field(f->f.name("Title").id("title").type(text)
                            .value("")
                            .value("Dr"))
                    .field(f->f.name("Suffix").id("honorific").type(text)
                            .value("")
                            .value("Sr")
                            .value("Jr")
                            .value("I")
                            .value("II")
                            .value("III")
                            .value("IV"));
        }
    },
    address {
        @Override
        public InputFieldGroupBuilder create(Locale locale) {
            return buildGroup(this).name("Address")
                    .field(f -> f.name("").id("line1").type(text))
                    .field(f -> f.name("").id("line2").type(text))
                    .field(f -> f.name("City").id("city").type(text))
                    .field(f -> f.name("State/Province").id("territory").type(text))
                    .field(f->f.name("Postal Code").id("postal-code").type(text))
                    .field(f->{
                        f.name("Country").id("country").type(text);
                        for(String country: Locale.getISOCountries()) {
                            f.value(country);
                        }
                        return f;
                    });
        }
    },
    gender {
        @Override
        public InputFieldBuilder create(Locale locale) {
            return buildField(this)
                    .name("Gender")
                    .type(text)
                    .value(Person.Gender.MALE.name())
                    .value(Person.Gender.FEMALE.name());
        }
    },
    age {
        @Override
        public InputFieldBuilder create(Locale locale) {
            return buildField(this).name("Age").type(integer);
        }
    },
    email {
        @Override
        public ChildBuilder<InputFieldGroup, InputFieldDesignator> create(Locale locale) {
            return buildField(this).name("Email Address").type(text);
        }
    };

    public abstract ChildBuilder<InputFieldGroup, InputFieldDesignator> create(Locale locale);

    public InputFieldGroupBuilder createGroup(Locale locale) {
        return (InputFieldGroupBuilder) create(locale);
    }

    public InputFieldBuilder createField(Locale locale) {
        return (InputFieldBuilder) create(locale);
    }

    private static InputFieldBuilder buildField(StandardInputFields field) {
        return new InputFieldBuilder().id(field.name());
    }

    private static InputFieldGroupBuilder buildGroup(StandardInputFields field) {
        return new InputFieldGroupBuilder().id(field.name());
    }

}

