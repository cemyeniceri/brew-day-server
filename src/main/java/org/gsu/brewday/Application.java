package org.gsu.brewday;

import org.gsu.brewday.domain.Recipe;
import org.gsu.brewday.dto.response.RecipeInfo;
import org.gsu.brewday.filter.AuthenticationFilter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * */
@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public FilterRegistrationBean jwtFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AuthenticationFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        PropertyMap<Recipe, RecipeInfo> recipeMap = new PropertyMap<Recipe, RecipeInfo>() {
            protected void configure() {
                map().setPrincipal(source.getPrincipal().getObjId());
            }
        };

        modelMapper.addMappings(recipeMap);
        return modelMapper;
    }
}
