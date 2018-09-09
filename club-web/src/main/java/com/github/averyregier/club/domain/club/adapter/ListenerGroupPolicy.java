package com.github.averyregier.club.domain.club.adapter;

import com.github.averyregier.club.domain.club.Clubber;
import com.github.averyregier.club.domain.club.Listener;

import java.util.stream.Stream;

public interface ListenerGroupPolicy {
    Stream<Clubber> limit(Stream<Clubber> stream, Listener listener);
}
