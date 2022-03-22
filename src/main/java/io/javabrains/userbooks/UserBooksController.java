package io.javabrains.userbooks;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import io.javabrains.book.Book;
import io.javabrains.book.BookRepository;

@Controller
public class UserBooksController {
	
    @Autowired 
    UserBooksRepository userBooksRepository;

    @Autowired 
    BookRepository bookRepository;
	
	
	@PostMapping("/addUserBook")
	public ModelAndView addBookForUser(@AuthenticationPrincipal OAuth2User principal,  @RequestBody MultiValueMap<String, String> formData) {
		
		if (principal == null || principal.getAttribute("login") == null) {
            return null;
        }

        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();

        UserBooks userBooks  = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        String userId = principal.getAttribute("login");
        key.setUserId(userId);
        key.setBookId(bookId);

        userBooks.setKey(key);

        int rating = Integer.parseInt(formData.getFirst("rating"));

        userBooks.setStartDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setEndDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);



        return new ModelAndView("redirect:/books/" + bookId);
		
		 
	}
	

}
