package template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Arrays.asList;

/**
 * @author <a href="mailto:vadim.yelisseyev@gmail.com">Vadim Yelisseyev</a>
 */
@Controller
public class ViewController {

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("App")
            .addObject("user", createUser())
            .addObject("items", Arrays.asList(1, 2, 3, 4, 5));
    }

    private User createUser() {
        final User user = new User();
        user.name = "Vadim";
        user.links = asList("https://tt.me/vadim", "https://ok.ru/vadim");
        user.access = true;
        return user;
    }

    private Map<String, Object> createUserObj() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Vadim");
        user.put("links", asList("https://tt.me/vadim", "https://ok.ru/vadim"));
        user.put("access", true);
        return user;
    }

    public static class User {
        public String name;
        public List<String> links;
        public boolean access;
    }
}
