package com.xeon.todolist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // just this line
// (you can also add this directly to the main class for cleaner design)
public class JpaConfig {

}
// add this to get baseEntity work