/*
 * Copyright Amazon.com, Inc. or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.opentelemetry.javaagent.instrumentation.log4j_1_2_17;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;

public class AwsXrayLog4j1InstrumentationModule extends InstrumentationModule {
  public AwsXrayLog4j1InstrumentationModule() {
    super("Superman");
    System.out.println("Testing log4j1 Module");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    System.out.println("---------------------------------------\n");
    System.out.println("Log4j1 Type Instrumentation\n");
    System.out.println("---------------------------------------");
    return Collections.singletonList(new AwsXrayLog4j1EventInstrumentation());
  }
}
