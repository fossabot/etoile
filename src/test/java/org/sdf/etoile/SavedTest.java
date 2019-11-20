/*
 * Copyright(C) 2019. See COPYING for more.
 */
package org.sdf.etoile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.spark.sql.Row;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link Saved}.
 *
 * @since 0.2.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
final class SavedTest extends SparkTestTemplate {
    /**
     * Writes data to provied location.
     * @throws IOException on error
     */
    @Test
    void savesInCorrectPath() throws IOException {
        final URI path = this.temp.newFolder().toURI();
        final Saved<Row> saved = new Saved<>(
            path,
            new Mode<>(
                "overwrite",
                new HeaderCsvOutput<>(
                    new FakeInput(
                        this.session,
                        "id int, val string",
                        Arrays.asList(
                            Factory.arrayOf(1, "abc"),
                            Factory.arrayOf(2, "abc")
                        )
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "returns path",
            Paths.get(saved.result()).toFile().list(),
            Matchers.not(Matchers.emptyArray())
        );
    }

    /**
     * Throws exception in case of unreachable URI.
     *
     * @throws IOException on error
     */
    @Test
    void failsForUnreachableUri() throws IOException {
        final URI path = this.temp.newFolder().toURI();
        final Saved<Row> saved = new Saved<>(
            URI.create(String.format("hdfs://%s", path.getPath())),
            new Mode<>(
                "overwrite",
                new HeaderCsvOutput<>(
                    new FakeInput(
                        this.session,
                        "id int, val string",
                        Arrays.asList(
                            Factory.arrayOf(1, "abc"),
                            Factory.arrayOf(2, "abc")
                        )
                    )
                )
            )
        );
        MatcherAssert.assertThat(
            "fails on hdfs uri",
            Assertions.assertThrows(Exception.class, saved::result),
            Matchers.hasProperty("message", Matchers.startsWith("Incomplete HDFS URI"))
        );
    }
}
