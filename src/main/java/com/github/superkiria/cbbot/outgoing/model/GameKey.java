package com.github.superkiria.cbbot.outgoing.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Builder
public class GameKey {

    private String round;
    private String white;
    private String black;

}
