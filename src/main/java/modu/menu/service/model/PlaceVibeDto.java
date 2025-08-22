package modu.menu.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import modu.menu.domain.VibeType;

@Getter
@AllArgsConstructor
public class PlaceVibeDto {

    private Long placeId;
    private VibeType vibeType;
}
