package com.github.lowestofthe1ow.misakibotjava.koboldcpp;

/*
 * The OpenHermes 2.5 Mistral 7B model uses the ChatML format for its prompts.
 * https://learn.microsoft.com/en-us/azure/ai-services/openai/how-to/chat-markup-language
 */
public class ChatML extends PromptTemplate {
  /**
   * The role name injected next to the start tag. Usually "system", "user", or "assistant" sometimes paired with the
   * tag " name=" to track sender names
   */
  private final String role;

  /**
   * Builds the ChatML-formatted string to send to the AI model. Generally results in strings such as: "<|im_start|>user
   * name=lowestofthelow\\nHello!<|im_end|>\\n"
   */
  @Override
  public String build() {
    return "<|im_start|>" + role + "\\n" + message + "<|im_end|>\\n";
  }

  ChatML(String role, String message) {
    super(message);
    this.role = role;
  }
}