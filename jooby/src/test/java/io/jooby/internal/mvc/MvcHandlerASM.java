package io.jooby.internal.mvc;

import io.jooby.Context;
import io.jooby.Extension;
import io.jooby.FileUpload;
import io.jooby.Jooby;
import io.jooby.MediaType;
import io.jooby.ProvisioningException;
import io.jooby.QueryString;
import io.jooby.Reified;
import io.jooby.Route;
import io.jooby.Session;
import io.jooby.StatusCode;
import io.jooby.annotations.GET;
import io.jooby.annotations.POST;
import io.jooby.annotations.Path;
import io.jooby.annotations.PathParam;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QPoint {
}

class Gen<A, B, C> {
}

class Poc {

  @POST
  @Path(("/body/json"))
  public String getIt(Gen<String, Integer, Void> body) {
      return body.toString();
  }

  @GET
  @Path("/{s}/{i}/{j}/{f}/{d}/{b}")
  public String mix(@PathParam String s, @PathParam Integer i, @PathParam double d, Context ctx,
      @PathParam long j, @PathParam double f, @PathParam boolean b) {
    return ctx.pathString();
  }
}

class MvcHandlerImpl implements MvcHandler {

  private Provider<Poc> provider;

  public MvcHandlerImpl(Provider<Poc> provider) {
    this.provider = provider;
  }

  //  private double tryParam0(Context ctx, String desc) {
  //    try {
  //      return ctx.path("l").doubleValue();
  //    } catch (ProvisioningException x) {
  //      throw x;
  //    } catch (Exception x) {
  //      throw new ProvisioningException(desc, x);
  //    }
  //  }
  //
  public final Object[] arguments(Context ctx) {
    return null;
  }

  @Nonnull @Override public Object apply(@Nonnull Context ctx) throws Exception {
    return provider.get().getIt(ctx.body().to(
        (Type) Reified.getParameterized(Gen.class, String.class, Integer.class, Void.class)));
  }
}

class MvcModule implements Extension {

  private Provider<Poc> provider;

  public MvcModule(Provider<Poc> provider) {
    this.provider = provider;
  }

  @Override public void install(@Nonnull Jooby app) throws Exception {
    Route route = app.get("/path", new MvcHandlerImpl(provider));
    route.setReturnType(String.class);
    route.setConsumes(Arrays.asList(MediaType.valueOf("application/json")));
  }
}

public class MvcHandlerASM {

  @Test
  public void compare() throws IOException, NoSuchMethodException, ClassNotFoundException {
    // Lio/jooby/SneakyThrows$Supplier<Lio/jooby/mvc/NoTopLevelPath;>;
    //    ASMifier.main(new String[] {"-debug",MvcHandler.class.getName()});
    //public String mix(@PathParam String s, @PathParam Integer i, @PathParam double d, Context ctx,
    //      @PathParam long j, @PathParam double f, @PathParam boolean b) {
    Method handler = Poc.class.getDeclaredMethod("getIt", Gen.class);
    Class runtime = MvcCompiler.compileClass(mvc(handler));

    System.out.println("Loaded: " + runtime);
    //    byte[] asm = writer.toByteCode(classname, handler);

    assertEquals(asmifier(new ClassReader(MvcModule.class.getName())),
        asmifier(new ClassReader(MvcCompiler.compile(mvc(handler)))));

  }

  private MvcMethod mvc(Method handler) {
    MvcMethod result = new MvcMethod();
    result.method = handler;
    result.setModel(new JoobyAnnotationParser().parse(handler).get(0));
    return result;
  }

  private String asmifier(ClassReader reader) throws IOException {
    ByteArrayOutputStream buff = new ByteArrayOutputStream();
    Printer printer = new ASMifier();
    TraceClassVisitor traceClassVisitor =
        new TraceClassVisitor(null, printer, new PrintWriter(buff));

    reader.accept(traceClassVisitor, ClassReader.SKIP_DEBUG);

    return new String(buff.toByteArray());
  }

  private String listOf(org.objectweb.asm.Type owner) {
    StringBuilder signature = new StringBuilder(
        org.objectweb.asm.Type.getType(List.class).getDescriptor());
    signature.insert(signature.length() - 1, "<" + owner.getDescriptor() + ">");
    return signature.toString();
  }
}
