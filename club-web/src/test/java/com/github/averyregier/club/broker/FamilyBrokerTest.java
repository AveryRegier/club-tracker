package com.github.averyregier.club.broker;

import com.github.averyregier.club.db.tables.Parent;
import com.github.averyregier.club.domain.club.Family;
import com.github.averyregier.club.domain.club.adapter.AddressAdapter;
import com.github.averyregier.club.domain.club.adapter.ClubberAdapter;
import com.github.averyregier.club.domain.club.adapter.FamilyAdapter;
import com.github.averyregier.club.domain.club.adapter.PersonAdapter;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.exception.DataAccessException;
import org.jooq.tools.jdbc.MockDataProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.averyregier.club.broker.BrokerTestUtil.*;
import static com.github.averyregier.club.db.tables.Family.FAMILY;
import static org.junit.Assert.assertTrue;

/**
 * Created by avery on 2/25/15.
 */
public class FamilyBrokerTest {
    @Test
    public void testPersist() {
        final Family family = newFamily();

        setup(mergeProvider((s) -> s.assertUUID(family, FAMILY.ID),
                (s)->s.assertUUID(family.getAddress(), FAMILY.ADDRESS_ID))).persist(family);
    }

    @Test
    public void testPersistAddress() {
        final Family family = newFamily();
        family.setAddress(new AddressAdapter(null, null, null, null, null, null));

        setup(mergeProvider((s) -> s.assertUUID(family, FAMILY.ID),
                (s) -> s.assertUUID(family.getAddress(), FAMILY.ADDRESS_ID))).persist(family);
    }


    @Test(expected = DataAccessException.class)
    public void testUpdatesNothing() throws Exception {
        final Family family = newFamily();

        MockDataProvider provider = new MockDataProviderBuilder()
                .updateCount(0)
                .build();

        setup(provider).persist(family);
    }

    private Family newFamily() {
        return new FamilyAdapter(new ClubberAdapter(new PersonAdapter()));
    }

    private FamilyBroker setup(MockDataProvider provider) {
        return new FamilyBroker(mockConnector(provider));
    }

    @Test
    public void findFamilyMembers() {
        String familyId = UUID.randomUUID().toString();

        String parent1Id = UUID.randomUUID().toString();
        String parent2Id = UUID.randomUUID().toString();
        String child1Id = UUID.randomUUID().toString();
        String child3Id = UUID.randomUUID().toString();

        List<String> all = Arrays.asList(parent1Id, parent2Id, child1Id, child3Id);

        List<String> allFamilyMembers = setup(select(
                (s)->s.assertUUID(familyId, Parent.PARENT.FAMILY_ID),
                (create)->{
                    Result<Record1<byte[]>> result = create.newResult(Parent.PARENT.ID);
                    setValue(parent1Id, create, result);
                    setValue(parent2Id, create, result);
                    setValue(child1Id, create, result);
                    setValue(child3Id, create, result);
                    return result;
                }
            )).getAllFamilyMembers(familyId).collect(Collectors.toList());

        assertTrue(allFamilyMembers.stream().allMatch(all::contains));
        assertTrue(all.stream().allMatch(allFamilyMembers::contains));
    }

    private Record1<byte[]> setValue(String parent1Id, DSLContext create, Result<Record1<byte[]>> result) {
        Record1<byte[]> record = create.newRecord(Parent.PARENT.ID);
        result.add(record);
        return record.value1(parent1Id.getBytes());
    }
}
