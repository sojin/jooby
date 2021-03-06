/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal.compiler;

import io.jooby.Body;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.channels.ReadableByteChannel;

import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Type.getMethodDescriptor;

public class BodyWriter extends ValueWriter {
  @Override
  public void accept(ClassWriter writer, String handlerInternalName, MethodVisitor visitor,
      ParamDefinition parameter)
      throws Exception {
    Method paramMethod = parameter.getObjectValue();
    visitor.visitMethodInsn(INVOKEINTERFACE, CTX.getInternalName(), paramMethod.getName(),
        getMethodDescriptor(paramMethod), true);
    if (parameter.is(byte[].class)) {
      Method bytes = Body.class.getDeclaredMethod("bytes");
      visitor.visitMethodInsn(INVOKEINTERFACE, "io/jooby/Body", bytes.getName(),
          getMethodDescriptor(bytes), true);
    } else if (parameter.is(InputStream.class)) {
      Method stream = Body.class.getDeclaredMethod("stream");
      visitor.visitMethodInsn(INVOKEINTERFACE, "io/jooby/Body", stream.getName(),
          getMethodDescriptor(stream), true);
    } else if (parameter.is(ReadableByteChannel.class)) {
      Method channel = Body.class.getDeclaredMethod("channel");
      visitor.visitMethodInsn(INVOKEINTERFACE, "io/jooby/Body", channel.getName(),
          getMethodDescriptor(channel), true);
    } else {
      super.accept(writer, handlerInternalName, visitor, parameter);
    }
  }
}
