package controller;

import model.*;

import java.util.*;

public class ModellingController {

    private Map<SensorEntity, List<Long>> sensorWayMap = new HashMap<>();
    private List<List<SensorEntity>> wayOfSensors = new ArrayList<>();
    private DatabaseController databaseController = new DatabaseController();
    private List<RelatedSensorsEntity> relatedSensorsEntities;
    private Map<SensorEntity, Integer> reserv = new HashMap<>();
    private int[][] matrix;

    public void modelling(TopologyUtil topologyUtil) {
        List<SensorEntity> sensorEntities = databaseController.getSensorsByTopology(topologyUtil);
        relatedSensorsEntities = topologyUtil.getRelatedSensorsEntitySet();
        matrix = new int[sensorEntities.size()][sensorEntities.size()];
        Long[][] ways = new Long[sensorEntities.size()][sensorEntities.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = 0;
            }
        }
        fillMatrix(sensorEntities, matrix, ways, topologyUtil.getTopologiesEntity());
        searchWays(sensorEntities, matrix, ways);
        checkReserving(topologyUtil, matrix, sensorEntities);
        System.out.println("that's all");
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

    private void fillMatrix(List<SensorEntity> sensorEntities, int[][] matrix, Long[][] ways,
                            TopologiesEntity topologiesEntity) {
        for (SensorEntity sensorEntity : sensorEntities) {
            Map<SensorEntity, FiberEntity> fiberMap = getRelatedFiberBuSensor(sensorEntity);
            for (Map.Entry<SensorEntity, FiberEntity> entry : fiberMap.entrySet()) {
                matrix[sensorEntities.indexOf(sensorEntity)][sensorEntities.indexOf(entry.getKey())] += 1;
                matrix[sensorEntities.indexOf(entry.getKey())][sensorEntities.indexOf(sensorEntity)] += 1;
               /* if (sensorEntity.equals(topologiesEntity.getSensorBySensor())) {
                    //ways[sensorEntities.indexOf(entry.getKey())] = entry.getValue().getLength();
                    int index = putToMap(entry.getKey(), entry.getValue().getLength());
                    ways[sensorEntities.indexOf(sensorEntity)][sensorEntities.indexOf(entry.getKey())] =
                            (long) index;
                    ways[sensorEntities.indexOf(entry.getKey())][sensorEntities.indexOf(sensorEntity)] =
                            entry.getValue().getLength();
                } else {
                    ways[sensorEntities.indexOf(sensorEntity)][sensorEntities.indexOf(entry.getKey())] =
                            entry.getValue().getLength();
                    ways[sensorEntities.indexOf(entry.getKey())][sensorEntities.indexOf(sensorEntity)] =
                            entry.getValue().getLength();
                }*/
            }
        }
    }

    private void printAllPathsUtil(SensorEntity source, SensorEntity dest,
                                   Map<SensorEntity, Boolean> isVisited,
                                   Long localPathList, List<SensorEntity> sensorEntities, List<Long> pathList) {

        // Mark the current node
        if (isVisited.containsKey(source)) {
            isVisited.replace(source, true);
        } else {
            isVisited.put(source, true);
        }

        if (source.equals(dest)) {
            putToMap(dest, localPathList);
            print(pathList);
        }

        // Recur for all the vertices
        // adjacent to current vertex
        int i=0;
        for (int j : matrix[sensorEntities.indexOf(source)]) {
            if ((!isVisited.containsKey(sensorEntities.get(i)))||(!isVisited.get(sensorEntities.get(i)))) {
                // store current node
                // in path[]
                if(j!=0) {
                    localPathList += getFiberBySensors(source, sensorEntities.get(i)).getLength();
                    pathList.add(getFiberBySensors(source, sensorEntities.get(i)).getLength());
                    printAllPathsUtil(sensorEntities.get(i), dest, isVisited, localPathList, sensorEntities,
                            pathList);

                    // remove current node
                    // in path[]
                    localPathList -= getFiberBySensors(source, sensorEntities.get(i)).getLength();
                    pathList.remove(getFiberBySensors(source, sensorEntities.get(i)).getLength());
                }
            }
            i++;
        }

        // Mark the current node
        isVisited.replace(source, false);
    }

    private void print(List<Long> path){
        for(Long l:path){
            System.out.print(path+", ");
        }
        System.out.println();
    }

