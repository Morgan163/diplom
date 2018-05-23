package model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "SENSOR", schema = "SYSTEM", catalog = "")
public class SensorEntity {
    @Id
   /* @SequenceGenerator(name="sensor_seq",
            sequenceName="SYSTEM.SENSOR_SEQUENCE")*/
    @GeneratedValue(strategy=GenerationType.AUTO)/*(strategy=GenerationType.SEQUENCE,
            generator="sensor_seq")*/
    @Column(name = "ID", nullable = false, precision = 0)
    private long id;

    @Basic
    @Column(name = "WAVE", nullable = true, precision = 0)
    private Long wave;
    @OneToMany(mappedBy = "sensorBySensor1Id",cascade=CascadeType.ALL)
    private Collection<RelatedSensorsEntity> relatedSensorsById;
    @OneToMany(mappedBy = "sensorBySensor2Id",cascade=CascadeType.ALL)
    private Collection<RelatedSensorsEntity> relatedSensorsById_0;
    @OneToMany(mappedBy = "sensorBySensor",cascade=CascadeType.ALL)
    private Collection<TopologiesEntity> topologiesById;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public Long getWave() {
        return wave;
    }

    public void setWave(Long wave) {
        this.wave = wave;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorEntity that = (SensorEntity) o;

        if (id != that.id) return false;
        if (wave != null ? !wave.equals(that.wave) : that.wave != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (wave != null ? wave.hashCode() : 0);
        return result;
    }


    public Collection<RelatedSensorsEntity> getRelatedSensorsById() {
        return relatedSensorsById;
    }

    public void setRelatedSensorsById(Collection<RelatedSensorsEntity> relatedSensorsById) {
        this.relatedSensorsById = relatedSensorsById;
    }


    public Collection<RelatedSensorsEntity> getRelatedSensorsById_0() {
        return relatedSensorsById_0;
    }

    public void setRelatedSensorsById_0(Collection<RelatedSensorsEntity> relatedSensorsById_0) {
        this.relatedSensorsById_0 = relatedSensorsById_0;
    }


    public Collection<TopologiesEntity> getTopologiesById() {
        return topologiesById;
    }

    public void setTopologiesById(Collection<TopologiesEntity> topologiesById) {
        this.topologiesById = topologiesById;
    }
}
