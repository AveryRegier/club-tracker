package com.github.averyregier.club.domain.program.awana;

import com.github.averyregier.club.domain.program.AccomplishmentLevel;
import com.github.averyregier.club.domain.program.Curriculum;
import com.github.averyregier.club.domain.program.adapter.BookBuilder;
import com.github.averyregier.club.domain.program.adapter.CurriculumBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionGroupBuilder;
import com.github.averyregier.club.domain.program.adapter.SectionHolderBuilder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.UnaryOperator;

import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.FIVE;
import static com.github.averyregier.club.domain.program.AgeGroup.DefaultAgeGroup.FOUR;
import static com.github.averyregier.club.domain.program.awana.CubbiesSectionTypes.*;

public class CubbiesCurriculum {
    public static final Curriculum curriculum = build(new CurriculumBuilder()).build();

    public static Curriculum get() {
        return curriculum;
    }

    public static CurriculumBuilder build(CurriculumBuilder builder) {
        return buildCubbies(builder
                .shortCode("Cubbies")
                .name("Cubbies")
                .accepts(FOUR, FIVE));
    }

    public static CurriculumBuilder buildCubbies(CurriculumBuilder builder) {
        return builder
                .scheduled(true)
                .curriculum(a -> a.name("Apple Acres").shortCode("AA")
                        .curriculum(c -> c
                                .shortCode("AS")
                                .name("AppleSeed")
                                .book(0, appleAcres())
                                .book(1, appleseed()))
                        .curriculum(c -> c
                                .shortCode("HC")
                                .name("HoneyComb")
                                .book(2, appleAcres())
                                .book(3, honeycomb())));
    }

    private static UnaryOperator<BookBuilder> appleAcres() {
        return b -> b
                .shortCode("EB")
                .mwhCode("EB")
                .name("Entrance Booklet")
                .publicationYear(2013) // ?
                .catalog("92866", "1")
                .catalog("92867", "25")
                .award(a -> {
                    a.type(AccomplishmentLevel.group);
                    return a.name("Cubbies Vest");
                })
                .group(0, g ->
                        g.name("Apple Acres").award(a -> a.name(start.getAwardName().get()))
                                .section(1, start, s -> s.shortCode("A").name("Parent Night; Cubbies Key Verse"))
                                .section(2, start, s -> s.shortCode("B").name("Cubbies Motto")));
    }

    private static UnaryOperator<BookBuilder> appleseed() {
        AtomicInteger hug = new AtomicInteger();
        return b -> b
                .shortCode("H")
                .name("Handbook")
                .publicationYear(2013)
                .catalog("92892")
                .group(1, apple("AppleSeed Trail", redBearHug, hug,
                        "A Is for All",
                        "C Is for Christ"))
                .group(2, apple("God Is Creator", greenBearHug, hug,
                        "Creation: Days One and Two",
                        "Creation: Days Three and Four",
                        "Creation: Days Five and Six (Animals)",
                        "Unit 1 Review, Day Six (People) and Day Seven"))
                .group(3, apple("God's Word Is Truth", redBearHug, hug,
                        "Adam and Eve Live in a Beautiful Garden",
                        "Adam and Eve Don't Listen to God's Truth",
                        "Adam and Eve Must Leave the Garden",
                        "Unit 2 Review"))
                .group(4, apple("God Keeps His Promises", greenBearHug, hug,
                        "God Tells Noah to Build an Ark",
                        "God Keeps Noah and His Family Safe",
                        "God's Rainbow Shows His Promise",
                        "Unit 3 Review"))
                .group(3, apple("God Is Mighty", redBearHug, hug,
                        "Abraham's Family Grows",
                        "God Sends the First Nine Plagues to Egypt",
                        "The 10th Plague and the Exodus",
                        "Unit 4 Review; Red Sea Crossing"))
                .group(5, apple("God Is in Charge", greenBearHug, hug,
                        "The Israelites Disobey God's Commands",
                        "God Chooses David as King",
                        "Future King David Defeats Goliath",
                        "Unit 5 Review"))
                .group(6, apple("God Sent the Savior", redBearHug, hug,
                        "Jesus Is Baptized and Says No to Satan",
                        "Jesus Calls His Disciples",
                        "Jesus and the Storm",
                        "Unit 6 Review"))
                .group(7, specialDay(
                        "Thanksgiving",
                        "Christmas",
                        "Easter",
                        "Missions"));
    }

    private static UnaryOperator<BookBuilder> honeycomb() {
        AtomicInteger hug = new AtomicInteger();
        return b -> b
                .shortCode("H")
                .name("Handbook")
                .publicationYear(2014)
                .catalog("95455")
                .group(1, apple("HoneyComb Trail", redBearHug, hug,
                        "A Is for All",
                        "C Is for Christ"))
                .group(2, apple("God Is Creator", greenBearHug, hug,
                        "Our Magnificent Creator",
                        "God Created All People and You Too!",
                        "God Made Your Family!",
                        "Unit 1 Review"))
                .group(3, apple("God Is the One True God", redBearHug, hug,
                        "The Israelites Worship God at the Temple",
                        "The Israelites Worship God at the Temple",
                        "Shadrach, Meshach and Abednego Worship Only God",
                        "Unit 2 Review"))
                .group(4, apple("Jesus Is the Good Shepherd", greenBearHug, hug,
                        "The Good Shepherd Knows and Leads His Sheep",
                        "The Good Shepherd Looks for Lost Sheep",
                        "The Good Shepherd Is With Us in Scary Times",
                        "Unit 3 Review"))
                .group(3, apple("Jesus Loves All People", redBearHug, hug,
                        "Jesus Loves the Paralyzed Man",
                        "Jesus Loves Two Daughters",
                        "Jesus Loves the Crowd of 5,000",
                        "Unit 4 Review"))
                .group(5, apple("Jesus Came to Save Us", greenBearHug, hug,
                        "Jesus Loves Blind Bartimaeus",
                        "Jesus Loves Zacchaeus",
                        "Jesus Loves Mary of Bethany",
                        "Unit 5 Review"))
                .group(6, apple("Jesus Says to Tell the Good News", redBearHug, hug,
                        "Peter and the Disciples Tell the Good News",
                        "Philip Tells the Good News",
                        "Paul and Silas Tell the Good News",
                        "We Can Tell the Good News"))
                .group(7, specialDay(
                        "Thanksgiving: One Thankful Leper",
                        "Christmas: God Sends a Savior",
                        "Valentine’s Day: The Parable of the Good Samaritan",
                        "Easter: He Is Risen!"));
    }

    private static UnaryOperator<SectionGroupBuilder> specialDay(String... titles) {
        AtomicInteger sd = new AtomicInteger();
        return apple("Special Days", CubbiesSectionTypes.specialDay, sd, titles);
    }


    private static UnaryOperator<SectionGroupBuilder> apple(String unit, CubbiesSectionTypes type, AtomicInteger hug, String... titles) {
        return g -> {
            g.name(unit);
            if (type.getAwardName().isPresent()) {
                String name = type.getAwardName().get();
                g.award(r -> addSections(type, hug, r.name(name), titles));
            } else {
                addSections(type, hug, g, titles);
            }
            return g;
        };
    }

    private static <T extends SectionHolderBuilder<T>> T addSections(CubbiesSectionTypes type, AtomicInteger hug, T b, String[] titles) {
        for (int i = 0; i < titles.length; i++) {
            String title = titles[i];
            b.section(i, type, s -> s.name(title).shortCode(Integer.toString(hug.incrementAndGet())));
        }
        return b;
    }
}
