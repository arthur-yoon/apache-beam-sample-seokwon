package com.seokwon.common.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlackChannel {
    /**
     * Beam Alert
     */
    BEAM_ALERT("https://hooks.slack.com/services/[slack url]", "#beam", "beam");

    String webHookUrl;
    String channel;
    String descr;
}
