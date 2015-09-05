package com.github.averyregier.club.domain.utility.adapter;

import com.github.averyregier.club.domain.club.Address;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.Name;
import com.github.averyregier.club.domain.club.Person;
import com.github.averyregier.club.domain.club.adapter.AddressAdapter;
import com.github.averyregier.club.domain.club.adapter.NameBuilder;
import com.github.averyregier.club.domain.program.AgeGroup;
import com.github.averyregier.club.domain.utility.Action;
import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.InputFieldDesignator;
import com.github.averyregier.club.domain.utility.InputFieldGroup;
import com.github.averyregier.club.domain.utility.builder.ChildBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.averyregier.club.domain.utility.InputField.Type.integer;
import static com.github.averyregier.club.domain.utility.InputField.Type.text;
import static com.github.averyregier.club.domain.utility.UtilityMethods.*;

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
                    .validate(StandardInputFields::mapName)
                    .map(p -> {
                        HashMap<String, String> model = new HashMap<>();
                        Name name1 = p.getName();
                        if (name1 != null) {
                            model.put("given", killWhitespace(name1.getGivenName()));
                            model.put("surname", killWhitespace(name1.getSurname()));
                            model.put("middle", p.getName().getMiddleNames().stream().collect(Collectors.joining(" ")));
                            model.put("friendly", p.getName().getFriendlyName());
                            model.put("honorific", p.getName().getHonorificName());
                            model.put("title", p.getName().getTitle().orElse(""));
                        }
                        return model;
                    })
                    .update((d,p, o) -> p.getUpdater().setName((Name) o));
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
                        CountryValue.getAllCountryDropDowns(locale).forEach(f::value);
                        return f;
                    })
                    .validate(StandardInputFields::mapAddress)
                    .map(p -> {
                        HashMap<String, String> model = new HashMap<>();
                        ifPresent(p.getFamily(), Family::getAddress, a -> {
                            model.put("line1", a.getLine1());
                            model.put("line2", a.getLine2());
                            model.put("city", a.getCity());
                            model.put("territory", a.getTerritory());
                            model.put("postal-code", a.getPostalCode());
                            model.put("country", orEmpty(a.getCountry(), CountryValue::getValue));
                        });
                        return model;
                    })
                    .update((d,p,o) -> p.getUpdater().setAddress((Address)o));
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
                    .map(p -> p.getGender().map(Person.Gender::name).orElse(null))
                    .update((d,p, o) -> p.getUpdater().setGender((Person.Gender) o));
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
                    .update((d,p,o)->p.getUpdater().setEmail((String)o));
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
                    .update((d,p,o)->p.getUpdater().setAgeGroup((AgeGroup)o));
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
                .validate(StandardInputFields::mapName)
                .map(p->{
                    HashMap<String, String> model = new HashMap<>();
                    model.put("given", p.getName().getGivenName());
                    model.put("surname", p.getName().getSurname());
                    model.put("middle", p.getName().getMiddleNames().stream().collect(Collectors.joining(" ")));
                    model.put("friendly", p.getName().getFriendlyName());
                    model.put("honorific", p.getName().getHonorificName());
                    return model;
                })
                .update((d,p,o)->p.getUpdater().setName((Name)o));
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

    private static Optional<Object> mapName(Map<String, Object> m) {
        return Optional.of(new NameBuilder().given((String) m.get("given"))
                .surname((String) m.get("surname"))
                .middle((String) m.get("middle"))
                .title((String) m.get("title"))
                .friendly((String) m.get("friendly"))
                .honorific((String) m.get("honorific"))
                .build());
    }

    private static Optional<Object> mapAddress(Map<String, Object> m) {
        return Optional.of(new AddressAdapter(
                (String) m.get("line1"),
                (String) m.get("line2"),
                (String) m.get("city"),
                (String) m.get("postal-code"),
                (String) m.get("territory"),
                CountryValue.findCountry((String) m.get("country"))
        ));
    }

    public static Optional<StandardInputFields> find(String id) {
        try {
            return Optional.ofNullable(valueOf(id));
        } catch(IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

