package model;

import javax.persistence.*;

@Entity
@Table(name = "TOPOLOGIES", schema = "SYSTEM", catalog = "")
public class TopologiesEntity {
    @Id
    @SequenceGenerator(name="top_seq",
            sequenceName="TOPOLOGY_SEQUENCE")
    @GeneratedValue(strategy=GenerationType.SEQUENCE,
            generator="top_seq")
    @Column(name = "ID", nullable = false, precision = 0)
    private long id;
    @Basic
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;
    @ManyToOne
    @JoinColumn(name = "SENSOR", referencedColumnName = "ID", nullable = false)
    private SensorEntity sensorBySensor;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


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


    public SensorEntity getSensorBySensor() {
        return sensorBySensor;
    }

    public void setSensorBySensor(SensorEntity sensorBySensor) {
        this.sensorBySensor = sensorBySensor;
    }
}
