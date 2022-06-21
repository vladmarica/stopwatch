package com.vladmarica.stopwatch.coremod;

import com.vladmarica.stopwatch.ModLoadingProfiler;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import java.util.concurrent.atomic.AtomicBoolean;

/** Class for receiving method calls from the injected ASM bytecode in {@link FMLModContainer}. */
public final class CoreModHooks {
  private static final AtomicBoolean finished = new AtomicBoolean(false);

  /** Fired from ASM whenever a mod enters the given event state */
  public static void onModEventStart(FMLModContainer container, Event event) {
    if (!finished.get()) {
      ModLoadingProfiler.onModEventStart(container, event);
    }
  }

  /** Fired from ASM whenever a mod finishes handling the given event */
  public static void onModEventEnd(FMLModContainer container, Event event) {
    if (!finished.get()) {
      ModLoadingProfiler.onModEventEnd(container, event);
    }
  }

  /** Fired from ASM when all mods finish loading */
  public static void onLoadingComplete() {
    finished.compareAndSet(false, true);
    ModLoadingProfiler.onComplete();
  }
}
