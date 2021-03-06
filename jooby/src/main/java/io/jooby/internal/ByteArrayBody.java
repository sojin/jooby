/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import io.jooby.Body;
import io.jooby.Value;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ByteArrayBody implements Body {
  public static final Body EMPTY = new ByteArrayBody(new byte[0]);

  private byte[] bytes;

  public ByteArrayBody(byte[] bytes) {
    this.bytes = bytes;
  }

  @Override public long getSize() {
    return bytes.length;
  }

  @Override public byte[] bytes() {
    return bytes;
  }

  @Override public ReadableByteChannel channel() {
    return Channels.newChannel(stream());
  }

  @Override public boolean isInMemory() {
    return true;
  }

  @Override public InputStream stream() {
    return new ByteArrayInputStream(bytes);
  }

  @Nonnull @Override public String value() {
    return value(StandardCharsets.UTF_8);
  }

  @Nonnull @Override public Value get(@Nonnull int index) {
    return index == 0 ? this : get(Integer.toString(index));
  }

  @Nonnull @Override public Value get(@Nonnull String name) {
    return new MissingValue(name);
  }

  @Override public String name() {
    return "body";
  }

  @Override public Map<String, List<String>> toMultimap() {
    return Collections.emptyMap();
  }
}
