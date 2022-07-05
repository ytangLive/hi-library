package org.devio.hi.library.util

import android.graphics.Color

object ColorUtil {
 fun getCurrentColor(startColor: Int, endColor: Int, fraction: Float): Int{

 val redStart = Color.red(startColor)
 val blueStart = Color.blue(startColor)
 val greenStart = Color.green(startColor)
 val alphaStart = Color.alpha(startColor)

 val redEnd = Color.red(endColor)
 val blueEnd = Color.blue(endColor)
 val greenEnd = Color.green(endColor)
 val alphaEnd = Color.alpha(endColor)

 val redDiff = redEnd - redStart
 val blueDiff = blueEnd - blueStart
 val greenDiff = greenEnd - greenStart
 val alphaDiff = alphaEnd - alphaStart

 val redCurrent = (redStart + redDiff * fraction).toInt()
 val blueCurrent = (blueStart + fraction * blueDiff).toInt()
 val greenCurrent = (greenStart + fraction * greenDiff).toInt()
 val alphaCurrent = (alphaStart + fraction * alphaDiff).toInt()

 return Color.argb(alphaCurrent, redCurrent, blueCurrent, greenCurrent)
 }
}