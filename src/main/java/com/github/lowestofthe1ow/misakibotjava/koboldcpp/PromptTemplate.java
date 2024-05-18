package com.github.lowestofthe1ow.misakibotjava.koboldcpp;

abstract class PromptTemplate {
  /** The message to be placed in between tags */
  public final String message;

  abstract String build();

  PromptTemplate(String message) {
    this.message = message;
  }
}