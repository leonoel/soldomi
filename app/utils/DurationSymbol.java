package utils;

public enum DurationSymbol {
    UNDEFINED           (0L, "undefined"),
    SIXTY_FOURTH        (1L, "sixty_fourth"), // Quadruple Croche
    THIRTY_SECOND       (2L, "thirty_second"), // Triple Croche
    SIXTEENTH           (4L, "sixteenth"), // Dble Croche
    EIGHTH              (8L, "eighth"), // Croche
    QUARTER            (16L, "quarter"), // Noire
    HALF               (32L, "half"), // Blanche
    WHOLE              (64L, "whole"); // Ronde

    public Long duration;
    public String baseValue;
    
    private DurationSymbol(Long _duration, String _baseValue)   {
	duration = _duration;
	baseValue = _baseValue;
    }

    public static DurationSymbol fromBaseValue(String baseValue) {
	for (DurationSymbol symbol : DurationSymbol.values()) {
	    if (symbol.baseValue.equals(baseValue)) {
		return symbol;
	    }
	}
	return null;
    }
}
