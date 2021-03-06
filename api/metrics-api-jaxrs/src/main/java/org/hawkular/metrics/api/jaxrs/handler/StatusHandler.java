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
package org.hawkular.metrics.api.jaxrs.handler;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.wordnik.swagger.annotations.ApiOperation;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.hawkular.metrics.core.api.MetricsService;

/**
 * @author mwringe
 */
@Path("/status")
@Produces(APPLICATION_JSON)
public class StatusHandler {

    public static final String PATH = "/status";

    @Inject
    private MetricsService metricsService;

    private static final String METRICSSERVICE_NAME = "MetricsService";

    @GET
    @ApiOperation(value = "Returns the current status for various components.",
                  response = String.class, responseContainer = "Map")
    public Response status() {
        Map<String, Object> status = new HashMap();

        MetricsService.State metricState = metricsService.getState();
        status.put(METRICSSERVICE_NAME, metricState.toString());

        return Response.ok(status).build();
    }
}
