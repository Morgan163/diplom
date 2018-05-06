package model;

import java.util.Set;

public class TopologyUtil {
    private TopologiesEntity topologiesEntity;
    private Set<RelatedSensorsEntity> relatedSensorsEntitySet;

    public TopologyUtil() {
    }

    public TopologyUtil(TopologiesEntity topologiesEntity, Set<RelatedSensorsEntity> relatedSensorsEntitySet) {

        this.topologiesEntity = topologiesEntity;
        this.relatedSensorsEntitySet = relatedSensorsEntitySet;
    }

    public TopologiesEntity getTopologiesEntity() {
        return topologiesEntity;
    }

    public void setTopologiesEntity(TopologiesEntity topologiesEntity) {
        this.topologiesEntity = topologiesEntity;
    }

    public Set<RelatedSensorsEntity> getRelatedSensorsEntitySet() {
        return relatedSensorsEntitySet;
    }

    public void setRelatedSensorsEntitySet(Set<RelatedSensorsEntity> relatedSensorsEntitySet) {
        this.relatedSensorsEntitySet = relatedSensorsEntitySet;
    }
}
