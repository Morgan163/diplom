package controller;

import model.*;

import java.util.*;

public class ModellingController {

    private Map<SensorEntity, List<Long>> sensorWayMap = new HashMap<>();
    private DatabaseController databaseController = new DatabaseController();
    private List<RelatedSensorsEntity> relatedSensorsEntities;
    private Map<SensorEntity, Integer> reserv = new HashMap<>();

    public void modelling(TopologyUtil topologyUtil) {
        List<SensorEntity> sensorEntities = databaseController.getSensorsByTopology(topologyUtil);
        relatedSensorsEntities = topologyUtil.getRelatedSensorsEntitySet();
        int[][] matrix = new int[sensorEntities.size()][sensorEntities.size()];
        Long[] ways = new Long[sensorEntities.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = 0;
            }
        }
        fillMatrix(sensorEntities, matrix, ways, topologyUtil.getTopologiesEntity());
        searchWays(sensorEntities, matrix, ways);
        checkReserving(topologyUtil, matrix, sensorEntities);

    }

    public Map<SensorEntity, FiberEntity> getRelatedFiberBuSensor(SensorEntity sensorEntity) {
        Map<SensorEntity, FiberEntity> fiberMap = new HashMap<>();
        relatedSensorsEntities.stream().filter(o -> o.getSensorBySensor1Id().equals(sensorEntity)).forEach(
                o -> fiberMap.put(o.getSensorBySensor2Id(), o.getFiberByFiberId())
        );
        return fiberMap;
    }

    public List<SensorEntity> getSensorsByFiber(FiberEntity fiberEntity) {
        List<SensorEntity> sensorEntities = new ArrayList<>();
        relatedSensorsEntities.stream().filter(o -> o.getFiberByFiberId().equals(fiberEntity)).forEach(
                o -> {
                    sensorEntities.add(o.getSensorBySensor1Id());
                    sensorEntities.add(o.getSensorBySensor2Id());
                }
        );
        return sensorEntities;
    }

    private void fillMatrix(List<SensorEntity> sensorEntities, int[][] matrix, Long[] ways,
                            TopologiesEntity topologiesEntity) {
        for (SensorEntity sensorEntity : sensorEntities) {
            Map<SensorEntity, FiberEntity> fiberMap = getRelatedFiberBuSensor(sensorEntity);
            for (Map.Entry<SensorEntity, FiberEntity> entry : fiberMap.entrySet()) {
                matrix[sensorEntities.indexOf(sensorEntity)][sensorEntities.indexOf(entry.getKey())] += 1;
                if (sensorEntity.equals(topologiesEntity.getSensorBySensor())) {
                    //ways[sensorEntities.indexOf(entry.getKey())] = entry.getValue().getLength();
                    putToMap(entry.getKey(), entry.getValue().getLength());
                }
            }
        }
    }

    private void searchWays(List<SensorEntity> sensorEntities, int[][] matrix, Long[] ways) {
        int[][] value = initValue(matrix, sensorEntities.size());
        for (int i = 0; i < sensorEntities.size() * 2; i++) {
            value = rowMatrix(sensorEntities, value, false);
        }
    }

    private int[][] rowMatrix(List<SensorEntity> sensorEntities, int[][] matrix, boolean modelling) {
        int[][] result = new int[sensorEntities.size()][sensorEntities.size()];
        for (int i = 0; i < sensorEntities.size(); i++) {
            for (int j = 0; j < sensorEntities.size(); j++) {
                Long res = 0L;
                for (int k = 0; k < sensorEntities.size(); k++) {
                    result[i][j] += matrix[i][k] * matrix[k][j];
                    if ((i == 0) && (matrix[i][k] != 0) && (matrix[k][j] != 0) && (!modelling)) {
                        res += getFiberBySensors(sensorEntities.get(i), sensorEntities.get(k)).getLength() *
                                getFiberBySensors(sensorEntities.get(k), sensorEntities.get(j)).getLength();
                    }
                }
                if ((i == 0) && (result[i][j] != 0) && (!modelling)) {
                    putToMap(sensorEntities.get(j), res);
                }
            }
        }
        return result;
    }

    private FiberEntity getFiberBySensors(SensorEntity sensorEntity1, SensorEntity sensorEntity2) {
        List<FiberEntity> fiberEntity = new ArrayList<>();
        relatedSensorsEntities.stream().filter(o -> ((o.getSensorBySensor2Id().equals(sensorEntity2))
                && (o.getSensorBySensor1Id().equals(sensorEntity1)))).forEach(
                o -> fiberEntity.add(o.getFiberByFiberId())
        );
        return fiberEntity.get(0);
    }

    private void putToMap(SensorEntity sensorEntity, Long way) {
        if (sensorWayMap.containsKey(sensorEntity)) {
            sensorWayMap.get(sensorEntity).add(way);
        } else {
            List<Long> longs = new ArrayList<>();
            longs.add(way);
            sensorWayMap.put(sensorEntity, longs);
        }
    }

    private void checkReserving(TopologyUtil topologyUtil, int[][] matrix, List<SensorEntity> sensorEntities) {
        int j = 0;
        for (FiberEntity fiberEntity : databaseController.getFibersByTopology(topologyUtil)) {
            j++;
            int[][] value = initValue(matrix, sensorEntities.size());
            List<SensorEntity> remove = getSensorsByFiber(fiberEntity);
            value[sensorEntities.indexOf(remove.get(0))][sensorEntities.indexOf(remove.get(1))] = 0;
            for (int i = 0; i < sensorEntities.size() * 2; i++) {
                value = rowMatrix(sensorEntities, value, true);
            }
            checkMatrix(value, sensorEntities, j);
        }
    }

    private int[][] initValue(int[][] matrix, int size) {
        int[][] value = new int[size][size];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                value[i][j] = matrix[i][j];
            }
        }
        return value;
    }

    private void checkMatrix(int[][] matrix, List<SensorEntity> sensorEntities, int count) {
        for (int j = 0; j < matrix[0].length; j++) {
            if ((matrix[0][j] != 0) && (reserv.get(sensorEntities.get(j)) != count)) {
                reserv.replace(sensorEntities.get(j), reserv.get(sensorEntities.get(j)) + 1);
            }
        }
    }

}
