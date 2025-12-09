package de.visualdigits.newshomereader.model.cache.images

import de.visualdigits.newshomereader.service.cache.ImageProxy
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.net.URI

@SpringBootTest
@ActiveProfiles("test")
class ImageProxyTest @Autowired constructor(
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
        val url = "https://images.ndr.de/image/eb126784-d97c-454a-adb1-cc4f84b18aa0/AAABkobKAFU/AAABmt42H9g/16x9-big/screenshot1830044.jpg?width=1920"
        val image = imageProxy.getImage(4711U, false, url)
        println(image)
    }
}