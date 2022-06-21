package com.vladmarica.stopwatch;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.forgespi.language.IModInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Represents the loading performance stats for a single mod */
public class ModLoadStats {
  private static final int MINIMUM_DURATION_MS = 2;

  private final IModInfo modInfo;
  private final Map<String, Long> eventStartTimes = new HashMap<>();
  private final Map<String, Long> eventEndTimes = new HashMap<>();

  public ModLoadStats(FMLModContainer modContainer) {
    this.modInfo = modContainer.getModInfo();
  }

  public void recordEventStartTime(Event event) {
    long currentTime = System.currentTimeMillis();
    String eventName = getEventName(event);

    if (!eventStartTimes.containsKey(eventName) && eventName != null) {
      eventStartTimes.put(eventName, currentTime);
      Stopwatch.LOGGER.trace("Event {} start for mod {}", eventName, modInfo.getModId());
    }
  }

  public void recordEventEndTime(Event event) {
    long currentTime = System.currentTimeMillis();
    String eventName = getEventName(event);
    if (eventName != null) {
      eventEndTimes.put(eventName, currentTime);
      Stopwatch.LOGGER.trace("Event {} end for mod {}", eventName, modInfo.getModId());
    }
  }

  public Summary getSummary() {
    return Summary.from(this);
  }

  private Map<String, Integer> getLoadingEventDurations() {
    final Map<String, Integer> result = new HashMap<>();
    eventStartTimes.forEach(
        (event, startTime) -> {
          if (!eventEndTimes.containsKey(event)) {
            throw new IllegalStateException("No end time for event " + event);
          }

          int duration = (int) (eventEndTimes.get(event) - startTime);
          if (duration >= MINIMUM_DURATION_MS) {
            result.put(event, duration);
          }
        });

    return ImmutableMap.copyOf(result);
  }

  /**
   * Returns a user-friendly class name for the given {@link Event}, or {@code null} if the event is
   * not to be included in profiling.
   */
  @Nullable
  private static String getEventName(Event event) {
    if (event instanceof RegistryEvent.Register) {
      return "Register (" + ((RegistryEvent.Register<?>) event).getName().toString() + ")";
    }

    if (event instanceof TextureStitchEvent.Pre) {
      return "Texture Stitch (" + ((TextureStitchEvent.Pre) event).getMap().location() + ")";
    }

    // Exclude all other events that are inner classes, such as TextureStitchEvent$Post
    if (event.getClass().getName().contains("$")) {
      return null;
    }

    return event.getClass().getSimpleName();
  }

  /** An immutable summary of a single mod's loading statistics */
  public static final class Summary implements Comparable<Summary> {
    private final IModInfo modInfo;
    private final int totalDurationMs;
    private final Map<String, Integer> eventDurationMap;

    @Nullable
    public static Summary from(ModLoadStats stats) {
      Map<String, Integer> loadingEventDurations = stats.getLoadingEventDurations();
      Optional<Integer> totalDuration =
          loadingEventDurations.values().stream().reduce(Integer::sum);
      return totalDuration
          .map(value -> new Summary(stats.modInfo, value, loadingEventDurations))
          .orElse(null);
    }

    private Summary(IModInfo modInfo, int totalDurationMs, Map<String, Integer> eventDurationMap) {
      this.modInfo = modInfo;
      this.totalDurationMs = totalDurationMs;
      this.eventDurationMap = eventDurationMap;
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder
          .append(modInfo.getDisplayName())
          .append(" (")
          .append(modInfo.getModId())
          .append("): ")
          .append(totalDurationMs)
          .append("ms\n");
      stringBuilder.append("\tEvents:\n");
      eventDurationMap.forEach(
          (eventName, duration) ->
              stringBuilder
                  .append("\t\t")
                  .append(eventName)
                  .append(": ")
                  .append(duration)
                  .append("ms\n"));
      return stringBuilder.append("\n").toString();
    }

    @Override
    public int compareTo(Summary other) {
      if (modInfo.getModId().equals(other.modInfo.getModId())) {
        return 0;
      }

      return Integer.compare(other.totalDurationMs, totalDurationMs);
    }
  }
}
