package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;

import java.util.*;
import java.util.function.Function;

import static com.github.averyregier.club.domain.utility.InputField.Type.integer;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;
import static com.github.averyregier.club.domain.utility.UtilityMethods.killWhitespace;

/**
 * Created by avery on 10/3/2014.
 */
public enum StandardInputFields {
    name() {
        @Override
        public InputFieldGroupBuilder create(Locale locale) {
            return buildGroup(this)
                    .name("Name")
                    .field(f->f.name("Given").id("given").type(text))
                    .field(f -> f.name("Middle").id("middle").type(text))
                    .field(f -> f.name("Surname").id("surname").type(text))
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
                            .value("IV"))
                    .validate(getNameFn())
                    .map(p -> {
                        HashMap<String, String> model = new HashMap<>();
                        Name name1 = p.getName();
                        if (name1 != null) {
                            model.put("given", killWhitespace(name1.getGivenName()));
                            model.put("surname", killWhitespace(name1.getSurname()));
                        }
                        return model;
                    })
                    .update((p, o) -> p.getUpdater().setName((Name) o));
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
                        CountryValue.getAllCountryDropDowns(locale).forEach(v->f.value(v));
                        return f;
                    });
        }
    },
    gender {
        @Override
        public InputFieldBuilder create(Locale locale) {
            return buildField(this)
                    .name("Gender")
                    .type(InputField.Type.gender)
                    .value(Person.Gender.MALE.name())
                    .value(Person.Gender.FEMALE.name())
                    .map(p->p.getGender().map(g->g.name()).orElse(null))
                    .update((p,o)->p.getUpdater().setGender((Person.Gender)o));
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
        public InputFieldBuilder create(Locale locale) {
            return buildField(this)
                    .name("Email Address")
                    .type(InputField.Type.email)
                    .map(p->p.getEmail().orElse(null))
                    .update((p,o)->p.getUpdater().setEmail((String)o));
        }
    },
    ageGroup {
        @Override
        public InputFieldBuilder create(Locale locale) {
            InputFieldBuilder builder = buildField(this)
                    .name("Age Group")
                    .type(InputField.Type.ageGroup)
                    .map(p->p.asClubber()
                            .map(c->c.getCurrentAgeGroup() != null ? c.getCurrentAgeGroup().name() : null)
                            .orElse(null))
                    .update((p,o)->p.getUpdater().setAgeGroup((AgeGroup)o));
            for(AgeGroup.DefaultAgeGroup group: AgeGroup.DefaultAgeGroup.values()) {
                builder.value(group.name(), group.getDisplayName(), false);
            }
            return builder;
        }
    },
    action {
        @Override
        public ChildBuilder<InputFieldGroup, InputFieldDesignator> create(Locale locale) {
            InputFieldBuilder builder = buildField(this)
                    .name("Action")
                    .type(InputField.Type.action);
            for(Action action: Action.values()) {
                builder.value(action.name(), action.getDisplayName(), false);
            }
            return builder;
        }
    },
    childName {
        @Override
        public InputFieldGroupBuilder create(Locale locale) {
            return buildGroup(this)
                .name("Name")
                .field(f->f.name("Given").id("given").type(text))
                .field(f->f.name("Middle").id("middle").type(text))
                .field(f->f.name("Surname").id("surname").type(text))
                .field(f->f.name("Friendly").id("friendly").type(text))
                .field(f->f.name("Suffix").id("honorific").type(text)
                        .value("")
                        .value("Jr")
                        .value("I")
                        .value("II")
                        .value("III")
                        .value("IV"))
                .validate(getNameFn())
                .map(p->{
                    HashMap<String, String> model = new HashMap<>();
                    model.put("given", p.getName().getGivenName());
                    model.put("surname", p.getName().getSurname());
                    return model;
                })
                .update((p,o)->p.getUpdater().setName((Name)o));
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

    private static Function<Map<String, Object>, Optional<Object>> getNameFn() {
        return m-> Optional.of(new NameBuilder().given((String) m.get("given"))
                .surname((String) m.get("surname"))
                .middle((String) m.get("middle"))
                .title((String) m.get("title"))
                .friendly((String) m.get("friendly"))
                .honorific((String) m.get("honorific"))
                .build());
    }
}

