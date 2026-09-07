package it.unicam.cs.mpgc.afc250713.model.item;

import java.util.Objects;

/**
 * Base implementation for items, providing identity semantics based on
 * {@link #getId()}.
 */
public abstract class AbstractItem implements Item {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractItem that = (AbstractItem) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
