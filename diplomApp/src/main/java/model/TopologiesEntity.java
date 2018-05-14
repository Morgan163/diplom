package model;

import javax.persistence.*;

@Entity
@Table(name = "TOPOLOGIES", schema = "SYSTEM", catalog = "")
public class TopologiesEntity {
    private long id;
    private String name;
    private SensorEntity sensorBySensor;

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, precision = 0)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopologiesEntity that = (TopologiesEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "SENSOR", referencedColumnName = "ID", nullable = false)
    public SensorEntity getSensorBySensor() {
        return sensorBySensor;
    }

    public void setSensorBySensor(SensorEntity sensorBySensor) {
        this.sensorBySensor = sensorBySensor;
    }
}
