/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.foundation.metrics.output.servo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.config.DynamicPropertyFactory;

import io.servicecomb.foundation.common.net.NetUtils;
import io.servicecomb.foundation.common.utils.JsonUtils;

@Component
public class SimpleMetricsContentFormatter implements MetricsContentFormatter {

  public static final String METRICS_FILE_NAME_PREFIX = "servicecomb.metrics.file.name_prefix";

  private static final Logger logger = LoggerFactory.getLogger(SimpleMetricsContentFormatter.class);

  private final String applicationName;

  private String hostName;

  public SimpleMetricsContentFormatter() {
    hostName = NetUtils.getHostName();
    if (StringUtils.isEmpty(hostName)) {
      hostName = NetUtils.getHostAddress();
    }

    applicationName = DynamicPropertyFactory.getInstance().getStringProperty(METRICS_FILE_NAME_PREFIX, "metrics").get();
  }

  @Override
  public Map<String, String> format(Map<String, String> input) {
    return input.entrySet().stream()
        .collect(Collectors.toMap(Entry::getKey, entry -> {
          try {
            return JsonUtils
                .writeValueAsString(new OutputJsonObject(applicationName, hostName, entry.getKey(), entry.getValue()));
          } catch (JsonProcessingException e) {
            logger.error("error convert metrics data to json convert", e);
            return "";
          }
        }));
  }

  class OutputJsonObject {
    private String plugin_id;

    private Map<String, Object> metric;

    public String getPlugin_id() {
      return plugin_id;
    }

    public Map<String, Object> getMetric() {
      return metric;
    }

    public OutputJsonObject() {
    }

    public OutputJsonObject(String plugin_id, String hostName, String metricName, String metricValue) {
      this();
      this.plugin_id = plugin_id;
      this.metric = new HashMap<>();
      this.metric.put("node", hostName);
      this.metric.put("scope_name", "");
      this.metric.put("timestamp", System.currentTimeMillis());
      this.metric.put("inface_name", "");
      this.metric.put(metricName, metricValue);
    }
  }
}
