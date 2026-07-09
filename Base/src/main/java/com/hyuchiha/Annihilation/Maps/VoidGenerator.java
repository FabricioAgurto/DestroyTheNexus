package com.hyuchiha.Annihilation.Maps;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
  @Override
  public Location getFixedSpawnLocation(World world, Random rand) {
    return new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
  }

  @Override
  public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    for (int x = 0; x < 16; x++) {
      for (int z = 0; z < 16; z++) {
        biome.setBiome(x, z, Biome.PLAINS);
      }
    }

    // Empty section array creates a void chunk in Bukkit/Spigot 1.8.8.
    return new byte[world.getMaxHeight() / 16][];
  }
}
