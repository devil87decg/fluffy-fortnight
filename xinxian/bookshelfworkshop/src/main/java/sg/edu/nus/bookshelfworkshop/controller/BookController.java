package sg.edu.nus.bookshelfworkshop.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import sg.edu.nus.bookshelfworkshop.model.Book;
import sg.edu.nus.bookshelfworkshop.service.BookService;
import sg.edu.nus.bookshelfworkshop.validator.BookValidator;

@Controller // (1) Tells Spring: this class handles web requests
public class BookController {

	private final BookService bookService;
	private final BookValidator bookValidator;

	// Spring injects BookService automatically via constructor injection
	public BookController(BookService bookService,
			BookValidator bookValidator) {
		this.bookService = bookService;
		this.bookValidator = bookValidator;
	}

	// Step 1: Home Page
	@GetMapping("/")
	public String home() {
		// No data needed - just return the view name
		return "index"; // resolves to templates/index.html
	}

	// Step 2: Book List
	@GetMapping("/books")
	public String listBooks(Model model) {

		// 1. Fetch data from the service layer
		List<Book> allBooks = bookService.findAll();

		// 2. Add it to the Model under the key "books"
		// The template accesses it with ${books}
		model.addAttribute("books", allBooks);

		// 3. Return the view name (resolves to templates/books.html)
		return "books";
	}

	// Step 3: Book Detail Page
	@GetMapping("/books/{id}")
	public String bookDetail(@PathVariable Long id, // Spring extracts "2" from /books/2
			Model model, HttpSession session,
			HttpServletResponse response) {
		return bookService.findById(id).map(book -> {
			model.addAttribute("book", book);
			
			//TODO: Retrieve the recently-viewed list from the session.
			//If absent, create a new ArrayList<Long>
			@SuppressWarnings("unchecked")
			List<Long> recentIds = (List<Long>) session.getAttribute("recentlyViewed");
			if (recentIds == null) recentIds = new ArrayList<>();
			
			//TODO: Add the current book's id to the front of the list.
			//Remove duplicates first so the same book does not appear twice.
			recentIds.remove(id);
			recentIds.add(0, id);
			
			//TODO: Keep only the 5 most recent entries.
			if (recentIds.size() > 5) {
				recentIds = new ArrayList<> (recentIds.subList(0, 4));
			}
			
			//TODO: Save the updated list back into the session.
			session.setAttribute("recentlyViewed", recentIds);
			
			return "book-detail"; // resolves to templates/book-detail.html
		}).orElseGet(() -> {
			response.setStatus(404);
			return "error/404"; // resolves to templates/error/404.html
		});
	}

	// Step 4a: Show the Add Book form
	@GetMapping("/books/add")
	public String showAddBookForm(Model model) {

		// Add an empty Book to the Model so Thymeleaf can bind
		// form fields to it using th:object
		model.addAttribute("book", new Book());

		return "add-book"; // resolves to add-book.html
	}

	// Step 4b: Handle the submitted form
	@PostMapping("/books/add")
	public String saveBook(@Valid @ModelAttribute Book book, // Spring maps form fields to Book properties
			//@Valid triggers constraint checking
			BindingResult result,
			HttpSession session,
			RedirectAttributes redirectAttrs) {
		
		if (result.hasErrors()) {
			return "add-book";
		}
		bookService.save(book); // Persist to the in-memory list

		Integer count = (Integer) session.getAttribute("booksAddedCount");
		if (count == null) count = 0;
		
		session.setAttribute("booksAddedCount", book);
		
		// Flash attributes survive the redirect and are available in the next request
		redirectAttrs.addFlashAttribute("successMessage", "Book ' " + book.getTitle() + " ' added successfully!");
		// Redirect so the user sees updated list

		return "redirect:/books"; // Not a view name - this is a redirect
	}

	
	  @GetMapping("/books/search") public String searchBooks(@RequestParam String query, Model model) {
	  
	  if (query != null && !query.isBlank()) {
		  
	  model.addAttribute("books", bookService.searchByTitleOrAuthor(query));
	  model.addAttribute("currentQuery", query);
	  } else
	  model.addAttribute("books", bookService.findAll());
	  
	  return "books";
	  }

	@GetMapping("books/delete/{id}")
	public String deleteBooks(Model model, @PathVariable Long id) {
		bookService.delete(bookService.findById(id).get());
		return "forward:/books";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(bookValidator);
	}
	
	@PostMapping("/session/clear")
	public String clearSession(HttpSession session, RedirectAttributes redirectAttrs) {
		//TODO: Decide between two approaches and implement the one you choose:
		// Option 1: Remove only specific keys (e.g. booksAddedCount, recentlyViewed)
		// using session.removeAttribute(key).
		// Option 2: Invalidate the entire session with session.invalidate().
		
		session.invalidate();
		redirectAttrs.addFlashAttribute("successMessage", "Session cleared.");
		return "redirect:/";
	}
}
