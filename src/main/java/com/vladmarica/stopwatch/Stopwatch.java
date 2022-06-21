package com.vladmarica.stopwatch;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.ExtensionPoint;

@Mod(Stopwatch.MOD_ID)
public class Stopwatch {
  public static final String MOD_ID = "stopwatch";
  public static final Logger LOGGER = LogManager.getLogger();

  public Stopwatch() {
    ModLoadingContext.get()
        .registerExtensionPoint(
            ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
  }
}
