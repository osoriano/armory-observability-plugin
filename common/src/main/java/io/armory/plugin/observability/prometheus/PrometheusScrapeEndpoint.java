/*
 * Copyright 2020 Armory, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.armory.plugin.observability.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;

/**
 * Port of PrometheusScrapeEndpoint
 *
 * <p>See:
 * https://github.com/spring-projects/spring-boot/blob/2.2.x/spring-boot-project/spring-boot-actuator/src/main/java/org/springframework/boot/actuate/metrics/export/prometheus/PrometheusScrapeEndpoint.java
 */
@WebEndpoint(id = "aop-prometheus")
public class PrometheusScrapeEndpoint {

  private final CollectorRegistry collectorRegistry;

  public PrometheusScrapeEndpoint(CollectorRegistry collectorRegistry) {
    this.collectorRegistry = collectorRegistry;
  }

  @ReadOperation(produces = TextFormat.CONTENT_TYPE_004)
  public String scrape() {
    try {
      Writer writer = new StringWriter();
      TextFormat.write004(writer, this.collectorRegistry.metricFamilySamples());
      return writer.toString();
    } catch (IOException ex) {
      // This actually never happens since StringWriter::write() doesn't throw any
      // IOException
      throw new RuntimeException("Writing metrics failed", ex);
    }
  }
}
