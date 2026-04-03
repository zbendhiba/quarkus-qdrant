package io.quarkiverse.qdrant.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkiverse.qdrant.runtime.QdrantRestClientApi;
import io.quarkiverse.qdrant.runtime.model.DeleteRequest;
import io.quarkiverse.qdrant.runtime.model.PointStruct;
import io.quarkiverse.qdrant.runtime.model.ScoredPoint;
import io.quarkiverse.qdrant.runtime.model.SearchRequest;
import io.quarkiverse.qdrant.runtime.model.SearchResponse;
import io.quarkiverse.qdrant.runtime.model.UpsertRequest;

@ApplicationScoped
public class DocumentService {

    private static final String COLLECTION = "documents";

    @Inject
    QdrantRestClientApi qdrant;

    public String index(Document document) {
        if (document.id == null) {
            document.id = UUID.randomUUID().toString();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("text", document.text);
        payload.put("source", document.source);

        // Fake vector for testing the client — in production, embeddings come from a model (e.g. via LangChain4j)
        List<Float> vector = fakeVector(document.text);

        PointStruct point = new PointStruct(document.id, vector, payload);
        qdrant.upsert(COLLECTION, new UpsertRequest(List.of(point)));

        return document.id;
    }

    public List<Document> search(float[] queryVector) {
        SearchRequest request = new SearchRequest();
        request.setVector(queryVector);
        request.setLimit(10);
        request.setWithPayload(true);
        request.setWithVector(false);

        SearchResponse response = qdrant.search(COLLECTION, request);

        List<Document> documents = new ArrayList<>();
        if (response.getResult() != null) {
            for (ScoredPoint point : response.getResult()) {
                Document doc = new Document();
                doc.id = point.getId();
                if (point.getPayload() != null) {
                    doc.text = (String) point.getPayload().get("text");
                    doc.source = (String) point.getPayload().get("source");
                }
                documents.add(doc);
            }
        }
        return documents;
    }

    public void delete(String id) {
        qdrant.delete(COLLECTION, DeleteRequest.byIds(List.of(id)));
    }

    /**
     * Generates a simple deterministic 4-dimension vector from text.
     * This is a fake — real embeddings would come from an embedding model.
     */
    static List<Float> fakeVector(String text) {
        int h = text.hashCode();
        return List.of(
                (h & 0xFF) / 255.0f,
                ((h >> 8) & 0xFF) / 255.0f,
                ((h >> 16) & 0xFF) / 255.0f,
                ((h >> 24) & 0xFF) / 255.0f);
    }
}
