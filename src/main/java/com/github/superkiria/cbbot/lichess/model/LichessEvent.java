package com.github.superkiria.cbbot.lichess.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichessEvent {

    private LichessTour tour;
    private List<LichessRound> rounds;

}
