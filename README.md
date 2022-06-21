# Stopwatch - Forge Mod Loading Profiler

A lightweight Minecraft Forge coremod to measure and profile the loading times of mods.

After all mods load, the loading time summaries are written to `stopwatch-mod-loading-summary.txt` in your
`.minecraft` folder. In the future, these results will be available in an in-game GUI.

> Mods and states that take 1ms or less to load are excluded from the results. 

> Mods can load in parallel, so the sum of mod loading times may be less than total loading times.

### Example Results
```
Tinkers' Construct (tconstruct): 5383ms
	Events:
		FMLCommonSetupEvent: 215ms
		Register (minecraft:block): 291ms
		Register (minecraft:recipe_serializer): 76ms
		Register (tconstruct:modifiers): 26ms
		Register (minecraft:entity_type): 3ms
		ParticleFactoryRegisterEvent: 5ms
		Register (minecraft:worldgen/block_state_provider_type): 2ms
		Texture Stitch (minecraft:textures/atlas/blocks.png): 4621ms
		Register (minecraft:block_entity_type): 2ms
		ModelRegistryEvent: 21ms
		Register (minecraft:worldgen/feature): 6ms
		FMLClientSetupEvent: 54ms
		Register (minecraft:particle_type): 2ms
		Register (minecraft:item): 59ms

Create (create): 2213ms
	Events:
		FMLCommonSetupEvent: 690ms
		Register (minecraft:menu): 18ms
		Register (minecraft:block): 225ms
		Register (minecraft:recipe_serializer): 23ms
		FMLLoadCompleteEvent: 2ms
		ParticleFactoryRegisterEvent: 10ms
		Texture Stitch (minecraft:textures/atlas/blocks.png): 2ms
		Register (minecraft:block_entity_type): 2ms
		ModelRegistryEvent: 4ms
		Register (minecraft:worldgen/decorator): 3ms
		Register (minecraft:worldgen/feature): 2ms
		FMLClientSetupEvent: 1091ms
		GatherContextEvent: 75ms
		Register (minecraft:particle_type): 26ms
		Register (minecraft:item): 40ms
		
...
```
