/*
 * Copyright 2022 Armory, Inc.
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

import io.armory.plugin.observability.model.PluginConfig;
import io.armory.plugin.observability.model.SecurityConfig;
import io.prometheus.client.exporter.common.TextFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
      PrometheusScrapeEndpointIntegrationTest.TestSpringApplication.class,
      PrometheusScrapeEndpoint.class,
      SecurityConfig.class,
      PluginConfig.class
    },
    properties = {
      "management.endpoints.web.exposure.include=*",
      "spinnaker.extensibility.plugins.armory.observability-plugin.config.metrics.prometheus.enabled=true"
    })
@AutoConfigureMockMvc
public class PrometheusScrapeEndpointIntegrationTest {

  @Autowired private MockMvc mock;

  @Test
  public void testRequest() throws Exception {
    mock.perform(MockMvcRequestBuilders.get("/aop-prometheus"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(TextFormat.CONTENT_TYPE_004))
        .andReturn();
  }

  @Test
  public void testRequestWithAcceptTextPlain() throws Exception {
    mock.perform(MockMvcRequestBuilders.get("/aop-prometheus").accept(MediaType.TEXT_PLAIN))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
        .andReturn();
  }

  @Test
  public void testRequestWithAcceptTextPlain004() throws Exception {
    mock.perform(MockMvcRequestBuilders.get("/aop-prometheus").accept(TextFormat.CONTENT_TYPE_004))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.content().contentTypeCompatibleWith(TextFormat.CONTENT_TYPE_004))
        .andReturn();
  }

  @SpringBootApplication
  public static class TestSpringApplication {}
}
