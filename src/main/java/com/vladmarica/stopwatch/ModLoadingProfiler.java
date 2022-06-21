package com.vladmarica.stopwatch;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ModLoadingProfiler {
  private static final String LOG_FILE_NAME = "stopwatch-mod-loading-summary.txt";
  private static final Object lock = new Object();

  @GuardedBy("lock")
  private static Map<String, ModLoadStats> modLoadStatsMap = new HashMap<>();

  public static void onModEventStart(FMLModContainer modContainer, Event event) {
    synchronized (lock) {
      ModLoadStats stats = modLoadStatsMap.get(modContainer.getModId());
      if (stats == null) {
        stats = new ModLoadStats(modContainer);
        modLoadStatsMap.put(modContainer.getModId(), stats);
      }

      stats.recordEventStartTime(event);
    }
  }

  public static void onModEventEnd(FMLModContainer modContainer, Event event) {
    synchronized (lock) {
      ModLoadStats stats = modLoadStatsMap.get(modContainer.getModId());
      stats.recordEventEndTime(event);
    }
  }

  @Nullable
  public static ModLoadStats.Summary getSummary(String modId) {
    synchronized (lock) {
      if (!modLoadStatsMap.containsKey(modId)) {
        return null;
      }

      ModLoadStats stats = modLoadStatsMap.get(modId);
      return stats.getSummary();
    }
  }

  public static void onComplete() {
    List<ModLoadStats.Summary> summaries =
        ModList.get().getMods().stream()
            .map(modInfo -> getSummary(modInfo.getModId()))
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());
    writeSummariesToFile(summaries);
  }

  private static void writeSummariesToFile(List<ModLoadStats.Summary> summaries) {
    try {
      File file = new File(LOG_FILE_NAME);
      if (file.exists()) {
        file.delete();
        file.createNewFile();
      }

      PrintWriter writer = new PrintWriter(file);
      summaries.forEach(summary -> writer.write(summary.toString()));
      writer.close();
      Stopwatch.LOGGER.info("Successfully wrote mod loading summary to {}", LOG_FILE_NAME);
    } catch (IOException ex) {
      Stopwatch.LOGGER.error("Failed to write mod loading summary to disk", ex);
    }
  }
}
