package com.github.averyregier.club;

import java.util.TimeZone;


public class TimeZoneTinkerer {
    public static void main(String[] args) {
		TimeZone tz = TimeZone.getDefault();
		System.out.println(tz.getID());
		System.out.println(tz.getDSTSavings());
		System.out.println(tz.useDaylightTime());
		System.out.println(tz.useDaylightTime());
		
		String[] availableIDs = TimeZone.getAvailableIDs();
		System.out.println(availableIDs.length);
		
		for (String string : availableIDs) {
			tz = TimeZone.getTimeZone(string);
			

			System.out.print((double)tz.getRawOffset()/(double)(1000*60*60));
			System.out.print('\t');
			System.out.print(tz.getDSTSavings()/(1000*60*60));
			System.out.print('\t');
			System.out.print(tz.useDaylightTime());
			System.out.print('\t');
			System.out.print(tz.getID());
			System.out.print('\t');
			System.out.println(tz.getDisplayName());
		}
	}
}
