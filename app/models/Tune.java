package models;

import java.util.*;

public class Tune {
    public final Long id;
    public final String name;
    public final Date lastModified;

    public Tune(Long id,
		String name,
		Date lastModified) {
	this.id = id;
	this.name = name;
	this.lastModified = lastModified;
    }
}
