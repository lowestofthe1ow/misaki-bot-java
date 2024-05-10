package com.github.lowestofthe1ow.misakibotjava;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.util.concurrent.CompletableFuture;

public class KoboldCPPGenerateRequest {
  /*
   * Ported from Genesis 13B Preset (https://github.com/KoboldAI/KoboldAI-Client/wiki/Settings-Presets) Stable and
   * logical, but with scattered creativity. Preset created in NovelAI for the Euterpe model. Suitable for KoboldAI
   * models trained on Fairseq Dense (Janeway 13B, Shinen 13B, Fairseq Dense 13B). See request body schema at
   * /api/v1/generate
   * 
   * Temperature: 0.63 / Output Length: 50 / Top K: Off (0) / Top P: 0.98 / Top A: Off (0.0) / Typical: Off (1.0)
   * Tail-Free: 0.98 / Repetition Penalty: 1.05 (KAI) / Repetition Penalty Range: 2048 / Repetition Penalty Slope: 0.1
   */
  public final int max_context_length = 2048;
  public final int max_length = 1024;
  public final Boolean quiet = true;

  public final float rep_pen = (float) 1.05;
  public final int rep_pen_range = 2048;
  public final float rep_pen_slope = (float) 0.1;
  public final int presence_penalty = 0;

  /*
   * The order by which all 7 samplers are applied, separated by commas
   * 
   * 0: top_k / 1: top_a / 2: top_p / 3: tfs / 4: typ / 5: temp / 6: rep_pen
   */
  public final int[] sampler_order = new int[] {
      6, 2, 0, 3, 5, 1, 4
  };

  /*
   * An array of string sequences where the API will stop generating further tokens. The returned text WILL contain the
   * stop sequence (must be removed manually). The sequences below are based on the OpenML format used by OpenHermes 2.5
   * Mistral 7B
   */
  public final String[] stop_sequence = new String[] {
      "<|im_end|>\n<|im_start|>user", "<|im_end|>\n<|im_start|>assistant", "<|im_end|>"
  };

  public final float temperature = (float) 0.63;

  /*
   * These settings control alternative samplers configurations. They are inactive by default, you usually do not need
   * to change them.
   */
  public final float tfs = (float) 0.98;
  public final int top_a = 0; /* OFF */
  public final int top_k = 0; /* OFF */
  public final float top_p = (float) 0.98;
  public final int min_p = 0;
  public final int typical = 1; /* OFF */

  /*
   * An array of string sequences to remove from model vocab. All matching tokens with matching substrings are removed.
   */
  public final String[] banned_tokens = new String[] {};

  public String memory;
  public String prompt;

  public CompletableFuture<HttpResponse<String>> generate() throws Exception {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI("http://localhost:5001/api/v1/generate/"))
        .headers("Content-Type", "text/plain;charset=UTF-8")
        .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(this))).build();
    HttpClient client = HttpClient.newBuilder().proxy(ProxySelector.getDefault()).build();

    return client.sendAsync(request, BodyHandlers.ofString());
  }

  KoboldCPPGenerateRequest(String memory, String prompt) {
    this.memory = memory;
    this.prompt = prompt;
  }

  /*
   * The following fields are left as their default value as per KoboldCPP's API
   * 
   * public final int sampler_seed; // (Uses global RNG) public final Boolean use_default_badwordsids = false; public
   * final float dynatemp_range = 0; public final float smoothing_factor = 0; public final float dynatemp_exponent = 1;
   * public final float mirostat; public final float mirostat_tau; public final float mirostat_eta; public final String
   * genkey = "KCPP7895"; public final String grammar; public final Boolean grammar_retain_state; public final String[]
   * images; // Base64 encoded public final Boolean trim_stop = false; public final Boolean render_special = false;
   * public final HashMap<Integer, Float> logit_bias = new HashMap<Integer, Float>();
   */
}