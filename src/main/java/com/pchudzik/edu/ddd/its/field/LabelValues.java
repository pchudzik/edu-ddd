package com.pchudzik.edu.ddd.its.field;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface LabelValues {
    static LabelValues of(Collection<? extends LabelValue> labels) {
        return new SimpleLabelValues(new ArrayList<>(labels));
    }

    static LabelValues empty() {
        return new SimpleLabelValues(Collections.emptyList());
    }

    List<? extends LabelValue> getLabels();

    default boolean isEmpty() {
        return getLabels().isEmpty();
    }

    default Stream<? extends LabelValue> stream() {
        return getLabels().stream();
    }

    interface LabelValue {
        static LabelValue of(String value) {
            return new SimpleLabelValue(value);
        }


        String getValue();
    }
}

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
class SimpleLabelValue implements LabelValues.LabelValue {
    private final String value;
}

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
class SimpleLabelValues implements LabelValues {
    private final List<LabelValue> labels;
}

