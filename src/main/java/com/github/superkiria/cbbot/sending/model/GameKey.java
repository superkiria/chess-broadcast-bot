package com.github.superkiria.cbbot.sending.model;

import lombok.Builder;
import lombok.ToString;

@ToString
@Builder
public class GameKey {

    private String round;
    private String white;
    private String black;


    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof GameKey)) return false;
        final GameKey other = (GameKey) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$round = this.round;
        final Object other$round = other.round;
        if (this$round == null ? other$round != null : !this$round.equals(other$round)) return false;
        final Object this$white = this.white;
        final Object other$white = other.white;
        if (this$white == null ? other$white != null : !this$white.equals(other$white)) return false;
        final Object this$black = this.black;
        final Object other$black = other.black;
        if (this$black == null ? other$black != null : !this$black.equals(other$black)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof GameKey;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $round = this.round;
        result = result * PRIME + ($round == null ? 43 : $round.hashCode());
        final Object $white = this.white;
        result = result * PRIME + ($white == null ? 43 : $white.hashCode());
        final Object $black = this.black;
        result = result * PRIME + ($black == null ? 43 : $black.hashCode());
        return result;
    }
}
