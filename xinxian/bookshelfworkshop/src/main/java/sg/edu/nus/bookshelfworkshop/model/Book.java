package sg.edu.nus.bookshelfworkshop.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Book {

	private Long id;
	@NotBlank(message = "Your book title must not be blank")
	@Size(max = 150, message = "Book Title must not exceed 150 characters")
	private String title;
	private String author;
	@Min(value = 1000, message = "Books must be written after year 1000")
	@Max(value = 2026, message = "Books cannot be written in the future")
	private int year;
	@NotBlank(message = "Genre must not be blank")
	private String genre;

	// TODO Auto-generated constructor stub

	// Constructors
	public Book() {}

	public Book(Long id, String title, String author, int year, String genre) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.year = year;
		this.genre = genre;
	}

	//Getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	
	
	
}
