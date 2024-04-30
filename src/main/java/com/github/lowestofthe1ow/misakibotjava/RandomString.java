package com.github.lowestofthe1ow.misakibotjava;

import java.util.Random;

public class RandomString {
  public static String randomize(String... list) {
    return list[(int) (new Random().nextInt(list.length))];
  }
}