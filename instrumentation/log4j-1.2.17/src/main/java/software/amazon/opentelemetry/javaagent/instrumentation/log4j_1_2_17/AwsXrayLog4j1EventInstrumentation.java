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

import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.util.VirtualField;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner.Typing;
import net.bytebuddy.matcher.ElementMatcher;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import org.apache.log4j.spi.LoggingEvent;


public class AwsXrayLog4j1EventInstrumentation implements TypeInstrumentation {
  private static final String TRACE_ID_KEY = "AWS-XRAY-TRACE-ID";

  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return named("org.apache.log4j.spi.LoggingEvent");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
            isMethod()
                    .and(isPublic())
                    .and(named("getMDC"))
                    .and(takesArguments(1))
                    .and(takesArgument(0, String.class)),
        AwsXrayLog4j1EventInstrumentation.class.getName() + "$GetMdcAdvice");
  }

  @SuppressWarnings("unused")
  public static class GetMdcAdvice {

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void onExit(
            @Advice.This LoggingEvent event,
            @Advice.Argument(0) String key,
            @Advice.Return(readOnly = false) Object value) {
      if (TRACE_ID_KEY.equals(key)) {
        if (value != null) {
          // Assume already instrumented event if traceId/spanId/sampled is present.
          return;
        }
        value = "1234";
//        Context context = VirtualField.find(LoggingEvent.class, Context.class).get(event);
//        if (context == null) {
//          return;
//        }
//
//
//        SpanContext spanContext = Java8BytecodeBridge.spanFromContext(context).getSpanContext();
//
//        if (spanContext.isValid()) {
//          String val =
//                  "1-"
//                          + spanContext.getTraceId().substring(0, 8)
//                          + "-"
//                          + spanContext.getTraceId().substring(8)
//                          + "@"
//                          + spanContext.getSpanId();
//          value = val;
//        }

      }
    }
  }
}
