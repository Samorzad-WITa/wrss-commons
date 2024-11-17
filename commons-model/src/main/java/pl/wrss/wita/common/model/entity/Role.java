package pl.wrss.wita.common.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import pl.wrss.wita.common.model.converter.PermissionGrantersConverter;

@Entity
@Table
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class Role extends EntityBase {

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String name;

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String code;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean adminPanelAccess;

    @Column(length = 500)
    @Convert(converter = PermissionGrantersConverter.class)
    private String[] permissionGranters;
}
