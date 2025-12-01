package de.visualdigits.newshomereader

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class Application {

    companion object {
        private var context: ConfigurableApplicationContext? = null

        @JvmStatic
        fun main(args: Array<String>) {
            val application = SpringApplication(Application::class.java)
            context = application.run(*args)
        }
    }
}
