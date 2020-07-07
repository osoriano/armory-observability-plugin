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

package io.armory.plugin.observability.newrelic;

import io.armory.plugin.observability.model.PluginConfig;
import io.armory.plugin.observability.model.PluginMetricsNewRelicConfig;
import io.armory.plugin.observability.registry.MeterRegistrySupplier;
import io.armory.plugin.observability.service.TagsService;
import io.micrometer.newrelic.NewRelicRegistry;
import java.util.concurrent.Executors;

/**
 * A Supplier bean that can be registered with Spring without providing an actual registry
 * implementation to confuse Spectator/Micrometer. This Supplier configures a New Relic Micrometer
 * Registry Instance.
 */
public class NewRelicRegistrySupplier implements MeterRegistrySupplier {

  private final PluginMetricsNewRelicConfig newRelicConfig;
  private final TagsService tagsService;
  private static final double ONE_MINUTE_IN_SECONDS = 60d;

  public NewRelicRegistrySupplier(PluginConfig pluginConfig, TagsService tagsService) {

    newRelicConfig = pluginConfig.getMetrics().getNewrelic();
    this.tagsService = tagsService;
  }

  @Override
  public NewRelicRegistry get() {
    if (!newRelicConfig.isEnabled()) {
      return null;
    }

    var config = new NewRelicRegistryConfig(newRelicConfig);
    var registry = new NewRelicRegistry.NewRelicRegistryBuilder(config).build();

    registry.gauge(
        "metrics.dpm",
        tagsService.getDefaultTags(),
        registry,
        reg ->
            reg.getMeters().size() * (ONE_MINUTE_IN_SECONDS / newRelicConfig.getStepInSeconds()));

    registry.start(Executors.defaultThreadFactory());
    return registry;
  }
}