    private void searchWays(List<SensorEntity> sensorEntities, int[][] matrix, Long[][] ways) {
       /* int[][] value = initValue(matrix, sensorEntities.size());
        for (int i = 0; i < sensorEntities.size() * 2; i++) {
            value = rowMatrix(sensorEntities, value, matrix, ways, false);
        }*/
       for(SensorEntity sensorEntity:sensorEntities){
           Long result =0L;
           if(sensorEntities.indexOf(sensorEntity)!=0){
               printAllPathsUtil(sensorEntities.get(0), sensorEntity, new HashMap<>(),result, sensorEntities,
                       new ArrayList<>());
           }
       }
    }

    private int[][] rowMatrix(List<SensorEntity> sensorEntities, int[][] value, int[][] matrix,
                              boolean modelling) {
        int[][] result = new int[sensorEntities.size()][sensorEntities.size()];
        for (int i = 0; i < sensorEntities.size(); i++) {
            for (int j = 0; j < sensorEntities.size(); j++) {
                for (int k = 0; k < sensorEntities.size(); k++) {
                    result[i][j] += value[i][k] * matrix[k][j];
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
        if(fiberEntity.size()==0){
            relatedSensorsEntities.stream().filter(o -> ((o.getSensorBySensor1Id().equals(sensorEntity2))
                    && (o.getSensorBySensor2Id().equals(sensorEntity1)))).forEach(
                    o -> fiberEntity.add(o.getFiberByFiberId())
            );
        }
        return fiberEntity.get(0);
    }

    private FiberEntity getFiberSBySensor0(SensorEntity sensorEntity) {
        List<FiberEntity> fiberEntity = new ArrayList<>();
        relatedSensorsEntities.stream().filter(o -> (o.getSensorBySensor2Id().equals(sensorEntity))).forEach(
                o -> fiberEntity.add(o.getFiberByFiberId())
        );
        return fiberEntity.get(0);
    }

    private int putToMap(SensorEntity sensorEntity, Long way) {
        if (sensorWayMap.containsKey(sensorEntity)) {
            sensorWayMap.get(sensorEntity).add(way);
           /* List<SensorEntity> sensorEntities = new ArrayList<>();
            sensorEntities.add(sensorEntity);
            wayOfSensors.add(sensorEntities);*/
            return sensorWayMap.get(sensorEntity).indexOf(way);
        } else {
            List<Long> longs = new ArrayList<>();
            longs.add(way);
            sensorWayMap.put(sensorEntity, longs);
          /*  wayOfSensors.get(sensorWayMap.get(sensorEntity).indexOf(way)).add(sensorEntity);*/
            return sensorWayMap.get(sensorEntity).indexOf(way);
        }
    }

    private void checkReserving(TopologyUtil topologyUtil, int[][] matrix, List<SensorEntity> sensorEntities) {
        int j = 0;
        for (FiberEntity fiberEntity : databaseController.getFibersByTopology(topologyUtil)) {
            j++;
            int[][] value = initValue(matrix, sensorEntities.size());
            List<SensorEntity> remove = getSensorsByFiber(fiberEntity);
            value[sensorEntities.indexOf(remove.get(0))][sensorEntities.indexOf(remove.get(1))] = 0;
            value[sensorEntities.indexOf(remove.get(1))][sensorEntities.indexOf(remove.get(0))] = 0;
            for (int i = 0; i < sensorEntities.size() * 2; i++) {
                value = rowMatrix(sensorEntities, value, matrix, true);
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
            if(reserv.containsKey(sensorEntities.get(j))) {
                if ((matrix[0][j] != 0) && (reserv.get(sensorEntities.get(j)) != count)) {
                    reserv.replace(sensorEntities.get(j), reserv.get(sensorEntities.get(j)) + 1);
                }
            }else{
                reserv.put(sensorEntities.get(j),1);
            }
        }
    }


    public Map<SensorEntity, List<Long>> getSensorWayMap() {
        return sensorWayMap;
    }

    public void setSensorWayMap(Map<SensorEntity, List<Long>> sensorWayMap) {
        this.sensorWayMap = sensorWayMap;
    }

    public DatabaseController getDatabaseController() {
        return databaseController;
    }

    public void setDatabaseController(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }

    public List<RelatedSensorsEntity> getRelatedSensorsEntities() {
        return relatedSensorsEntities;
    }

    public void setRelatedSensorsEntities(List<RelatedSensorsEntity> relatedSensorsEntities) {
        this.relatedSensorsEntities = relatedSensorsEntities;
    }

    public Map<SensorEntity, Integer> getReserv() {
        return reserv;
    }

    public void setReserv(Map<SensorEntity, Integer> reserv) {
        this.reserv = reserv;
    }
}
