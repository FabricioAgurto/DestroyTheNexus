package com.hyuchiha.Annihilation.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class BaseUtils {

  public static boolean isANewVersionSign(Block block) {
    return block != null && block.getState() instanceof Sign;
  }
}
