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

package io.servicecomb.swagger.generator.springmvc.processor.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.servicecomb.swagger.generator.core.OperationGenerator;
import io.swagger.models.Operation;

public class GetMappingMethodAnnotationProcessor extends AbstractHttpMethodMappingAnnotationProcessor {

  @Override
  public void process(Object annotation, OperationGenerator operationGenerator) {
    GetMapping mappingAnnotation = (GetMapping) annotation;
    Operation operation = operationGenerator.getOperation();

    // path/value是等同的
    this.processPath(mappingAnnotation.path(), operationGenerator);
    this.processPath(mappingAnnotation.value(), operationGenerator);
    this.processMethod(RequestMethod.GET, operationGenerator);
    this.processConsumes(mappingAnnotation.consumes(), operation);
    this.processProduces(mappingAnnotation.produces(), operation);

    if (StringUtils.isEmpty(operationGenerator.getHttpMethod())
        && StringUtils.isEmpty(operationGenerator.getSwaggerGenerator().getHttpMethod())) {
      throw new Error("HttpMethod must not both be empty in class and method");
    }
  }
}
