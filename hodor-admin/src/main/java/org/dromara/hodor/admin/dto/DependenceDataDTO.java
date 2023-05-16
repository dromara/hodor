package org.dromara.hodor.admin.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author tomgs
 * @since 1.0
 **/
@Data
public class DependenceDataDTO implements Serializable {

    private Map<String, Object> metadata;

    private List<GraEntry> edges;

    private List<String> props;

    private List<JobNodeDTO> nodes;

    private List<String> disabled;

    @Override
    public String toString() {
        return "DependenceDataDTO{" +
                "metadata=" + metadata +
                ", edges=" + edges +
                ", props=" + props +
                ", nodes=" + nodes +
                ", disabled=" + disabled +
                '}';
    }

    public List<GraEntry> getEdges() {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        return edges;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void setEdges(List<GraEntry> edges) {
        this.edges = edges;
    }

    public List<String> getProps() {
        return props;
    }

    public void setProps(List<String> props) {
        this.props = props;
    }

    public List<String> getDisabled() {
        if (disabled == null) {
            disabled = new ArrayList<>();
        }
        return disabled;
    }

    public void setDisabled(List<String> disabled) {
        this.disabled = disabled;
    }

    public Boolean checkAndAdd(List<GraEntry> list, GraEntry gra) {
        boolean flag = true;
        if (!list.contains(gra)) {
            list.add(gra);
        }
        /*for (GraEntry graEntry: list) {
            if(graEntry.equals(gra)){
                flag = false;
                break;
            }
        }
        if(!flag){
            list.add(gra);
        }*/
        return true;
    }

}
