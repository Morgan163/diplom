package model;

import java.util.List;
import java.util.Set;

public class TopologyUtil {
    private TopologiesEntity topologiesEntity;
    private List<RelatedSensorsEntity> relatedSensorsEntitySet;

    public TopologyUtil() {
    }

    public TopologyUtil(TopologiesEntity topologiesEntity, List<RelatedSensorsEntity> relatedSensorsEntitySet) {

        this.topologiesEntity = topologiesEntity;
        this.relatedSensorsEntitySet = relatedSensorsEntitySet;
    }

    public TopologiesEntity getTopologiesEntity() {
        return topologiesEntity;
    }

    public void setTopologiesEntity(TopologiesEntity topologiesEntity) {
        this.topologiesEntity = topologiesEntity;
    }

    public List<RelatedSensorsEntity> getRelatedSensorsEntitySet() {
        return relatedSensorsEntitySet;
    }

    public void setRelatedSensorsEntitySet(List<RelatedSensorsEntity> relatedSensorsEntitySet) {
        this.relatedSensorsEntitySet = relatedSensorsEntitySet;
    }
}
