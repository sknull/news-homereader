package de.visualdigits.newshomereader.model.cache.images

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.net.URI

@SpringBootTest
@ActiveProfiles("test")
class ImageCacheTest @Autowired constructor(
    private val imageProxy: ImageProxy
) {

    @Test
    fun urlEncode() {
        val uri = URI("https://www.n-tv.de/img/30071453/1764159658/Img_4_3/250/Screenshot 2025-11-26 121950.jpg".replace(" ", "+"))
        val path = uri.path
        println(path.replace("+", " "))
    }

    @Test
    fun testCache() {
        val image = imageProxy.getImage("https://images.ndr.de/image/94a73d43-4832-4e9c-a4c6-cd21371d0142/AAABmrVBW4o/AAABmgWmh8Q/16x9-big/prozess-382.jpg?width=1920")
        println(image)
    }
}