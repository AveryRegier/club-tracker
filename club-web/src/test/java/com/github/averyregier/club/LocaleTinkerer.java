package com.github.averyregier.club;

import com.github.averyregier.club.domain.utility.InputField;
import com.github.averyregier.club.domain.utility.UtilityMethods;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by rx39789 on 10/30/2014.
 */
public class LocaleTinkerer {

    public static void main(String... args) {

        Locale aDefault = Locale.getDefault();
        Map<String, List<MyValue>> languageCountryList = getAllCountryDropDown(aDefault);

        languageCountryList.entrySet().forEach(e->{
            System.out.println(e.getKey());

            e.getValue().forEach(v->{
                System.out.println(v.getValue()+'\t'+v.getDisplayName()+'\t'+v.isDefault());
            });
            System.out.println();
        });
    }

    private static Map<String, List<MyValue>> getAllCountryDropDown(final Locale aDefault) {

        Map<String, List<MyValue>> collect = Arrays.asList(Locale.getISOLanguages()).stream()
                .flatMap(lang -> getCountryLocales(lang))
                .filter(l -> !"".equals(l.getCountry()))
                .distinct()
                .map(l -> new MyValue(l, aDefault))
                .collect(Collectors.groupingBy(MyValue::getLanguage, Collectors.toCollection(TreeSet::new)))
                .entrySet().stream()
                .collect(Collectors.toMap(e->e.getKey(), e->new ArrayList(e.getValue())));
        return collect;
    }

    public static List<MyValue> getAllCountryDropDown(final String lang, final Locale aDefault) {
        return getCountryLocales(lang)
                .filter(l -> !"".equals(l.getCountry()))
                .distinct()
                .map(l -> new MyValue(l, aDefault))
                .collect(Collectors.toCollection(TreeSet::new))
                .stream()
                .collect(Collectors.toList());
    }

    private static Stream<Locale> getCountryLocales(String lang) {
        return Arrays.asList(Locale.getISOCountries()).stream()
                .map(country -> new Locale(lang, country));
    }

    private static Map<String, String> languagesMap = new TreeMap<String, String>();

    static {
        initLanguageMap();
    }

    public static List<Locale> getListOfCountries() {
        List<Locale> ownLanguages = new ArrayList<>();
        String[] countries = Locale.getISOCountries();

        int supportedLocale = 0, nonSupportedLocale = 0;

        for (String countryCode : countries) {

            Locale obj = null;
            if (languagesMap.get(countryCode) == null) {

                obj = new Locale("", countryCode);
                nonSupportedLocale++;

            } else {

                //create a Locale with own country's languages
                obj = new Locale(languagesMap.get(countryCode), countryCode);
                supportedLocale++;

            }
            ownLanguages.add(obj);
            System.out.println("Country Code = " + obj.getCountry()
                    + ", Country Name = " + obj.getDisplayCountry(obj)
                    + ", Languages = " + obj.getDisplayLanguage());

        }

        System.out.println("nonSupportedLocale : " + nonSupportedLocale);
        System.out.println("supportedLocale : " + supportedLocale);

        return ownLanguages;
    }

    // create Map with country code and languages
    private static void initLanguageMap() {
        for (Locale obj : Locale.getAvailableLocales()) {
            if ((obj.getDisplayCountry() != null) && (!"".equals(obj.getDisplayCountry()))) {
                languagesMap.put(obj.getCountry(), obj.getLanguage());
            }
        }
    }

    public static class MyValue implements InputField.Value, Comparable<MyValue>{

        private final Locale l;
        private final Locale aDefault;

        public MyValue(Locale l, Locale aDefault) {
            this.l = l;
            this.aDefault = aDefault;
        }

        @Override
        public String getDisplayName() {
            return l.getDisplayCountry(l);
        }

        @Override
        public String getValue() {
            return l.getCountry();
        }

        @Override
        public boolean isDefault() {
            return aDefault.getCountry().equals(l.getCountry());
        }

        String getLanguage() {
            return l.getLanguage();
        }

        @Override
        public int compareTo(MyValue o) {
            return getDisplayName().compareTo(o.getDisplayName());
        }
    }
}
