package org.baaahs

import java.awt.Dimension
import java.awt.image.BufferedImage

class CamWatcher(val listener: Listener) {
  @Volatile private var running = true

  private var diffImage: BufferedImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
  var baseImage: BufferedImage? = null

  private val lock = Object()
  private var nextImage: BufferedImage? = null

  fun start() {
    Thread({
      while(running) {
        val theNextImage: BufferedImage?
        synchronized(lock) {
          theNextImage = nextImage
          nextImage = null
        }

        if (theNextImage != null) {
          calculateDiff(theNextImage)
        }

        synchronized(lock) {
          lock.wait()
        }
      }
    }, "CamWatcher").start()
  }

  fun calculateDiff(newImage: BufferedImage) {
    val dims = Dimension(newImage.width, newImage.height)
    if (diffImage.width != newImage.width || diffImage.height != newImage.height) {
      diffImage = BufferedImage(newImage.width, newImage.height, BufferedImage.TYPE_BYTE_GRAY)
      baseImage = null
    }

    val baseImage = baseImage
    if (baseImage != null) {
      val numBands = baseImage.sampleModel.numBands
      assert(numBands == 3)

      val priorPixels = IntArray(dims.width * numBands)

      val newPixels = IntArray(dims.width * numBands)

      val canvasRaster = diffImage.raster
      val diffPixels = IntArray(dims.width)

      for (y in 0 until dims.height) {
        baseImage.getRGB(0, y, dims.width, 1, priorPixels, 0, 0)
        newImage.getRGB(0, y, dims.width, 1, newPixels, 0, 0)

        for (x in 0 until dims.width) {
          val priorPixelL = brightness(priorPixels[x])
          val newPixelL = brightness(newPixels[x])
          val brightnessDiff = newPixelL - priorPixelL
          diffPixels[x] = if (brightnessDiff < 0) 0 else brightnessDiff
        }

        canvasRaster.setPixels(0, y, dims.width, 1, diffPixels)
      }
    } else {
      this.baseImage = newImage
    }

    listener.onDiff(diffImage)
  }

  private fun brightness(color: Int): Int {
    return (color.and(0xff0000).shr(16) + color.and(0xff00).shr(8) + color.and(0xff)) / 3
  }

  fun stop() {
    synchronized(lock) {
      running = false
      lock.notifyAll()
    }
  }

  fun onImage(image: BufferedImage) {
    nextImage = image

    synchronized (lock) {
      lock.notifyAll()
    }
  }

  interface Listener {
    fun onDiff(image: BufferedImage);
  }
}