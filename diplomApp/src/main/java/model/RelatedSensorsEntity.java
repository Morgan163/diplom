package model;

import javax.persistence.*;

@Entity
@Table(name = "RELATED_SENSORS", schema = "SYSTEM", catalog = "")
public class RelatedSensorsEntity {
    @Id
    /*@SequenceGenerator(name="rel_seq",
            sequenceName="RELATED_SENSORS_SEQUENCE")*/
    @GeneratedValue(strategy=GenerationType.AUTO)/*(strategy=GenerationType.SEQUENCE,
            generator="rel_seq")*/
    @Column(name = "ID", nullable = false, precision = 0)
    private long id;
    @ManyToOne
    @JoinColumn(name = "SENSOR1_ID", referencedColumnName = "ID", nullable = false)
    private SensorEntity sensorBySensor1Id;
    @ManyToOne
    @JoinColumn(name = "FIBER_ID", referencedColumnName = "ID", nullable = false)
    private FiberEntity fiberByFiberId;
    @ManyToOne
    @JoinColumn(name = "SENSOR2_ID", referencedColumnName = "ID", nullable = false)
    private SensorEntity sensorBySensor2Id;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatedSensorsEntity that = (RelatedSensorsEntity) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    public SensorEntity getSensorBySensor1Id() {
        return sensorBySensor1Id;
    }

    public void setSensorBySensor1Id(SensorEntity sensorBySensor1Id) {
        this.sensorBySensor1Id = sensorBySensor1Id;
    }


    public FiberEntity getFiberByFiberId() {
        return fiberByFiberId;
    }

    public void setFiberByFiberId(FiberEntity fiberByFiberId) {
        this.fiberByFiberId = fiberByFiberId;
    }


    public SensorEntity getSensorBySensor2Id() {
        return sensorBySensor2Id;
    }

    public void setSensorBySensor2Id(SensorEntity sensorBySensor2Id) {
        this.sensorBySensor2Id = sensorBySensor2Id;
    }
}
