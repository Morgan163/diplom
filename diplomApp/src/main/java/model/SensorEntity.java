package model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "SENSOR", schema = "SYSTEM", catalog = "")
public class SensorEntity {
    private long id;
    private Long wave;
    private Collection<RelatedSensorsEntity> relatedSensorsById;
    private Collection<RelatedSensorsEntity> relatedSensorsById_0;
    private Collection<TopologiesEntity> topologiesById;

    @Id
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "WAVE", nullable = true, precision = 0)
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

    @OneToMany(mappedBy = "sensorBySensor1Id")
    public Collection<RelatedSensorsEntity> getRelatedSensorsById() {
        return relatedSensorsById;
    }

    public void setRelatedSensorsById(Collection<RelatedSensorsEntity> relatedSensorsById) {
        this.relatedSensorsById = relatedSensorsById;
    }

    @OneToMany(mappedBy = "sensorBySensor2Id")
    public Collection<RelatedSensorsEntity> getRelatedSensorsById_0() {
        return relatedSensorsById_0;
    }

    public void setRelatedSensorsById_0(Collection<RelatedSensorsEntity> relatedSensorsById_0) {
        this.relatedSensorsById_0 = relatedSensorsById_0;
    }

    @OneToMany(mappedBy = "sensorBySensor")
    public Collection<TopologiesEntity> getTopologiesById() {
        return topologiesById;
    }

    public void setTopologiesById(Collection<TopologiesEntity> topologiesById) {
        this.topologiesById = topologiesById;
    }
}
