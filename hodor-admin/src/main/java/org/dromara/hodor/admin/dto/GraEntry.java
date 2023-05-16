package org.dromara.hodor.admin.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class GraEntry implements Serializable {

    private String source;

    private String target;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //if (!super.equals(o)) return false;

        GraEntry entry = (GraEntry) o;

        if (source != null ? !source.equals(entry.source) : entry.source != null){
            return false;
        }
        return target != null ? target.equals(entry.target) : entry.target == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }
}
