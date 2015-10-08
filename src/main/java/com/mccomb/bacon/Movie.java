package com.mccomb.bacon;

import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String title;
	private Long id;
	private List<Actor> cast = new ArrayList<Actor>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Actor> getCast() {
		return cast;
	}
	public void addCast(Actor actor) {
		this.cast.add(actor);
	}
	
	@Override
	public String toString() {
		return title + " (" + id + ")";
	}
}
