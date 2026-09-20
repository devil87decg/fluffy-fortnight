package sg.edu.nus.bookshelfworkshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sg.edu.nus.bookshelfworkshop.model.Book;

@Service
public class BookService {

	private final List<Book> books = new ArrayList<>();
	private final AtomicLong idCounter = new AtomicLong(1);
	
	public BookService() {
		// Pre-load sample books
		books.add(new Book (idCounter.getAndIncrement(), "Clean Code", "Robert C. Martin", 2008, "Technology"));
		books.add(new Book (idCounter.getAndIncrement(), "The Pragmatic Programmer", "Hunt & Thomas", 1999, "Technology"));
		books.add(new Book (idCounter.getAndIncrement(), "1984", "George Orwell", 1949, "Fiction"));
		books.add(new Book (idCounter.getAndIncrement(), "Psychology of Money", "Morgan Housel", 2020, "Self-Help"));
	}
	
	public List<Book> findAll(){
		return books;
	}
	
	public Optional<Book> findById(Long id){
		return books.stream().filter(b -> b.getId().equals(id)).findFirst();
	}
	
	public void save(Book book) {
		book.setId(idCounter.getAndIncrement());
		books.add(book);
	}
	
	public void delete(Book book) {
		books.remove(book);
	}
	
	public List<Book> searchByTitleOrAuthor(String query){
		return books.stream()
                .filter(book -> (book.getTitle() != null && book.getTitle().toLowerCase().contains(query.toLowerCase()))
                        || (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(query.toLowerCase())))
           .collect(Collectors.toList());
	}
}
