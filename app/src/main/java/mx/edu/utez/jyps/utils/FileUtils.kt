package mx.edu.utez.jyps.utils

/**
 * Utility functions for file handling and string formatting.
 */
object FileUtils {

    /**
     * Truncates a filename if it's too long, preserving the extension.
     * Example: "my_very_long_file_name.pdf" -> "my_very_long...pdf"
     * 
     * @param fileName Full filename with extension.
     * @param maxLength Maximum characters before truncating.
     * @return Formatted string.
     */
    fun formatFileName(fileName: String, maxLength: Int = 18): String {
        if (fileName.length <= maxLength) return fileName
        
        val lastDotIndex = fileName.lastIndexOf('.')
        if (lastDotIndex == -1) return fileName.take(maxLength - 3) + "..."
        
        val extension = fileName.substring(lastDotIndex)
        val nameWithoutExt = fileName.substring(0, lastDotIndex)
        
        val availableForName = maxLength - extension.length - 3
        return if (availableForName > 0) {
            nameWithoutExt.take(availableForName) + "..." + extension
        } else {
            fileName.take(maxLength - 3) + "..."
        }
    }
}
