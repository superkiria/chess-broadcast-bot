package com.github.superkiria.cbbot.incoming.lichess.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichessTour {

        private String description;
        private String id;
        private String name;
        private String slug;
        private String url;
        private String markup;

}
