package com.pchudzik.edu.ddd.its.field;

import com.pchudzik.edu.ddd.its.infrastructure.domain.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

@EqualsAndHashCode
public class FieldId implements Id {
    @Getter
    private final UUID value;

    @Getter
    private final int version;

    public FieldId() {
        this(UUID.randomUUID(), 1);
    }

    public FieldId(UUID value, int version) {
        this.value = value;
        this.version = version;
    }

    public FieldId nextVersion() {
        return new FieldId(this.value, version + 1);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("version")
                .append(version)
                .append("value")
                .append(value)
                .toString();
    }
}
