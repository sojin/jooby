/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import io.jooby.TypeMismatchException;
import io.jooby.Value;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayValue implements Value {
  private final String name;

  private final List<Value> value = new ArrayList<>(5);

  public ArrayValue(String name) {
    this.name = name;
  }

  @Override public String name() {
    return name;
  }

  public ArrayValue add(Value value) {
    this.value.add(value);
    return this;
  }

  public ArrayValue add(List<String> values) {
    for (String value : values) {
      add(value);
    }
    return this;
  }

  public ArrayValue add(String value) {
    return this.add(new SingleValue(name, value));
  }

  @Override public Value get(@Nonnull int index) {
    try {
      return value.get(index);
    } catch (IndexOutOfBoundsException x) {
      return new MissingValue(name + "[" + index + "]");
    }
  }

  @Override public Value get(@Nonnull String name) {
    return new MissingValue(this.name + "." + name);
  }

  @Override public int size() {
    return value.size();
  }

  @Override public String value() {
    String name = name();
    throw new TypeMismatchException(name == null ? getClass().getSimpleName() : name, String.class);
  }

  @Override public String toString() {
    return value.toString();
  }

  @Override public Iterator<Value> iterator() {
    return value.iterator();
  }

  @Override public Map<String, List<String>> toMultimap() {
    List<String> values = new ArrayList<>();
    value.stream().forEach(it -> it.toMultimap().values().forEach(values::addAll));
    return Collections.singletonMap(name, values);
  }

  @Override public List<String> toList() {
    return fill(new ArrayList<>());
  }

  @Override public Set<String> toSet() {
    return fill(new LinkedHashSet<>());
  }

  private <C extends Collection<String>> C fill(C values) {
    value.forEach(v -> values.addAll(v.toList()));
    return values;
  }
}
