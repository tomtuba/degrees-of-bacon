package com.mccomb.bacon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Bacon {

	public static final String API_KEY = "40d37b35939b61d06f4357da96d11978";
	public static final String URL_BASE = "http://api.tmdb.org/3";
	public static final String SEARCH_PARAM = "/search";
	public static final String DISCOVER_PARAM = "/discover";
	public static final String MOVIE_PARAM = "/movie?with_cast=";
	public static final String MOVIE_CAST_PARAM = "/movie/";
	public static final String CAST_PARAM = "/casts";

	public static final String NAME_PARAM = "/person?query=";
	public static final String PAGE_PARAM = "&page=";
	public static final String API_PARAM = "&api_key=";

	public static void main(String[] args) {
		// String id = getActorId("John Cleese");
		System.out.println(getActorFromFile());

		for (Movie movie : getMoviesFromFile()) {
			System.out.println(movie);
		}
		
		for (Actor actor: getActorsFromFile()) {
			System.out.println(actor);
		}
	}

	static Actor getActor(String actorName) {
		StringWriter sw = new StringWriter();

		try {
			String url = URL_BASE + SEARCH_PARAM + NAME_PARAM + actorName.replace(" ", "%20") + API_PARAM + API_KEY;
			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			PrintWriter pw = new PrintWriter(sw);
			for (String inputLine = in.readLine(); (inputLine = in.readLine()) != null; inputLine = in.readLine()) {
				pw.println(inputLine);
			}
			in.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return parseActor(sw.toString());
	}

	static List<Movie> getMoviesForActor(Long actorId) {
		StringWriter sw = new StringWriter();

		List<Movie> answer = new ArrayList<Movie>();
		int page = 1;

		try {
			do {
				sw = new StringWriter();

				String url = URL_BASE + DISCOVER_PARAM + MOVIE_PARAM + actorId + PAGE_PARAM + page + API_PARAM
						+ API_KEY;
				URL oracle = new URL(url);
				URLConnection yc = oracle.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
				PrintWriter pw = new PrintWriter(sw);
				for (String inputLine = in.readLine(); (inputLine = in.readLine()) != null; inputLine = in.readLine()) {
					pw.println(inputLine);
				}
				in.close();
				pw.close();
				answer.addAll(getMovies(sw.toString()));
			} while (!isLastPage(sw.toString()));
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return answer;
	}

	static List<Actor> getActorsForMovie(Movie movie) {
		// http://api.tmdb.org/3/movie/10200/casts?api_key=40d37b35939b61d06f4357da96d11978
		String url = URL_BASE + MOVIE_CAST_PARAM + movie.getId() + CAST_PARAM + API_PARAM + API_KEY;

		try {
			StringWriter sw = new StringWriter();

			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			PrintWriter pw = new PrintWriter(sw);
			for (String inputLine = in.readLine(); (inputLine = in.readLine()) != null; inputLine = in.readLine()) {
				pw.println(inputLine);
			}
			in.close();
			pw.close();
			return getActors(sw.toString());

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return null;
	}

	static List<Actor> getActors(String json) {
		List<Actor> answer = new ArrayList<Actor>();

		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(json);

			JSONArray results = (JSONArray) obj.get("cast");

			for (int x = 0; x < results.size(); x++) {

				JSONObject idObj = (JSONObject) results.get(x);

				if (idObj.get("character") != null) {
					Actor actor = new Actor();

					actor.setId((Long) idObj.get("id"));
					actor.setName((String) idObj.get("name"));
					answer.add(actor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return answer;
	}

	static Actor getActorFromFile() {
		StringWriter sw = new StringWriter();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/main/resources/actor.txt"));
			PrintWriter pw = new PrintWriter(sw);
			for (String inputLine = in.readLine(); inputLine != null; inputLine = in.readLine()) {
				pw.println(inputLine);
			}
			in.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return parseActor(sw.toString());
	}
	
	static List<Actor> getActorsFromFile() {
		StringWriter sw = new StringWriter();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/main/resources/cast.txt"));
			PrintWriter pw = new PrintWriter(sw);
			for (String inputLine = in.readLine(); inputLine != null; inputLine = in.readLine()) {
				pw.println(inputLine);
			}
			in.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return getActors(sw.toString());
	}

	static List<Movie> getMoviesFromFile() {
		StringWriter sw = new StringWriter();
		try {
			BufferedReader in = new BufferedReader(new FileReader("src/main/resources/movie-list.txt"));
			PrintWriter pw = new PrintWriter(sw);
			for (String inputLine = in.readLine(); inputLine != null; inputLine = in.readLine()) {
				pw.println(inputLine);
			}
			in.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return getMovies(sw.toString());
	}

	static boolean isLastPage(String json) {
		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(json);

			Long page = (Long) obj.get("page");

			Long pageCount = (Long) obj.get("total_pages");

			return page >= pageCount;

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return true;
	}

	static List<Movie> getMovies(String json) {
		List<Movie> answer = new ArrayList<Movie>();

		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(json);

			JSONArray results = (JSONArray) obj.get("results");

			for (int x = 0; x < results.size(); x++) {

				JSONObject idObj = (JSONObject) results.get(x);

				Movie movie = new Movie();

				movie.setId((Long) idObj.get("id"));
				movie.setTitle((String) idObj.get("title"));
				answer.add(movie);
			}

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return answer;
	}

	static Actor parseActor(String json) {
		JSONParser parser = new JSONParser();

		try {
			JSONObject obj = (JSONObject) parser.parse(json);

			JSONArray results = (JSONArray) obj.get("results");

			JSONObject idObj = (JSONObject) results.get(0);

			Actor actor = new Actor();

			actor.setId((Long) idObj.get("id"));
			actor.setName((String) idObj.get("name"));

			return actor;

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

		return null;
	}

}
