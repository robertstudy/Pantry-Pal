package entities;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import entities.Users.User;
import entities.Users.UserRepository;
import entities.Recipes.RecipeRepository;
import entities.Ingredients.IngredientRepository;
import entities.Favorites.FavoriteRepository;

@SpringBootApplication
@EnableJpaRepositories
@EnableWebSocket
public class Main implements WebSocketConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MyWebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    @Bean
    CommandLineRunner initUser(UserRepository userRepository, RecipeRepository recipeRepository,
                               IngredientRepository ingredientRepository, FavoriteRepository favoriteRepository) {
        return args -> {
        };
    }

    private static class MyWebSocketHandler extends TextWebSocketHandler {

    }
}
