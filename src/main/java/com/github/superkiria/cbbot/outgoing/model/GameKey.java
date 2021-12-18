package com.github.superkiria.cbbot.outgoing.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@Builder
public class GameKey {

    private String round;
    private String white;
    private String black;

}
