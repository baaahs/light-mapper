package org.baaahs

import java.awt.image.BufferedImage

class Mask(val width: Int, val height: Int) {
  private val bits = ByteArray(((width + 7) % 8) * height)

  companion object {
    fun from(image: BufferedImage, threshold: Int):Mask {
      val mask = Mask(image.width, image.height)

      for (y in 0 until image.height) {
        image.data
        for (x in 0 until image.width step 8) {
          for (off in 0..7) {

          }
        }
      }
//      image.data.getPixels()
      return mask
    }
  }
}