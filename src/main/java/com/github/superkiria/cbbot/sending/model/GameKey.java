package com.github.superkiria.cbbot.sending.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@Builder
@EqualsAndHashCode
public class GameKey {

    private String round;
    private String white;
    private String black;

}
