/*
 * Copyright 2014-2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.metrics.core.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.hawkular.metrics.core.api.GaugeData;
import org.hawkular.metrics.core.api.Interval;
import org.hawkular.metrics.core.api.MetricId;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.google.common.base.Function;

/**
 * @author John Sanda
 */
public class GaugeDataMapper implements Function<ResultSet, List<GaugeData>> {

    private enum ColumnIndex {
        TIME,
        METRIC_TAGS,
        DATA_RETENTION,
        VALUE,
        TAGS,
        WRITE_TIME
    }

    private interface RowConverter {
        GaugeData getData(Row row);
    }

    private RowConverter rowConverter;

    public GaugeDataMapper() {
        this(false);
    }

    public GaugeDataMapper(boolean includeWriteTime) {
        if (includeWriteTime) {
            rowConverter = row -> new GaugeData(row.getUUID(ColumnIndex.TIME.ordinal()),
                row.getDouble(ColumnIndex.VALUE.ordinal()), getTags(row),
                row.getLong(ColumnIndex.WRITE_TIME.ordinal()) / 1000);
        } else {
            rowConverter = row -> new GaugeData(row.getUUID(ColumnIndex.TIME.ordinal()),
                row.getDouble(ColumnIndex.VALUE.ordinal()), getTags(row));
        }
    }

    @Override
    public List<GaugeData> apply(ResultSet resultSet) {
        return StreamSupport.stream(resultSet.spliterator(), false).map(row ->
            new GaugeData(row.getUUID(ColumnIndex.TIME.ordinal()), row.getDouble(ColumnIndex.VALUE.ordinal()),
                row.getMap(ColumnIndex.TAGS.ordinal(), String.class, String.class))
        ).collect(toList());
    }

    private MetricId getId(Row row) {
        return new MetricId(row.getString(1), Interval.parse(row.getString(2)));
    }

    private Map<String, String> getTags(Row row) {
        return row.getMap(8, String.class, String.class);
    }
}
