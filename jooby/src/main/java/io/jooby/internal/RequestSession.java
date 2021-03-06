/**
 * Jooby https://jooby.io
 * Apache License Version 2.0 https://jooby.io/LICENSE.txt
 * Copyright 2014 Edgar Espina
 */
package io.jooby.internal;

import io.jooby.Context;
import io.jooby.Session;
import io.jooby.SessionId;
import io.jooby.SessionOptions;
import io.jooby.Value;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Map;

public class RequestSession implements Session {

  private Context context;

  private Session session;

  public RequestSession(Context context, Session session) {
    this.context = context;
    this.session = session;
    context.attribute(Session.NAME, this);
  }

  @Nonnull @Override public String getId() {
    return session.getId();
  }

  @Nonnull @Override public Value get(@Nonnull String name) {
    return session.get(name);
  }

  @Nonnull @Override public Session put(@Nonnull String name, int value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, long value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, float value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, double value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, @Nonnull Number value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, boolean value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, @Nonnull CharSequence value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Session put(@Nonnull String name, String value) {
    session.put(name, value);
    return this;
  }

  @Nonnull @Override public Value remove(@Nonnull String name) {
    return session.remove(name);
  }

  @Nonnull @Override public Map<String, String> toMap() {
    return session.toMap();
  }

  @Nonnull @Override public Instant getCreationTime() {
    return session.getCreationTime();
  }

  @Nonnull @Override public Session setCreationTime(@Nonnull Instant creationTime) {
    session.setCreationTime(creationTime);
    return this;
  }

  @Nonnull @Override public Instant getLastAccessedTime() {
    return session.getLastAccessedTime();
  }

  @Nonnull @Override public Session setLastAccessedTime(@Nonnull Instant lastAccessedTime) {
    session.setLastAccessedTime(lastAccessedTime);
    return this;
  }

  @Override public boolean isModify() {
    return session.isModify();
  }

  @Nonnull @Override public Session setModify(boolean modify) {
    session.setModify(modify);
    return this;
  }

  @Override public boolean isNew() {
    return session.isNew();
  }

  @Nonnull @Override public Session setNew(boolean isNew) {
    session.setNew(isNew);
    return this;
  }

  public Session getSession() {
    return session;
  }

  @Override public Session clear() {
    session.clear();
    return this;
  }

  public void destroy() {
    if (context != null) {
      try {
        context.getAttributes().remove("session");
        SessionOptions options = context.getRouter().getSessionOptions();
        String sessionId = session.getId();
        options.getSessionId().deleteSessionId(context, sessionId);
        options.getStore().deleteSession(sessionId);
      } finally {
        session.destroy();
        context = null;
        session = null;
      }
    }
  }
}
