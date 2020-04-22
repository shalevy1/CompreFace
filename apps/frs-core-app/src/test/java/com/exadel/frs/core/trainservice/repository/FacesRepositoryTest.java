package com.exadel.frs.core.trainservice.repository;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import com.exadel.frs.core.trainservice.domain.Face;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
public class FacesRepositoryTest {

    @Autowired
    private FacesRepository facesRepository;
    private final static String MODEL_KEY = "model_key";
    private final static String MODEL_KEY_OTHER = "model_key_other";

    @BeforeEach
    public void init() {
        val faceA = makeFace("A", MODEL_KEY);
        val faceB = makeFace("B", MODEL_KEY_OTHER);
        val faceC = makeFace("C", MODEL_KEY);

        facesRepository.saveAll(List.of(faceA, faceB, faceC));
    }

    @AfterEach
    public void cleanUp() {
        facesRepository.deleteAll();
    }

    public static Face makeFace(final String name, final String modelApiKey) {
        val face = new Face();
        face.setFaceName(name);
        face.setApiKey(modelApiKey);
        face.setEmbeddings(List.of(
                new Face.Embedding()
                        .setEmbedding(List.of(0.0D))
                        .setCalculatorVersion("1.0")
                )
        );
        face.setFaceImgId(new ObjectId("hex-string-1".getBytes()));
        face.setRawImgId(new ObjectId("hex-string-2".getBytes()));
        face.setId("Id_" + name);

        return face;
    }

    @Test
    public void getAll() {
        val actual = facesRepository.findAll();

        assertThat(actual).isNotNull();
        assertThat(actual).hasSize(3);
        assertThat(actual).allSatisfy(
                face -> {
                    assertThat(face.getId()).isNotEmpty();
                    assertThat(face.getFaceName()).isNotEmpty();
                    assertThat(face.getApiKey()).isNotEmpty();
                    assertThat(face.getFaceImgId()).isNotNull();
                    assertThat(face.getRawImgId()).isNotNull();
                    assertThat(face.getEmbeddings()).isNotEmpty();
                }
        );
    }

    @Test
    public void findNamesForApiGuid() {
        val expected = Arrays.asList("A", "C");
        val actual = facesRepository.findByApiKey(MODEL_KEY).stream()
                                    .map(Face::getFaceName)
                                    .collect(toList());

        assertThat(actual).isEqualTo(expected);
    }
}