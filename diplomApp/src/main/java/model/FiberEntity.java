package model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "FIBER", schema = "SYSTEM", catalog = "")
public class FiberEntity {
    @Id
    /*@SequenceGenerator(name="fiber_seq",
            sequenceName="FIBER_SEQUENCE")*/
    @GeneratedValue(strategy=GenerationType.AUTO)/*(strategy=GenerationType.SEQUENCE,
            generator="fiber_seq")*/
    @Column(name = "ID", nullable = false, precision = 0)
    private long id;
    @Basic
    @Column(name = "LENGTH", nullable = true, precision = 0)
    private Long length;
    @OneToMany(mappedBy = "fiberByFiberId")
    private Collection<RelatedSensorsEntity> relatedSensorsById;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


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


    public Collection<RelatedSensorsEntity> getRelatedSensorsById() {
        return relatedSensorsById;
    }

    public void setRelatedSensorsById(Collection<RelatedSensorsEntity> relatedSensorsById) {
        this.relatedSensorsById = relatedSensorsById;
    }
}
