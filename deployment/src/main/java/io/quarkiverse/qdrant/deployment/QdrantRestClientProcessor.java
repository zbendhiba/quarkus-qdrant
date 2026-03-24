package io.quarkiverse.qdrant.deployment;

import org.jboss.jandex.DotName;

import io.quarkiverse.qdrant.runtime.QdrantRestClientConfig;
import io.quarkiverse.qdrant.runtime.QdrantRestClientProducer;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.BeanDefiningAnnotationBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class QdrantRestClientProcessor {

    private static final String FEATURE = "qdrant-rest-client";
    private static final DotName QDRANT_REST_CLIENT_CONFIG = DotName.createSimple(QdrantRestClientConfig.class.getName());

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem build() {
        return AdditionalBeanBuildItem.unremovableOf(QdrantRestClientProducer.class);
    }

    @BuildStep
    void qdrantClientConfigSupport(BuildProducer<AdditionalBeanBuildItem> additionalBeans,
            BuildProducer<BeanDefiningAnnotationBuildItem> beanDefiningAnnotations) {
        // add the @QdrantRestClientConfig class otherwise it won't be registered as a qualifier
        additionalBeans.produce(AdditionalBeanBuildItem.builder().addBeanClass(QdrantRestClientConfig.class).build());

        beanDefiningAnnotations
                .produce(new BeanDefiningAnnotationBuildItem(QDRANT_REST_CLIENT_CONFIG, DotNames.APPLICATION_SCOPED, false));
    }

}
