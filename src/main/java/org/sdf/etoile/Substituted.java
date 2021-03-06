/*
 * Copyright(C) 2019, 2020. See LICENSE for more.
 */
package org.sdf.etoile;

import java.util.Map;
import org.apache.spark.sql.Row;

/**
 * A {@link Transformation} with values substituted.
 * @since 0.3.0
 */
final class Substituted extends TransformationEnvelope<Row> {
    /**
     * Ctor.
     * @param input Input transformations.
     * @param dict Dictionary.
     */
    Substituted(final Transformation<Row> input,
        final Map<Type, Map<Object, Object>> dict) {
        super(
            new MappedTransformation(
                input,
                new Substitute(new SerializableOnly<>(dict).get())
            )
        );
    }
}

