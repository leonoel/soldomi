package utils;

import models.Symbol;
import models.Symbol.SymbolType;

public enum DurationSymbol {
    UNDEFINED           (0L,
			 null,
			 null),
    SIXTY_FOURTH        (1L,
			 SymbolType.SIXTY_FOURTH,
			 SymbolType.SIXTY_FOURTH_R), // Quadruple Croche
    THIRTY_SECOND       (2L,
			 SymbolType.THIRTY_SECOND,
			 SymbolType.THIRTY_SECOND_R), // Triple Croche
    SIXTEENTH           (4L,
			 SymbolType.SIXTEENTH,
			 SymbolType.SIXTEENTH_R), // Dble Croche
    EIGHTH              (8L,
			 SymbolType.EIGHTH,
			 SymbolType.EIGHTH_R), // Croche
    QUARTER             (16L,
			 SymbolType.QUARTER,
			 SymbolType.QUARTER_R), // Noire
    HALF                (32L,
			 SymbolType.HALF,
			 SymbolType.HALF_R), // Blanche
    WHOLE               (64L,
			 SymbolType.WHOLE,
			 SymbolType.WHOLE_R); // Ronde


    public final Long baseDuration;
    public final SymbolType noteSymbolType;
    public final SymbolType restSymbolType;
    
    private DurationSymbol(Long _baseDuration,
			   SymbolType _noteSymbolType,
			   SymbolType _restSymbolType)   {
	baseDuration = _baseDuration;
	noteSymbolType = _noteSymbolType;
	restSymbolType = _restSymbolType;
    }

    public Long duration(int dotCount) {
	Long duration = baseDuration;
	for (int i = 0; i < dotCount; i++) {
	    duration += (Long) (baseDuration / 2);
	}
	return duration;
    }

}
