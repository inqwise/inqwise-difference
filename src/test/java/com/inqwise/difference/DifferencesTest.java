package com.inqwise.difference;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.common.collect.Lists;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;

@ExtendWith(VertxExtension.class)
public class DifferencesTest {
    private static final Logger logger = LogManager.getLogger(DifferencesTest.class);

    @BeforeEach
    public void setUp(Vertx vertx) {
        // Initialization logic, if needed, can be added here.
    }

    private static class ModelBase {
        private Integer i1;
        private String s1;
        private List<SubModel> list;

        public ModelBase(Integer i1, String s1, List<SubModel> list) {
            this.i1 = i1;
            this.s1 = s1;
            this.list = list;
        }

        public Integer getI1() {
            return i1;
        }

        public void setI1(Integer i1) {
            this.i1 = i1;
        }

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }

        public List<SubModel> getList() {
            return list;
        }

        public void setList(List<SubModel> list) {
            this.list = list;
        }
    }

    public static class SubModel {
        private int i2;
        private String s2;
        private List<LocalDate> dates;

        public SubModel(int i2, String s2, List<LocalDate> dates) {
            this.i2 = i2;
            this.s2 = s2;
            this.dates = dates;
        }

        public int getI2() {
            return i2;
        }

        public void setI2(int i2) {
            this.i2 = i2;
        }

        public String getS2() {
            return s2;
        }

        public void setS2(String s2) {
            this.s2 = s2;
        }

        public List<LocalDate> getDates() {
            return dates;
        }

        public void setDates(List<LocalDate> dates) {
            this.dates = dates;
        }
    }

    @Test
    public void testModelDifferences() {
        ModelBase m1 = new ModelBase(1, "string1", Lists.newArrayList(new SubModel(2, "string2", Lists.newArrayList(LocalDate.now()))));
        ModelBase m2 = new ModelBase(1, "string1", Lists.newArrayList(new SubModel(2, "string3", Lists.newArrayList(LocalDate.now(), LocalDate.now().plusDays(1)))));

        Differences diffs = Differences.between(m1, m2, null);

        System.out.println(diffs.toString());
        Assertions.assertTrue(diffs.size() > 0);
    }

    @Test
    public void testJsonsDiff1() {
        JsonObject json1 = new JsonObject().put("a", "1").put("b", 2);
        JsonObject json2 = new JsonObject().put("a", 1).put("b", 2);

        Differences diffs = Differences.between(json1, json2, null);

        Assertions.assertTrue(diffs.size() > 0);
    }

    @Test
    public void testJsonsDiff2() {
        JsonObject json1 = new JsonObject().put("a", 1).put("b", 2);
        JsonObject json2 = new JsonObject().put("a", 1).put("b", 2);

        Differences diffs = Differences.between(json1, json2, null);

        Assertions.assertTrue(diffs.isEmpty());
    }

    @Test
    public void testSilentFields() {
        logger.debug("testSilentFields");
        JsonObject json1 = readJson("test_silent_fields1.json");
        JsonObject json2 = readJson("test_silent_fields2.json");

        logger.debug("---- diffs1 ----");
        Differences diffs1 = Differences.between(json1, json2, Arrays.asList("**.modify_date"));

        Assertions.assertTrue(diffs1.isEmpty(), diffs1.toString());

        logger.debug("---- diffs2 ----");
        Differences diffs2 = Differences.between(json1, json2, Arrays.asList("modify_date"));

        Assertions.assertFalse(diffs2.isEmpty(), diffs2.toString());
    }

    static String readFile(String relativePath) {
        try (InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(relativePath);
             Scanner scanner = new Scanner(resourceAsStream, "UTF-8")) {
            if (resourceAsStream == null) {
                throw new IllegalArgumentException("Resource not found: " + relativePath);
            }
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
			throw new AssertionError(e);
		}
    }

    static JsonObject readJson(String relativePath) {
        return new JsonObject(readFile(relativePath));
    }
}