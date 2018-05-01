package model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "FIBER", schema = "SYSTEM", catalog = "")
public class FiberEntity {
    private long id;
    private Long length;
    private Collection<RelatedSensorsEntity> relatedSensorsById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "LENGTH", nullable = true, precision = 0)
    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FiberEntity that = (FiberEntity) o;

        if (id != that.id) return false;
        if (length != null ? !length.equals(that.length) : that.length != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (length != null ? length.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "fiberByFiberId")
    public Collection<RelatedSensorsEntity> getRelatedSensorsById() {
        return relatedSensorsById;
    }

    public void setRelatedSensorsById(Collection<RelatedSensorsEntity> relatedSensorsById) {
        this.relatedSensorsById = relatedSensorsById;
    }
}
