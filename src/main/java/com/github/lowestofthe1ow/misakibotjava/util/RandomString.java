package com.github.lowestofthe1ow.misakibotjava.util;

import java.util.Random;

public class RandomString {
  /* Choose a random string from a list */
  public static String randomize(String... list) {
    return list[(int) (new Random().nextInt(list.length))];
  }
}