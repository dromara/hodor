package org.dromara.hodor.admin.domain;

import java.io.Serializable;
import java.util.List;

/**
 * UserRole
 *
 * @author tomgs
 * @since 1.0
 **/
public class UserRole implements Serializable {

    private static final long serialVersionUID = 3519883221401672845L;

    private Integer id;
    private String roleName;
    private String description;
    private List<String> items;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "DBUserRole{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
