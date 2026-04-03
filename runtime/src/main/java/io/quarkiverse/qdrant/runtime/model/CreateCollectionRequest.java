package io.quarkiverse.qdrant.runtime.model;

public class CreateCollectionRequest {

    private VectorsConfig vectors;

    public CreateCollectionRequest() {
    }

    public CreateCollectionRequest(int size, String distance) {
        this.vectors = new VectorsConfig(size, distance);
    }

    public VectorsConfig getVectors() {
        return vectors;
    }

    public void setVectors(VectorsConfig vectors) {
        this.vectors = vectors;
    }

    public static class VectorsConfig {

        private int size;
        private String distance;

        public VectorsConfig() {
        }

        public VectorsConfig(int size, String distance) {
            this.size = size;
            this.distance = distance;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }
    }
}
