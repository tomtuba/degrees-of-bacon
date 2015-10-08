package com.mccomb.bacon;

import java.util.ArrayList;
import java.util.List;

public class Actor {
	private String name;
	private Long id;
	private List<Movie> movies = new ArrayList<Movie>();
	private int separation;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<Movie> getMovies() {
		return movies;
	}
	public void addMovie(Movie movie) {
		this.movies.add(movie);
	}
	public int getSeparation() {
		return separation;
	}
	public void setSeparation(int separation) {
		this.separation = separation;
	}
	
	@Override
	public String toString() {
		return name + " (" + id + "), " + separation + " levels away.";
	}
}
