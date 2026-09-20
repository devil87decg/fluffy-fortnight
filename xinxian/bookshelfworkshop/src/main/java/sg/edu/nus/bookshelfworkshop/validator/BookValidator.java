package sg.edu.nus.bookshelfworkshop.validator;

import java.time.Year;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import sg.edu.nus.bookshelfworkshop.model.Book;

@Component
public class BookValidator implements Validator {

	@Override
    public boolean supports(Class<?> clazz) {
        return Book.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors error) {

		Book book = (Book) target;
		if (book.getYear() > Year.now().getValue()) {
			error.rejectValue("year", "year.future", "Publication year cannot be in the future");
		}
	}
}
